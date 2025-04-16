package com.niuyin.service.recommend;

import com.niuyin.common.cache.service.RedisService;
import com.niuyin.service.recommend.event.VideoRecommendEvent;
import com.niuyin.service.recommend.mapper.UserVideoBehaveMapper;
import com.niuyin.service.recommend.service.VideoRecommendLoadBalancer;
import com.niuyin.service.recommend.service.UserBasedCollaborativeFilterService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;


import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * VideoRecommendTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@Slf4j
@SpringBootTest
public class VideoRecommendTest {

    @Resource
    private UserVideoBehaveMapper userVideoBehaveMapper;

    @Resource
    private UserBasedCollaborativeFilterService userBasedCollaborativeFilterService;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoRecommendLoadBalancer videoRecommendLoadBalancer;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void testGenRecommends() {
        Long userIdGen = 2L;

        log.debug("视频推荐列表如下：");
        List<String> entries = userBasedCollaborativeFilterService.generateVideoRecommendations(userIdGen);
        String list_key = "recommend:user_recommend_videos:" + userIdGen;
        redisService.setCacheList(list_key, entries);
        redisService.expire(list_key, 1, TimeUnit.DAYS);

        // 推荐列表存入到redis

    }

    @Test
    void testGenRecommendsTwo() {
        Long userIdGen = 2L;
        applicationEventPublisher.publishEvent(new VideoRecommendEvent(this, userIdGen));
        for (int i = 0; i < 5; i++) {
            List<String> strings = videoRecommendLoadBalancer.callNextMethod(userIdGen);
            // 推荐列表存入到redis
            String list_key = "recommend:user_recommend_videos:" + userIdGen;
            redisService.setCacheList(list_key, strings);
            redisService.expire(list_key, 1, TimeUnit.DAYS);
        }

    }

}
