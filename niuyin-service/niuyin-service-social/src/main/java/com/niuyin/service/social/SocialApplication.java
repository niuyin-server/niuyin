package com.niuyin.service.social;

import com.niuyin.common.annotations.EnableRedisConfig;
import com.niuyin.common.config.MybatisPlusConfig;
import com.niuyin.common.swagger.Swagger2Configuration;
import com.niuyin.feign.config.FeignConfig;
import com.niuyin.common.annotations.EnableUserTokenInterceptor;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * SocialApplication
 * 社交服务
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/2
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.niuyin.feign", defaultConfiguration = {FeignConfig.class})
@EnableUserTokenInterceptor
@EnableRedisConfig
@EnableAsync
@EnableDubbo
@Import({MybatisPlusConfig.class, Swagger2Configuration.class})
public class SocialApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }
}
