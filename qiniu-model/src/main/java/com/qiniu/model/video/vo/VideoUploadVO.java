package com.qiniu.model.video.vo;

import lombok.Data;

/**
 * VideoUploadVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/3
 * 视频上传成功响应体
 **/
@Data
public class VideoUploadVO {
    // 视频转码后链接
    private String videoUrl;
    // 第一帧封面
    private String vframe;
}
