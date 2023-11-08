package com.niuyin.feign.user.fallback;

import com.niuyin.common.domain.R;
import com.niuyin.feign.user.RemoteUserService;
import com.niuyin.model.user.domain.User;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteUserServiceFallback implements FallbackFactory<RemoteUserService> {
    @Override
    public RemoteUserService create(Throwable cause) {
        return new RemoteUserService() {
            @Override
            public R<User> userInfoById(Long userId) {
//                return R.fail("获取信息失败:" + cause.getMessage());
                return R.fail(new User());
            }
        };
    }
}
