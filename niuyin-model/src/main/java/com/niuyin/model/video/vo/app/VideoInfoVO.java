package com.niuyin.model.video.vo.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.niuyin.model.video.domain.VideoPosition;
import com.niuyin.model.video.vo.Author;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * VideoInfoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/31
 **/
@Data
public class VideoInfoVO {
    private String videoId;
    private String videoTitle; // 标题
    private String coverImage; // 封面
    private String videoDesc; // 描述
    private String videoUrl; // 视频地址
    private Long viewNum; // 观看量
    private Long likeNum;  // 点赞量
    private Long favoriteNum;  // 收藏量
    private Long commentNum; // 评论量
    private String publishType; // 发布类型（0视频，1图文）
    private String positionFlag; // 定位功能（0关闭，1开启）
    private String videoInfo; //视频详情
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 发布时间
    private Long userId;
    private Author author;
    // 是否关注
    private boolean weatherFollow;
    // 是否点赞
    private boolean weatherLike;
    // 是否收藏
    private boolean weatherFavorite;
    // 标签数组
    private String[] tags;
    // 图片集合
    private String[] imageList;
    // 位置信息
    private VideoPosition position;
    // 热力值
    private Double hotScore;
}
