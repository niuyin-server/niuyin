package com.niuyin.common.cache.annotations;

import com.niuyin.common.cache.aspect.DubboCacheAspect;
import com.niuyin.common.cache.aspect.RedissonLockAspect;
import com.niuyin.common.cache.config.CaffeineConfig;
import com.niuyin.common.cache.config.RedisConfig;
import com.niuyin.common.cache.service.LockService;
import com.niuyin.common.cache.service.RedisService;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EnableRedisConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RedisConfig.class, CaffeineConfig.class, RedisService.class, RedissonLockAspect.class, DubboCacheAspect.class, LockService.class})
public @interface EnableCacheConfig {
}
