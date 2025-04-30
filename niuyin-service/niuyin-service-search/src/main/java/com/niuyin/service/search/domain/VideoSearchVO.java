//package com.niuyin.service.search.domain;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//
//import java.util.Date;
//
//import static com.niuyin.service.search.constant.ESIndexConstants.INDEX_VIDEO;
//
///**
// * VideoSearchVO
// *
// * @AUTHOR: roydon
// * @DATE: 2023/10/31
// * 视频搜索返回体
// **/
//@Data
//@JsonIgnoreProperties(ignoreUnknown=true)
//@Document(indexName = INDEX_VIDEO, createIndex = true)
//public class VideoSearchVO {
//
//    // 视频id
//    @Id
//    private String videoId;
//    public static final String VIDEO_ID = "videoId";
//    // 标题
//    private String videoTitle;
//    public static final String VIDEO_TITLE = "videoTitle";
//    // 文章发布时间
//    private Date publishTime;
//    public static final String PUBLISH_TIME = "publishTime";
//    // 视频封面
//    private String coverImage;
//    public static final String COVER_IMAGE = "coverImage";
//    // 视频地址
//    private String videoUrl;
//    public static final String VIDEO_URL = "videoUrl";
//    // 视频类型
//    private String publishType;
//    public static final String PUBLISH_TYPE = "publishType";
//    // 用户id
//    private Long userId;
//    public static final String USER_ID = "userId";
//    // 标签
//    private String[] tags;
//    public static final String TAGS = "tags";
//
//}
