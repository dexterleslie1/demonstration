package com.future.demo.crond;

import com.future.demo.constant.Const;
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
public class ConfigRedisStreamListener {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> configStreamListener(
            RedisConnectionFactory connectionFactory,
            MyStreamListener myStreamListener) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>>
                options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .errorHandler((e) -> {
                    log.error(e.getMessage(), e);
                }).pollTimeout(Duration.ofMillis(100)).build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        for (int i = 0; i < Const.StreamCount; i++) {
            String streamName = Const.StreamName + i;

            try {
                this.redisTemplate.opsForStream().createGroup(streamName, Const.GroupName);
            } catch (RedisSystemException ex) {
                if (ex.getRootCause() != null && !ex.getRootCause().getMessage().contains("Group name already exists")) {
                    throw ex;
                }
            }
            container.receive(Consumer.from(Const.GroupName, Const.ConsumerName),
                    StreamOffset.create(streamName, ReadOffset.lastConsumed()),
                    myStreamListener);
        }

        return container;
    }
}
