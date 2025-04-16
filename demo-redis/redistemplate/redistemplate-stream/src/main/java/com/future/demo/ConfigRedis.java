package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Configuration
@Slf4j
public class ConfigRedis {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> configStreamListener(
            RedisConnectionFactory connectionFactory) {
        // region 创建多个消费组，每个消费组中有多个消费者
        // 多个消费组会收到同一个消息，但是每个消费组内同一个消息不会被重复消费
        try {
            this.redisTemplate.opsForStream().createGroup(Const.StreamName, Const.GroupNameA);
        } catch (RedisSystemException ex) {
            if (ex.getRootCause() != null && !ex.getRootCause().getMessage().contains("Group name already exists")) {
                throw ex;
            }
        }
        try {
            this.redisTemplate.opsForStream().createGroup(Const.StreamName, Const.GroupNameB);
        } catch (RedisSystemException ex) {
            if (ex.getRootCause() != null && !ex.getRootCause().getMessage().contains("Group name already exists")) {
                throw ex;
            }
        }

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>>
                options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .errorHandler((e) -> {
                    log.error(e.getMessage(), e);
                })
                .pollTimeout(Duration.ofMillis(100))
                .build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        container.receive(Consumer.from(Const.GroupNameA, Const.ConsumerNameA),
                StreamOffset.create(Const.StreamName, ReadOffset.lastConsumed()),
                new MyStreamListener(Const.GroupNameA, Const.ConsumerNameA, this.redisTemplate));
        container.receive(Consumer.from(Const.GroupNameA, Const.ConsumerNameB),
                StreamOffset.create(Const.StreamName, ReadOffset.lastConsumed()),
                new MyStreamListener(Const.GroupNameA, Const.ConsumerNameB, this.redisTemplate));

        container.receive(Consumer.from(Const.GroupNameB, Const.ConsumerNameA),
                StreamOffset.create(Const.StreamName, ReadOffset.lastConsumed()),
                new MyStreamListener(Const.GroupNameB, Const.ConsumerNameA, this.redisTemplate));
        container.receive(Consumer.from(Const.GroupNameB, Const.ConsumerNameB),
                StreamOffset.create(Const.StreamName, ReadOffset.lastConsumed()),
                new MyStreamListener(Const.GroupNameB, Const.ConsumerNameB, this.redisTemplate));

        // endregion

        // region 创建多个 stream 并监听消息

        for (int i = 0; i < Const.StreamCount; i++) {
            String streamName = Const.StreamName + i;
            String groupName = Const.GroupNameA;
            String consumerName = Const.ConsumerNameA;

            try {
                this.redisTemplate.opsForStream().createGroup(streamName, groupName);
            } catch (RedisSystemException ex) {
                if (ex.getRootCause() != null && !ex.getRootCause().getMessage().contains("Group name already exists")) {
                    throw ex;
                }
            }

            // 每个 stream 注册多次 listener 否则无法并发处理消息
            /*for (int j = 0; j < 64; j++) {*/
            container.receive(Consumer.from(groupName, consumerName),
                    StreamOffset.create(streamName, ReadOffset.lastConsumed()),
                    new MyStreamListener(groupName, consumerName, this.redisTemplate));
            /*}*/
        }

        // endregion

        return container;
    }
}
