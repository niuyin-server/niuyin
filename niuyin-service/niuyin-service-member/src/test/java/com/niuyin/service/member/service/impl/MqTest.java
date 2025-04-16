package com.niuyin.service.member.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

import static com.niuyin.model.behave.mq.BehaveQueueConstant.*;

/**
 * MqTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Slf4j
@SpringBootTest
public class MqTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    void testPublishMsg() {
//        Long userId = 1L;
//        String msg = JSON.toJSONString(userId);
//        // 1.发送整个video对象发送消息，利用消息后置处理器添加消息头
//        rabbitTemplate.convertAndSend(BHSYNC_EXCHANGE, BHSYNC_ROUTING_KEY, msg);
//        log.debug(" ==> {} 发送了一条消息 ==> {}", BHSYNC_EXCHANGE, msg);
    }

    @Test
    void testPublishBehaveFavoriteCreate() {
        rabbitTemplate.convertAndSend(BEHAVE_EXCHANGE, CREATE_ROUTING_KEY, "1");
    }

}
