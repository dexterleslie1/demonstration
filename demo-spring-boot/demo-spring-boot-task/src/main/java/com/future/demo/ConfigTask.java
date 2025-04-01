package com.future.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Disable @EnableScheduling on Spring Tests
 * https://stackoverflow.com/questions/29014496/disable-enablescheduling-on-spring-tests
 */
@ConditionalOnProperty(
        value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
)
@Configuration
@EnableScheduling
public class ConfigTask {
    // 配置spring scheduling核心执行线程，spring默认执行线程数为1
    // https://stackoverflow.com/questions/29796651/what-is-the-default-scheduler-pool-size-in-spring-boot
    /*@Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        return scheduler;
    }*/
}
