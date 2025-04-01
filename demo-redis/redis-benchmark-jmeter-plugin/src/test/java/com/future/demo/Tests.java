//package com.future.demo;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//import java.util.UUID;
//
//public class Tests {
//
//    RedisConnectionFactory connectionFactory;
//    StringRedisTemplate redisTemplate;
//
//    @Before
//    public void before() {
//        String host = "localhost";
//        int port = 6379;
//        String password = "123456";
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
//        configuration.setPassword(password);
//        connectionFactory = new LettuceConnectionFactory(configuration);
//        ((LettuceConnectionFactory)this.connectionFactory).afterPropertiesSet();
//        redisTemplate = new StringRedisTemplate(connectionFactory);
//        redisTemplate.afterPropertiesSet();
//    }
//
//    @After
//    public void after() throws Exception {
//        if (this.connectionFactory != null) {
//            ((DisposableBean) this.connectionFactory).destroy();
//            this.connectionFactory = null;
//        }
//    }
//
//    @Test
//    public void test() {
//        String key = UUID.randomUUID().toString();
//        this.redisTemplate.opsForValue().set(key, key);
//    }
//}
