package com.future.demo;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 性能测试批量消费的 ListenerContainerFactory。
 * <p>
 * 必须和 {@link BenchmarkDirectReceiver} 分开：同一 {@code @Configuration} 上既定义 factory
 * 又挂 {@code @RabbitListener} 会触发循环依赖。
 * </p>
 */
@Configuration
public class BenchmarkBatchListenerConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory benchmarkBatchListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 手动 ACK：整批处理后对最后一条 deliveryTag 做 multiple=true 确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 启用消费者批量投递，监听方法入参为 List
        factory.setConsumerBatchEnabled(true);
        // 声明监听器是批量类型：适配器按 List 调用方法，而不是单条 Message
        factory.setBatchListener(true);
        // batchSize：应用层凑批大小，容器攒满这么多条（或等 receiveTimeout）后一次回调 List
        factory.setBatchSize(BenchmarkDirectReceiver.BatchSize);
        // prefetchCount：Broker 侧 QoS，每个消费者最多未确认多少条；需 >= batchSize，否则永远凑不满一批
        factory.setPrefetchCount(BenchmarkDirectReceiver.BatchSize);
        // 等待凑批的超时；超时后投递当前不足 BatchSize 的一批
        factory.setReceiveTimeout(BenchmarkDirectReceiver.BatchTimeoutMillis);
        // 并发消费者数：同一队列启动多少个消费者线程并行拉取/回调
        factory.setConcurrentConsumers(BenchmarkDirectReceiver.ConcurrentConsumers);
        return factory;
    }
}
