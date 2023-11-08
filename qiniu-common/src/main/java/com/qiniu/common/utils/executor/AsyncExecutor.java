package com.qiniu.common.utils.executor;

import com.qiniu.common.utils.spring.SpringUtils;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 */
public class AsyncExecutor {
    // 操作延迟10毫秒
    private final int OPERATE_DELAY_TIME = 10;

    // 异步操作任务调度线程池
    private ScheduledExecutorService executor = SpringUtils.getBean("scheduledExecutorService");

    // 单例模式
    private AsyncExecutor() {
    }

    private static AsyncExecutor instance = new AsyncExecutor();

    public static AsyncExecutor instance() {
        return instance;
    }

    /**
     * 延时 OPERATE_DELAY_TIME TimeUnit.MILLISECONDS 执行任务
     */
    public void execute(TimerTask task) {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() {
        Threads.shutdownAndAwaitTermination(executor);
    }
}
