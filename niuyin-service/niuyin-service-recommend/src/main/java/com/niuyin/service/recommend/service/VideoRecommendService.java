package com.niuyin.service.recommend.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.service.RedisService;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboBehaveService;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.dubbo.api.DubboSocialService;
import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoPosition;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.enums.PositionFlag;
import com.niuyin.model.video.enums.PublishType;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.recommend.event.VideoRecommendEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.niuyin.model.constants.VideoCacheConstants.VIDEO_IMAGES_PREFIX_KEY;
import static com.niuyin.model.constants.VideoCacheConstants.VIDEO_POSITION_PREFIX_KEY;

/**
 * 视频推荐服务
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@Slf4j
@Service
public class VideoRecommendService {

    private static final int VIDEO_RECOMMEND_COUNT = 10; // 推荐视频数量
    private static final int PULL_VIDEO_RECOMMEND_THRESHOLDS = 100; // 拉取推荐视频阈值
    private static final int PULL_VIDEO_RECOMMEND_COUNT = 100; // 拉取推荐视频数量

    @Resource
    private RedisService redisService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private VideoRecommendLoadBalancer videoRecommendLoadBalancer;

    @DubboReference(retries = 3, mock = "return null")
    private DubboVideoService dubboVideoService;

    @DubboReference(mock = "return null")
    private DubboBehaveService dubboBehaveService;

    @DubboReference(mock = "return null")
    private DubboMemberService dubboMemberService;

    @DubboReference(mock = "return null")
    private DubboSocialService dubboSocialService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private UserTagModalRecommendService userTagModalRecommendService;

    /**
     * 解决异步线程无法访问主线程的ThreadLocal
     */
    private static final ThreadLocal<Long> userIdThreadLocal = new InheritableThreadLocal<>();

    public static void setUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }

    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    /**
     * 获取推荐视频流
     */
    public List<VideoVO> pullVideoFeed() {
        if (UserContext.hasLogin()) {
            long startTime = System.currentTimeMillis();
            Long userId = UserContext.getUserId();
            String listKey = "recommend:user_recommend_videos:" + userId;
//            List<String> top10Items = lpopItemsFromRedisList(listKey, VIDEO_RECOMMEND_COUNT); // 耗时占了30% 400ms =》优化后 33ms
            List<String> top10Items = lpopItemsFromRedisListOptimized(redisTemplate, listKey, VIDEO_RECOMMEND_COUNT); // 优化后 33ms
            if (llengthOfRedisList(listKey) < PULL_VIDEO_RECOMMEND_THRESHOLDS) {
                // redis list 推荐列表小于 阈值 发送事件补充推荐列表
                applicationEventPublisher.publishEvent(new VideoRecommendEvent(this, userId));
            }
            List<Video> videoList = dubboVideoService.apiGetVideoListByVideoIds(top10Items); // 耗时占了17% 220ms
            if (CollectionUtils.isEmpty(videoList)) {
                return new ArrayList<>();
            }
            // 过滤空值
            videoList = videoList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(videoList, VideoVO.class);
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoVOList
                    .stream()
                    .map(vo -> packageUserVideoVOAsync(vo, userId))
                    .toArray(CompletableFuture[]::new));
            allFutures.join();
            long endTime = System.currentTimeMillis();

            // 计算方法的执行时间
            long duration = endTime - startTime;

            System.out.println("方法执行时间：" + duration + " 毫秒");
            return videoVOList;
        } else {
            // todo 未登录 用户未登录如何推送
            Long userIdUnLogin = 2l;
            List<String> videoIdsByUserModel = userTagModalRecommendService.getVideoIdsByUserModel(userIdUnLogin);
            List<Video> videoList = dubboVideoService.apiGetVideoListByVideoIds(videoIdsByUserModel);
            List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(videoList, VideoVO.class);
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoVOList
                    .stream()
                    .map(vo -> packageUserVideoVOAsync(vo, null))
                    .toArray(CompletableFuture[]::new));
            allFutures.join();
            return videoVOList;
        }
    }

    /**
     * redis list长度
     *
     * @param key
     * @return
     */
    public Long llengthOfRedisList(String key) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        return listOps.size(key);
    }

    /**
     * redis list lpop
     *
     * @param key
     * @param count
     * @return
     */
    public List<String> lpopItemsFromRedisList(String key, int count) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> items = listOps.range(key, 0, count - 1); // 获取列表中的前20条数据
        for (int i = 0; i < count; i++) {
            listOps.leftPop(key); // 从左侧弹出数据
        }
        return items;
    }

    public List<String> lpopItemsFromRedisListOptimized(RedisTemplate<String, String> redisTemplate, String key, int count) {
        // 使用Redis Script执行批量LPOP操作
        DefaultRedisScript<List> script = new DefaultRedisScript<>(
                "local result = {}; " +
                        "for i=1," + count + " do " +
                        "  table.insert(result, redis.call('lpop', KEYS[1])); " +
                        "end; " +
                        "return result;",
                List.class
        );

        // 执行Lua脚本并获取结果
        List<String> items = (List<String>) redisTemplate.execute(script, Collections.singletonList(key));

        return items;
    }

    @Async
    public CompletableFuture<Void> packageUserVideoVOAsync(VideoVO videoVO, Long loginUserId) {
        return CompletableFuture.runAsync(() -> packageUserVideoVO(videoVO, loginUserId));
    }

    @Async
    public void packageUserVideoVO(VideoVO videoVO, Long loginUserId) {
        CompletableFuture<Void> behaveDataFuture = packageVideoBehaveDataAsync(videoVO);
        CompletableFuture<Void> memberDataFuture = packageMemberDataAsync(videoVO);
        CompletableFuture<Void> imageDataFuture = packageVideoImageDataAsync(videoVO);
        CompletableFuture<Void> socialDataAsync = packageVideoSocialDataAsync(videoVO, loginUserId);
        CompletableFuture<Void> tagDataAsync = packageVideoTagDataAsync(videoVO);
        CompletableFuture<Void> positionDataAsync = packageVideoPositionDataAsync(videoVO);
        CompletableFuture.allOf(
                behaveDataFuture,
                memberDataFuture,
                imageDataFuture,
                socialDataAsync,
                tagDataAsync,
                positionDataAsync
        ).join();
    }

    @Async
    public CompletableFuture<Void> packageVideoBehaveDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoBehaveData(videoVO));
    }

    @Async
    public CompletableFuture<Void> packageMemberDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageMemberData(videoVO));
    }

    @Async
    public CompletableFuture<Void> packageVideoImageDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoImageData(videoVO));
    }

    @Async
    public CompletableFuture<Void> packageVideoSocialDataAsync(VideoVO videoVO, Long loginUserId) {
        return CompletableFuture.runAsync(() -> packageVideoSocialData(videoVO, loginUserId));
    }

    @Async
    public CompletableFuture<Void> packageVideoTagDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoTagData(videoVO));
    }

    @Async
    public CompletableFuture<Void> packageVideoPositionDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoPositionData(videoVO));
    }

    /**
     * 封装视频行为数据
     *
     * @param videoVO
     */
    @Async
    public void packageVideoBehaveData(VideoVO videoVO) {
        // 封装观看量、点赞数、收藏量
//        Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoVO.getVideoId());
//        videoVO.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
        Long likeNum = dubboBehaveService.apiGetVideoLikeNum(videoVO.getVideoId());
        videoVO.setLikeNum(likeNum == null ? 0L : likeNum);
        Long favoriteNum = dubboBehaveService.apiGetVideoFavoriteNum(videoVO.getVideoId());
        videoVO.setFavoritesNum(favoriteNum == null ? 0L : favoriteNum);
        // 评论数
        Long commentNum = dubboBehaveService.apiGetVideoCommentNum(videoVO.getVideoId());
        videoVO.setCommentNum(commentNum == null ? 0L : commentNum);
    }

    /**
     * 封装用户数据
     *
     * @param videoVO
     */
    @Async
    public void packageMemberData(VideoVO videoVO) {
        // 封装用户信息
        Member publishUser = dubboMemberService.apiGetById(videoVO.getUserId());
        videoVO.setUserNickName(StringUtils.isNull(publishUser) ? "-" : publishUser.getNickName());
        videoVO.setUserAvatar(StringUtils.isNull(publishUser) ? null : publishUser.getAvatar());
    }

    /**
     * 封装视频社交数据
     *
     * @param videoVO
     */
    @Async
    public void packageVideoSocialData(VideoVO videoVO, Long loginUserId) {
        if (StringUtils.isNotNull(loginUserId)) {
            // 是否关注、是否点赞、是否收藏
            videoVO.setWeatherLike(dubboBehaveService.apiWeatherLikeVideo(videoVO.getVideoId(), loginUserId));
            videoVO.setWeatherFavorite(dubboBehaveService.apiWeatherFavoriteVideo(videoVO.getVideoId(), loginUserId));
            if (videoVO.getUserId().equals(loginUserId)) {
                videoVO.setWeatherFollow(true);
            } else {
                videoVO.setWeatherFollow(dubboSocialService.apiWeatherFollow(loginUserId, videoVO.getUserId()));
            }
        }
    }

    /**
     * 封装视频标签数据
     */
    @Async
    public void packageVideoTagData(VideoVO videoVO) {
        // 封装标签返回
        List<VideoTag> videoTagList = dubboVideoService.apiGetVideoTagStack(videoVO.getVideoId());
        videoVO.setTags(videoTagList.stream().map(VideoTag::getTag).toArray(String[]::new));
    }

    /**
     * 封装图文数据
     *
     * @param videoVO
     */
    @Async
    public void packageVideoImageData(VideoVO videoVO) {
        // 若是图文则封装图片集合
        if (videoVO.getPublishType().equals(PublishType.IMAGE.getCode())) {
            Object imgsCacheObject = redisService.getCacheObject(VIDEO_IMAGES_PREFIX_KEY + videoVO.getVideoId());
            if (StringUtils.isNotNull(imgsCacheObject)) {
                if (imgsCacheObject instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) imgsCacheObject;
                    videoVO.setImageList(jsonArray.toArray(new String[0]));
                } else if (imgsCacheObject instanceof String) {
                    String jsonString = (String) imgsCacheObject;
                    videoVO.setImageList(JSON.parseObject(jsonString, String[].class));
                }
            } else {
                String[] imgs = dubboVideoService.apiGetVideoImagesByVideoId(videoVO.getVideoId());
                videoVO.setImageList(imgs);
                // 重建缓存
                redisService.setCacheObject(VIDEO_IMAGES_PREFIX_KEY + videoVO.getVideoId(), imgs);
                redisService.expire(VIDEO_IMAGES_PREFIX_KEY + videoVO.getVideoId(), 1, TimeUnit.DAYS);
            }
        }
    }

    /**
     * 封装视频定位数据
     *
     * @param videoVO
     */
    @Async
    public void packageVideoPositionData(VideoVO videoVO) {
        // 若是开启定位，封装定位
        if (videoVO.getPositionFlag().equals(PositionFlag.OPEN.getCode())) {
            // 查询redis缓存
            VideoPosition videoPositionCache = redisService.getCacheObject(VIDEO_POSITION_PREFIX_KEY + videoVO.getVideoId());
            if (StringUtils.isNotNull(videoPositionCache)) {
                videoVO.setPosition(videoPositionCache);
            } else {
                VideoPosition videoPosition = dubboVideoService.apiGetVideoPositionByVideoId(videoVO.getVideoId());
                videoVO.setPosition(videoPosition);
                // 重建缓存
                redisService.setCacheObject(VIDEO_POSITION_PREFIX_KEY + videoVO.getVideoId(), videoPosition);
                redisService.expire(VIDEO_POSITION_PREFIX_KEY + videoVO.getVideoId(), 1, TimeUnit.DAYS);
            }
        }
    }


}
