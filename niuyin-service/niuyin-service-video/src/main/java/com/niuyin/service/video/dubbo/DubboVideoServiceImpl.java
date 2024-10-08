package com.niuyin.service.video.dubbo;

import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoPosition;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.vo.UserModel;
import com.niuyin.model.video.vo.UserVideoCompilationInfoVO;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.video.mapper.VideoMapper;
import com.niuyin.service.video.service.*;
import com.niuyin.service.video.service.cache.VideoRedisBatchCache;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DubboVideoServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/7
 **/
@DubboService(timeout = 2000, retries = 3, weight = 1)
public class DubboVideoServiceImpl implements DubboVideoService {

    @Resource
    private InterestPushService interestPushService;

    @Resource
    private IVideoService videoService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private IVideoTagRelationService videoTagRelationService;

    @Resource
    private UserFollowVideoPushService userFollowVideoPushService;

    @Resource
    private IVideoPositionService videoPositionService;

    @Resource
    private VideoRedisBatchCache videoRedisBatchCache;

    @Resource
    private IUserVideoCompilationRelationService userVideoCompilationRelationService;

    @Resource
    private IUserVideoCompilationService userVideoCompilationService;

    /**
     * 同步视频标签库
     *
     * @param videoId
     * @param tagsIds
     */
    @Override
    public void apiSyncVideoTagStack(String videoId, List<Long> tagsIds) {
        interestPushService.cacheVideoToTagRedis(videoId, tagsIds);
    }

    /**
     * 通过id获取视频
     *
     * @param videoId
     */
    @Override
    public Video apiGetVideoByVideoId(String videoId) {
        return videoService.getById(videoId);
    }

    /**
     * 通过ids获取视频
     *
     * @param videoIds
     */
    @Override
    public List<Video> apiGetVideoListByVideoIds(List<String> videoIds) {
        Map<String, Video> batch = videoRedisBatchCache.getBatch(new ArrayList<>(videoIds));
        return new ArrayList<>(batch.values());
    }

    /**
     * 通过id获取视频图文
     *
     * @param videoId
     */
    @Override
    public String[] apiGetVideoImagesByVideoId(String videoId) {
        return videoService.getVideoImages(videoId);
    }

    /**
     * 是否点赞某视频
     *
     * @param videoId
     * @param userId
     * @return
     */
    @Override
    public boolean apiWeatherLikeVideo(String videoId, Long userId) {
        return videoMapper.selectUserLikeVideo(videoId, userId) > 0;
    }

    /**
     * 是否收藏某视频
     *
     * @param videoId
     * @param userId
     * @return
     */
    @Override
    public boolean apiWeatherFavoriteVideo(String videoId, Long userId) {
        return videoMapper.userWeatherFavoriteVideo(videoId, userId) > 0;
    }

    /**
     * 获取视频标签
     *
     * @param videoId
     * @return
     */
    @Override
    public List<VideoTag> apiGetVideoTagStack(String videoId) {
        return videoTagRelationService.queryVideoTagsByVideoId(videoId);
    }

    /**
     * 获取视频标签ids
     *
     * @param videoId
     * @return
     */
    @Override
    public List<Long> apiGetVideoTagIds(String videoId) {
        return videoTagRelationService.queryVideoTagIdsByVideoId(videoId);
    }

    /**
     * 更新用户模型
     *
     * @param userModel
     */
    @Override
    public void apiUpdateUserModel(UserModel userModel) {
        interestPushService.updateUserModel(userModel);
    }

    /**
     * 初始化用户关注收件箱
     *
     * @param userId
     * @return
     */
    @Override
    public void apiInitFollowVideoFeed(Long userId, List<Long> followIds) {
        userFollowVideoPushService.initFollowVideoFeed(userId, followIds);
    }

    /**
     * 通过videoIds获取vo
     *
     * @param videoIds
     * @return
     */
    @Override
    public List<VideoVO> apiGetVideoVOListByVideoIds(Long loginUserId, List<String> videoIds) {
        return videoService.packageVideoVOByVideoIds(loginUserId, videoIds);
    }

    /**
     * 获取视频定位
     *
     * @param videoId
     * @return
     */
    @Override
    public VideoPosition apiGetVideoPositionByVideoId(String videoId) {
        return videoPositionService.queryPositionByVideoId(videoId);
    }

    /**
     * 根据视频获取所在视频合集
     *
     * @param videoId
     * @return
     */
    @Override
    public UserVideoCompilationInfoVO apiGetUserVideoCompilationInfoVO(String videoId) {
        return userVideoCompilationService.getCompilationInfoVOByVideoId(videoId);
    }
}
