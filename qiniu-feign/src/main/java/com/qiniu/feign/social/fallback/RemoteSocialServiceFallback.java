package com.qiniu.feign.social.fallback;

import com.qiniu.common.domain.R;
import com.qiniu.feign.behave.RemoteBehaveService;
import com.qiniu.feign.social.RemoteSocialService;
import feign.hystrix.FallbackFactory;
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
