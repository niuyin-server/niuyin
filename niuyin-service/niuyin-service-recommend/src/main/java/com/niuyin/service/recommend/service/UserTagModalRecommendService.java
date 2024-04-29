package com.niuyin.service.recommend.service;

import java.util.List;

/**
 * 用户标签模型服务
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
public interface UserTagModalRecommendService {

    /**
     * 用户模型初始化
     */
    void initUserModel(Long userId, List<Long> tagIds);

    /**
     * 基于用户标签模型推荐视频
     *
     * @return videoIds
     */
    List<String> getVideoIdsByUserModel(Long userId);

}
