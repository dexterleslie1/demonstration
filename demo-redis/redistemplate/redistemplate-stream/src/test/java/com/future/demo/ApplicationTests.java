package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate = null;

    // 不结合消费者组使用 stream
    @Test
    public void testUsageWithoutGroup() throws InterruptedException {
        String streamKey = UUID.randomUUID().toString();

        int totalMessageCount = 5;
        // 发送消息
        for (int i = 0; i < totalMessageCount; i++) {
            int finalI = i;
            this.redisTemplate.opsForStream().add(MapRecord.create(streamKey, new HashMap<String, String>() {{
                put("k" + finalI, "v" + finalI);
            }}));
        }

        AtomicInteger readCounter = new AtomicInteger();

        // 演示多个消费者会重复消费同一个消息
        ExecutorService executor = Executors.newCachedThreadPool();
        int concurrentThreads = 2;
        for (int i = 0; i < concurrentThreads; i++) {
            executor.submit(() -> {
                StreamOffset<String> offset = StreamOffset.create(streamKey, ReadOffset.from("0"));
                while (true) {
                    StreamReadOptions readOptions = StreamReadOptions.empty().count(1);
                    List<MapRecord<String, Object, Object>> recordList = redisTemplate.opsForStream().read(readOptions, offset);
                    if (recordList == null || recordList.isEmpty()) {
                        break;
                    }

                    recordList.forEach(o -> readCounter.incrementAndGet());
                    offset = StreamOffset.create(streamKey, ReadOffset.from(recordList.get(0).getId()));
                }
            });
        }
        executor.shutdown();
        while (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        Assert.assertEquals(totalMessageCount * concurrentThreads, readCounter.get());
    }

    // 模拟 RabbitMQ 的订阅模式
    @Test
    public void testSimRabbitMQSubscriptionMode() throws InterruptedException {
        String streamKey = UUID.randomUUID().toString();
        String groupKey1 = UUID.randomUUID().toString();
        String groupKey2 = UUID.randomUUID().toString();
        AtomicInteger group1ReadCounter = new AtomicInteger();
        AtomicInteger group2ReadCounter = new AtomicInteger();

        // 创建 stream，todo 寻找更加优雅的方法
        RecordId recordId = this.redisTemplate.opsForStream().add(MapRecord.create(streamKey, new HashMap<String, String>() {{
            put("k1", "v1");
        }}));
        Long deleteResult = this.redisTemplate.opsForStream().delete(streamKey, recordId);
        Assert.assertEquals(1L, deleteResult.longValue());

        // 创建消费者组
        String result = this.redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), groupKey1);
        Assert.assertEquals("OK", result);
        result = this.redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), groupKey2);
        Assert.assertEquals("OK", result);
        List<String> consumerGroupList = Arrays.asList(groupKey1, groupKey2);

        int totalMessageCount = 5;
        // 发送消息
        for (int i = 0; i < totalMessageCount; i++) {
            int finalI = i;
            this.redisTemplate.opsForStream().add(MapRecord.create(streamKey, new HashMap<String, String>() {{
                put("k" + finalI, "v" + finalI);
            }}));
        }

        // 演示多个消费者也不会重复消费同一个消息
        ExecutorService executor = Executors.newCachedThreadPool();
        int concurrentThreads = 32;
        for (String groupKey : consumerGroupList) {
            for (int i = 0; i < concurrentThreads; i++) {
                executor.submit(() -> {
                    String consumerKey = UUID.randomUUID().toString();
                    while (true) {
                        List<MapRecord<String, Object, Object>> recordList = this.redisTemplate.opsForStream().read(
                                Consumer.from(groupKey, consumerKey)
                                // block(Duration.ofSeconds(0)) 表示阻塞等待，直到有消息才返回
                                , StreamReadOptions.empty().count(1).block(Duration.ofSeconds(1))
                                , StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
                        if (recordList != null && !recordList.isEmpty()) {
                            if (groupKey.equals(groupKey1)) {
                                group1ReadCounter.incrementAndGet();
                            } else if (groupKey.equals(groupKey2)) {
                                group2ReadCounter.incrementAndGet();
                            }
                        } else {
                            break;
                        }
                    }
                });
            }
        }
        executor.shutdown();
        while (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        Assert.assertEquals(totalMessageCount, group1ReadCounter.get());
        Assert.assertEquals(totalMessageCount, group1ReadCounter.get());
    }

    // 模拟 RabbitMQ 的工作模式
    @Test
    public void testSimRabbitMQWorkerMode() throws InterruptedException {
        String streamKey = UUID.randomUUID().toString();
        String groupKey = UUID.randomUUID().toString();

        // 创建 stream，todo 寻找更加优雅的方法
        RecordId recordId = this.redisTemplate.opsForStream().add(MapRecord.create(streamKey, new HashMap<String, String>() {{
            put("k1", "v1");
        }}));
        Long deleteResult = this.redisTemplate.opsForStream().delete(streamKey, recordId);
        Assert.assertEquals(1L, deleteResult.longValue());

        // 创建消费者组
        String result = this.redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), groupKey);
        Assert.assertEquals("OK", result);

        int totalMessageCount = 5;
        // 发送消息
        for (int i = 0; i < totalMessageCount; i++) {
            int finalI = i;
            this.redisTemplate.opsForStream().add(MapRecord.create(streamKey, new HashMap<String, String>() {{
                put("k" + finalI, "v" + finalI);
            }}));
        }

        AtomicInteger readCounter = new AtomicInteger();

        // 演示多个消费者也不会重复消费同一个消息
        ExecutorService executor = Executors.newCachedThreadPool();
        int concurrentThreads = 32;
        for (int i = 0; i < concurrentThreads; i++) {
            executor.submit(() -> {
                String consumerKey = UUID.randomUUID().toString();
                while (true) {
                    List<MapRecord<String, Object, Object>> recordList = this.redisTemplate.opsForStream().read(
                            Consumer.from(groupKey, consumerKey)
                            // block(Duration.ofSeconds(0)) 表示阻塞等待，直到有消息才返回
                            , StreamReadOptions.empty().count(1).block(Duration.ofSeconds(1))
                            , StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
                    if (recordList != null && !recordList.isEmpty()) {
                        readCounter.incrementAndGet();
                    } else {
                        break;
                    }
                }
            });
        }
        executor.shutdown();
        while (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        Assert.assertEquals(totalMessageCount, readCounter.get());
    }

    // todo
    @Test
    public void testExceptionHandling() {

    }
}
