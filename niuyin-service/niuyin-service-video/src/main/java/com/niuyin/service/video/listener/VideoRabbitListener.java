package com.niuyin.service.video.listener;

import com.niuyin.model.video.mq.VideoDelayedQueueConstant;
import com.niuyin.service.video.service.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.niuyin.model.video.mq.VideoDirectExchangeConstant.*;

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
    private IVideoService videoService;

    /**
     * video info 消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = DIRECT_QUEUE_INFO),
            exchange = @Exchange(name = EXCHANGE_VIDEO_DIRECT, type = ExchangeTypes.DIRECT),
            key = DIRECT_KEY_INFO
    ))
    public void listenVideoInfoMessage(String msg) {
        // 接收到的为videoId
        log.info("video 接收到获取视频详情消息：{}", msg);
        // 同步video info到db
        videoService.updateVideoInfo(msg);
    }

}
