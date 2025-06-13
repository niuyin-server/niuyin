package com.niuyin.service.social.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.date.DateUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.social.cache.DynamicUser;
import com.niuyin.service.social.service.SocialDynamicsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;


import jakarta.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.niuyin.model.cache.SocialCacheConstants.FOLLOW;
import static com.niuyin.model.cache.SocialCacheConstants.SOCIAL_DYNAMICS;
import static com.niuyin.model.constants.VideoConstants.IN_FOLLOW;
import static com.niuyin.model.constants.VideoConstants.OUT_FOLLOW;

/**
 * 社交动态服务
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/6
 **/
@Slf4j
@Service("socialDynamicsService")
public class SocialDynamicsServiceImpl implements SocialDynamicsService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisService redisService;

    @DubboReference(mock = "return null")
    private DubboMemberService dubboMemberService;

    /**
     * 初始化用户收件箱 拉模式 从关注列表用户发件箱主动拉取最近一年视频数据存入收件箱
     */
    @Override
    public void initUserFollowInBox(Long userId) {
        long time = DateUtils.addDays(DateUtils.getNowDate(), -365).getTime();
        log.debug("last week time:{}", time);
        // 获取用户关注列表
        Set followUserIds = redisService.getCacheZSetRange(FOLLOW + userId, 0, -1);
        // 从redis用户发件箱获取所有关注用户近一周视频
        Set<ZSetOperations.TypedTuple<String>> videoIdSet = new HashSet<>();

        for (Object followUserId : followUserIds) {
            // 获取关注列表用户发件箱近一月视频数据
            Set<ZSetOperations.TypedTuple<String>> followUserVideoIds = redisService.getCacheZSetWithScoresByScoreRange(OUT_FOLLOW + (Long) followUserId, time, DateUtils.getNowDate().getTime());
            if (followUserVideoIds.isEmpty()) {
                continue;
            }
            videoIdSet.addAll(followUserVideoIds);
            // 说明此用户最近一周发了动态
            // 设置用户动态redis数据// 对 followUserVideoIds 进行降序排序
            List<ZSetOperations.TypedTuple<String>> collect = followUserVideoIds.stream()
                    .sorted(Comparator.comparing(ZSetOperations.TypedTuple<String>::getScore).reversed())
                    .collect(Collectors.toList());
            // 拿到最晚发布的一条视频的发布时间
            ZSetOperations.TypedTuple<String> stringTypedTuple = collect.get(0);
            // 封装用户
            DynamicUser dynamicUser = new DynamicUser();
            dynamicUser.setUserId((Long) followUserId);
            dynamicUser.setHasRead(false);
            Member member = dubboMemberService.apiGetById((Long) followUserId);
            if (member != null) {
                // 只存userid即可，剩下的使用rpc查
                dynamicUser.setNickname(member.getNickName());
                dynamicUser.setAvatar(member.getAvatar());
            } else {
                // todo 查库
            }
            redisService.setCacheZSet(SOCIAL_DYNAMICS + userId, dynamicUser, stringTypedTuple.getScore());
            redisService.expire(SOCIAL_DYNAMICS + userId, 365, TimeUnit.DAYS);
        }
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
                // 过期时间一月 todo 这里是否需要对视频做过期处理
//                connection.expire(key, 365 * 24 * 60 * 60L);
            }
            return null;
        });
    }

    /**
     * 获取社交动态列表
     * 从redis分页用户收件箱
     */
    @Override
    public PageData<String> getSocialDynamics(PageDTO pageDTO) {
        int startIndex = (pageDTO.getPageNum() - 1) * pageDTO.getPageSize();
        int endIndex = startIndex + pageDTO.getPageSize() - 1;
        Set<DefaultTypedTuple<String>> cacheZSetRange = redisService.getCacheZSetRangeWithScores(IN_FOLLOW + UserContext.getUserId(), startIndex, endIndex, true);
        List<String> collect = cacheZSetRange.stream().map(DefaultTypedTuple::getValue).collect(Collectors.toList());
        Long cacheZSetZCard = redisService.getCacheZSetZCard(IN_FOLLOW + UserContext.getUserId());
        return PageData.genPageData(collect, cacheZSetZCard);
    }

    /**
     * 获取社交动态用户
     */
    @Override
    public PageData<DynamicUser> getSocialDynamicsUser() {
        Set<DynamicUser> cacheZSetRange = redisService.getCacheZSetReverseRange(SOCIAL_DYNAMICS + UserContext.getUserId(), 0, -1);
        return PageData.genPageData(new ArrayList<>(cacheZSetRange), cacheZSetRange.size());
    }
}
