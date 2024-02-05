package com.niuyin.dubbo.api;

import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.vo.UserModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务提供者：video
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/7
 **/
public interface DubboVideoService {

    /**
     * 同步视频标签库
     */
    void apiSyncVideoTagStack(String videoId, List<Long> tagsIds);

    /**
     * 通过id获取视频
     */
    Video apiGetVideoByVideoId(String videoId);

    /**
     * 通过ids获取视频
     */
    List<Video> apiGetVideoListByVideoIds(List<String> videoIds);

    /**
     * 通过id获取视频图文
     */
    String[] apiGetVideoImagesByVideoId(String videoId);

    /**
     * 是否点赞某视频
     *
     * @param videoId
     * @param userId
     * @return
     */
    boolean apiWeatherLikeVideo(String videoId, Long userId);

    /**
     * 是否收藏某视频
     *
     * @param videoId
     * @param userId
     * @return
     */
    boolean apiWeatherFavoriteVideo(String videoId, Long userId);

    /**
     * 获取视频标签
     *
     * @param videoId
     * @return
     */
    List<VideoTag> apiGetVideoTagStack(String videoId);

    /**
     * 获取视频标签ids
     *
     * @param videoId
     * @return
     */
    List<Long> apiGetVideoTagIds(String videoId);

    /**
     * 更新用户模型
     *
     * @param userModel
     */
    void apiUpdateUserModel(UserModel userModel);

    /**
     * 初始化用户关注收件箱
     *
     * @param userId
     * @return
     */
    void apiInitFollowVideoFeed(Long userId, List<Long> followIds);
}
