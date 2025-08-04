package com.future.demo.config;

import com.future.demo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@Slf4j
public class ConfigKafkaListener {

    @Autowired
    StringRedisTemplate redisTemplate;

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @KafkaListener(topics = Constant.Topic1,
            groupId = "group-topic1",
            containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTopic1(List<String> messages) throws Exception {
        try {
            log.info("concurrent=" + this.concurrentCounter.incrementAndGet()
                    + ",size=" + messages.size()
                    + ",total=" + counter.addAndGet(messages.size()));

            // 这个计数器用于协助测试 Kafka auto.offset.reset 配置项目
            redisTemplate.opsForValue().increment(Constant.KeyConfigOptionAutoOffsetResetCounter, messages.size());

            /*TimeUnit.MILLISECONDS.sleep(1500);*/

            // 辅助测试失败重试
            /*boolean b = true;
            if (b) {
                throw new BusinessException("测试异常");
            }*/
        } finally {
            this.concurrentCounter.decrementAndGet();
        }
    }

    private AtomicInteger concurrentCount2 = new AtomicInteger();
    private AtomicLong counter2 = new AtomicLong();

    @KafkaListener(topics = Constant.Topic2,
            concurrency = "2",
            containerFactory = "topic2KafkaListenerContainerFactory")
    public void receiveMessageFromTopic2(List<String> messages) throws Exception {
        try {
            log.info("concurrent=" + this.concurrentCount2.incrementAndGet()
                    + ",size=" + messages.size()
                    + ",total=" + counter2.addAndGet(messages.size()));
        } finally {
            this.concurrentCount2.decrementAndGet();
        }
    }
}
