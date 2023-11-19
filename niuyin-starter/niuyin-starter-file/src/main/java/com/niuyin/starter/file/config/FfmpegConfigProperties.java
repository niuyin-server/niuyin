package com.niuyin.starter.file.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/18 23:50
 */
@Data
@Component
@ConfigurationProperties(prefix = "ffmpeg.vfrme")
public class FfmpegConfigProperties {

    private String[] fbl;



}
