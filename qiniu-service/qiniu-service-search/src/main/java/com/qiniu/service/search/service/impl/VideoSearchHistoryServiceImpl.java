package com.qiniu.service.search.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.common.enums.HttpCodeEnum;
import com.qiniu.service.search.domain.VideoSearchHistory;
import com.qiniu.service.search.service.VideoSearchHistoryService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword));
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
        getOne.setCreatedTime(LocalDateTime.now());

        Query query2 = Query.query(Criteria.where("userId").is(userId));
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
        // 判断是否登录
        Long userId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            throw new CustomException(HttpCodeEnum.NEED_LOGIN);
        }
        // 根据用户id查询数据，按照时间倒序
        return mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Direction.DESC, "createdTime")), VideoSearchHistory.class);
    }

    /**
     * 删除历史记录
     */
    @Override
    public boolean delSearchHistory(String id) {
        // 判断是否登录
        Long userId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            throw new CustomException(HttpCodeEnum.NEED_LOGIN);
        }
        // 删除
        DeleteResult remove = mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId).and("id").is(id)), VideoSearchHistory.class);
        return remove.getDeletedCount() > 0;
    }
}
