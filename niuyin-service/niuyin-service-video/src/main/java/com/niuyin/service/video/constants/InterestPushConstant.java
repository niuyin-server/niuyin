package com.niuyin.service.video.constants;

/**
 * 兴趣推送 redis key 常量
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/20
 **/
public class InterestPushConstant {
    public static final String VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX = "video:tag:videos:";
    public static final String VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX = "video:category:videos:";
    // 根据分类已推送过的视频 通过userId区分
    public static final String VIDEO_CATEGORY_PUSHED_CACHE_KEY_PREFIX = "video:category:pushed:"; // + userId
    public static final String VIDEO_MEMBER_MODEL_CACHE_KEY_PREFIX = "video:member:model:";
    // 视频观看历史，使用redis保存7天
    public static final String VIDEO_VIEW_HISTORY_CACHE_KEY_PREFIX = "video:view:history:";
}
