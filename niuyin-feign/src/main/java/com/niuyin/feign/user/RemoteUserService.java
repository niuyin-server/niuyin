package com.niuyin.feign.user;

import com.niuyin.feign.user.fallback.RemoteUserServiceFallback;
import com.niuyin.common.constant.ServiceNameConstants;
import com.niuyin.common.domain.R;
import com.niuyin.model.user.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * RemoteUserService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/27
 **/
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.USER_SERVICE, fallbackFactory = RemoteUserServiceFallback.class)
public interface RemoteUserService {

    /**
     * 获取用户信息
     */
    @GetMapping("/api/v1/{userId}")
    R<User> userInfoById(@PathVariable Long userId);

}
