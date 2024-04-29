package com.niuyin.dubbo.api;

/**
 * DubboBehaveService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/22
 **/
public interface DubboBehaveService {

    /**
     * 视频点赞量
     *
     * @param videoId
     * @return
     */
    Long apiGetVideoLikeNum(String videoId);

    /**
     * 视频收藏量
     *
     * @param videoId
     * @return
     */
    Long apiGetVideoFavoriteNum(String videoId);

    /**
     * 视频评论量
     *
     * @param videoId
     * @return
     */
    Long apiGetVideoCommentNum(String videoId);

    boolean apiDeleteVideoDocumentByVideoId(String videoId);

    /**
     * 删除视频评论
     *
     * @param videoId
     * @return
     */
    boolean removeVideoCommentByVideoId(String videoId);

    boolean removeOtherLikeVideoBehaveRecord(String videoId);

    boolean removeOtherFavoriteVideoBehaveRecord(String videoId);

    /**
     * 是否点赞视频
     *
     * @param videoId
     * @param userId
     * @return
     */
    boolean apiWeatherLikeVideo(String videoId, Long userId);

    boolean apiWeatherFavoriteVideo(String videoId, Long userId);
}
