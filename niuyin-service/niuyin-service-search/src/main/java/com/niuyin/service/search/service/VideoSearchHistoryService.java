package com.niuyin.service.search.service;

import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
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
     * 查询搜索历史
     */
    List<VideoSearchHistory> findAllSearch();

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
