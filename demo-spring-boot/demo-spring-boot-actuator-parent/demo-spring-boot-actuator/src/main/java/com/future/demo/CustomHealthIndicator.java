package com.future.demo;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * @author Dexterleslie.Chan
 */
@Component
public class CustomHealthIndicator extends AbstractHealthIndicator {
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        // 构建up状态
        builder.up().withDetail("detail1", "Detail value 1")
                .withDetail("detail2", "Detail value 2");

        // 构建down状态
        /*builder.down().withDetail("error", "Error value")
                .withException(new Exception("测试异常"));*/
    }
}
