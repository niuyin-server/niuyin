package com.niuyin.service.notice.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.niuyin.model.notice.mq.NoticeDirectConstant.*;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/17 9:51
 */
@Configuration
public class NoticeDelayedMessageConfig {

    @Bean
    public Queue noticeDirectQueue() {
        return new Queue(NOTICE_CREATE_QUEUE, true);
    }

    /**
     * 交换机的类型为 direct
     */
    @Bean
    public Exchange noticeExchange() {
        return new DirectExchange(NOTICE_DIRECT_EXCHANGE);
    }

    @Bean
    public Binding noticeFavoriteCreateBinding() {
        return BindingBuilder.bind(noticeDirectQueue()).to(noticeExchange()).with(NOTICE_CREATE_ROUTING_KEY).noargs();
    }
}
