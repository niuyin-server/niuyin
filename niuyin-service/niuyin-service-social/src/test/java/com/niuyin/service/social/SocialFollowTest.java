package com.niuyin.service.social;

import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.date.DateUtils;
import com.niuyin.model.social.domain.UserFollow;
import com.niuyin.service.social.service.IUserFollowService;
import com.niuyin.service.social.service.SocialDynamicsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;

import static com.niuyin.model.cache.SocialCacheConstants.FOLLOW;

/**
 * SocialFollowTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/7
 **/
@Slf4j
@SpringBootTest
public class SocialFollowTest {

    @Resource
    private RedisService redisService;

    @Resource
    IUserFollowService userFollowService;

    @Resource
    SocialDynamicsService socialDynamicsService;

    @Test
    @DisplayName("初始化用户关注列表")
    void testPutOutBox() {
        List<UserFollow> list = userFollowService.list();
        for (UserFollow userFollow : list) {
            redisService.setCacheZSet(FOLLOW + userFollow.getUserId(), userFollow.getUserFollowId(), DateUtils.toDate(userFollow.getCreateTime()).getTime());
        }
    }

    @Test
    @DisplayName("获取用户关注列表")
    void testGetUserFollowUserIds() {
        Set cacheZSetRange = redisService.getCacheZSetRange(FOLLOW + 2, 0, -1);
        cacheZSetRange.forEach(System.out::println);
    }

//    ***********************************

    @Test
    @DisplayName("初始化用户收件箱")
    void testInitUserFollowInBox() {
        socialDynamicsService.initUserFollowInBox(2L);
    }


}
