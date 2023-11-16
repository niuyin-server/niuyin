package com.niuyin.service.notice.listener;

import com.alibaba.fastjson2.JSON;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.mq.NoticeDirectConstant;
import com.niuyin.service.notice.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * VideoRabbitListener
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * video视频服务mq监听器
 **/
@Slf4j
@Component
public class NoticeDirectListener {

    @Resource
    private INoticeService noticeService;

    /**
     * video延时消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = NoticeDirectConstant.NOTICE_CREATE_QUEUE, durable = "true"),
            exchange = @Exchange(name = NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE),
            key = NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY))
    public void listenDirectQueueCreate(String msg) {
        Notice notice = JSON.parseObject(msg, Notice.class);
        noticeService.save(notice);
        log.info("notice 接收到创建通知的消息：{}", msg);
    }

}
