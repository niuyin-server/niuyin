package com.niuyin.service.social.dubbo;

import com.niuyin.dubbo.api.DubboSocialService;
import com.niuyin.service.social.service.IUserFollowService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * DubboSocialServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/8
 **/
@DubboService
public class DubboSocialServiceImpl implements DubboSocialService {

    @Resource
    private IUserFollowService userFollowService;

    /**
     * 是否关注用户
     *
     * @param userId       当前用户id
     * @param followUserId 被关注用户id
     */
    @Override
    public Boolean apiWeatherFollow(Long userId, Long followUserId) {
        return userFollowService.weatherFollow(userId, followUserId);
    }

    /**
     * 用户关注数量
     *
     * @param userId
     * @return
     */
    @Override
    public Long apiUserFollowCount(Long userId) {
        return userFollowService.getUserFollowCount(userId);
    }

    /**
     * 用户粉丝数量
     *
     * @param userId
     * @return
     */
    @Override
    public Long apiUserFansCount(Long userId) {
        return userFollowService.getUserFansCount(userId);
    }
}
