package com.future.demo.security.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
public class ConfigTokenStore {

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    // 配置令牌存在的地方
    // 支持InMemoryTokenStore、JdbcTokenStore、RedisTokenStore、JwtTokenStore、JwkTokenStore方式
    @Bean
    TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
        // return new InMemoryTokenStore();
    }

}
