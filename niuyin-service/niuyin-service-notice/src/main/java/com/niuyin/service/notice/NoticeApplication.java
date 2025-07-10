package com.niuyin.service.notice;

import com.niuyin.common.cache.annotations.EnableCacheConfig;
import com.niuyin.common.core.annotations.EnableUserTokenInterceptor;
import com.niuyin.common.core.config.MybatisPlusConfig;
import com.niuyin.common.core.swagger.Swagger2Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * NoticeService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/2
 **/
@SpringBootApplication(scanBasePackages = {"com.niuyin.service.notice", "com.niuyin.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.niuyin.feign")
@EnableUserTokenInterceptor
@EnableCacheConfig
@EnableAsync
@EnableScheduling
@Import({MybatisPlusConfig.class})
public class NoticeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoticeApplication.class, args);
    }
}
