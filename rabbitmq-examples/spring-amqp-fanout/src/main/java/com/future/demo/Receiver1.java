package com.future.demo;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Component
@RabbitListener(bindings = @QueueBinding(
        // 注意：spring-amqp+rabbitmq不能使用命名queue实现广播，
        // 经过测试两个jvm进程使用同名queue绑定到exchange，
        // rabbitmq系统会采用ribbon负载均衡方式一次只能有其中一个同名queue接收到消息，
        // 想要实现消息广播，需要使用AnonymousQueue绑定到exchange，
        // 在此情况每个AnonymousQueue都能够接收到到消息
        //return new Queue(queueName, false, false, true);
        value = @Queue(durable = "false", exclusive = "true", autoDelete = "true"),
        exchange = @Exchange(value = Config.ExchangeName, type = ExchangeTypes.FANOUT, autoDelete = "true")
))
public class Receiver1 {
    private static final Logger logger = LoggerFactory.getLogger(Receiver1.class);

    @Resource
    AtomicInteger counter;

    @RabbitHandler
    public void receiveMessage(String message,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        logger.info("Received <" + message + ">");
        counter.incrementAndGet();
        channel.basicAck(deliveryTag, false);
    }

}
