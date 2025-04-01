package com.future.demo.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CaffeineConfig {
    @Bean
    public Cache<String, String> caffeineInstance() {
        return Caffeine.newBuilder().build();
    }

    // 基于容量的 caffeine 实例
    @Bean
    public Cache<String, String> caffeineInstanceSizeBased() {
        // 缓存最大容量不超过 1 个元素
        return Caffeine.newBuilder().maximumSize(1).build();
    }

    // 基于时间的 caffeine 实例
    @Bean
    public Cache<String, String> caffeineInstanceTimeBased() {
        // 一个元素在上一次读写操作后一段时间之后，在指定的时间后没有被再次访问将会被认定为过期项。在当被缓存的元素时被绑定在一个session上时，当session因为不活跃而使元素过期的情况下，这是理想的选择。
        return Caffeine.newBuilder().expireAfterAccess(Duration.ofSeconds(1)).build();
    }
}
