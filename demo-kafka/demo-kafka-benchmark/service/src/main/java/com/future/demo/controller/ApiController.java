package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ApiController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplateWithoutTransaction;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplateWithTransaction;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 用于测试各个消费者配置独立
     */
    @GetMapping("sendToTopic1")
    public ObjectResponse<String> sendToTopic1() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplateWithoutTransaction.send(Constant.Topic1, message).get();

        // 用于协助测试发送消息期间，消费者重启或者崩溃是否会丢失消息
        int randomMillisecond = RandomUtil.randomInt(1, 1000);
        TimeUnit.MILLISECONDS.sleep(randomMillisecond);

        return ResponseUtils.successObject("消息发送成功");
    }

    /**
     * 用于测试各个消费者配置独立
     */
    @GetMapping("sendToTopic2")
    public ObjectResponse<String> sendToTopic2() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplateWithoutTransaction.send(Constant.Topic2, message).get();
        return ResponseUtils.successObject("消息发送成功");
    }

    /**
     * 发生消息到 topic-test-alter-partitions-online 主题
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("sendToTopicTestAlterPartitionsOnline")
    public ObjectResponse<String> sendToTopicTestAlterPartitionsOnline() throws Exception {
        String message = UUID.randomUUID().toString();
        kafkaTemplateWithoutTransaction.send(Constant.TopicTestAlterPartitionsOnline, message).get();
        return ResponseUtils.successObject("消息发送成功");
    }

    /**
     * 用于协助测试分区迁移是否丢失消息
     */
    @GetMapping("sendToTopic1PartitionReassignAssist")
    public ObjectResponse<String> sendToTopic1PartitionReassignAssist() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplateWithoutTransaction.send(Constant.Topic1, message).get();
        int randomMillisecond = RandomUtil.randomInt(1, 1000);
        TimeUnit.MILLISECONDS.sleep(randomMillisecond);
        return ResponseUtils.successObject("消息发送成功");
    }

    /**
     * 重置 redis 中 Kafka autoOffsetReset 配置项计数器
     *
     * @return
     */
    @GetMapping("resetConfigOptionAutoOffsetResetCounter")
    public ObjectResponse<String> resetConfigOptionAutoOffsetResetCounter() {
        redisTemplate.delete(Constant.KeyConfigOptionAutoOffsetResetCounter);
        return ResponseUtils.successObject("成功调用");
    }

    /**
     * 查询 redis 中 Kafka autoOffsetReset 配置项计数器
     *
     * @return
     */
    @GetMapping("getConfigOptionAutoOffsetResetCounter")
    public ObjectResponse<Integer> getConfigOptionAutoOffsetResetCounter() {
        String value = redisTemplate.opsForValue().get(Constant.KeyConfigOptionAutoOffsetResetCounter);
        int count = StringUtils.isEmpty(value) ? 0 : Integer.parseInt(value);
        return ResponseUtils.successObject(count);
    }

    /**
     * 用于协助测试事务
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("testAssistTransactionSendMessageWithoutTransaction")
    public ObjectResponse<String> testAssistTransactionSendMessageWithoutTransaction() throws ExecutionException, InterruptedException {
        String uuidStr = UUID.randomUUID().toString();

        List<ListenableFuture> futureList = new ArrayList<>();
        futureList.add(kafkaTemplateWithoutTransaction.send(Constant.TestAssistTransactionTopic1, uuidStr));
        futureList.add(kafkaTemplateWithoutTransaction.send(Constant.TestAssistTransactionTopic2, uuidStr));
        futureList.add(kafkaTemplateWithoutTransaction.send(Constant.TestAssistTransactionTopic3, uuidStr));
        if (!futureList.isEmpty()) {
            int index = -1;
            try {
                for (int i = 0; i < futureList.size(); i++) {
                    index = i;
                    futureList.get(i).get();
                }
            } catch (Exception ex) {
                log.error("发送Kafka消息失败，原因：{}，出错的 futureList 索引为 {}", ex.getMessage(), index, ex);
                throw ex;
            }
        }

        return ResponseUtils.successObject("成功调用");
    }

    /**
     * 用于协助测试事务
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("testAssistTransactionSendMessageWithTransaction")
    public ObjectResponse<String> testAssistTransactionSendMessageWithTransaction() throws ExecutionException, InterruptedException {
        String uuidStr = UUID.randomUUID().toString();

        // 开始事务
        List<ListenableFuture> futureList = kafkaTemplateWithTransaction.executeInTransaction(t -> {
            List<ListenableFuture> futureListInternal = new ArrayList<>();
            futureListInternal.add(t.send(Constant.TestAssistTransactionTopic1, uuidStr));
            futureListInternal.add(t.send(Constant.TestAssistTransactionTopic2, uuidStr));
            futureListInternal.add(t.send(Constant.TestAssistTransactionTopic3, uuidStr));
            return futureListInternal;
        });
        if (!futureList.isEmpty()) {
            int index = -1;
            try {
                for (int i = 0; i < futureList.size(); i++) {
                    index = i;
                    futureList.get(i).get();
                }
            } catch (Exception ex) {
                log.error("发送Kafka消息失败，原因：{}，出错的 futureList 索引为 {}", ex.getMessage(), index, ex);
                throw new RuntimeException(ex);
            }
        }

        return ResponseUtils.successObject("成功调用");
    }

//    /**
//     * 用于协助测试事务
//     *
//     * @return
//     */
//    @GetMapping("testAssistTransactionGetCounter")
//    public ObjectResponse<String> testAssistTransactionGetCounter() {
//        String counter1 = redisTemplate.opsForValue().get(Constant.TestAssistTransactionKeyCounterTopic1);
//        String counter2 = redisTemplate.opsForValue().get(Constant.TestAssistTransactionKeyCounterTopic2);
//        String counter3 = redisTemplate.opsForValue().get(Constant.TestAssistTransactionKeyCounterTopic3);
//        String builder = "counter1=" + counter1 + "," +
//                "counter2=" + counter2 + "," +
//                "counter3=" + counter3;
//        return ResponseUtils.successObject(builder);
//    }
//
//    /**
//     * 用于协助测试事务
//     *
//     * @return
//     */
//    @GetMapping("testAssistTransactionResetCounter")
//    public ObjectResponse<String> testAssistTransactionResetCounter() {
//        redisTemplate.delete(Arrays.asList(
//                Constant.TestAssistTransactionKeyCounterTopic1,
//                Constant.TestAssistTransactionKeyCounterTopic2,
//                Constant.TestAssistTransactionKeyCounterTopic3));
//        return ResponseUtils.successObject("成功调用");
//    }
}
