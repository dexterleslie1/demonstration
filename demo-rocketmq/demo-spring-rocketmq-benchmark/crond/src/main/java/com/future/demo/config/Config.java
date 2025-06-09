package com.future.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class Config {
    public final static String ProducerAndConsumerGroup = "demo-producer-and-consumer-group";

    @Value("${namesrvaddr}")
    String namesrvaddr;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQPushConsumer consumer(AtomicInteger counter,
                                          Map<Integer, Integer> batchSizeToCountMap) throws MQClientException {
        // 创建消费者实例，并设置消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(ProducerAndConsumerGroup);
        // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
        consumer.setNamesrvAddr(this.namesrvaddr);
        // 订阅指定的主题和标签（* 表示所有标签）
        consumer.subscribe("TestTopic", "*");

        // 设置单个批次最大消息数量为1024，下面两个设置需要同时设置，否则设置无效
        consumer.setConsumeMessageBatchMaxSize(1024);
        consumer.setPullBatchSize(1024);

        // 设置并发线程数
        consumer.setConsumeThreadMin(16);
        consumer.setConsumeThreadMax(128);

        // 注册消息监听器
        Object objectMutex = new Object();
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                /*log.info("Received message: " + new String(msg.getBody()));*/
                counter.incrementAndGet();
            }

            // 模拟业务延迟，否则批量处理的每个批次消息量会很小
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (Exception ignored) {

            }

            synchronized (objectMutex) {
                int batchSize = msgs.size();
                if (!this.batchSizeToCountMap().containsKey(batchSize)) {
                    this.batchSizeToCountMap().put(batchSize, 0);
                }
                this.batchSizeToCountMap().put(batchSize, this.batchSizeToCountMap().get(batchSize) + 1);
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        return consumer;
    }

    @Bean
    public AtomicInteger counter() {
        return new AtomicInteger();
    }

    /**
     * batch size 的统计数
     *
     * @return
     */
    @Bean
    public Map<Integer, Integer> batchSizeToCountMap() {
        return new HashMap<>();
    }
}
