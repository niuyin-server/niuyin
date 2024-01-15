package com.niuyin.service.video.dubbo;

import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.vo.UserModel;
import com.niuyin.service.video.mapper.VideoMapper;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.service.video.service.IVideoTagRelationService;
import com.niuyin.service.video.service.InterestPushService;
import com.niuyin.service.video.service.UserFollowVideoPushService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

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

}
