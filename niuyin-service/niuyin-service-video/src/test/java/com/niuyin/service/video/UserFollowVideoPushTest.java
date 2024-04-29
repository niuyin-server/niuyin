package com.niuyin.service.video;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuyin.common.core.service.RedisService;
import com.niuyin.common.core.utils.date.DateUtils;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.service.video.service.UserFollowVideoPushService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.niuyin.model.cache.SocialCacheConstants.FOLLOW;
import static com.niuyin.model.constants.VideoConstants.IN_FOLLOW;
import static com.niuyin.model.constants.VideoConstants.OUT_FOLLOW;

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
        List<Video> allUnDeletedVideo = videoService.getAllUnDeletedVideo();
        for (Video v : allUnDeletedVideo) {
            redisService.setCacheZSet(OUT_FOLLOW + v.getUserId(), v.getVideoId(), DateUtils.toDate(v.getCreateTime()).getTime());
        }
    }

    @Test
    @DisplayName("获取某用户最近一周朋友的动态")
    void testGetFriend() {
        long time = DateUtils.addDays(DateUtils.getNowDate(), -7).getTime();
        log.debug("last week time:{}", time);
        // 获取用户关注列表
        Long userId = 2L;
        Set followUserIds = redisService.getCacheZSetRange(FOLLOW + userId, 0, -1);
        // 从redis用户发件箱获取所有关注用户近一周视频
        Set<ZSetOperations.TypedTuple<String>> videoIdSet = new HashSet<>();

        for (Object followUserId : followUserIds) {
            Set<ZSetOperations.TypedTuple<String>> followUserVideoIds = redisService.getCacheZSetWithScoresByScoreRange(OUT_FOLLOW + (Long) followUserId, time, DateUtils.getNowDate().getTime());
            videoIdSet.addAll(followUserVideoIds);
        }

        videoIdSet.forEach(System.out::println);

        final ObjectMapper objectMapper = new ObjectMapper();
        // 将视频信息存入redis用户收件箱
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (ZSetOperations.TypedTuple<String> videoIdZSet : videoIdSet) {
                String videoId = (String) videoIdZSet.getValue();
                byte[] key = (IN_FOLLOW + userId).getBytes();
                try {
                    connection.zAdd(key, videoIdZSet.getScore(), objectMapper.writeValueAsBytes(videoId));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                // 过期时间一周
                connection.expire(key, 7 * 24 * 60 * 60L);
            }
            return null;
        });

//        Set cacheZSetRange = redisService.getCacheZSetRange(OUT_FOLLOW + 44, 1711877458891L, DateUtils.getNowDate().getTime());
//        cacheZSetRange.forEach(System.out::println);
    }

    @Test
    @DisplayName("获取某用户最近一周更新过视频的用户列表")
    void testGetFriendZUIJIN() {

    }

}
