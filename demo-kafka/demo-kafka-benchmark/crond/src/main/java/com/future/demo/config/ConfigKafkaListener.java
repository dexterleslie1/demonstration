package com.future.demo.config;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;
import java.util.concurrent.TimeUnit;
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

    AtomicInteger testAlterPartitionsOnlineConcurrentCounter = new AtomicInteger();
    AtomicInteger testAlterPartitionsOnlineCounter = new AtomicInteger();

    @KafkaListener(topics = Constant.TopicTestAlterPartitionsOnline,
            groupId = "group-topic-test-alter-partitions-online",
            containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTopicTestAlterPartitionsOnline(List<String> messages) throws Exception {
        try {
            log.info("concurrent=" + this.testAlterPartitionsOnlineConcurrentCounter.incrementAndGet()
                    + ",size=" + messages.size()
                    + ",total=" + testAlterPartitionsOnlineCounter.addAndGet(messages.size()));

            // 模拟业务延迟，以能够更加清晰地观察线程并发数
            int randomInt = RandomUtil.randomInt(1, 1000);
            TimeUnit.MILLISECONDS.sleep(randomInt);
        } finally {
            this.testAlterPartitionsOnlineConcurrentCounter.decrementAndGet();
        }
    }

    // 用于协助测试事务
    @KafkaListener(topics = Constant.TestAssistTransactionTopic1,
            groupId = Constant.TestAssistTransactionTopic1,
            concurrency = "128",
            containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTestAssistTransactionTopic1(List<String> messages) {
        redisTemplate.opsForValue().increment(Constant.TestAssistTransactionKeyCounterTopic1, messages.size());
    }

    @KafkaListener(topics = Constant.TestAssistTransactionTopic2,
            groupId = Constant.TestAssistTransactionTopic2,
            concurrency = "128",
            containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTestAssistTransactionTopic2(List<String> messages) {
        redisTemplate.opsForValue().increment(Constant.TestAssistTransactionKeyCounterTopic2, messages.size());
    }

    @KafkaListener(topics = Constant.TestAssistTransactionTopic3,
            groupId = Constant.TestAssistTransactionTopic3,
            concurrency = "128",
            containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTestAssistTransactionTopic3(List<String> messages) {
        redisTemplate.opsForValue().increment(Constant.TestAssistTransactionKeyCounterTopic3, messages.size());
    }
}
