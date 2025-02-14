package com.niuyin.model.video.vo.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.niuyin.model.video.vo.Author;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * app首页视频推荐vo
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/31
 **/
@Data
public class VideoRecommendVO {
    private String videoId;
    private String videoTitle; // 标题
    private String coverImage; // 封面
    private String videoUrl; // 视频地址
    // 图片集合
    private String[] imageList;
    private Long viewNum; // 观看量
    private Long likeNum;  // 点赞量
    private Long favoriteNum;  // 收藏量
    private Long commentNum; //评论量
    private String publishType; //发布类型（0视频，1图文）
    private String videoInfo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; //发布时间
    private Long userId;
    private Author author;
}
