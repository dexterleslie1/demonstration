package com.future.demo;

import com.future.demo.config.ConfigKafkaListener;
import com.future.demo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = {ApplicationService.class})
@Slf4j
public class ApplicationTests {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    ConfigKafkaListener configKafkaListener;

    @Test
    public void contextLoads() throws InterruptedException {
        // 测试异步发送消息以提高消息发送效率

        int totalMessageCount = 1000000;
        int concurrentThreads = 32;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        AtomicInteger counter = new AtomicInteger();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                int count;
                List<ListenableFuture<SendResult<String, String>>> futureList = new ArrayList<>();
                while ((count = counter.getAndIncrement()) <= totalMessageCount) {
                    ListenableFuture<SendResult<String, String>> future =
                            kafkaTemplate.send(Constant.Topic1, String.valueOf(count));

                    // 发送效率低，发送一个消息等待一个消息发送结果响应
                    /*try {
                        future.get();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }*/

                    // 发送效率高，发送多个消息后异步等待多个消息发送结果响应
                    futureList.add(future);
                    if (futureList.size() >= 1024 || count >= totalMessageCount) {
                        for (ListenableFuture<SendResult<String, String>> futureInternal : futureList) {
                            try {
                                futureInternal.get();
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                throw new RuntimeException(e);
                            }
                        }
                        futureList = new ArrayList<>();
                    }
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        stopWatch.stop();
        log.info("发送 {} 个消息耗时 {} 毫秒", totalMessageCount, stopWatch.getTotalTimeMillis());

        // 等待完成消费所有消息
        TimeUnit.SECONDS.sleep(2);
        Assertions.assertTrue(configKafkaListener.List.size() >= totalMessageCount, "接收到的消息个数为 " + configKafkaListener.List.size());

        // endregion
    }

}
