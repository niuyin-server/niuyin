package com.qiniu.model.video.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * VideoFeedDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 视频流dto，传递时间
 **/
@Data
public class VideoFeedDTO {
    private LocalDateTime createTime;
}
