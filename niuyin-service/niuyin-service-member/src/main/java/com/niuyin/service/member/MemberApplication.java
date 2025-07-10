package com.niuyin.service.member;

import com.niuyin.common.cache.annotations.EnableCacheConfig;
import com.niuyin.common.core.annotations.EnableUserTokenInterceptor;
import com.niuyin.common.core.config.MybatisPlusConfig;
import com.niuyin.common.core.swagger.Swagger2Configuration;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
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
@SpringBootApplication(scanBasePackages = {"com.niuyin.service.member", "com.niuyin.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.niuyin.feign")
@EnableCacheConfig
@EnableAsync
@EnableDubbo
@Import({MybatisPlusConfig.class})
public class MemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }
}
