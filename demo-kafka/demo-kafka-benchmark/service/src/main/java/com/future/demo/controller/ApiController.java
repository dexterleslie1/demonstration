package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 用于测试各个消费者配置独立
     */
    @GetMapping("sendToTopic1")
    public ObjectResponse<String> sendToTopic1() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplate.send(Constant.Topic1, message).get();

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
        kafkaTemplate.send(Constant.Topic2, message).get();
        return ResponseUtils.successObject("消息发送成功");
    }

    /**
     * 用于协助测试分区迁移是否丢失消息
     */
    @GetMapping("sendToTopic1PartitionReassignAssist")
    public ObjectResponse<String> sendToTopic1PartitionReassignAssist() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplate.send(Constant.Topic1, message).get();
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
}
