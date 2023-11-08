package com.qiniu.model.video.mq;

/**
 * VideoDelayedQueueConstant
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 视频延时队列常量配置
 **/
public class VideoDelayedQueueConstant {
    /**
     * 延迟交换机
     */
    public static final String ESSYNC_DELAYED_EXCHANGE = "exchange.video.esSync";
    /**
     * 队列
     */
    public static final String ESSYNC_DIRECT_QUEUE = "queue.video.esSync";
    /**
     * 绑定的routing key
     */
    public static final String ESSYNC_ROUTING_KEY = "video.esSync";

    /**
     * video同步es延时时间，默认1分钟
     */
    public static final Integer ESSYNC_DELAYED_TIME = 60 * 1000;

}
