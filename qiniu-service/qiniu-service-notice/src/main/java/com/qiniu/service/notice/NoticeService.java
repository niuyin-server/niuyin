package com.qiniu.service.notice;

import com.qiniu.common.annotations.EnableRedisConfig;
import com.qiniu.common.annotations.EnableUserTokenInterceptor;
import com.qiniu.common.config.MybatisPlusConfig;
import com.qiniu.common.swagger.Swagger2Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * NoticeService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/2
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.qiniu.feign")
@EnableUserTokenInterceptor
@EnableRedisConfig
@EnableAsync
@Import({MybatisPlusConfig.class, Swagger2Configuration.class})
public class NoticeService {
    public static void main(String[] args) {
        SpringApplication.run(NoticeService.class, args);
    }
}
