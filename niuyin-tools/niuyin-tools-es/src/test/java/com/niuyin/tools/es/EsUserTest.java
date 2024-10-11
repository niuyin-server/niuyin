package com.niuyin.tools.es;

import com.alibaba.fastjson.JSON;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.date.LocalDateTimeUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.tools.es.domain.UserEO;
import com.niuyin.tools.es.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * EsUserTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/10/11
 **/
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class EsUserTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private MemberMapper memberMapper;

    @Test
    @DisplayName("初始化用户数据")
    public void initUser() throws IOException {
        // 1.0 查询全部用户
        List<Member> memberList = memberMapper.searchExistUserList();
        List<UserEO> userEOS = new ArrayList<>();
        memberList.forEach(item -> {
            UserEO userEO = BeanCopyUtils.copyBean(item, UserEO.class);
            userEO.setUserId(item.getUserId().toString());
            userEO.setUsername(item.getUserName());
            userEO.setCreateTime(LocalDateTimeUtils.localDateTime2Date(item.getCreateTime()));
            userEOS.add(userEO);
        });
        // 1.1 批量保存到es
        BulkRequest bulkRequest = new BulkRequest("doc_user");
        for (UserEO vo : userEOS) {
            IndexRequest indexRequest = new IndexRequest().id(vo.getUserId()).source(JSON.toJSONString(vo), XContentType.JSON);
            //批量添加数据
            bulkRequest.add(indexRequest);
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
}
