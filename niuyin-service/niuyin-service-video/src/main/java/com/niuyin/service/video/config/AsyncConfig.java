package com.niuyin.service.video.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
//@EnableAsync
public class AsyncConfig  implements AsyncConfigurer{

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
    public TaskExecutor getAsyncExecutor() {
        return videoAsyncExecutor();
    }

    @Bean(name = VIDEO_EXECUTOR, destroyMethod = "shutdown")
    @Primary
    public ThreadPoolTaskExecutor videoAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(VIDEO_EXECUTOR_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 新增建议配置（Spring Boot 3 优化项）
//        executor.setTaskDecorator(new MDCTaskDecorator()); // 支持MDC上下文传递
        executor.setWaitForTasksToCompleteOnShutdown(true); // 优雅停机等待任务完成
        executor.setAwaitTerminationSeconds(30); // 等待超时时间

        executor.initialize();
        log.info("Video async executor initialized: core={}, max={}, queue={}", coreSize, maxSize, queueCapacity);
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
