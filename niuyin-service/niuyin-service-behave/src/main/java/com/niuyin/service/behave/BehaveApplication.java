package com.niuyin.service.behave;

import com.niuyin.common.annotations.EnableRedisConfig;
import com.niuyin.common.annotations.EnableUserTokenInterceptor;
import com.niuyin.common.config.MybatisPlusConfig;
import com.niuyin.common.swagger.Swagger2Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * BehaveApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/2
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.niuyin.feign")
@EnableUserTokenInterceptor
@EnableRedisConfig
@EnableAsync
@Import({MybatisPlusConfig.class, Swagger2Configuration.class})
public class BehaveApplication {
    public static void main(String[] args) {
        SpringApplication.run(BehaveApplication.class, args);
    }
}
