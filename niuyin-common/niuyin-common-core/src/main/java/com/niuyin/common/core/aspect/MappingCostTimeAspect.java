package com.niuyin.common.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * MappingCostTimeAspect
 *
 * @AUTHOR: roydon
 * @DATE: 2024/5/5
 **/
@Slf4j
@Aspect
@Component
public class MappingCostTimeAspect {

    @Pointcut(value = "@annotation(com.niuyin.common.core.annotations.MappingCostTime)")
    public void mappingCostTime() {}

    @Around("mappingCostTime()")
    public Object mappingCostTimeAround(ProceedingJoinPoint joinPoint) {
        Object obj = null;
        try {
            long beginTime = System.currentTimeMillis();
            obj = joinPoint.proceed();
            //获取类名称
            String className = joinPoint.getSignature().getDeclaringTypeName();
            //获取方法名称
            String method = joinPoint.getSignature().getName();
            //计算耗时
            long cost = System.currentTimeMillis() - beginTime;
            log.info("类:[{}]，方法:[{}] 接口耗时:[{}]", className, method, cost + "毫秒");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return obj;
    }
}
