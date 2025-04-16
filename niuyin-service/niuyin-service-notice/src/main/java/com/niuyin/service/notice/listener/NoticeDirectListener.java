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
public class NoticeDirectListener {

    @Resource
    private INoticeService noticeService;

    /**
     * video延时消息 todo 考虑到业务场景，需要引入消息堆积解决方案：1、多个消费者；2、线程池；3、扩大队列容积（惰性队列lazy）
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = NoticeDirectConstant.NOTICE_CREATE_QUEUE, durable = "true"),
            exchange = @Exchange(name = NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE),
            key = NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY))
    public void listenDirectQueueCreate(String msg) {
        Notice notice = JSON.parseObject(msg, Notice.class);
        // todo 接口调用多次
        noticeService.save(notice);
//        noticeService.saveNotice(notice);
        // todo 通知websocket推送消息未读数
        // todo 用户未登录
//        Long unreadNoticeCount = noticeService.getUnreadNoticeCount();
//        WebSocketServer.sendOneMessage(notice.getNoticeUserId(), WebSocketBaseResp.build(WebSocketMsgType.NOTICE_UNREAD_COUNT.getCode(), unreadNoticeCount.toString()));
        log.info("notice 接收到创建通知的消息：{}", msg);
    }

}
