package com.niuyin.service.video.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.date.DateUtils;
import com.niuyin.service.video.service.UserFollowVideoPushService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import jakarta.annotation.Resource;
import java.util.*;

import static com.niuyin.model.constants.VideoConstants.IN_FOLLOW;
import static com.niuyin.model.constants.VideoConstants.OUT_FOLLOW;

/**
 * userFollowVideoPushServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/4
 **/
@Slf4j
@Service
public class UserFollowVideoPushServiceImpl implements UserFollowVideoPushService {

    @Resource
    private RedisService redisService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 推入发件箱
     *
     * @param userId  发件箱用户id
     * @param videoId 视频id
     * @param time    视频发布时间戳
     */
    @Override
    public void pusOutBoxFeed(Long userId, String videoId, Long time) {
        redisService.setCacheZSet(OUT_FOLLOW + userId, videoId, time);
    }

    /**
     * 推入收件箱
     *
     * @param userId  用户id
     * @param videoId 视频id
     * @param time    视频发布时间戳
     */
    @Override
    public void pushInBoxFeed(Long userId, String videoId, Long time) {
        // todo 主动推模式暂时不用（当粉丝数据过多或者僵尸粉丝带来过大性能开销）
    }

    /**
     * 删除发件箱
     * 当前用户删除视频时 调用->删除当前用户的发件箱中视频以及粉丝下的收件箱
     *
     * @param userId  当前用户
     * @param fans    粉丝ids
     * @param videoId 视频id 需要删除的
     */
    @Override
    public void deleteOutBoxFeed(Long userId, List<Long> fans, String videoId) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long fan : fans) {
                connection.zRem((IN_FOLLOW + fan).getBytes(), videoId.getBytes());
            }
            connection.zRem((OUT_FOLLOW + userId).getBytes(), videoId.getBytes());
            return null;
        });
    }

    /**
     * 删除收件箱
     * 当前用户取关用户时调用->删除自己收件箱中的videoIds
     *
     * @param userId
     * @param videoIds 关注人发的视频id
     */
    @Override
    public void deleteInBoxFeed(Long userId, List<String> videoIds) {
        redisTemplate.opsForZSet().remove(IN_FOLLOW + userId, videoIds.toArray());
    }

    /**
     * 初始化关注流->拉取模式 with TTL
     *
     * @param userId
     * @param followIds
     */
    @Override
    public void initFollowVideoFeed(Long userId, List<Long> followIds) {
        // 当前时间
        Date curDate = DateUtils.getNowDate();
        // 前365天时间
        Date limitDate = DateUtils.addDays(curDate, -365);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisService.zRangeWithScores(IN_FOLLOW + userId, -1, -1);
        if (!CollectionUtils.isEmpty(typedTuples)) {
            Double oldTime = typedTuples.iterator().next().getScore();
            init(userId, oldTime.longValue(), new Date().getTime(), followIds);
        } else {
            init(userId, limitDate.getTime(), curDate.getTime(), followIds);
        }
    }

    @SneakyThrows
    public void init(Long userId, Long min, Long max, Collection<Long> followIds) {
        // 查看关注人的发件箱
        List<Set<DefaultTypedTuple>> result = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long followId : followIds) {
                connection.zRevRangeByScoreWithScores((OUT_FOLLOW + followId).getBytes(), min, max, 0, 20);
            }
            return null;
        });
        final ObjectMapper objectMapper = new ObjectMapper();
        // 放入用户收件箱
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Set<DefaultTypedTuple> tuples : result) {
                if (!ObjectUtils.isEmpty(tuples)) {
                    for (DefaultTypedTuple tuple : tuples) {
                        String value = (String) tuple.getValue();
                        byte[] key = (IN_FOLLOW + userId).getBytes();
                        try {
                            connection.zAdd(key, tuple.getScore(), objectMapper.writeValueAsBytes(value));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        // 过期时间一年
                        connection.expire(key, 365 * 24 * 60 * 60L);
                    }
                }
            }
            return null;
        });

    }

}
