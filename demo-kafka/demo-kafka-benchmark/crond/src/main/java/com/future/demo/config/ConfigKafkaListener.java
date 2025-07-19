package com.future.demo.config;

import com.future.demo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@Slf4j
public class ConfigKafkaListener {

    /*@Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory,
            @Autowired DefaultErrorHandler retryErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数，需要设置 topic 分区数不为 0 才能并发消费消息。
        factory.setConcurrency(256);
        // 绑定重试错误处理器
        factory.setCommonErrorHandler(retryErrorHandler);
        return factory;
    }*/

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @KafkaListener(topics = Constant.Topic1, containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTopic1(List<String> messages) throws Exception {
        try {
            log.info("concurrent=" + this.concurrentCounter.incrementAndGet()
                    + ",size=" + messages.size()
                    + ",total=" + counter.addAndGet(messages.size()));

            TimeUnit.MILLISECONDS.sleep(1500);

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

    @KafkaListener(topics = Constant.Topic2, concurrency = "2", containerFactory = "topic2KafkaListenerContainerFactory")
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
