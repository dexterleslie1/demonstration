package com.future.demo.config;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;
import java.util.stream.Collectors;

@Configuration
// 启用 RedisProperties 自动绑定
@EnableConfigurationProperties(RedisProperties.class)
public class ConfigRedis {
    private final RedisProperties redisProperties;

    public ConfigRedis(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // 设置拓扑自动刷新
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                // 启用定期拓扑刷新（可选，每 120 秒刷新一次）
                .enablePeriodicRefresh(Duration.ofSeconds(120))
                // 自适应刷新超时时间（最长等待 10 秒）
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(10))
                // 触发自适应刷新的事件（MOVED 重定向、持续重连失败）
                .enableAdaptiveRefreshTrigger(
                        ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT,
                        ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(topologyRefreshOptions)
                .build();

        // 设置 Lettuce 连接池
        // 从 RedisProperties 中提取 Lettuce 连接池配置
        RedisProperties.Pool poolProperties = redisProperties.getLettuce().getPool();
        // 构建 Lettuce 连接池配置（关键：绑定 max-active、min-idle 等参数）
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig() {{
            // 最大活跃连接数（对应 application.properties 的 max-active）
            this.setMaxTotal(poolProperties.getMaxActive());
            // 最小空闲连接数（对应 min-idle）
            this.setMinIdle(poolProperties.getMinIdle());
            // 最大空闲连接数（可选）
            this.setMaxIdle(poolProperties.getMaxIdle());
            // 最大等待时间（可选）
            this.setMaxWaitMillis(poolProperties.getMaxWait().toMillis());
        }};

        LettucePoolingClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .clientOptions(clusterClientOptions)
                .poolConfig(poolConfig)
                .build();

        RedisClusterConfiguration configuration = new RedisClusterConfiguration();
        configuration.setClusterNodes(
                redisProperties.getCluster().getNodes().stream().map(o -> new RedisNode(o.split(":")[0], Integer.parseInt(o.split(":")[1])))
                        .collect(Collectors.toList()));
        configuration.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());

        return new LettuceConnectionFactory(configuration, clientConfiguration);
    }
}
