package com.niuyin.service.search.listener;

import com.niuyin.service.search.service.VideoSearchService;
import com.niuyin.model.video.mq.VideoDelayedQueueConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


import jakarta.annotation.Resource;
/**
 * VideoRabbitListener
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * video视频服务mq监听器
 **/
@Slf4j
@Component
public class VideoRabbitListener {

    @Resource
    private VideoSearchService videoSearchService;

    /**
     * video延时消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = VideoDelayedQueueConstant.ESSYNC_DIRECT_QUEUE, durable = "true"),
            exchange = @Exchange(name = VideoDelayedQueueConstant.ESSYNC_DELAYED_EXCHANGE, delayed = "true"),
            key = VideoDelayedQueueConstant.ESSYNC_ROUTING_KEY
    ))
    public void listenVideoDelayMessage(String msg) {
        // 接收到的为videoId
        log.info("search 接收到同步视频到es的延迟消息：{}", msg);
        videoSearchService.videoSync(msg);
    }

}
