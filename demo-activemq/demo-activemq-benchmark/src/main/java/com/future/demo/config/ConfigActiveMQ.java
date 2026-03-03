package com.future.demo.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigActiveMQ {

    // 手动配置 ActiveMQ 连接工厂（非必须，Spring Boot 自动配置已足够）
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://localhost:61616");
        factory.setUserName("admin");
        factory.setPassword("admin");
        // 启用异步发送（与配置文件中的 async-send 一致）
        factory.setUseAsyncSend(true);
        return factory;
    }

    // 连接池（若启用 pool.enabled=true，Spring Boot 会自动使用 PooledConnectionFactory）
    @Bean
    public JmsPoolConnectionFactory pooledConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        JmsPoolConnectionFactory pool = new JmsPoolConnectionFactory();
        pool.setConnectionFactory(activeMQConnectionFactory);
        // 最大连接数
        pool.setMaxConnections(512);
        // 空闲超时时间（毫秒）
        pool.setConnectionIdleTimeout(30000);
        return pool;
    }
}
