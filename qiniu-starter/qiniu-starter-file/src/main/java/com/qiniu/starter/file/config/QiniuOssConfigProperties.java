package com.qiniu.starter.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * QiniuOssConfigProperties
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/29
 **/
@Data
@ConfigurationProperties(prefix = "qiniu.oss")
public class QiniuOssConfigProperties implements Serializable {
    private String accessKey;
    private String secretKey;
    private String bucket;
}
