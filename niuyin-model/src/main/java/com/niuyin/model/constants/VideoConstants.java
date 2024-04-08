package com.niuyin.model.constants;

/**
 * VideoConstants
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/5
 **/
public class VideoConstants {

    /**
     * 缓存常量
     */

    /**
     * 发件箱，用户发布视频后推送视频id到用户视频发件箱
     * zSet
     *      field：videoId，value：publishTime
     */
    public static final String OUT_FOLLOW = "video:follow:out:feed:";

    /**
     * 收件箱，用户关注视频流（拉模式）
     * zSet
     *      field：videoId，value：publishTime
     */
    public static final String IN_FOLLOW = "video:follow:in:feed:";


}
