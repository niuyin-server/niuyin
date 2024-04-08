package com.niuyin.starter.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AliyunSmsConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
@Data
@Configuration
@EnableConfigurationProperties({AliyunSmsConfigProperties.class})
//@ConditionalOnProperty(prefix = "aliyun.sms", name = "enabled", havingValue = "true")
public class AliyunSmsConfig {

    @Autowired
    private AliyunSmsConfigProperties aliyunSmsConfigProperties;

    @Bean
    public IAcsClient buildAcsClient() {
        DefaultProfile profile = DefaultProfile.getProfile("default",
                aliyunSmsConfigProperties.getAccessKeyId(),
                aliyunSmsConfigProperties.getAccessKeySecret());
        return new DefaultAcsClient(profile);
    }

}
