package com.niuyin.service.video.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解防止视频表单重复提交
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VideoRepeatSubmit {

    String key() default "";

    /**
     * key过期时间，单位毫秒
     */
    long interval() default 5000;

    TimeUnit timeunit() default TimeUnit.MILLISECONDS;

    /**
     * 提示消息
     */
    String message() default "您的操作太快了，请稍候再试";
}
