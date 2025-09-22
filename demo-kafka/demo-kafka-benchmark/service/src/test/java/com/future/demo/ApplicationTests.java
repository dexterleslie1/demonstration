package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.demo.config.ConfigKafkaListener;
import com.future.demo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = {ApplicationService.class})
@Slf4j
public class ApplicationTests {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplateWithoutTransaction;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplateWithTransaction;

    @Resource
    ConfigKafkaListener configKafkaListener;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 提示：测试时不能启动 crond 服务
     *
     * @throws InterruptedException
     */
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
                try {
                    int count;
                    List<ListenableFuture<SendResult<String, String>>> futureList = new ArrayList<>();
                    while ((count = counter.getAndIncrement()) <= totalMessageCount) {
                        ListenableFuture<SendResult<String, String>> future =
                                kafkaTemplateWithoutTransaction.send(Constant.TopicTestSendPerf, String.valueOf(count));

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
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
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

    /**
     * 测试事务
     * 提示：需要启动 crond 配合测试
     */
    @Test
    public void testTransaction() throws InterruptedException {
        redisTemplate.delete(Arrays.asList(
                Constant.TestAssistTransactionKeyCounterTopic1,
                Constant.TestAssistTransactionKeyCounterTopic2,
                Constant.TestAssistTransactionKeyCounterTopic3));

        try {
            String uuidStr = UUID.randomUUID().toString();
            String finalUuidStr = uuidStr;
            kafkaTemplateWithTransaction.executeInTransaction(t -> {
                List<ListenableFuture> futureListInternal = new ArrayList<>();
                try {
                    futureListInternal.add(t.send(Constant.TestAssistTransactionTopic1, finalUuidStr));

                    // 模拟 topic2、topic3 消息发送失败情况
                    boolean b = true;
                    if (b) {
                        throw new BusinessException("测试异常");
                    }

                    futureListInternal.add(t.send(Constant.TestAssistTransactionTopic2, finalUuidStr));
                    futureListInternal.add(t.send(Constant.TestAssistTransactionTopic3, finalUuidStr));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                } finally {
                    if (!futureListInternal.isEmpty()) {
                        int index = -1;
                        try {
                            for (int i = 0; i < futureListInternal.size(); i++) {
                                index = i;
                                futureListInternal.get(i).get();
                            }
                        } catch (Exception ex) {
                            log.error("发送Kafka消息失败，原因：{}，出错的 futureList 索引为 {}", ex.getMessage(), index, ex);
                            throw new RuntimeException(ex);
                        }
                    }
                }
                return null;
            });
            Assertions.fail();
        } catch (Exception ex) {
            Assertions.assertTrue(ex.getCause() instanceof BusinessException);
            BusinessException businessException = (BusinessException) ex.getCause();
            Assertions.assertEquals("测试异常", businessException.getErrorMessage());
        }

        TimeUnit.SECONDS.sleep(5);
        String counter1 = redisTemplate.opsForValue().get(Constant.TestAssistTransactionKeyCounterTopic1);
        String counter2 = redisTemplate.opsForValue().get(Constant.TestAssistTransactionKeyCounterTopic2);
        String counter3 = redisTemplate.opsForValue().get(Constant.TestAssistTransactionKeyCounterTopic3);
        Assertions.assertNull(counter1);
        Assertions.assertNull(counter2);
        Assertions.assertNull(counter3);
    }

}
