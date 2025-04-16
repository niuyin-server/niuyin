package com.niuyin.service.video.aspectj;

import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.spring.SpElUtils;
import com.niuyin.service.video.annotation.VideoRepeatSubmit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 限流处理
 */
@Aspect
@Component
public class VideoRepeatSubmitAspect {

    @Resource
    RedisService redisService;

    /**
     * todo 前端在dto传入一个唯一业务字段
     *
     * @param point
     * @param videoRepeatSubmit
     * @throws Throwable
     */
    @Before("@annotation(videoRepeatSubmit)")
    public void doBefore(JoinPoint point, VideoRepeatSubmit videoRepeatSubmit) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        String prefixKey = SpElUtils.getMethodKey(method);
        String key = SpElUtils.parseSpEl(method, point.getArgs(), videoRepeatSubmit.key());
        String redisKey = prefixKey + "_" + key + "_" + UserContext.getUserId();
        if (redisService.hasKey(redisKey)) {
            throw new RuntimeException(videoRepeatSubmit.message());
        }
        redisService.setCacheObject(redisKey, 1, videoRepeatSubmit.interval(), videoRepeatSubmit.timeunit());
    }

    @After("@annotation(videoRepeatSubmit)")
    public void doAfter(JoinPoint point, VideoRepeatSubmit videoRepeatSubmit) throws Throwable {

    }

}
