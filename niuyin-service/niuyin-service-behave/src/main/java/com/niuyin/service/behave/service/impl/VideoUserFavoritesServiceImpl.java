package com.niuyin.service.behave.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import com.niuyin.model.behave.vo.UserFavoriteVideoVO;
import com.niuyin.model.behave.vo.app.MyFavoriteVideoVO;
import com.niuyin.model.behave.vo.app.MyLikeVideoVO;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.enums.NoticeType;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoImage;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.model.video.enums.PublishType;
import com.niuyin.model.video.vo.UserModel;
import com.niuyin.service.behave.constants.VideoCacheConstants;
import com.niuyin.service.behave.mapper.UserFavoriteVideoMapper;
import com.niuyin.service.behave.mapper.VideoUserFavoritesMapper;
import com.niuyin.service.behave.mapper.VideoUserLikeMapper;
import com.niuyin.service.behave.service.IUserFavoriteVideoService;
import com.niuyin.service.behave.service.IVideoUserFavoritesService;
import com.niuyin.service.behave.service.IVideoUserLikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE;

/**
 * 视频收藏表(VideoUserFavorites)表服务实现类
 *
 * @author lzq
 * @since 2023-10-31 15:57:38
 */
@Slf4j
@AllArgsConstructor
@Service("videoUserFavoritesService")
public class VideoUserFavoritesServiceImpl extends ServiceImpl<VideoUserFavoritesMapper, VideoUserFavorites> implements IVideoUserFavoritesService {
    @Resource
    private VideoUserFavoritesMapper videoUserFavoritesMapper;

    @Resource
    private UserFavoriteVideoMapper userFavoriteVideoMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoUserLikeMapper videoUserLikeMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Lazy // 解决循环依赖问题
    private IUserFavoriteVideoService userFavoriteVideoService;

    @DubboReference
    private DubboVideoService dubboVideoService;

    @Resource
    private IVideoUserLikeService videoUserLikeService;

