package com.future.demo.crond;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.config.PrometheusCustomMonitor;
import com.future.demo.dto.IncreaseCountDTO;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.CassandraMapper;
import com.future.demo.mapper.CommonMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.future.demo.constant.Const.*;


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

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @KafkaListener(topics = TopicOrderInCacheSyncToDb)
    public void receiveMessage(List<String> messages) throws Exception {
        try {
            log.info("concurrent=" + this.concurrentCounter.incrementAndGet() + ",size=" + messages.size() + ",total=" + counter.addAndGet(messages.size()));

            List<OrderModel> orderModelList = new ArrayList<>();
            for (String JSON : messages) {
                OrderModel orderModel = objectMapper.readValue(JSON, OrderModel.class);
                orderModelList.add(orderModel);
            }

            this.orderService.fillupOrderRandomly(orderModelList);
            this.orderService.insertBatch(orderModelList);

            this.redisTemplate.executePipelined(new SessionCallback<String>() {
                @Override
                public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                    try {
                        RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                        for (OrderModel orderModel : orderModelList) {
                            /*log.info("Received message: " + new String(msg.getBody()));*/
                            long userId = orderModel.getUserId();
                            long productId = orderModel.getOrderDetailList().get(0).getProductId();
                            String userIdStr = String.valueOf(userId);
                            redisOperations.delete(userIdStr);

                            String key = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productId);
                            redisOperations.opsForSet().remove(key, userIdStr);
                        }

                        // 返回null即可，因为返回值会被管道的返回值覆盖，外层取不到这里的返回值
                        return null;
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            this.monitor.incrementOrderSyncCount(orderModelList.size());
        } catch (Exception ex) {
            throw ex;
        } finally {
            this.concurrentCounter.decrementAndGet();
        }
    }

    @KafkaListener(topics = TopicIncreaseCount)
    public void receiveMessageIncreaseCount(List<String> messages) throws Exception {
        List<IncreaseCountDTO> dtoListMySQL = new ArrayList<>();
        List<IncreaseCountDTO> dtoListCassandra = new ArrayList<>();
        for (String JSON : messages) {
            IncreaseCountDTO increaseCountDTO = objectMapper.readValue(JSON, IncreaseCountDTO.class);
            IncreaseCountDTO.Type type = increaseCountDTO.getType();
            if (type == IncreaseCountDTO.Type.MySQL) {
                dtoListMySQL.add(increaseCountDTO);
            } else if (type == IncreaseCountDTO.Type.Cassandra) {
                dtoListCassandra.add(increaseCountDTO);
            }
        }

        // MySQL计数器
        Map<String, Integer> flagToCountMap = new HashMap<>();
        for (IncreaseCountDTO dto : dtoListMySQL) {
            String flag = dto.getFlag();
            int count = dto.getCount();
            if (!flagToCountMap.containsKey(flag)) {
                flagToCountMap.put(flag, 0);
            }
            flagToCountMap.put(flag, flagToCountMap.get(flag) + count);
        }

        for (String flag : flagToCountMap.keySet()) {
            int count = flagToCountMap.get(flag);
            this.commonMapper.updateIncreaseCount(flag, count);
        }

        // Cassandra计数器
        flagToCountMap = new HashMap<>();
        for (IncreaseCountDTO dto : dtoListCassandra) {
            String flag = dto.getFlag();
            int count = dto.getCount();
            if (!flagToCountMap.containsKey(flag)) {
                flagToCountMap.put(flag, 0);
            }
            flagToCountMap.put(flag, flagToCountMap.get(flag) + count);
        }

        for (String flag : flagToCountMap.keySet()) {
            int count = flagToCountMap.get(flag);
            this.cassandraMapper.updateIncreaseCount(flag, count);
        }
    }

    /**
     * 创建订单 cassandra 索引
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = TopicCreateOrderCassandraIndex)
    public void receiveMessageCreateOrderCassandraIndex(List<String> messages) throws Exception {
        List<OrderModel> modelList = new ArrayList<>();
        for (String message : messages) {
            OrderModel orderModel = this.objectMapper.readValue(message, OrderModel.class);
            modelList.add(orderModel);
        }

        // 普通下单不需要设置merchantId，只有秒杀才需要设置merchantId
        boolean needToSetMerchantId = false;
        for (OrderModel model : modelList) {
            if (model.getMerchantId() == null) {
                needToSetMerchantId = true;
                break;
            }
        }
        if (needToSetMerchantId) {
            List<Long> productIdList = modelList.stream().map(o -> o.getOrderDetailList().get(0).getProductId()).distinct().collect(Collectors.toList());
            List<ProductModel> productModelList = this.productMapper.list(productIdList);
            Map<Long, ProductModel> productIdToModelMap = productModelList.stream().collect(Collectors.toMap(ProductModel::getId, o -> o));
            for (OrderModel model : modelList) {
                long productId = model.getOrderDetailList().get(0).getProductId();
                ProductModel productModel = productIdToModelMap.get(productId);
                model.setMerchantId(productModel.getMerchantId());
            }
        }

        // 建立 listByUserId Cassandra 索引
        cassandraMapper.insertBatchOrderIndexListByUserId(modelList);
        // 建立 listByMerchantId Cassandra 索引
        cassandraMapper.insertBatchOrderIndexListByMerchantId(modelList);

        // 异步更新 t_count
        IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO();
        increaseCountDTO.setType(IncreaseCountDTO.Type.Cassandra);
        increaseCountDTO.setFlag("orderListByUserId");
        increaseCountDTO.setCount(modelList.size());
        String JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
        kafkaTemplate.send(TopicIncreaseCount, JSON).get();

        increaseCountDTO = new IncreaseCountDTO();
        increaseCountDTO.setType(IncreaseCountDTO.Type.Cassandra);
        increaseCountDTO.setFlag("orderListByMerchantId");
        increaseCountDTO.setCount(modelList.size());
        JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
        kafkaTemplate.send(TopicIncreaseCount, JSON).get();
    }
}
