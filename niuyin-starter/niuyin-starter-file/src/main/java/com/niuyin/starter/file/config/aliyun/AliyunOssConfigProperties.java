package com.niuyin.starter.file.config.aliyun;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.InitializingBean;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssConfigProperties implements InitializingBean {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.accessKeyId}")
    public String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    public String accessKeySecret;

    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endpoint;
        BUCKET_NAME = bucketName;
        ACCESS_KEY_ID = accessKeyId;
        ACCESS_KEY_SECRET = accessKeySecret;
    }
}
