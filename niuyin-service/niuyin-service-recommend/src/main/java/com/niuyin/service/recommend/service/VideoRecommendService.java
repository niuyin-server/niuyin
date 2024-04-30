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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    private static final int VIDEO_RECOMMEND_COUNT = 20; // 推荐视频数量
    private static final int PULL_VIDEO_RECOMMEND_THRESHOLDS = 100; // 拉取推荐视频阈值
    private static final int PULL_VIDEO_RECOMMEND_COUNT = 100; // 拉取推荐视频数量

    @Resource
    private RedisService redisService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private VideoRecommendLoadBalancer videoRecommendLoadBalancer;

    @DubboReference(mock = "return null")
    private DubboVideoService dubboVideoService;

    @DubboReference(mock = "return null")
    private DubboBehaveService dubboBehaveService;

    @DubboReference(mock = "return null")
    private DubboMemberService dubboMemberService;

    @DubboReference(mock = "return null")
    private DubboSocialService dubboSocialService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

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
            Long userId = UserContext.getUserId();
            String listKey = "recommend:user_recommend_videos:" + userId;
            List<String> top20Items = get20ItemsFromList(listKey);
            if (top20Items.isEmpty()) {
                // 发布事件补充推荐列表
                applicationEventPublisher.publishEvent(new VideoRecommendEvent(this, userId));
                return new ArrayList<>();
            }
            if (top20Items.size() < VIDEO_RECOMMEND_COUNT) {
                // 发布事件补充推荐列表
                applicationEventPublisher.publishEvent(new VideoRecommendEvent(this, userId));
            }
            List<Video> videoList = dubboVideoService.apiGetVideoListByVideoIds(top20Items);
            List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(videoList, VideoVO.class);
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoVOList
                    .stream()
                    .map(vo -> packageUserVideoVOAsync(vo, userId))
                    .toArray(CompletableFuture[]::new));
            allFutures.join();
            return videoVOList;
        } else {
            // todo 未登录
            return new ArrayList<>();
        }
    }

    public List<String> get20ItemsFromList(String key) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> items = listOps.range(key, 0, VIDEO_RECOMMEND_COUNT - 1); // 获取列表中的前20条数据
        for (int i = 0; i < VIDEO_RECOMMEND_COUNT; i++) {
            listOps.leftPop(key); // 从左侧弹出数据
        }
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
        log.debug("packageVideoBehaveData开始");
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
        log.debug("packageVideoBehaveData结束");
    }

    /**
     * 封装用户数据
     *
     * @param videoVO
     */
    @Async
    public void packageMemberData(VideoVO videoVO) {
        log.debug("packageMemberData开始");
        // 封装用户信息
        Member userCache = redisService.getCacheObject("member:userinfo:" + videoVO.getUserId());
        if (StringUtils.isNotNull(userCache)) {
            videoVO.setUserNickName(userCache.getNickName());
            videoVO.setUserAvatar(userCache.getAvatar());
        } else {
            Member publishUser = dubboMemberService.apiGetById(videoVO.getUserId());
            videoVO.setUserNickName(StringUtils.isNull(publishUser) ? "-" : publishUser.getNickName());
            videoVO.setUserAvatar(StringUtils.isNull(publishUser) ? null : publishUser.getAvatar());
        }
        log.debug("packageMemberData结束");
    }

    /**
     * 封装视频社交数据
     *
     * @param videoVO
     */
    @Async
    public void packageVideoSocialData(VideoVO videoVO, Long loginUserId) {
        log.debug("packageVideoSocialData开始：{}", getUserId());
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
        log.debug("packageVideoSocialData结束");
    }

    /**
     * 封装视频标签数据
     */
    @Async
    public void packageVideoTagData(VideoVO videoVO) {
        log.debug("packageVideoTagData开始");
        // 封装标签返回
        List<VideoTag> videoTagList = dubboVideoService.apiGetVideoTagStack(videoVO.getVideoId());
        videoVO.setTags(videoTagList.stream().map(VideoTag::getTag).toArray(String[]::new));
        log.debug("packageVideoTagData结束");
    }

    /**
     * 封装图文数据
     *
     * @param videoVO
     */
    @Async
    public void packageVideoImageData(VideoVO videoVO) {
        log.debug("packageVideoImageData开始");
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
        log.debug("packageVideoImageData结束");
    }

    /**
     * 封装视频定位数据
     *
     * @param videoVO
     */
    @Async
    public void packageVideoPositionData(VideoVO videoVO) {
        log.debug("packageVideoPositionData开始");
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
        log.debug("packageVideoPositionData结束");
    }


}
