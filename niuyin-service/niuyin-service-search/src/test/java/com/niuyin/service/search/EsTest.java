package com.niuyin.service.search;

import com.alibaba.fastjson.JSON;
import com.niuyin.common.utils.date.DateUtils;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.service.search.constant.ESIndexConstants;
import com.niuyin.service.search.domain.VideoSearchVO;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
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
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.*;
import java.util.Date;
import java.util.Map;

import static com.niuyin.service.search.constant.ESQueryConstants.*;

/**
 * EsTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Slf4j
@SpringBootTest
public class EsTest {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    void testCreateIndex() {
//        Date date = new Date();
//        System.out.println("date.getTime() = " + date.getTime());
//        LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
//        Instant.ofEpochSecond()
//        Date date = new Date();
//        Instant instant = date.toInstant();
//        ZoneId zoneId = ZoneId.systemDefault();
//
////        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
//        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), zoneId);
//        System.out.println("Date = " + date);
//        System.out.println("LocalDateTime = " + localDateTime);

        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        System.out.println("LocalDateTime = " + localDateTime);
        System.out.println("Date = " + date);
    }

    @Test
    void searchVideo() {
        // 构建查询请求
        VideoSearchKeywordDTO videoSearchKeywordDTO = new VideoSearchKeywordDTO();
        videoSearchKeywordDTO.setKeyword("这次不卡了   地平线，启动！");
        videoSearchKeywordDTO.setPageNum(1);
        videoSearchKeywordDTO.setPageSize(10);
//        long todayStartLong = DateUtils.getTodayPlusStartLocalLong(-1); //今日数据
//        long dayStartLong = DateUtils.getTodayPlusStartLocalLong(-7); //本周数据
//        log.debug("todayStartLong:{}", dayStartLong);
//        videoSearchKeywordDTO.setMinBehotTime(new Date(dayStartLong));
        SearchRequest searchRequest = buildSearchRequest(videoSearchKeywordDTO);

        try {
            // 执行查询请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理搜索结果
            processSearchResponse(searchResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static SearchRequest buildSearchRequest(VideoSearchKeywordDTO videoSearchKeywordDTO) {
        SearchRequest searchRequest = new SearchRequest(ESIndexConstants.INDEX_VIDEO);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 设置分页
        searchSourceBuilder.from((videoSearchKeywordDTO.getPageNum() - 1) * videoSearchKeywordDTO.getPageSize());
        searchSourceBuilder.size(videoSearchKeywordDTO.getPageSize());

        // 构建查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 构建多字段匹配查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(videoSearchKeywordDTO.getKeyword(), VideoSearchVO.VIDEO_TITLE, VideoSearchVO.USER_NICKNAME);
        boolQueryBuilder.must(multiMatchQueryBuilder);

        // 构建范围过滤器
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(VideoSearchVO.PUBLISH_TIME)
                .gte(videoSearchKeywordDTO.getMinBehotTime())
                .lte(new Date().getTime());
        boolQueryBuilder.filter(rangeQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);

        // 设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .boundaryScannerLocale(zh_CN)
                .field(VideoSearchVO.VIDEO_TITLE)
                .field(VideoSearchVO.USER_NICKNAME)
                .preTags(Highlight_preTags)
                .postTags(Highlight_postTags);
        searchSourceBuilder.highlighter(highlightBuilder);

        // 设置排序
        ScoreSortBuilder scoreSortField = SortBuilders.scoreSort().order(SortOrder.DESC);
        FieldSortBuilder publishTimeSortField = SortBuilders.fieldSort(VideoSearchVO.PUBLISH_TIME).order(SortOrder.DESC).sortMode(SortMode.MAX);
        searchSourceBuilder.sort(scoreSortField);
        searchSourceBuilder.sort(publishTimeSortField);

        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private static void processSearchResponse(SearchResponse searchResponse) throws Exception {
        // 处理搜索结果
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            // 处理每个搜索结果

            // 获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField titleHighlightField = highlightFields.get(VideoSearchVO.VIDEO_TITLE);
            HighlightField nicknameHighlightField = highlightFields.get(VideoSearchVO.USER_NICKNAME);

            // 处理高亮显示的片段
            String highlightedTitle = "";
            String highlightedNickname = "";
            if (titleHighlightField != null) {
                Text[] titleFragments = titleHighlightField.fragments();
                for (Text fragment : titleFragments) {
                    highlightedTitle += fragment;
                }
            }
            if (nicknameHighlightField != null) {
                Text[] nicknameFragments = nicknameHighlightField.fragments();
                for (Text fragment : nicknameFragments) {
                    highlightedNickname += fragment;
                }
            }

            Map map = JSON.parseObject(hit.getSourceAsString(), Map.class);
            VideoSearchVO searchVO = new VideoSearchVO();
            BeanUtils.populate(searchVO, map);
            searchVO.setVideoTitle((highlightedTitle.equals("") || highlightedTitle.isEmpty() ? (String) hit.getSourceAsMap().get(VideoSearchVO.VIDEO_TITLE) : highlightedTitle));
            searchVO.setUserNickName((highlightedNickname.equals("") || highlightedNickname.isEmpty() ? (String) hit.getSourceAsMap().get(VideoSearchVO.USER_NICKNAME) : highlightedNickname));
            System.out.println("searchVO = " + searchVO);
            // 打印结果
//            System.out.println("videoTitle: " + (highlightedTitle.equals("") || highlightedTitle.isEmpty() ? hit.getSourceAsMap().get(VideoSearchVO.VIDEO_TITLE) : highlightedTitle));
//            System.out.println("userNickname: " + (highlightedNickname.equals("") || highlightedNickname.isEmpty() ? hit.getSourceAsMap().get(VideoSearchVO.USER_NICKNAME) : highlightedNickname));
//            System.out.println("Publish Time: " + hit.getSourceAsMap().get(VideoSearchVO.PUBLISH_TIME));
//            System.out.println("Publish Type: " + hit.getSourceAsMap().get(VideoSearchVO.PUBLISH_TYPE));
//            System.out.println("coverImage: " + hit.getSourceAsMap().get(VideoSearchVO.COVER_IMAGE));
//            System.out.println("userAvatar: " + hit.getSourceAsMap().get(VideoSearchVO.USER_AVATAR));
//            System.out.println("userId: " + hit.getSourceAsMap().get(VideoSearchVO.USER_ID));
//            System.out.println("videoId: " + hit.getSourceAsMap().get(VideoSearchVO.VIDEO_ID));
//            System.out.println("videoUrl: " + hit.getSourceAsMap().get(VideoSearchVO.VIDEO_URL));
//            System.out.println("Score: " + hit.getScore());
            System.out.println("---------------------------------------");

        }
    }

}
