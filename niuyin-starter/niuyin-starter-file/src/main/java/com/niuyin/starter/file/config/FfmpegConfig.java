package com.niuyin.starter.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/18 23:53
 */
@Data
@Configuration
@EnableConfigurationProperties({FfmpegConfigProperties.class})
public class FfmpegConfig {

}
