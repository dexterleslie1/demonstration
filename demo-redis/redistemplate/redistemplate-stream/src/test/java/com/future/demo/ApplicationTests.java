package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

        Assertions.assertEquals(totalMessageCount * concurrentThreads, readCounter.get());
    }

    // 模拟 RabbitMQ 的工作模式
    @Test
    public void testSimRabbitMQWorkerMode() throws InterruptedException {
        String streamKey = UUID.randomUUID().toString();
        String groupKey = UUID.randomUUID().toString();

        // 创建消费者组
        String result = this.redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), groupKey);
        Assertions.assertEquals("OK", result);

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

        Assertions.assertEquals(totalMessageCount, readCounter.get());
    }

    /**
     * 测试 StreamMessageListenerContainer 配置是否正确
     *
     * @throws InterruptedException
     */
    @Test
    public void testMessageListenerContainer() throws InterruptedException {
        Const.RecordIdList.clear();
        StringRecord record = StringRecord.of(new HashMap<String, String>() {{
            this.put("k1", "v1");
        }}).withStreamKey(Const.StreamName);
        RecordId recordId = this.redisTemplate.opsForStream().add(record);
        TimeUnit.MILLISECONDS.sleep(500);
        Assertions.assertEquals(2, Const.RecordIdList.size());
        Const.RecordIdList.forEach(o -> Assertions.assertEquals(recordId.getValue(), o));
    }

    /**
     * 测试同时监听多个 stream
     */
    @Test
    public void testReadMultipleStreamConcurrently() throws InterruptedException {
        List<String> recordIdList = new ArrayList<>();
        for (int i = 0; i < Const.StreamCount; i++) {
            String streamName = Const.StreamName + i;
            StringRecord record = StringRecord.of(new HashMap<String, String>() {{
                this.put("k1", "v1");
            }}).withStreamKey(streamName);
            RecordId recordId = this.redisTemplate.opsForStream().add(record);
            recordIdList.add(recordId.getValue());
        }

        TimeUnit.SECONDS.sleep(1);

        Assertions.assertEquals(recordIdList.size(), Const.RecordIdList.size());
        recordIdList.forEach(o -> {
            Assertions.assertTrue(Const.RecordIdList.contains(o));
        });
    }

}
