package com.niuyin.service.social.service;

import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.social.cache.DynamicUser;

/**
 * 社交动态服务
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/6
 **/
public interface SocialDynamicsService {

    /**
     * 初始化用户收件箱 拉模式 从关注列表用户发件箱主动拉取最近一周视频数据存入收件箱
     * 对外暴露接口，用户可以主动调用
     */
    void initUserFollowInBox(Long userId);

    /**
     * 获取社交动态列表
     */
    PageDataInfo<String> getSocialDynamics(PageDTO pageDTO);

    /**
     * 获取社交动态用户
     */
    PageDataInfo<DynamicUser> getSocialDynamicsUser();
}
