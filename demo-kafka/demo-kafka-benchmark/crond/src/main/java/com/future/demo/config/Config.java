package com.future.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.future.demo.constant.Constant.TopicName;

@Configuration
@Slf4j
public class Config {
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // 根据你的 Kafka 服务器地址设置
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "your-group-id"); // 设置消费者组 ID
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000); // 设置每次 poll 返回的最大记录数
//        return new DefaultKafkaConsumerFactory<>(props);
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数，需要设置 topic 分区数不为 0 才能并发消费消息。
        factory.setConcurrency(256);
        return factory;
    }

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @KafkaListener(topics = TopicName)
    public void receiveMessage(List<String> messages) throws InterruptedException {
        log.info("concurrent=" + this.concurrentCounter.incrementAndGet() + ",size=" + messages.size() + ",total=" + counter.addAndGet(messages.size()));

        TimeUnit.MILLISECONDS.sleep(500);

        this.concurrentCounter.decrementAndGet();
    }

}
