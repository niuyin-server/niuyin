package com.niuyin.service.video;

import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.date.DateUtils;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.service.video.service.UserFollowVideoPushService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * UserFollowVideoPushTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/5
 **/
@Slf4j
@SpringBootTest
public class UserFollowVideoPushTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoService videoService;

    @Resource
    private UserFollowVideoPushService userFollowVideoPushService;

    @Test
    @DisplayName("初始化用户发件箱")
    void testPutOutBox() {
        List<Video> allVideo = videoService.getAllUnDeletedVideo();
        allVideo.forEach(v -> {
            String videoId = v.getVideoId();
            userFollowVideoPushService.pusOutBoxFeed(v.getUserId(), videoId, DateUtils.toDate(v.getCreateTime()).getTime());
        });
    }
}
