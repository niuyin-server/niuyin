package com.niuyin.common.cache.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.niuyin.common.cache.annotations.DoubleCache;
import com.niuyin.common.cache.constant.CacheConstant;
import com.niuyin.common.cache.enums.CacheType;
import com.niuyin.common.cache.util.ElParser;
import com.niuyin.common.core.utils.string.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@AllArgsConstructor
@Aspect
@Component
public class DubboCacheAspect {

    private Cache cache;
    private RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.niuyin.common.cache.annotations.DoubleCache)")
    public void cacheAspect() {
    }

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        //拼接解析springEl表达式的map
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            treeMap.put(paramNames[i], args[i]);
        }

        DoubleCache annotation = method.getAnnotation(DoubleCache.class);
        String realKey = annotation.cachePrefix();
        if (!StringUtils.isEmpty(annotation.key())) {
            String elResult = ElParser.parse(annotation.key(), treeMap);
            realKey = annotation.cachePrefix() + CacheConstant.COLON + elResult;
        }
        //强制更新
        if (annotation.type() == CacheType.PUT) {
            Object object = point.proceed();
            redisTemplate.opsForValue().set(realKey, object, annotation.expire(), annotation.unit());
            cache.put(realKey, object);
            return object;
        }
        //删除
        else if (annotation.type() == CacheType.DELETE) {
            redisTemplate.delete(realKey);
            cache.invalidate(realKey);
            return point.proceed();
        }

        //读写，查询Caffeine
        Object caffeineCache = cache.getIfPresent(realKey);
        if (Objects.nonNull(caffeineCache)) {
            log.info("get data from caffeine");
            return caffeineCache;
        }

        //查询Redis
        Object redisCache = redisTemplate.opsForValue().get(realKey);
        if (Objects.nonNull(redisCache)) {
            log.info("get data from redis");
            cache.put(realKey, redisCache);
            return redisCache;
        }

        log.info("get data from database");
        Object object = point.proceed();
        if (Objects.nonNull(object)) {
            //写入Redis
            redisTemplate.opsForValue().set(realKey, object, annotation.expire(), annotation.unit());
            //写入Caffeine
            cache.put(realKey, object);
        }
        return object;
    }
}
