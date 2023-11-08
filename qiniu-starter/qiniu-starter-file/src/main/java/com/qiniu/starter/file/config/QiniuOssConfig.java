package com.qiniu.starter.file.config;

import com.qiniu.starter.file.service.FileStorageService;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * QiniuOssConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/26
 **/
@Data
@Configuration
@EnableConfigurationProperties({QiniuOssConfigProperties.class})
@ConditionalOnClass(FileStorageService.class)
public class QiniuOssConfig {

    @Resource
    private QiniuOssConfigProperties qiniuOssConfigProperties;

    @Bean
    public UploadManager buildUploadManager() {
        //构造一个带指定 Region 对象的配置类
        com.qiniu.storage.Configuration cfg = new com.qiniu.storage.Configuration(Region.autoRegion());
        //...其他参数参考类注释
        return new UploadManager(cfg);
    }
}
