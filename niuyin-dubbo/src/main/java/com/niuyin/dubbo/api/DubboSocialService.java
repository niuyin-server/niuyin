package com.niuyin.dubbo.api;

/**
 * 服务提供者：social
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/7
 **/
public interface DubboSocialService {

    /**
     * 是否关注用户
     *
     * @param userId       当前用户id
     * @param followUserId 被关注用户id
     */
    Boolean apiWeatherFollow(Long userId, Long followUserId);

    /**
     * 用户关注数量
     *
     * @param userId
     * @return
     */
    Long apiUserFollowCount(Long userId);

    /**
     * 用户粉丝数量
     *
     * @param userId
     * @return
     */
    Long apiUserFansCount(Long userId);

}
