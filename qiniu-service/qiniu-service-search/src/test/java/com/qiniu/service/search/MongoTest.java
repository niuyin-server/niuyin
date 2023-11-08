package com.qiniu.service.search;

import com.qiniu.service.search.domain.VideoSearchHistory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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

    //条件查询
    @Test
    public void findUserList() {
        Query query = new Query(Criteria.where("userId").is(3L));
        List<VideoSearchHistory> list = mongoTemplate.find(query, VideoSearchHistory.class);
        list.forEach(System.out::println);
    }


}
