package com.niuyin.model.behave.mq;

/**
 * BehaveQueueConstant
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 视频延时队列常量配置
 **/
public class BehaveQueueConstant {
    /**
     * 交换机
     */
    public static final String BEHAVE_EXCHANGE = "exchange.behave.favorite";
    /**
     * 队列
     */
    public static final String FAVORITE_DIRECT_QUEUE = "queue.favorite.create";
    /**
     * 绑定的routing key
     */
    public static final String CREATE_ROUTING_KEY = "create";

}
