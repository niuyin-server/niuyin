package com.niuyin.feign.social;

import com.niuyin.feign.social.fallback.RemoteSocialServiceFallback;
import com.niuyin.common.constant.ServiceNameConstants;
import com.niuyin.common.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * RemoteSocialService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/4
 **/
@FeignClient(contextId = "remoteSocialService", value = ServiceNameConstants.SOCIAL_SERVICE, fallbackFactory = RemoteSocialServiceFallback.class)
public interface RemoteSocialService {

    /**
     * 是否关注某人
     *
     * @param userId
     * @return
     */
    @GetMapping("/api/v1/follow/weatherfollow/{userId}")
    R<Boolean> weatherfollow(@PathVariable("userId") Long userId);

}
