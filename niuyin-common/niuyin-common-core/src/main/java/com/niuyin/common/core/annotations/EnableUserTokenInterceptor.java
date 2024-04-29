package com.niuyin.common.core.annotations;

import com.niuyin.common.core.config.WebMvcConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EnableUserTokenInterceptor
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(WebMvcConfig.class)
public @interface EnableUserTokenInterceptor {
}
