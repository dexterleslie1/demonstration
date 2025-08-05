package com.future.demo.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Configuration
@Slf4j
public class ConfigKafkaListener {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory,
            @Autowired DefaultErrorHandler retryErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数，需要设置 topic 分区数不为 0 才能并发消费消息。
        factory.setConcurrency(256);
        factory.setCommonErrorHandler(retryErrorHandler);
        return factory;
    }

    // 定义重试错误处理器（核心）
    @Bean
    public DefaultErrorHandler retryErrorHandler() {
        // 配置重试策略：无限次重试，每次间隔5秒
        // 5000ms间隔，FixedBackOff.UNLIMITED_ATTEMPTS 表示无限次
        FixedBackOff fixedBackOff = new FixedBackOff(5000L, FixedBackOff.UNLIMITED_ATTEMPTS);

        // 使用RetryTopic的ErrorHandler（自动处理重试和DLQ）
        return new DefaultErrorHandler(
                // 自定义恢复逻辑（可选，当重试耗尽时触发）
                (record, ex) -> {
                    log.error("重试耗尽，消息进入死信队列：{}", record.value());
                },
                fixedBackOff
        );
    }

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @Resource
    ObjectMapper objectMapper;
    Consumer<List<JsonNode>> callback = null;

    public void registerCallback(Consumer<List<JsonNode>> callback) {
        this.callback = callback;
    }

    @KafkaListener(topics = "demo-debezium.demo.t_user")
    public void receiveMessage(List<String> messages) throws Exception {
        String lastMessage = null;
        try {
            log.info("concurrent=" + this.concurrentCounter.incrementAndGet() + ",size=" + messages.size() + ",total=" + counter.addAndGet(messages.size()));
            /*log.info("接收到消息列表 {}", messages);*/

            List<JsonNode> jsonNodeList = new ArrayList<>();
            for (String message : messages) {
                // 删除操作时会有一条 null 的消息。
                if (StringUtils.isBlank(message))
                    continue;
                lastMessage = message;
                JsonNode jsonNode = objectMapper.readTree(message);
                jsonNodeList.add(jsonNode);
            }

            if (callback != null) {
                callback.accept(jsonNodeList);
            }

            /*TimeUnit.MILLISECONDS.sleep(1000);*/
        } catch (Exception ex) {
            log.error("解析 json 时异常，原因 {} json {}", ex.getMessage(), lastMessage, ex);
            throw ex;
        } finally {
            this.concurrentCounter.decrementAndGet();
        }
    }
}
