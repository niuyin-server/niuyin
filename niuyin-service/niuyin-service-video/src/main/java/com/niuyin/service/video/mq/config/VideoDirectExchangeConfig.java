package com.niuyin.service.video.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.niuyin.model.video.mq.VideoDirectExchangeConstant.*;

/**
 * VideoDirectExchangeConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/4
 * 用户DirectExchange交换机
 **/
@Configuration
public class VideoDirectExchangeConfig {

    @Bean
    public Queue directQueue() {
        return new Queue(DIRECT_QUEUE_INFO);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_VIDEO_DIRECT);
    }

    @Bean
    public Binding directExchangeBinding() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(DIRECT_KEY_INFO);
    }
}
