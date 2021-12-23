package com.future.demo.quartz;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

/**
 * Disable @EnableScheduling on Spring Tests
 * https://stackoverflow.com/questions/29014496/disable-enablescheduling-on-spring-tests
 */
@ConditionalOnProperty(
        value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
)
@Configuration
@EnableScheduling
public class ConfigQuartz implements SchedulingConfigurer {
    // 配置spring scheduling核心执行线程，spring默认执行线程数为1
    // https://stackoverflow.com/questions/29796651/what-is-the-default-scheduler-pool-size-in-spring-boot
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
