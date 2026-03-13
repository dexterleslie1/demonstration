package com.future.demo.config;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * @author Dexterleslie.Chan
 */
@Component
public class CustomHealthIndicator extends AbstractHealthIndicator {
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        Health mqHealth = Health.up()
                .withDetail("lag", "0")
                .build();

        // 聚合多个组件的健康状态
        /*builder.down()
                .withDetail("database", dbHealth)
                .withDetail("redis", redisHealth)
                .withDetail("messageQueue", mqHealth)
                .withException(new Exception("整体健康状态降级，某些组件异常"));*/
        builder.up()
                .withDetail("messageQueue", mqHealth);
    }
}
