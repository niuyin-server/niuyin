package com.niuyin.service.behave.dubbo;

import com.niuyin.dubbo.api.DubboBehaveService;
import com.niuyin.model.search.dubbo.VideoBehaveData;
import com.niuyin.service.behave.service.IVideoUserCommentService;
import com.niuyin.service.behave.service.IVideoUserFavoritesService;
import com.niuyin.service.behave.service.IVideoUserLikeService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * DubboBehaveServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/22
 **/
@DubboService
public class DubboBehaveServiceImpl implements DubboBehaveService {

    @Resource
    private IVideoUserCommentService videoUserCommentService;

    @Resource
    private IVideoUserLikeService videoUserLikeService;

    @Resource
    private IVideoUserFavoritesService videoUserFavoritesService;

    /**
     * 视频点赞量
     *
     * @param videoId
     * @return
     */
    @Override
    public Long apiGetVideoLikeNum(String videoId) {
        return videoUserLikeService.getVideoLikeNum(videoId);
    }

    /**
     * 视频收藏量
     *
     * @param videoId
     * @return
     */
    @Override
    public Long apiGetVideoFavoriteNum(String videoId) {
        return videoUserFavoritesService.getFavoriteCountByVideoId(videoId);
    }

    /**
     * 视频评论量
     *
     * @param videoId
     * @return
     */
    @Override
    public Long apiGetVideoCommentNum(String videoId) {
        return videoUserCommentService.queryCommentCountByVideoId(videoId);
    }

    @Override
    public boolean apiDeleteVideoDocumentByVideoId(String videoId) {
        return true;
    }

    /**
     * 删除视频评论
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean removeVideoCommentByVideoId(String videoId) {
        return videoUserCommentService.removeCommentByVideoId(videoId);
    }

    @Override
    public boolean removeOtherLikeVideoBehaveRecord(String videoId) {
        return videoUserLikeService.removeLikeRecordByVideoId(videoId);
    }

    @Override
    public boolean removeOtherFavoriteVideoBehaveRecord(String videoId) {
        return videoUserFavoritesService.removeFavoriteRecordByVideoId(videoId);
    }

    /**
     * 是否点赞视频
     *
     * @param videoId
     * @param userId
     * @return
     */
    @Override
    public boolean apiWeatherLikeVideo(String videoId, Long userId) {
        return videoUserLikeService.weatherLikeVideo(videoId, userId);
    }

    @Override
    public boolean apiWeatherFavoriteVideo(String videoId, Long userId) {
        return videoUserFavoritesService.weatherFavoriteVideo(videoId, userId);
    }

    @Override
    public VideoBehaveData apiGetVideoBehaveData(String videoId) {
        VideoBehaveData videoBehaveData = new VideoBehaveData();
        videoBehaveData.setViewCount(100L);
        videoBehaveData.setLikeCount(videoUserLikeService.getVideoLikeNum(videoId));
        videoBehaveData.setCommentCount(videoUserCommentService.queryCommentCountByVideoId(videoId));
        videoBehaveData.setFavoriteCount(videoUserFavoritesService.getFavoriteCountByVideoId(videoId));
        return videoBehaveData;
    }
}
