package com.niuyin.service.search.domain;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * VideoSearchVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 视频搜索返回体
 **/
@Data
@Document(indexName = "search_video", createIndex = true)
public class VideoSearchVO {

    // 视频id
    private String videoId;
    public static final String VIDEO_ID = "videoId";
    // 标题
    private String videoTitle;
    public static final String VIDEO_TITLE = "videoTitle";
    // 文章发布时间
    private Date publishTime;
    public static final String PUBLISH_TIME = "publishTime";
    // 视频封面
    private String coverImage;
    public static final String COVER_IMAGE = "coverImage";
    // 视频地址
    private String videoUrl;
    public static final String VIDEO_URL = "videoUrl";
    // 视频类型
    private String publishType;
    public static final String PUBLISH_TYPE = "publishType";
    // 用户id
    private Long userId;
    public static final String USER_ID = "userId";
    // 用户昵称
    private String userNickName;
    public static final String USER_NICKNAME = "userNickName";
    // 用户头像
    private String userAvatar;
    public static final String USER_AVATAR = "userAvatar";

}
