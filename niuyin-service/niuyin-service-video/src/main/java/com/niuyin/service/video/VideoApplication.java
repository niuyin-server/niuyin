package com.niuyin.service.video;

import com.niuyin.common.annotations.EnableRedisConfig;
import com.niuyin.common.annotations.EnableUserTokenInterceptor;
import com.niuyin.common.config.MybatisPlusConfig;
import com.niuyin.common.swagger.Swagger2Configuration;
import com.niuyin.feign.config.FeignConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * VideoApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/25
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.niuyin.feign", defaultConfiguration = {FeignConfig.class})
@EnableUserTokenInterceptor
@EnableRedisConfig
@EnableScheduling
@EnableCaching
@EnableDubbo
@Import({MybatisPlusConfig.class, Swagger2Configuration.class})
public class VideoApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
    }

    /**
     * mq的消息转换器防止乱码
     */
    @Bean
    public MessageConverter jacksonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }
}
