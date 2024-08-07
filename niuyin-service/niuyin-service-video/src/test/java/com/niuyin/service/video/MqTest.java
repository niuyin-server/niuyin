package com.niuyin.service.video;

import com.alibaba.fastjson.JSON;
import com.niuyin.model.video.domain.Video;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.niuyin.model.video.mq.VideoDelayedQueueConstant.ESSYNC_DELAYED_EXCHANGE;
import static com.niuyin.model.video.mq.VideoDelayedQueueConstant.ESSYNC_ROUTING_KEY;
import static com.niuyin.model.video.mq.VideoDirectExchangeConstant.*;

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
    void simpleQueue() {
        String queue = "simple.queue";
        rabbitTemplate.convertAndSend(queue, "hello,mq!");
        log.debug("==>向 {} 发送了一条消息", queue);
    }

    @Test
    void testPublishDelayedMsg() {
        Video video = new Video();
        video.setVideoTitle("测试延时消息的视频");
        video.setCreateTime(LocalDateTime.now());
        String msg = JSON.toJSONString(video);
        // 1.发送整个video对象发送消息，利用消息后置处理器添加消息头
        rabbitTemplate.convertAndSend(ESSYNC_DELAYED_EXCHANGE, ESSYNC_ROUTING_KEY, msg, message -> {
            // 2.添加延迟消息属性，设置10秒
            message.getMessageProperties().setDelay(10000);
            return message;
        });
        log.debug(" ==> {} 发送了一条消息 ==> {}", ESSYNC_DELAYED_EXCHANGE, msg);
    }

    @DisplayName("向video direct同步视频info发送消息")
    @Test
    void testDirectQueue() {
        String msg = "1195178163873775616e0a90612";
        rabbitTemplate.convertAndSend(EXCHANGE_VIDEO_DIRECT, DIRECT_KEY_INFO, msg);
        log.debug(" ==> {} 发送了一条消息 ==> {}", EXCHANGE_VIDEO_DIRECT, msg);
    }

}
