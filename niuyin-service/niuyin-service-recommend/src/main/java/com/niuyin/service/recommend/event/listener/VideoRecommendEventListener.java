package com.niuyin.service.recommend.event.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.niuyin.common.core.service.RedisService;
import com.niuyin.service.recommend.event.VideoRecommendEvent;
import com.niuyin.service.recommend.service.VideoRecommendLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VideoRecommendEventListener {

    @Resource
    private RedisService redisService;

    @Resource
    private VideoRecommendLoadBalancer videoRecommendLoadBalancer;

    @Async
    @TransactionalEventListener(classes = VideoRecommendEvent.class, fallbackExecution = true)
    public void handleUserLoginEvent(VideoRecommendEvent event) {
        Long userId = event.getUserId();
        log.debug("Handling VideoRecommendEvent userId：{}", userId);
        for (int i = 0; i < 5; i++) {
            List<String> strings = videoRecommendLoadBalancer.callNextMethod(userId);
            if (CollectionUtil.isEmpty(strings)) {
                return;
            }
            // 推荐列表存入到redis
            String list_key = "recommend:user_recommend_videos:" + userId;
            redisService.setCacheList(list_key, strings);
            redisService.expire(list_key, 1, TimeUnit.DAYS);
        }
    }

}
