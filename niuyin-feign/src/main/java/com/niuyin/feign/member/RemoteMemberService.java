package com.niuyin.feign.member;

import com.niuyin.feign.member.fallback.RemoteMemberServiceFallback;
import com.niuyin.common.core.constant.ServiceNameConstants;
import com.niuyin.common.core.domain.R;
import com.niuyin.model.member.domain.Member;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * RemoteMemberService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/27
 **/
@FeignClient(contextId = "remoteMemberService", value = ServiceNameConstants.USER_SERVICE, fallbackFactory = RemoteMemberServiceFallback.class)
public interface RemoteMemberService {

    /**
     * 获取用户信息
     */
    @GetMapping("/api/v1/{userId}")
    R<Member> userInfoById(@PathVariable Long userId);

}
