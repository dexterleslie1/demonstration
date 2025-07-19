package com.future.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class ConfigKafkaListenerContainerFactory {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean("defaultConsumerFactory")
    public ConsumerFactory<String, String> defaultConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 从application.properties中获取Bootstrap Server（兼容原有配置）
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "${random.uuid}"); // 建议为不同主题设置不同Group ID（可选）
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 为Topic2单独设置max-poll-records
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1024);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean("defaultKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> defaultKafkaListenerContainerFactory(
            @Qualifier("defaultConsumerFactory") ConsumerFactory<String, String> consumerFactory,
            @Autowired DefaultErrorHandler retryErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(256);
        factory.setCommonErrorHandler(retryErrorHandler);
        return factory;
    }

    @Bean("topicCreateOrderCassandraIndexConsumerFactory")
    public ConsumerFactory<String, String> topicCreateOrderCassandraIndexConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 从application.properties中获取Bootstrap Server（兼容原有配置）
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "topicCreateOrderCassandraIndex"); // 建议为不同主题设置不同Group ID（可选）
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 为Topic2单独设置max-poll-records
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 128);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean("topicCreateOrderCassandraIndexKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> topicCreateOrderCassandraIndexKafkaListenerContainerFactory(
            @Qualifier("topicCreateOrderCassandraIndexConsumerFactory") ConsumerFactory<String, String> consumerFactory,
            @Autowired DefaultErrorHandler retryErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(512);
        factory.setCommonErrorHandler(retryErrorHandler);
        return factory;
    }

    // 定义重试错误处理器（核心）
    @Bean
    public DefaultErrorHandler retryErrorHandler() {
        // 配置重试策略：无限次重试，每次间隔5秒
        // 5000ms间隔，FixedBackOff.UNLIMITED_ATTEMPTS 表示无限次
        FixedBackOff fixedBackOff = new FixedBackOff(5000L, /*FixedBackOff.UNLIMITED_ATTEMPTS*/ 180);

        // 使用RetryTopic的ErrorHandler（自动处理重试和DLQ）
        return new DefaultErrorHandler(
                // 自定义恢复逻辑（可选，当重试耗尽时触发）
                (record, ex) -> {
                    log.error("重试耗尽，消息进入死信队列：{}", record.value());
                },
                fixedBackOff
        );
    }
}
