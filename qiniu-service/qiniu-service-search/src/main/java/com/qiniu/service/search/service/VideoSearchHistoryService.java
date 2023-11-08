package com.qiniu.service.search.service;

import com.qiniu.model.search.dto.VideoSearchHistoryDTO;
import com.qiniu.service.search.domain.VideoSearchHistory;

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
     * 删除历史记录
     */
    boolean delSearchHistory(String id);
}
