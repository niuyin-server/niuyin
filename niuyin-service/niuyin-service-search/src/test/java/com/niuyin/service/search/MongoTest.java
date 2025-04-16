package com.niuyin.service.search;

import com.mongodb.client.MongoCursor;
import com.niuyin.service.search.domain.VideoSearchHistory;
import com.niuyin.service.search.service.VideoSearchHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MongoTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Slf4j
@SpringBootTest
public class MongoTest {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private VideoSearchHistoryService videoSearchHistoryService;

    @DisplayName("获取今日所有搜索记录")
    @Test
    void testInsert() {
        VideoSearchHistory videoSearchHistory = new VideoSearchHistory();
        videoSearchHistory.setKeyword("第1条搜索记录");
        videoSearchHistory.setUserId(2L);
        videoSearchHistory.setCreatedTime(LocalDateTime.now());
        VideoSearchHistory insert = mongoTemplate.insert(videoSearchHistory);
        log.debug("mongo inset ==> {}", insert);
    }

    @Test
    void testFindAll() {
        List<VideoSearchHistory> all = mongoTemplate.findAll(VideoSearchHistory.class);
        all.forEach(System.out::println);
    }

    @DisplayName("条件查询")
    @Test
    public void findUserList() {
        Query query = new Query(Criteria.where("userId").is(3L));
        List<VideoSearchHistory> list = mongoTemplate.find(query, VideoSearchHistory.class);
        list.forEach(System.out::println);
    }

    @DisplayName("获取今日所有搜索记录")
    @Test
    public void findTodaySearchRecord3() {
        List<VideoSearchHistory> todaySearchRecord = videoSearchHistoryService.findTodaySearchRecord();
        todaySearchRecord.forEach(System.out::println);
    }

    @Test
    @DisplayName("搜索推荐")
    void searchCommend() {
        // 构建模糊查询条件
//        Criteria criteria = Criteria.where("keyword").regex("原神");
//
//        // 创建查询对象，并设置查询条件和限制返回结果数量为 10 条
//        Query query = new Query(criteria).limit(10);
//
//        // 执行查询并返回结果
//        List<VideoSearchHistory> videoSearchHistories = mongoTemplate.find(query, VideoSearchHistory.class);
//        Set<VideoSearchHistory> res= new HashSet<>(videoSearchHistories);
//        res.forEach(System.out::println);
        // 构建聚合管道
        Set<String> keywords = new HashSet<>();
        keywords.add("原神");
        keywords.add("启动");
        // 构建模糊查询条件
        Criteria criteria = Criteria.where("keyword").regex(String.join("|", keywords), "i"); // "i" 表示不区分大小写

        // 构建聚合管道
        MatchOperation matchOperation = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("keyword").first("$$ROOT").as("doc");
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "doc.timestamp"));
        LimitOperation limitOperation = Aggregation.limit(10);
        SampleOperation sampleOperation = Aggregation.sample(10);

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation, sortOperation, limitOperation, sampleOperation);

        // 执行聚合查询
        List<VideoSearchHistory> videoSearchHistory = mongoTemplate.aggregate(aggregation, "video_search_history", VideoSearchHistory.class).getMappedResults();
        videoSearchHistory.forEach(System.out::println);
    }

}
