package com.future.demo.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.CassandraMapper;
import com.future.demo.mapper.CommonMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.CacheService;
import com.future.demo.service.OrderService;
import com.future.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.future.demo.constant.Const.TopicPublishChooseProductRandomlyForOrdering;


@Configuration
@Slf4j
public class ConfigKafkaListener {
    @Resource
    ObjectMapper objectMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    OrderService orderService;
    @Resource
    PrometheusCustomMonitor monitor;
    @Resource
    CommonMapper commonMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    CassandraMapper cassandraMapper;
    @Resource
    KafkaTemplate kafkaTemplate;
    @Resource
    ProductService productService;
    @Resource
    CacheService cacheService;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数
        factory.setConcurrency(256);
        // 绑定重试错误处理器
        factory.setCommonErrorHandler(retryErrorHandler(kafkaTemplate));
        return factory;
    }

    // 定义重试错误处理器（核心）
    @Bean
    public DefaultErrorHandler retryErrorHandler(KafkaTemplate<Object, Object> template) {
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

    /**
     * 随机抽取商品列表设置到缓存中
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = TopicPublishChooseProductRandomlyForOrdering)
    public void receiveMessage(List<String> messages) throws Exception {
        try {
            if (messages == null) {
                messages = new ArrayList<>();
            }

            List<ProductModel> modelList = new ArrayList<>();
            for (String message : messages) {
                List<ProductModel> modelListInternal = objectMapper.readValue(message, new TypeReference<List<ProductModel>>() {
                });
                if (!modelListInternal.isEmpty())
                    modelList.addAll(modelListInternal);
            }
            cacheService.setProductList(modelList);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
