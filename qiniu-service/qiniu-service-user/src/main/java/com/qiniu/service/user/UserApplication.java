package com.qiniu.service.user;

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
 * UserApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.qiniu.feign")
@EnableUserTokenInterceptor
@EnableRedisConfig
@EnableAsync
@Import({MybatisPlusConfig.class,Swagger2Configuration.class})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
