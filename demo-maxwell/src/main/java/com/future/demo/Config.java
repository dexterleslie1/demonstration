package com.future.demo;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Maxwell RabbitMQ 交换机 / 队列名称（与 docker-compose 中 Maxwell 配置一致）
 * <p>
 * 批量 ListenerContainerFactory 与 {@link Receiver} 分开定义，
 * 避免同一 {@code @Configuration} 上既定义 factory 又挂 {@code @RabbitListener} 触发循环依赖。
 * </p>
 *
 * @author Dexterleslie.Chan
 */
@Configuration
public class Config {
    /** Maxwell --rabbitmq_exchange */
    public static final String ExchangeName = "maxwell";
    /** SpringBoot 消费者队列 */
    public static final String QueueName = "maxwell-queue";

    /** 应用层凑批大小 */
    public static final int BatchSize = 100;
    /** 并发消费者数 */
    public static final int ConcurrentConsumers = 3;
    /** 凑不满 BatchSize 时，等待多久后投递当前已攒到的一批 */
    public static final long BatchTimeoutMillis = 1000L;

    @Bean
    public SimpleRabbitListenerContainerFactory batchListenerContainerFactory(
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
        factory.setBatchSize(BatchSize);
        // prefetchCount：Broker 侧 QoS；需 >= batchSize，否则永远凑不满一批
        factory.setPrefetchCount(BatchSize);
        // 等待凑批的超时；超时后投递当前不足 BatchSize 的一批
        factory.setReceiveTimeout(BatchTimeoutMillis);
        factory.setConcurrentConsumers(ConcurrentConsumers);
        return factory;
    }
}
