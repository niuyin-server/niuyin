package com.niuyin.model.notice.mq;

/**
 * 通知模块队列常量参数
 */
public class NoticeDirectConstant {
    /**
     * 交换机
     */
    public static final String NOTICE_DIRECT_EXCHANGE = "exchange.notice.direct";
    /**
     * 队列
     */
    public static final String NOTICE_CREATE_QUEUE = "queue.notice.create";
    /**
     * 绑定的routing key
     */
    public static final String NOTICE_CREATE_ROUTING_KEY = "create";

}
