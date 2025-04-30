package com.niuyin.service.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import com.niuyin.model.video.enums.IkAnalyzeTypeEnum;
import com.niuyin.service.search.service.EsIkAnalyzeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * EsAnalyzeServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2024/9/25
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class EsIkAnalyzeServiceImpl implements EsIkAnalyzeService {

    private final ElasticsearchClient elasticsearchClient;

    /**
     * 根据关键词使用ik分词器进行分词
     *
     * @param keyword           关键词
     * @param ikAnalyzeTypeEnum 分词模式
     * @return 分词set集合
     */
    @Override
    public Set<String> getIkAnalyzeSetResult(String keyword, IkAnalyzeTypeEnum ikAnalyzeTypeEnum) {
        Set<String> res = new HashSet<>();
        try {
            // 构建分析请求
            AnalyzeResponse response = elasticsearchClient.indices().analyze(a -> a
                    .analyzer(ikAnalyzeTypeEnum.getCode())
                    .text(keyword)
            );

            // 处理分析结果
            response.tokens().forEach(token -> {
                String term = token.token();
                long startOffset = token.startOffset();
                long endOffset = token.endOffset();
                String type = token.type();

                System.out.println("Term: " + term +
                        ", Start Offset: " + startOffset +
                        ", End Offset: " + endOffset +
                        ", Type: " + type);
                res.add(term);
            });

        } catch (IOException e) {
            log.error("分词异常: {}", e.getMessage(), e);
            // 根据业务需求决定是否抛出异常或返回空集合
        }
        return res;
    }

}
