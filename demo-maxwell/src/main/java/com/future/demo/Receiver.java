package com.future.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 批量消费 Maxwell 写入 RabbitMQ 的 CDC JSON 消息
 *
 * @author Dexterleslie.Chan
 */
@Component
public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** 已消费消息条数 */
    private final AtomicLong consumed = new AtomicLong();
    /** 批量回调次数 */
    private final AtomicLong batchCallbacks = new AtomicLong();
    /** 首条消息消费时间（epoch millis） */
    private final AtomicLong firstConsumeAt = new AtomicLong();
    /** 末条消息消费时间（epoch millis） */
    private final AtomicLong lastConsumeAt = new AtomicLong();

    public AtomicLong getConsumed() {
        return consumed;
    }

    public AtomicLong getBatchCallbacks() {
        return batchCallbacks;
    }

    public long getFirstConsumeAt() {
        return firstConsumeAt.get();
    }

    public long getLastConsumeAt() {
        return lastConsumeAt.get();
    }

    public void resetStats() {
        consumed.set(0);
        batchCallbacks.set(0);
        firstConsumeAt.set(0);
        lastConsumeAt.set(0);
    }

    @RabbitListener(
            containerFactory = "batchListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = Config.QueueName, durable = "true"),
                    exchange = @Exchange(
                            value = Config.ExchangeName,
                            type = ExchangeTypes.FANOUT,
                            durable = "true"
                    ),
                    key = ""
            )
    )
    public void receiveMessages(List<Message> messages, Channel channel) throws Exception {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (Message message : messages) {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            JsonNode json = OBJECT_MAPPER.readTree(body);
            String database = json.path("database").asText();
            String table = json.path("table").asText();
            String type = json.path("type").asText();
            JsonNode data = json.path("data");
            JsonNode old = json.path("old");

            // 性能测试时避免 INFO 刷屏拖慢吞吐，细节看 DEBUG
            if (logger.isDebugEnabled()) {
                logger.debug("CDC database={}, table={}, type={}, data={}, old={}",
                        database, table, type, data, old.isMissingNode() ? null : old);
            }
        }

        // 整批处理后对最后一条 deliveryTag 做 multiple=true 确认
        Message last = messages.get(messages.size() - 1);
        long deliveryTag = last.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, true);

        long now = System.currentTimeMillis();
        firstConsumeAt.compareAndSet(0, now);
        lastConsumeAt.set(now);
        long total = consumed.addAndGet(messages.size());
        long batches = batchCallbacks.incrementAndGet();

        if (logger.isDebugEnabled()) {
            logger.debug("batch consumed size={}, total={}, batches={}", messages.size(), total, batches);
        }
    }
}
