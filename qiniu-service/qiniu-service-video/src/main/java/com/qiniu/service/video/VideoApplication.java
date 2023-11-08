package com.qiniu.service.video;

import com.qiniu.common.annotations.EnableRedisConfig;
import com.qiniu.common.annotations.EnableUserTokenInterceptor;
import com.qiniu.common.config.MybatisPlusConfig;
import com.qiniu.common.swagger.Swagger2Configuration;
import com.qiniu.feign.behave.RemoteBehaveService;
import com.qiniu.feign.config.FeignConfig;
import com.qiniu.feign.social.RemoteSocialService;
import com.qiniu.feign.user.RemoteUserService;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * VideoApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/25
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.qiniu.feign", defaultConfiguration = {FeignConfig.class})
@EnableUserTokenInterceptor
@EnableRedisConfig
@EnableScheduling
@EnableAsync
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
