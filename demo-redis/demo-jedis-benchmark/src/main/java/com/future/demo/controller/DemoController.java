package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.JedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DemoController {
    public final static Random RANDOM = new Random(System.currentTimeMillis());
    public final static Integer MaximumKey = 10240;

    @Autowired(required = false)
    JedisPool jedisPool;
    @Autowired(required = false)
    JedisCluster jedisCluster;

    /**
     * 测试 set 命令性能
     *
     * @return
     */
    @GetMapping("testSet")
    public ObjectResponse<String> testSet() {
        int randInt = RANDOM.nextInt(MaximumKey);
        String key = String.valueOf(randInt);
        String value = UUID.randomUUID().toString();
        if (this.jedisPool != null) {
            Jedis jedis = null;
            try {
                jedis = JedisPoolUtil.getJedis(this.jedisPool);
                jedis.set(key, value);
            } finally {
                JedisPoolUtil.returnJedis(jedis);
            }
        } else {
            this.jedisCluster.set(key, value);
        }
        return ResponseUtils.successObject("成功调用 set 命令，key=" + key + ",value=" + value);
    }
}
