package com.qiniu.feign.interceptor;

import com.qiniu.common.context.UserContext;
import com.qiniu.common.utils.string.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * UserInterceptor
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 * <a href="https://www.bilibili.com/video/BV1kH4y1S7wz?p=33&vd_source=616a9dd528080cf576646147166fe033">...</a>
 * openfeign发送请求时传递token
 **/
public class UserInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId = UserContext.getUserId();
        if (StringUtils.isNotNull(userId)) {
            requestTemplate.header("userId", userId + "");
        }
    }

    // .Knife4j
//https://www.bilibili.com/video/BV1kH4y1S7wz?p=35&spm_id_from=pageDriver&vd_source=616a9dd528080cf576646147166fe033

}
