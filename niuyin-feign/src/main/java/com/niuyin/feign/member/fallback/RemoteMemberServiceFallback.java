package com.niuyin.feign.member.fallback;

import com.niuyin.common.domain.R;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.member.domain.Member;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteMemberServiceFallback implements FallbackFactory<RemoteMemberService> {
    @Override
    public RemoteMemberService create(Throwable cause) {
        return new RemoteMemberService() {
            @Override
            public R<Member> userInfoById(Long userId) {
//                return R.fail("获取信息失败:" + cause.getMessage());
                return R.fail(new Member());
            }
        };
    }
}
