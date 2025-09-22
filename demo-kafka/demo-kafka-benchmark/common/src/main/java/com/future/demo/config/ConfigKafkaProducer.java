package com.future.demo.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigKafkaProducer {

    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapServers;

    // 有事务
    @Bean
    public ProducerFactory<String, String> producerFactoryWithTransaction() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 发送消息启用事务
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "tx-");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // 有事务
    @Bean
    public KafkaTemplate<String, String> kafkaTemplateWithTransaction() {
        return new KafkaTemplate<>(producerFactoryWithTransaction());
    }

    // 非事务
    @Bean
    public ProducerFactory<String, String> producerFactoryWithoutTransaction() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // 非事务
    @Bean
    public KafkaTemplate<String, String> kafkaTemplateWithoutTransaction() {
        return new KafkaTemplate<>(producerFactoryWithoutTransaction());
    }
}