package com.future.demo.config;

import io.lettuce.core.ReadFrom;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean
    LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        // 优先从 slave 节点读取，如果没有可用 slave 节点则从 master 节点读取
        return clientConfigurationBuilder -> clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }
}
