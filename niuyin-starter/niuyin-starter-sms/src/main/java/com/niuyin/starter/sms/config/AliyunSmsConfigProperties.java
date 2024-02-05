package com.niuyin.starter.sms.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AliyunSmsConfigProperties
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
@Data
@Component
@ConfigurationProperties(prefix = "sms.aliyun")
public class AliyunSmsConfigProperties implements InitializingBean {

    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;

    public static String SMS_ACCESS_KEY_ID;
    public static String SMS_ACCESS_KEY_SECRET;
    public static String SMS_SIGN_NAME;
    public static String SMS_TEMPLATE_CODE;

    @Override
    public void afterPropertiesSet() throws Exception {
        SMS_ACCESS_KEY_ID = this.accessKeyId;
        SMS_ACCESS_KEY_SECRET = this.accessKeySecret;
        SMS_SIGN_NAME = this.signName;
        SMS_TEMPLATE_CODE = this.templateCode;
    }
}
