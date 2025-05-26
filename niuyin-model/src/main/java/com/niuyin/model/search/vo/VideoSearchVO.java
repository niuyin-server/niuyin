package com.niuyin.model.search.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

/**
 * VideoSearchVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 视频搜索返回体
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@Document(indexName = "search_video", createIndex = true)
public class VideoSearchVO {

    @Id
    private String videoId;
    public static final String VIDEO_ID = "videoId";
    // 标题
//    @Field(analyzer = "ik_smart",searchAnalyzer = "ik_smart")
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
    // 标签
//    @Field(analyzer = "ik_smart")
    private String[] tags;
    public static final String TAGS = "tags";

}
