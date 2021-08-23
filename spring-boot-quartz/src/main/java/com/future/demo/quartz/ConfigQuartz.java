package com.future.demo.quartz;

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
public class ConfigQuartz {
}
