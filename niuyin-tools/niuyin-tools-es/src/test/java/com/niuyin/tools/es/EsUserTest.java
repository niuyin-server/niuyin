package com.niuyin.tools.es;

import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.date.LocalDateTimeUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.search.domain.UserEO;
import com.niuyin.tools.es.mapper.MemberMapper;
import com.niuyin.tools.es.service.UserEsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
//@RunWith(SpringRunner.class)
public class EsUserTest {

    @Resource
    private UserEsService userEsService;

    @Resource
    private MemberMapper memberMapper;

    @Test
    @DisplayName("初始化用户数据")
    public void initUser() {
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
        userEOS.forEach(item -> {
            userEsService.save(item);
        });
    }
}
