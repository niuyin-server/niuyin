package com.niuyin.service.video.constants;

/**
 * HotVideoConstants
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/7
 * 热门视频常量
 **/
public class HotVideoConstants {

    // 筛选近7天的视频数据
    public static final int VIDEO_BEFORE_DAT7 = 7;
    public static final int VIDEO_BEFORE_DAT5 = 5;

    // 视频热门算分规则，按照权重
    public static final Long WEIGHT_LIKE = 12L;
    public static final Long WEIGHT_FAVORITE = 20L;
    public static final Long WEIGHT_VIEW = 1L;
    public static final Long WEIGHT_CREATE_TIME = 3L;
    public static final Long WEIGHT_COMMENT = 10L;
    public static final Long WEIGHT_SHARE = 15L;
}
