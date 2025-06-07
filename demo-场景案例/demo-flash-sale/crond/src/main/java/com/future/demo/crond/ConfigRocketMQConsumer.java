package com.future.demo.crond;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.dto.PreOrderDTO;
import com.future.demo.entity.OrderModel;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ConfigRocketMQConsumer {
    public final static String ProducerAndConsumerGroup = "demo-producer-and-consumer-group";

    @Value("${namesrvaddr}")
    String namesrvaddr;

    @Resource
    ObjectMapper objectMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderService orderService;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQPushConsumer consumer() throws MQClientException {
        // 创建消费者实例，并设置消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(ProducerAndConsumerGroup);
        // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
        consumer.setNamesrvAddr(this.namesrvaddr);
        // 订阅指定的主题和标签（* 表示所有标签）
        consumer.subscribe(com.future.demo.config.ConfigRocketMQ.ProducerAndConsumerGroup, "*");
        // 支持批量处理消息
        consumer.setConsumeMessageBatchMaxSize(256);
        // 设置并发线程数
        consumer.setConsumeThreadMin(16);
        consumer.setConsumeThreadMax(64);

        // 注册消息监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            List<PreOrderDTO> preOrderDTOList = new ArrayList<>();
            try {
                for (MessageExt messageExt : msgs) {
                    String JSON = new String(messageExt.getBody(), StandardCharsets.UTF_8);
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

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }

            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        });
        return consumer;
    }

}
