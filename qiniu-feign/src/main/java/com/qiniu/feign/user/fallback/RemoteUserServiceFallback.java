package com.qiniu.feign.user.fallback;

import com.qiniu.common.domain.R;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.user.domain.User;
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
