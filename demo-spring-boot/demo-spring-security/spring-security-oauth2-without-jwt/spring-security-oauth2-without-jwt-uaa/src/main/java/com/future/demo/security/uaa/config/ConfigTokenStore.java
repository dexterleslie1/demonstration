package com.future.demo.security.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
public class ConfigTokenStore {

    // 配置令牌存在的地方
    // 支持InMemoryTokenStore、JdbcTokenStore、RedisTokenStore、JwtTokenStore、JwkTokenStore方式
    @Bean
    TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

}
