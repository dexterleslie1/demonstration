package com.future.demo.captcha;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCacheManager {
    @Bean
    public CacheManagera cacheManager() throws Exception {
        return new CacheManagera("cacheManagerA");
    }
}
