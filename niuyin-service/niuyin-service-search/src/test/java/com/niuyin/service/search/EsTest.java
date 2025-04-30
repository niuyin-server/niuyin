package com.niuyin.service.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.json.JsonData;
import com.niuyin.common.core.utils.date.DateUtils;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.model.search.enums.TimeRange;
import com.niuyin.model.search.vo.VideoSearchVO;
import com.niuyin.model.video.enums.IkAnalyzeTypeEnum;
import com.niuyin.service.search.service.VideoSearchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.QueryBuilders;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.io.IOException;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EsTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Slf4j
@SpringBootTest
public class EsTest {

//    @Resource
//    private RestHighLevelClient restHighLevelClient;

    @Resource
    private ElasticsearchClient elasticsearchClient;
    @Resource
    private VideoSearchService videoSearchService;

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
        videoSearchKeywordDTO.setKeyword("如何学习并发编程");
        videoSearchKeywordDTO.setPageNum(1);
        videoSearchKeywordDTO.setPageSize(10);
//        long todayStartLong = DateUtils.getTodayPlusStartLocalLong(-1); //今日数据
        long dayStartLong = DateUtils.getTodayPlusStartLocalLong(-777); //本周数据
//        long dayStartLong = DateUtils.getTodayPlusStartLocalLong(-30); //本月数据
//        log.debug("todayStartLong:{}", dayStartLong);
//        videoSearchKeywordDTO.setMinBehotTime(new Date(dayStartLong));
        List<VideoSearchVO> videoSearchVOS = null;
        try {
            videoSearchVOS = videoSearchService.searchAllVideoFromES(videoSearchKeywordDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        videoSearchVOS.forEach(videoSearchVO -> {
            System.out.println("videoSearchVO = " + videoSearchVO);
        });
    }

    @Test
    @DisplayName("测试ik分词器")
    void testIkAnalyzer() {
        Set<String> res = analyzeText("ik_smart", "如何学习并发编程");
        res.forEach(System.out::println);
    }

    private Set<String> analyzeText(String analyzer, String text) {
        Set<String> res = new HashSet<>();
        try {
            AnalyzeResponse response = elasticsearchClient.indices().analyze(a -> a
                    .analyzer(analyzer)
                    .text(text)
            );

            response.tokens().forEach(token -> res.add(token.token()));

        } catch (IOException e) {
            System.err.println("分词分析失败: " + e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    @Test
    void testSearchVideos() {
        try {
            Page<VideoSearchVO> videoPage = searchVideos("如何学习并发编程", TimeRange.THIS_YEAR, null, null, PageRequest.of(1, 10));
            videoPage.getContent().forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<VideoSearchVO> searchVideos(String keyword,
                                            TimeRange timeRange, Date customStartDate, Date customEndDate,
                                            Pageable pageable) throws IOException {
        // 构建时间范围查询
        Query timeRangeQuery = buildTimeRangeQuery(timeRange, customStartDate, customEndDate);

        // 构建高亮字段
        Map<String, HighlightField> highlightFields = Map.of(
                VideoSearchVO.VIDEO_TITLE, HighlightField.of(builder -> builder),
                VideoSearchVO.TAGS, HighlightField.of(builder -> builder)
        );

        // 构建查询请求
        SearchRequest searchRequest = SearchRequest.of(b -> b
                .index("search_video")
                .query(q -> q
                        .bool(b2 -> b2
                                .must(b3 -> b3
                                        .bool(b4 -> b4
                                                .should(s -> s
                                                        .match(m -> m
                                                                .field(VideoSearchVO.VIDEO_TITLE)
                                                                .query(keyword)
                                                        )
                                                )
                                                .should(s -> s
                                                        .match(m -> m
                                                                .field(VideoSearchVO.TAGS)
                                                                .query(keyword)
                                                        )
                                                )
                                        )
                                )
                                .must(timeRangeQuery)
                        )
                )
                .highlight(h -> h
                        .preTags("<font class='keyword-hint'>")
                        .postTags("</font>")
                        .fields(highlightFields)
                )
                .from((pageable.getPageNumber() - 1) * pageable.getPageSize())
                .size(pageable.getPageSize())
        );

        // 执行查询
        SearchResponse<VideoSearchVO> response = elasticsearchClient.search(searchRequest, VideoSearchVO.class);

        // 处理结果
        List<VideoSearchVO> videos = response.hits().hits().stream()
                .map(hit -> {
                    VideoSearchVO video = hit.source();
                    // 处理高亮
                    if (hit.highlight() != null) {
                        if (hit.highlight().containsKey(VideoSearchVO.VIDEO_TITLE)) {
                            video.setVideoTitle(hit.highlight().get(VideoSearchVO.VIDEO_TITLE).get(0));
                        }
                        if (hit.highlight().containsKey(VideoSearchVO.TAGS)) {
                            video.setTags(hit.highlight().get(VideoSearchVO.TAGS).toArray(new String[0]));
                        }
                    }
                    return video;
                })
                .collect(Collectors.toList());

        // 返回分页结果
        return new PageImpl<>(
                videos,
                pageable,
                response.hits().total() != null ? response.hits().total().value() : 0
        );
    }

    private Query buildTimeRangeQuery(TimeRange timeRange, Date customStartDate, Date customEndDate) {
        Instant startInstant = null;
        Instant endInstant = Instant.now();

        LocalDateTime now = LocalDateTime.now();

        switch (timeRange) {
            case TODAY:
                startInstant = now.with(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant();
                break;
            case THIS_WEEK:
                startInstant = now.with(DayOfWeek.MONDAY).with(LocalTime.MIN)
                        .atZone(ZoneId.systemDefault()).toInstant();
                break;
            case THIS_MONTH:
                startInstant = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN)
                        .atZone(ZoneId.systemDefault()).toInstant();
                break;
            case THIS_YEAR:
                startInstant = now.with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN)
                        .atZone(ZoneId.systemDefault()).toInstant();
                break;
            case CUSTOM:
                startInstant = customStartDate.toInstant();
                endInstant = customEndDate != null ? customEndDate.toInstant() : Instant.now();
                break;
        }

        Instant finalStartInstant = startInstant;
        Instant finalEndInstant = endInstant;
        return Query.of(q -> q
                .range(r -> r
                        .field(VideoSearchVO.PUBLISH_TIME)
                        .gte(JsonData.of(finalStartInstant.toEpochMilli()))
                        .lte(JsonData.of(finalEndInstant.toEpochMilli()))
                )
        );
    }
}
