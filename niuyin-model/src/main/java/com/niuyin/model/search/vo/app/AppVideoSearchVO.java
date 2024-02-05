package com.niuyin.model.search.vo.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.niuyin.model.video.vo.Author;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * AppVideoSearchVO
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/3
 **/
@Data
public class AppVideoSearchVO {

    // 视频id
    private String videoId;
    // 标题
    private String videoTitle;
    // 视频封面
    private String coverImage;
    private String publishType;
    // 文章发布时间
    private Date publishTime;
    // 标签
    private String[] tags;

    private Long viewNum; //观看量
//    private Long likeNum;  // 点赞量
//    private Long favoriteNum;  // 收藏量
//    private Long commentNum; //评论量

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; //发布时间

    private Long userId;
    private Author author;


}
