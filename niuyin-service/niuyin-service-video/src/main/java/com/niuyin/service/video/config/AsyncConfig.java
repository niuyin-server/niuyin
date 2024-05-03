package com.niuyin.service.video.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Value("${video-async-executor.core-size}")
    private int coreSize;

    @Value("${video-async-executor.max-size}")
    private int maxSize;

    @Value("${video-async-executor.queue-capacity}")
    private int queueCapacity;

    /**
     * video-service 项目共用线程池
     */
    public static final String VIDEO_EXECUTOR = "videoAsyncExecutor";
    public static final String VIDEO_EXECUTOR_PREFIX = "video-async-executor-";

    @Override
    public Executor getAsyncExecutor() {
        return videoAsyncExecutor();
    }

    @Bean(VIDEO_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor videoAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize); // 核心线程数
        executor.setMaxPoolSize(maxSize); // 最大线程数
        executor.setQueueCapacity(queueCapacity); // 队列容量
        executor.setThreadNamePrefix(VIDEO_EXECUTOR_PREFIX); // 线程名称前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//满了调用线程执行，认为重要任务
        executor.initialize();
        log.debug("videoAsyncExecutor init {}", executor.getCorePoolSize());
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
