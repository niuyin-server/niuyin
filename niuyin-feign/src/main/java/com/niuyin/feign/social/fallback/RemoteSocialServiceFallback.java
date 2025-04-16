package com.niuyin.feign.social.fallback;

import com.niuyin.common.core.domain.R;
import com.niuyin.feign.social.RemoteSocialService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * RemoteSocialServiceFallback
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/28
 **/
@Component
public class RemoteSocialServiceFallback implements FallbackFactory<RemoteSocialService> {

    @Override
    public RemoteSocialService create(Throwable throwable) {
        return new RemoteSocialService() {
            @Override
            public R<Boolean> weatherfollow(Long userId) {
                return R.fail(false);
            }
        };
    }
}
