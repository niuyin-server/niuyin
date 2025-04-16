package com.niuyin.service.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.search.dto.UserSearchKeywordDTO;
import com.niuyin.service.search.constant.ESIndexConstants;
import com.niuyin.service.search.domain.UserEO;
import com.niuyin.service.search.domain.VideoSearchVO;
import com.niuyin.service.search.domain.vo.UserSearchVO;
import com.niuyin.service.search.service.UserSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.*;
import org.springframework.stereotype.Service;


import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.niuyin.service.search.constant.ESQueryConstants.*;

/**
 * UserSearchServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2024/10/11
 **/
@Slf4j
@Service
public class UserSearchServiceImpl implements UserSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 从es分页搜索用户
     *
     * @param dto
     */
    @Override
    public List<UserSearchVO> searchUserFromES(UserSearchKeywordDTO dto) {
        // 1.0 构建搜索请求
        SearchRequest searchRequest = buildUserSearchRequest(dto);
        try {
            // 执行查询请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理搜索结果
            List<UserEO> userEOS = processUserSearchResponse(searchResponse);

            // 封装vo
            List<UserSearchVO> userSearchVOS = BeanCopyUtils.copyBeanList(userEOS, UserSearchVO.class);

            return userSearchVOS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private SearchRequest buildUserSearchRequest(UserSearchKeywordDTO dto) {
        SearchRequest searchRequest = new SearchRequest(ESIndexConstants.INDEX_USER);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 设置分页
        searchSourceBuilder.from((dto.getPageNum() - 1) * dto.getPageSize());
        searchSourceBuilder.size(dto.getPageSize());

        // 构建查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 构建多字段匹配查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(dto.getKeyword(), UserEO.USERNAME, UserEO.NICK_NAME);
        boolQueryBuilder.must(multiMatchQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);

        // 设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .boundaryScannerLocale(zh_CN)
                .field(UserEO.USERNAME)
                .field(UserEO.NICK_NAME)
                .preTags(Highlight_preTags)
                .postTags(Highlight_postTags);
        searchSourceBuilder.highlighter(highlightBuilder);

        // 设置排序
        ScoreSortBuilder scoreSortField = SortBuilders.scoreSort().order(SortOrder.DESC);
        searchSourceBuilder.sort(scoreSortField);

        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private List<UserEO> processUserSearchResponse(SearchResponse searchResponse) {
        // 处理搜索结果
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<UserEO> res = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 处理每个搜索结果
            // 获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField usernameHighlightField = highlightFields.get(UserEO.USERNAME);
            HighlightField nickNameHighlightField = highlightFields.get(UserEO.NICK_NAME);
            // 处理高亮显示的片段
            String highlightedUsername = getHighlightedText(usernameHighlightField);
            String highlightedNickName = getHighlightedText(nickNameHighlightField);
            // 结果封装
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            UserEO searchVO = JSON.parseObject(JSON.toJSONString(sourceMap), UserEO.class);
            searchVO.setUsername(StringUtils.isBlank(highlightedUsername) ? searchVO.getUsername() : highlightedUsername);
            searchVO.setNickName(StringUtils.isBlank(highlightedNickName) ? searchVO.getNickName() : highlightedNickName);
            res.add(searchVO);
        }
        return res;
    }

    private String getHighlightedText(HighlightField field) {
        if (field != null) {
            Text[] fragments = field.fragments();
            StringBuilder highlightedText = new StringBuilder();
            for (Text fragment : fragments) {
                highlightedText.append(fragment.string());
            }
            return highlightedText.toString();
        }
        return "";
    }

}
