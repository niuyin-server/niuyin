package com.niuyin.starter.file.config;

import com.niuyin.starter.file.service.FileStorageService;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

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
