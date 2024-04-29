package com.niuyin.service.recommend.service;

import java.util.List;

/**
 * 基于用户的协同过滤
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
public interface UserBasedCollaborativeFilterService {

    List<String> generateVideoRecommendations(Long userId);

}
