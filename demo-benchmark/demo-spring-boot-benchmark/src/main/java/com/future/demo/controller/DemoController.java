package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/")
public class DemoController {

    @Autowired(required = false)
    StringRedisTemplate redisTemplate;

    private long totalKey = 100000;
    private List<String> redisKeyList = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 生成10w个随机key放到内中
        for (int i = 0; i < totalKey; i++) {
            String key = UUID.randomUUID().toString();
            redisKeyList.add(key);
        }

        // 把随机key set 到 redis 中
        if (this.redisTemplate != null) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < totalKey; i++) {
                String key = redisKeyList.get(i);
                map.put(key, key);

                if ((i + 1) % 1000 == 0) {
                    Map<String, String> finalMap = map;
                    this.redisTemplate.executePipelined(new SessionCallback<String>() {
                        @Override
                        public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                            RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                            for (String key : finalMap.keySet()) {
                                redisOperations.opsForValue().set(key, finalMap.get(key));
                            }

                            // 返回null即可，因为返回值会被管道的返回值覆盖，外层取不到这里的返回值
                            return null;
                        }
                    });
                    map = new HashMap<>();
                }
            }
        }
    }

    /**
     * 不操作 redis 直接在内存中返回 uuid
     *
     * @return
     */
    @GetMapping(value = "/")
    public ObjectResponse<String> index() {
        String uuid = this.redisKeyList.get((int) RANDOM.nextLong(totalKey));
        String str = "UUID:" + uuid;
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(str);
        return response;
    }

    final static Random RANDOM = new Random(System.currentTimeMillis());

    /**
     * 协助测试 redis get 性能
     *
     * @return
     */
    @GetMapping(value = "redisGet")
    public ObjectResponse<String> redisGet() {
        String key = this.redisKeyList.get((int) RANDOM.nextLong(totalKey));
        String uuid = this.redisTemplate.opsForValue().get(key);
        String str = "UUID:" + uuid;
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(str);
        return response;
    }

    /**
     * 协助测试 redis set 性能
     *
     * @return
     */
    @GetMapping(value = "redisSet")
    public ObjectResponse<String> redisSet() {
        String key = this.redisKeyList.get((int) RANDOM.nextLong(totalKey));
        this.redisTemplate.opsForValue().set(key, key);
        String str = "UUID:" + key;
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(str);
        return response;
    }
}
