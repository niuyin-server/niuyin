package com.niuyin.service.search.service.impl;

import com.niuyin.model.video.enums.IkAnalyzeTypeEnum;
import com.niuyin.service.search.service.EsIkAnalyzeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
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

    private final RestHighLevelClient restHighLevelClient;

    /**
     * 根据关键词使用ik分词器进行分词
     *
     * @param keyword           关键词
     * @param ikAnalyzeTypeEnum 分词模式
     * @return 分词set集合
     */
    @Override
    public Set<String> getIkAnalyzeSetResult(String keyword, IkAnalyzeTypeEnum ikAnalyzeTypeEnum) {
        AnalyzeRequest request = AnalyzeRequest.withGlobalAnalyzer(ikAnalyzeTypeEnum.getCode(), keyword);
        Set<String> res = new HashSet<>();
        try {
            AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
            response.getTokens().forEach(token -> {
                String term = token.getTerm();
                int startOffset = token.getStartOffset();
                int endOffset = token.getEndOffset();
                String type = token.getType();
                System.out.println("Term: " + term + ", Start Offset: " + startOffset + ", End Offset: " + endOffset + ", Type: " + type);
                res.add(term);
            });
        } catch (IOException e) {
            log.debug("分词异常");
            e.printStackTrace();
        }
        return res;
    }
}
