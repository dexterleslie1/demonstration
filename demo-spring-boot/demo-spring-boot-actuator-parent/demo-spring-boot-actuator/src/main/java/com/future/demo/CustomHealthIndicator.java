package com.future.demo;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * @author Dexterleslie.Chan
 */
@Component
public class CustomHealthIndicator extends AbstractHealthIndicator {
    // Health 里的 ping 是 Spring Boot Actuator 自带的 PingHealthIndicator。
    // 作用：最轻量的存活检查，只表示“应用已启动、上下文在运行”，不做数据库、Redis 等依赖检查。
    // 状态：永远返回 UP（只要应用能正常启动）。
    // 用途：适合给负载均衡、K8s 的 liveness 等做“进程是否活着”的探测，响应快、不依赖外部服务
    // 如果不想在 health 的 components 里看到 ping，可以在配置里关掉：application.propertiesmanagement.health.ping.enabled=false
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        // 构建多个组件的健康状态
        // 提示：不需要手动探测db状态，因为有内置的indicator了
        Health dbHealth = Health.up()
                .withDetail("version", "5.7.0")
                .withDetail("schema", "demo")
                .build();

        // 提示：不需要手动探测db状态，因为有内置的indicator了
        Health redisHealth = Health.down()
                .withDetail("error", "Redis 连接失败")
                .build();

        Health mqHealth = Health.up()
                .withDetail("lag", "0")
                .build();

        // 聚合多个组件的健康状态
        builder.down()
                .withDetail("database", dbHealth)
                .withDetail("redis", redisHealth)
                .withDetail("messageQueue", mqHealth)
                .withException(new Exception("整体健康状态降级，某些组件异常"));
    }
}
