package com.future.demo;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
//        configuration.setPassword("123456");
//        return new JedisConnectionFactory(configuration);
//    }

//    @Bean
//    public LettuceConnectionFactory lettuceConnectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
//        configuration.setPassword("123456");
//        return new LettuceConnectionFactory(configuration);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
////        template.setConnectionFactory(jedisConnectionFactory());
//        template.setConnectionFactory(lettuceConnectionFactory());
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//        return template;
//    }
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
//        poolConfig.setMaxTotal(512);
//        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
//                .poolConfig(poolConfig)
//                .clientResources(ClientResources.builder().build())
//                .clientOptions(ClientOptions.builder().build())
//                .build();
//
//        RedisClusterConfiguration configuration = new RedisClusterConfiguration()
//                .clusterNode("192.168.1.185", 6380)
//                .clusterNode("192.168.1.185", 6381)
//                .clusterNode("192.168.1.185", 6382);
//        return new LettuceConnectionFactory(configuration, clientConfig);
//    }
}
