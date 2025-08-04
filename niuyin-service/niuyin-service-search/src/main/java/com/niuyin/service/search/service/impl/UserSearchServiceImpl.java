package com.niuyin.service.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.model.search.ESIndexConstants;
import com.niuyin.model.search.dto.UserSearchKeywordDTO;
import com.niuyin.model.search.domain.UserEO;
import com.niuyin.service.search.domain.vo.UserSearchVO;
import com.niuyin.service.search.service.UserSearchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.niuyin.service.search.constant.ESQueryConstants.Highlight_postTags;
import static com.niuyin.service.search.constant.ESQueryConstants.Highlight_preTags;

@Slf4j
@Service
public class UserSearchServiceImpl implements UserSearchService {

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public List<UserSearchVO> searchUserFromES(UserSearchKeywordDTO dto) {
        try {
            // 构建搜索请求
            SearchRequest searchRequest = buildUserSearchRequest(dto);

            // 执行搜索
            SearchResponse<UserEO> response = elasticsearchClient.search(searchRequest, UserEO.class);

            // 处理结果
            return processSearchResponse(response);

        } catch (IOException e) {
            log.error("用户搜索失败", e);
            return Collections.emptyList();
        }
    }

    private SearchRequest buildUserSearchRequest(UserSearchKeywordDTO dto) {
        return SearchRequest.of(s -> s
                .index(ESIndexConstants.INDEX_USER)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .multiMatch(mm -> mm
                                                .query(dto.getKeyword())
                                                .fields(UserEO.USERNAME, UserEO.NICK_NAME)
                                        )
                                )
                        )
                )
                .from((dto.getPageNum() - 1) * dto.getPageSize())
                .size(dto.getPageSize())
                .highlight(h -> h
                        .fields(UserEO.USERNAME, f -> f
                                .preTags(Highlight_preTags)
                                .postTags(Highlight_postTags)
                        )
                        .fields(UserEO.NICK_NAME, f -> f
                                .preTags(Highlight_preTags)
                                .postTags(Highlight_postTags)
                        )
                )
                .sort(so -> so.score(sort -> sort.order(SortOrder.Desc))
                )
        );
    }

    private List<UserSearchVO> processSearchResponse(SearchResponse<UserEO> response) {
        return response.hits().hits().stream()
                .map(hit -> {
                    UserEO userEO = hit.source();
                    UserSearchVO vo = BeanCopyUtils.copyBean(userEO, UserSearchVO.class);

                    // 处理高亮
                    if (hit.highlight() != null) {
                        handleHighlight(hit.highlight(), vo);
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }

    private void handleHighlight(Map<String, List<String>> highlightFields, UserSearchVO vo) {
        List<String> usernameHighlights = highlightFields.get(UserEO.USERNAME);
        List<String> nickNameHighlights = highlightFields.get(UserEO.NICK_NAME);

        if (usernameHighlights != null && !usernameHighlights.isEmpty()) {
            vo.setUsername(String.join("", usernameHighlights));
        }
        if (nickNameHighlights != null && !nickNameHighlights.isEmpty()) {
            vo.setNickName(String.join("", nickNameHighlights));
        }
    }
}
