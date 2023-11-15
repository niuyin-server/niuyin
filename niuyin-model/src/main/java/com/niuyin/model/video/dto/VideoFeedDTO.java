package com.niuyin.model.video.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
