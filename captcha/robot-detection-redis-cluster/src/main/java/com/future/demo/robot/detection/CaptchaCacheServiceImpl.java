package com.future.demo.robot.detection;

import org.redisson.api.RedissonClient;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.List;

public class CaptchaCacheServiceImpl extends AbstractCaptchaCacheService {
    private final static List<String> RedirectUriList = Arrays.asList("/", "/index.jsp");
    private final static String Location = "/index.jsp";
    private final static String UriEnable = "/api/v1/biz/setEnable.do";

    public CaptchaCacheServiceImpl(JedisCluster jedisCluster,
                                   CacheManagera cacheManager,
                                   RedissonClient redissonClient) {
        super(jedisCluster, cacheManager, redissonClient);
    }

    @Override
    List<String> getRedirectUriList() {
        return RedirectUriList;
    }

    @Override
    String getRedirectLocationSuccessfullyVerify() {
        return Location;
    }

    @Override
    String getEnableUri() {
        return UriEnable;
    }
}
