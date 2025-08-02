package com.niuyin.common.cache.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.niuyin.common.cache.ratelimiter.core.annotation.RateLimiter;
import com.niuyin.common.cache.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import com.niuyin.common.core.utils.ServletUtils;
import org.aspectj.lang.JoinPoint;

/**
 * IP 级别的限流 Key 解析器，使用方法名 + 方法参数 + IP，组装成一个 Key
 *
 * 为了避免 Key 过长，使用 MD5 进行“压缩”
 */
public class ClientIpRateLimiterKeyResolver implements RateLimiterKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtil.join(",", joinPoint.getArgs());
        String clientIp = ServletUtils.getClientIP();
        return SecureUtil.md5(methodName + argsStr + clientIp);
    }

}