    /**
     * 用户收藏
     */
    @Transactional
    @Override
    public boolean userOnlyFavoriteVideo(String videoId) {
        //从token获取用户id
        Long userId = UserContext.getUserId();
        //构建查询条件
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId, videoId).eq(VideoUserFavorites::getUserId, userId);
        //判断当前表中有没有记录
        List<VideoUserFavorites> list = this.list(queryWrapper);
        if (StringUtils.isNull(list) || list.isEmpty()) {
            //没有记录，则新建对象存入数据库
            VideoUserFavorites videoUserFavorites = new VideoUserFavorites();
            videoUserFavorites.setVideoId(videoId);
            videoUserFavorites.setUserId(userId);
            videoUserFavorites.setCreateTime(LocalDateTime.now());
            //将本条点赞信息存储到redis（key为videoId,value为videoUrl）
            favoriteNumIncrease(videoId);
            // 发送消息到通知
            sendNotice2MQ(videoId, userId);
            // 更新用户模型
            List<Long> tagIds = dubboVideoService.apiGetVideoTagIds(videoId);
            dubboVideoService.apiUpdateUserModel(UserModel.buildUserModel(userId, tagIds, 2.0));
            return this.save(videoUserFavorites);
        }
        return false;
    }

    /**
     * 取消收藏
     */
    @Override
    public boolean userUnFavoriteVideo(String videoId) {
        //将本条点赞信息从redis移除
        favoriteNumDecrease(videoId);
        //如果收藏夹中有此视频，同时移除
        LambdaQueryWrapper<UserFavoriteVideo> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFavoriteVideo::getUserId, UserContext.getUserId())
                .eq(UserFavoriteVideo::getVideoId, videoId);
        userFavoriteVideoMapper.delete(qw);
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId, videoId)
                .eq(VideoUserFavorites::getUserId, UserContext.getUserId());
        return this.remove(queryWrapper);
    }

    /**
     * 用户收藏视频，通知mq
     *
     * @param videoId
     * @param operateUserId
     */
    private void sendNotice2MQ(String videoId, Long operateUserId) {
        // 根据视频获取发布者id
        Video video = videoUserLikeMapper.selectVideoByVideoId(videoId);
        if (StringUtils.isNull(video)) {
            return;
        }
        if (operateUserId.equals(video.getUserId())) {
            return;
        }
        // 封装notice实体
        Notice notice = new Notice();
        notice.setOperateUserId(operateUserId);
        notice.setNoticeUserId(video.getUserId());
        notice.setVideoId(videoId);
        notice.setContent("视频被人收藏了0.o");
        notice.setNoticeType(NoticeType.FAVORITE.getCode());
        notice.setReceiveFlag(ReceiveFlag.WAIT.getCode());
        notice.setCreateTime(LocalDateTime.now());
        // notice消息转换为json
        String msg = JSON.toJSONString(notice);
        rabbitTemplate.convertAndSend(NOTICE_DIRECT_EXCHANGE, NOTICE_CREATE_ROUTING_KEY, msg);
        log.debug(" ==> {} 发送了一条消息 ==> {}", NOTICE_DIRECT_EXCHANGE, msg);
    }

    /**
     * @param pageDto
     * @return
     */
    @Override
    public IPage<VideoUserFavorites> queryFavoritePage(VideoPageDto pageDto) {
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getUserId, UserContext.getUserId());
        queryWrapper.orderByDesc(VideoUserFavorites::getCreateTime);
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

    /**
     * 分页查询用户收藏的视频
     *
     * @param pageDto
     * @return
     */
    @Override
    public PageDataInfo queryUserFavoriteVideoPage(VideoPageDto pageDto) {
        pageDto.setPageNum((pageDto.getPageNum() - 1) * pageDto.getPageSize());
        pageDto.setUserId(UserContext.getUserId());
        // 查询用户收藏的视频列表（包含分页）
        List<UserFavoriteVideoVO> videos = videoUserFavoritesMapper.selectUserFavoriteVideos(pageDto);
        // 收集图文视频的视频ID
        List<String> imageVideoIds = videos.stream()
                .filter(r -> r.getPublishType().equals(PublishType.IMAGE.getCode()))
                .map(UserFavoriteVideoVO::getVideoId)
                .collect(Collectors.toList());
        // 批量查询图文视频对应的图片集合（异步）
        CompletableFuture<List<VideoImage>> videoImagesFuture = CompletableFuture.supplyAsync(() ->
                videoUserLikeMapper.selectImagesByVideoIds(imageVideoIds));
        // 更新视频对象的图片集合
        CompletableFuture<Void> updateVideosFuture = videoImagesFuture.thenAcceptAsync(videoImages -> {
            if (videoImages != null) {
                Map<String, List<VideoImage>> videoImageMap = videoImages.stream()
                        .collect(Collectors.groupingBy(VideoImage::getVideoId));
                videos.forEach(r -> {
                    if (r.getPublishType().equals(PublishType.IMAGE.getCode())) {
                        List<VideoImage> videoImageList = videoImageMap.getOrDefault(r.getVideoId(), Collections.emptyList());
                        String[] imgs = videoImageList.stream()
                                .map(VideoImage::getImageUrl)
                                .toArray(String[]::new);
                        r.setImageList(imgs);
                    }
                });
            }
        });
        // 等待异步操作完成
        CompletableFuture.allOf(videoImagesFuture, updateVideosFuture).join();
        // 查询用户收藏的视频总数
        Long count = videoUserFavoritesMapper.selectUserFavoriteVideosCount(pageDto);
        return PageDataInfo.genPageData(videos, count);
    }

    @Override
    public PageDataInfo queryUserFavoriteVideoPageForApp(VideoPageDto pageDto) {
        pageDto.setPageNum((pageDto.getPageNum() - 1) * pageDto.getPageSize());
        pageDto.setUserId(UserContext.getUserId());
        // 查询用户收藏的视频列表（包含分页）
        List<String> videoIds = videoUserFavoritesMapper.selectUserFavoriteVideoIds(pageDto);
        if(videoIds.isEmpty()){
            return PageDataInfo.emptyPage();
        }
        // 查询视频
        List<Video> videos = dubboVideoService.apiGetVideoListByVideoIds(videoIds);
        List<MyFavoriteVideoVO> myFavoriteVideoVOList = BeanCopyUtils.copyBeanList(videos, MyFavoriteVideoVO.class);
        // 设置点赞量
        CompletableFuture.allOf(myFavoriteVideoVOList.stream().map(this::packageMyLikeVideoPageAsync).toArray(CompletableFuture[]::new)).join();
        // 查询用户收藏的视频总数
        Long count = videoUserFavoritesMapper.selectUserFavoriteVideosCount(pageDto);
        return PageDataInfo.genPageData(myFavoriteVideoVOList, count);
    }

    @Async
    public CompletableFuture<Void> packageMyLikeVideoPageAsync(MyFavoriteVideoVO vo) {
        return CompletableFuture.runAsync(() -> packageMyLikeVideoPage(vo));
    }

    @Async
    public void packageMyLikeVideoPage(MyFavoriteVideoVO vo) {
        vo.setLikeNum(videoUserLikeService.getVideoLikeNum(vo.getVideoId()));
    }


    @Async
    protected void favoriteNumIncrease(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, videoId, 1);
    }

    @Async
    protected void favoriteNumDecrease(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, videoId, -1);
    }

    /**
     * 删除说有用户收藏此视频记录 ！！！
     *
     * @param videoId
     * @return
     */
    @Transactional
    @Override
    public boolean removeFavoriteRecordByVideoId(String videoId) {
        // 删除仅收藏
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId, videoId);
        boolean remove = this.remove(queryWrapper);
        // 从收藏夹删除
        LambdaQueryWrapper<UserFavoriteVideo> qwUFV = new LambdaQueryWrapper<>();
        qwUFV.eq(UserFavoriteVideo::getVideoId, videoId);
        boolean removed = userFavoriteVideoService.remove(qwUFV);
        return remove && removed;
    }

    @Override
    public Long getFavoriteCountByVideoId(String videoId) {
        return videoUserFavoritesMapper.selectFavoriteCountByVideoId(videoId);
    }
}
