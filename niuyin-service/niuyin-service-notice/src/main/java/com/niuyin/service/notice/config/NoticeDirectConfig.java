package com.niuyin.service.notice.config;

import com.niuyin.model.notice.mq.NoticeDirectConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * notice交换机
 */
@Configuration
public class NoticeDirectConfig {

    @Bean
    public DirectExchange noticeDirectExchange() {
        return new DirectExchange(NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE);
    }

    @Bean
    public Queue noticeDirectQueueCreate() {
        return new Queue(NoticeDirectConstant.NOTICE_CREATE_QUEUE); //默认会消息持久化
    }

    @Bean
    public Binding noticeDirectBinding2Create() {
        return BindingBuilder.bind(noticeDirectQueueCreate()).to(noticeDirectExchange()).with(NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY);
    }

}
