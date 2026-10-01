package com.future.demo;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能测试专用 Direct 交换机消费者。
 * <p>
 * 使用 {@link org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer}
 * 的 consumer batch（Spring AMQP 2.2+）：监听方法一次收到一批 {@link Message}。
 * DirectMessageListenerContainer 不支持 consumer batch，因此容器类型用 Simple。
 * 独立队列，不和 {@link Receiver1}、{@link Receiver2} 抢 {@link Config#QueueName}。
 * </p>
 *
 * @author Dexterleslie.Chan
 */
@Component
public class BenchmarkDirectReceiver {
    private static final Logger logger = LoggerFactory.getLogger(BenchmarkDirectReceiver.class);

    public static final String ExchangeName = "spring-amqp-direct-benchmark-exchange";
    public static final String QueueName = "direct-benchmark-queue";
    public static final String RoutingKey = "benchmarkRoutingKey";
    public static final int BatchSize = 1024;
    public static final int ConcurrentConsumers = 4;
    /** 凑不满 BatchSize 时，等待多久后投递当前已攒到的一批 */
    public static final long BatchTimeoutMillis = 1000L;

    private final AtomicLong consumed = new AtomicLong();

    @RabbitListener(
            containerFactory = "benchmarkBatchListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = QueueName, durable = "false", autoDelete = "true"),
                    exchange = @Exchange(value = ExchangeName, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
                    key = RoutingKey
            )
    )
    public void receiveMessages(List<Message> messages, Channel channel) throws IOException {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        Message last = messages.get(messages.size() - 1);
        String consumerTag = last.getMessageProperties().getConsumerTag();
        long deliveryTag = last.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, true);
        long total = this.consumed.addAndGet(messages.size());
        if (logger.isDebugEnabled()) {
            logger.debug("consumer={} batch consumed {}, total {}", consumerTag, messages.size(), total);
        }
    }
}
