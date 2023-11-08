package com.qiniu.service.behave.constants;

/**
 * UserCacheConstant
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/29
 **/
public class VideoCacheConstants {

    public static final String VIDEO_INFO_PREFIX = "video:videoinfo:";
    public static final String VIDEO_CATEGORY_PREFIX = "video:category";

    public static final long VIDEO_INFO_EXPIRE_TIME = 3600 * 24; //1天

    public static final String VIDEO_LIKE_NUM_MAP_KEY = "video:like:num";
    public static final String VIDEO_FAVORITE_NUM_MAP_KEY = "video:favorite:num";


    public static final String VIDEO_VIEW_NUM_MAP_KEY = "video:view:num";

    public static final String VIDEO_VIEW_COVER_IMAGE_KEY = "?vframe/jpg/offset/1";


}
