package com.niuyin.model.video.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * VideoPushVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/20
 **/
@Data
public class VideoPushVO {
    private String videoId;
    private Long userId;
    private String videoTitle;
    private String videoDesc;
    // 视频封面地址
    private String coverImage;
    // 视频地址
    private String videoUrl;
    // 发布类型（0视频，1图文）
    private String publishType;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    // 视频作者
    private Author author;
    // 是否关注
    private boolean weatherFollow;
    // 图片集合
    private String[] imageList;
}
