package com.future.demo;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
// 启用异步支持
// 启用@Async注解
@EnableAsync
public class ConfigAsync implements AsyncConfigurer {
    @Bean("myTaskExecutor") // 给线程池定义一个名字
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：即使空闲也会保留的线程数
        executor.setCorePoolSize(5);
        // 最大线程数：线程池能容纳的最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量：用于存放等待执行的任务的队列大小
        executor.setQueueCapacity(100);
        // 线程名前缀
        executor.setThreadNamePrefix("async-");
        // 初始化
        executor.initialize();
        return executor;
    }

    // 在异步方法中，异常不会直接抛给调用者。
    // 实现 AsyncConfigurer 接口，提供一个全局的 AsyncUncaughtExceptionHandler。
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            // 在这里处理未捕获的异常，比如记录日志、发送告警等
            System.err.println("异步方法 '" + method.getName() + "' 发生异常: " + ex.getMessage() + " 调用参数: " + params);
        };
    }
}
