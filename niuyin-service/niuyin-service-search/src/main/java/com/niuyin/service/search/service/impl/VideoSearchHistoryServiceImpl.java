package com.niuyin.service.search.service.impl;

import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.mongodb.client.result.DeleteResult;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.utils.date.DateUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.common.enums.VideoPlatformEnum;
import com.niuyin.service.search.domain.VideoSearchHistory;
import com.niuyin.service.search.service.VideoSearchHistoryService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * VideoSearchHistoryServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Service("videoSearchHistoryServiceImpl")
public class VideoSearchHistoryServiceImpl implements VideoSearchHistoryService {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 保存用户搜索历史记录
     *
     * @param keyword
     * @param userId
     */
    @Async
    @Override
    public void insert(String keyword, Long userId) {
        // 此关联词存在就覆盖
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword).and("platform").is(VideoPlatformEnum.WEB.getCode()));
        VideoSearchHistory getOne = mongoTemplate.findOne(query, VideoSearchHistory.class);
        if (StringUtils.isNotNull(getOne)) {
            // 存在就更新时间
            getOne.setCreatedTime(LocalDateTime.now());
            mongoTemplate.save(getOne);
            return;
        }
        // 不存在则插入
        getOne = new VideoSearchHistory();
        getOne.setKeyword(keyword);
        getOne.setUserId(userId);
        getOne.setPlatform(VideoPlatformEnum.WEB.getCode());
        getOne.setCreatedTime(LocalDateTime.now());
        // 查询搜索记录总条数
        Query query2 = Query.query(Criteria.where("userId").is(userId).and("platform").is(VideoPlatformEnum.WEB.getCode()));
        query2.with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<VideoSearchHistory> list = mongoTemplate.find(query2, VideoSearchHistory.class);
        if (list.size() < 10) {
            mongoTemplate.save(getOne);
        } else {
            // 替换后一个元素
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(list.get(list.size() - 1).getId())), getOne);
        }
    }

    /**
     * 根据平台插入数据
     *
     * @param keyword
     * @param userId
     * @param platform
     */
    @Override
    public void insertPlatform(Long userId, String keyword, VideoPlatformEnum platform) {
        // 此关联词存在就覆盖
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword).and("platform").is(platform.getCode()));
        VideoSearchHistory getOne = mongoTemplate.findOne(query, VideoSearchHistory.class);
        if (StringUtils.isNotNull(getOne)) {
            // 存在就更新时间
            getOne.setCreatedTime(LocalDateTime.now());
            mongoTemplate.save(getOne);
            return;
        }
        // 不存在则插入
        getOne = new VideoSearchHistory();
        getOne.setKeyword(keyword);
        getOne.setUserId(userId);
        getOne.setPlatform(platform.getCode());
        getOne.setCreatedTime(LocalDateTime.now());
        // 查询搜索记录总条数
        Query query2 = Query.query(Criteria.where("userId").is(userId).and("platform").is(platform.getCode()));
        query2.with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<VideoSearchHistory> list = mongoTemplate.find(query2, VideoSearchHistory.class);
        if (list.size() < 10) {
            mongoTemplate.save(getOne);
        } else {
            // 替换后一个元素
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(list.get(list.size() - 1).getId())), getOne);
        }
    }

    /**
     * 查询搜索历史
     */
    @Override
    public List<VideoSearchHistory> findAllSearch() {
        Long userId = UserContext.getUserId();
        // 根据用户id查询数据，按照时间倒序
        return mongoTemplate.find(Query.query(Criteria.where("userId").is(userId).and("platform").is(VideoPlatformEnum.WEB.getCode())).with(Sort.by(Sort.Direction.DESC, "createdTime")), VideoSearchHistory.class);
    }

    @Override
    public List<VideoSearchHistory> findAppSearchHistory() {
        Long userId = UserContext.getUserId();
        // 根据用户id查询数据，按照时间倒序
        return mongoTemplate.find(Query.query(Criteria.where("userId").is(userId).and("platform").is(VideoPlatformEnum.APP.getCode())).with(Sort.by(Sort.Direction.DESC, "createdTime")), VideoSearchHistory.class);
    }

    /**
     * 查询当天的所有搜索记录
     */
    @Override
    public List<VideoSearchHistory> findTodaySearchRecord() {
        return mongoTemplate.find(Query.query(Criteria.where("createdTime").gte(DateUtils.getTodayStartLocalDateTime())), VideoSearchHistory.class);
    }

    /**
     * 删除历史记录
     */
    @Override
    public boolean delSearchHistory(String id) {
        // 判断是否登录
        Long userId = UserContext.getUserId();
        // 删除
        DeleteResult remove = mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId).and("id").is(id)), VideoSearchHistory.class);
        return remove.getDeletedCount() > 0;
    }

    /**
     * 热搜排行榜
     *
     * @return
     */

//    public PageDataInfo findSearchHot() {
//
//
//        return PageDataInfo.genPageData(keyList, keyList.size());
//    }
}
