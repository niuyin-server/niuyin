package com.qiniu.service.video.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.qiniu.model.video.mq.VideoDelayedQueueConstant.*;

/**
 * VideoDelayedMessageConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/11
 * 延时交换机绑定
 **/
@Configuration
public class VideoDelayedMessageConfig {

    @Bean
    public Queue directQueue() {
        return new Queue(ESSYNC_DIRECT_QUEUE,true);
    }

    /**
     * 延时交换机
     * 交换机的类型为 x-delayed-message
     */
    @Bean
    public CustomExchange orderDelayedExchange() {
        Map<String,Object> map= new HashMap<>();
        map.put("x-delayed-type","direct");
        return new CustomExchange(ESSYNC_DELAYED_EXCHANGE,"x-delayed-message",true,false,map);
    }

    @Bean
    public Binding delayOrderBinding() {
        return BindingBuilder.bind(directQueue()).to(orderDelayedExchange()).with(ESSYNC_ROUTING_KEY).noargs();
    }

}
