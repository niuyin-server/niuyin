package com.niuyin.common.cache.ratelimiter.core.aop;

import com.niuyin.common.cache.ratelimiter.core.annotation.RateLimiter;
import com.niuyin.common.cache.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import com.niuyin.common.cache.ratelimiter.core.redis.RateLimiterRedisDAO;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.core.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static com.niuyin.model.common.enums.HttpCodeEnum.TOO_MANY_REQUESTS;

/**
 * 拦截声明了 {@link RateLimiter} 注解的方法，实现限流操作
 */
@Aspect
@Slf4j
public class RateLimiterAspect {

    /**
     * RateLimiterKeyResolver 集合
     */
    private final Map<Class<? extends RateLimiterKeyResolver>, RateLimiterKeyResolver> keyResolvers;

    private final RateLimiterRedisDAO rateLimiterRedisDAO;

    public RateLimiterAspect(List<RateLimiterKeyResolver> keyResolvers, RateLimiterRedisDAO rateLimiterRedisDAO) {
        this.keyResolvers = CollectionUtils.convertMap(keyResolvers, RateLimiterKeyResolver::getClass);
        this.rateLimiterRedisDAO = rateLimiterRedisDAO;
    }

    @Before("@annotation(rateLimiter)")
    public void beforePointCut(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // 获得 IdempotentKeyResolver 对象
        RateLimiterKeyResolver keyResolver = keyResolvers.get(rateLimiter.keyResolver());
        Assert.notNull(keyResolver, "找不到对应的 RateLimiterKeyResolver");
        // 解析 Key
        String key = keyResolver.resolver(joinPoint, rateLimiter);

        // 获取 1 次限流
        boolean success = rateLimiterRedisDAO.tryAcquire(key, rateLimiter.count(), rateLimiter.time(), rateLimiter.timeUnit());
        if (!success) {
            log.info("[beforePointCut][方法({}) 参数({}) 请求过于频繁]", joinPoint.getSignature().toString(), joinPoint.getArgs());
//            String message = StrUtil.blankToDefault(rateLimiter.message(), TOO_MANY_REQUESTS.getMsg());
            throw new CustomException(TOO_MANY_REQUESTS);
        }
    }

}

