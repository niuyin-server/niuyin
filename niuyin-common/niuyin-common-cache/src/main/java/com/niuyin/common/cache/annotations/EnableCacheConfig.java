package com.niuyin.common.cache.annotations;

import com.niuyin.common.cache.config.RedisConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EnableRedisConfig
 * todo 将来启动类中的@EnableRedisConfig注解改为@EnableCacheConfig继承本包中的分布式锁与二级缓存
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(RedisConfig.class)
public @interface EnableCacheConfig {
}
