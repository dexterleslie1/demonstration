package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

@Slf4j
public class MyStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
    String groupName;
    String consumerName;
    StringRedisTemplate redisTemplate;

    public MyStreamListener(String groupName, String consumerName, StringRedisTemplate redisTemplate) {
        this.groupName = groupName;
        this.consumerName = consumerName;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        Const.RecordIdList.add(message.getId().getValue());
        log.info("{} - {} - {}", groupName, consumerName, message);
        this.redisTemplate.opsForStream().acknowledge(groupName, message);
    }
}
