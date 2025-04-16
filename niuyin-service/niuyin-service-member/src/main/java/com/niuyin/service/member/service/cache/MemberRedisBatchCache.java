package com.niuyin.service.member.service.cache;

import com.niuyin.model.member.domain.Member;
import com.niuyin.service.member.service.IMemberService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * VideoRedisBatchCache
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/8
 **/
@Service
public class MemberRedisBatchCache extends AbstractRedisStringCache<Long, Member> {

    @Resource
    @Lazy
    private IMemberService memberService;

    @Override
    protected String getKey(Long userId) {
        return "member:member_batch:" + userId;
    }

    @Override
    protected Long getExpireSeconds() {
        return 24 * 60 * 60L;// 24小时
    }

    @Override
    protected Map<Long, Member> load(List<Long> userIds) {
        List<Member> memberList = memberService.listByIds(userIds);
        return memberList.stream().collect(Collectors.toMap(Member::getUserId, Function.identity()));
    }
}
