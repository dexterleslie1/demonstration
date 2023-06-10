package com.future.demo.canal;

import com.fasterxml.jackson.databind.JsonNode;
import com.yyd.common.json.JSONUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "my-queue", autoDelete = "false", durable = "true"),
        exchange = @Exchange(value = "my-exchange", type = ExchangeTypes.FANOUT, autoDelete = "false", durable = "true"),
        key = ""
), containerFactory = "rabbitListenerContainerFactory")
@Slf4j
public class Receiver {

    @Getter
    AtomicInteger counterInsert = new AtomicInteger();
    @Getter
    AtomicInteger counterDelete = new AtomicInteger();
    @Getter
    AtomicInteger counterUpdate = new AtomicInteger();

    @RabbitHandler
    public void receiveMessage(byte[] data) throws Exception {
        String message = new String(data, StandardCharsets.UTF_8);
        JsonNode jsonNode = JSONUtil.ObjectMapperInstance.readTree(message);
        String type = jsonNode.get("type").asText();
        if (type.equals("INSERT")) {
            this.counterInsert.incrementAndGet();
        } else if (type.equals("DELETE")) {
            this.counterDelete.incrementAndGet();
        } else if (type.equals("UPDATE")) {
            this.counterUpdate.incrementAndGet();
        }
    }

}
