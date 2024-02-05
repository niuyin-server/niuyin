package com.niuyin.starter.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/18 23:50
 */
@Data
@Component
@ConfigurationProperties(prefix = "ffmpeg.vframes")
public class FfmpegConfigProperties {

    // 目标路径
    private String targetPath;
    // 0~31 数字越小质量越高
    private String quantity;
    // 指定截取时间
    private String timestamp;

}
