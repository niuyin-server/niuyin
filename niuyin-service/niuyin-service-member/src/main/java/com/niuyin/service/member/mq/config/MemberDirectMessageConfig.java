package com.niuyin.service.member.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.niuyin.model.behave.mq.BehaveQueueConstant.*;

/**
 * MemberDirectMessageConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/11
 * 延时交换机绑定
 **/
@Configuration
public class MemberDirectMessageConfig {

    @Bean
    public Queue favoriteDirectQueue() {
        return new Queue(FAVORITE_DIRECT_QUEUE, true);
    }

    /**
     *
     * 交换机的类型为 x-delayed-message
     */
    @Bean
    public Exchange behaveExchange() {
        return new DirectExchange(BEHAVE_EXCHANGE);
    }

    @Bean
    public Binding behaveFavoriteCreateBinding() {
        return BindingBuilder.bind(favoriteDirectQueue()).to(behaveExchange()).with(CREATE_ROUTING_KEY).noargs();
    }

}
