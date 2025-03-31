package com.niuyin.service.recommend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频推荐负载均衡器：
 * 默认两套视频推荐策略：
 * 1、基于用户协同过滤
 * 2、基于视频标签
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@Slf4j
@Component
public class VideoRecommendLoadBalancer {

    @Resource
    private UserBasedCollaborativeFilterService userBasedCollaborativeFilterService;
    @Resource
    private UserTagModalRecommendService userTagModalRecommendService;
    // todo 添加基于新稿件流量池的推荐，且推荐算法要用布隆过滤器过滤防止重复推荐

    private List<MethodWithReturn> methods = new ArrayList<>();
    private int currentIndex = 0;

    public VideoRecommendLoadBalancer() {
        methods.add(this::method1);
        methods.add(this::method2);
    }

    public List<String> method1(Long userId) {
        log.debug("基于用户协同过滤的视频推荐");
        return userBasedCollaborativeFilterService.generateVideoRecommendations(userId);
    }

    public List<String> method2(Long userId) {
        log.debug("基于视频标签的视频推荐");
        return userTagModalRecommendService.getVideoIdsByUserModel(userId);
    }

    public List<String> callNextMethod(Long userId) {
        MethodWithReturn method = methods.get(currentIndex);
        currentIndex = (currentIndex + 1) % methods.size();
        return method.run(userId);
    }

    @FunctionalInterface
    interface MethodWithReturn {
        List<String> run(Long userId);
    }
}
