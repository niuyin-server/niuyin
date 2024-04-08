package com.niuyin.service.behave.listener;

import com.niuyin.model.behave.mq.BehaveQueueConstant;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.service.behave.service.IUserFavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * MemberRabbitListener
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 用户服务mq监听器
 **/
@Slf4j
@Component
public class MemberRabbitListener {

    @Resource
    private IUserFavoriteService userFavoriteService;

    /**
     * 用户注册创建默认收藏夹
     *
     * @param msg 用户id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = BehaveQueueConstant.FAVORITE_DIRECT_QUEUE, durable = "true"),
            exchange = @Exchange(name = BehaveQueueConstant.BEHAVE_EXCHANGE),
            key = BehaveQueueConstant.CREATE_ROUTING_KEY
    ))
    public void listenMemberMessage(String msg) {
        log.info("behave 接收到用户注册时发送的消息：{}", msg);
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUserId(Long.valueOf(msg));
        userFavorite.setTitle("默认文件夹");
        userFavorite.setDescription("用户默认创建的收藏夹");
        userFavorite.setCreateTime(LocalDateTime.now());
        userFavoriteService.save(userFavorite);
        log.info("用户默认收藏夹创建成功");
    }

}
