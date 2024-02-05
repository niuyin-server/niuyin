package com.niuyin.starter.file.config.aliyun;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aliyun.oss.OSSClient;

import lombok.Data;
import javax.annotation.Resource;

/**
 * AliyunOssConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/29
 **/
@Data
@Configuration
@EnableConfigurationProperties({AliyunOssConfigProperties.class})
public class AliyunOssConfig {

    @Resource
    private AliyunOssConfigProperties aliyunOssConfigProperties;

    @Bean
    public OSSClient buildOSSClient() {
        return new OSSClient(aliyunOssConfigProperties.getEndpoint(),
                aliyunOssConfigProperties.getAccessKeyId(),
                aliyunOssConfigProperties.getAccessKeySecret());
    }

}
