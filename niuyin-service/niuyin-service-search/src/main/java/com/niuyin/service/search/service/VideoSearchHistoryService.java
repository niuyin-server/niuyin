package com.niuyin.service.search.service;

import com.niuyin.model.common.enums.VideoPlatformEnum;
import com.niuyin.service.search.domain.VideoSearchHistory;

import java.util.List;

/**
 * VideoSearchHistoryService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
public interface VideoSearchHistoryService {

    /**
     * 保存用户搜索历史记录
     */
    void insert(String keyword, Long userId);

    /**
     * 根据平台插入数据
     *
     * @param keyword
     * @param userId
     * @param platform
     */
    void insertPlatform(Long userId, String keyword, VideoPlatformEnum platform);

    /**
     * 查询搜索历史
     */
    List<VideoSearchHistory> findAllSearch();
    List<VideoSearchHistory> findAppSearchHistory();

    /**
     * 查询当天的所有搜索记录
     */
    List<VideoSearchHistory> findTodaySearchRecord();

    /**
     * 删除历史记录
     */
    boolean delSearchHistory(String id);

//    /**
//     * 热搜排行榜
//     * @return
//     */
//    PageDataInfo findSearchHot();
}
