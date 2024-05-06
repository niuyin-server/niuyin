package com.niuyin.common.cache.annotations;

import com.niuyin.common.cache.enums.CacheType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author niuyin
 * Caffeine+Redis二级缓存
 * 支持springEl表达式
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DoubleCache {

    String cachePrefix();

    String key() default "";    //支持springEl表达式

    long expire() default 60; // 默认1分钟

    TimeUnit unit() default TimeUnit.SECONDS;

    CacheType type() default CacheType.FULL;
}
