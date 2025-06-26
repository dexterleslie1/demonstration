package com.future.demo.crond;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.config.PrometheusCustomMonitor;
import com.future.demo.dto.PreOrderDTO;
import com.future.demo.entity.OrderModel;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.future.demo.constant.Const.TopicName;


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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数
        factory.setConcurrency(256);
        return factory;
    }

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @KafkaListener(topics = TopicName)
    public void receiveMessage(List<String> messages) throws JsonProcessingException {
        try {
            log.info("concurrent=" + this.concurrentCounter.incrementAndGet() + ",size=" + messages.size() + ",total=" + counter.addAndGet(messages.size()));

            List<PreOrderDTO> preOrderDTOList = new ArrayList<>();
            for (String JSON : messages) {
                PreOrderDTO preOrderDTO = objectMapper.readValue(JSON, PreOrderDTO.class);
                preOrderDTOList.add(preOrderDTO);
            }

            List<OrderModel> orderModelList = this.orderService.createOrderModel(preOrderDTOList);
            this.orderService.insertBatch(orderModelList);

            this.redisTemplate.executePipelined(new SessionCallback<String>() {
                @Override
                public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                    try {
                        RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                        for (PreOrderDTO preOrderDTO : preOrderDTOList) {
                            /*log.info("Received message: " + new String(msg.getBody()));*/
                            long userId = preOrderDTO.getUserId();
                            long productId = preOrderDTO.getProductId();
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

}
