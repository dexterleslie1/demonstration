package com.future.demo.pkg2;

import org.springframework.context.annotation.Bean;

// 用于创建LocalCacheService bean的配置
public class MyLocalCacheConfiguration {
    @Bean
    CacheService cacheService() {
        return new LocalCacheService();
    }
}
