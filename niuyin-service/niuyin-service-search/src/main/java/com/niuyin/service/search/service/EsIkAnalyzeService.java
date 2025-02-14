package com.niuyin.service.search.service;

import com.niuyin.model.video.enums.IkAnalyzeTypeEnum;

import java.util.Set;

/**
 * EsIkAnalyzeService
 *
 * @AUTHOR: roydon
 * @DATE: 2024/9/25
 **/
public interface EsIkAnalyzeService {
    /**
     * 根据关键词使用ik分词器进行分词
     *
     * @param keyword           关键词
     * @param ikAnalyzeTypeEnum 分词模式
     * @return 分词set集合
     */
    Set<String> getIkAnalyzeSetResult(String keyword, IkAnalyzeTypeEnum ikAnalyzeTypeEnum);
}
