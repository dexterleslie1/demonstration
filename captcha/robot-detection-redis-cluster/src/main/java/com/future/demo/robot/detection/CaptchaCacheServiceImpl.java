package com.future.demo.robot.detection;

import com.yyd.common.ddos.AbstractCaptchaCacheService;
import org.redisson.api.RedissonClient;
import redis.clients.jedis.JedisCluster;

import javax.servlet.ServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CaptchaCacheServiceImpl extends AbstractCaptchaCacheService {
    private final static List<String> RedirectUriList = Arrays.asList("/", "/index.jsp");
    private final static String Location = "/index.jsp";
    private final static String UriEnable = "/api/v1/biz/setEnable.do";

    public CaptchaCacheServiceImpl(JedisCluster jedisCluster,
                                   CacheManagera cacheManager,
                                   RedissonClient redissonClient,
                                   String uriPrefix,
                                   boolean createCaptcha) {
        super(jedisCluster, cacheManager.getCacheManager(), redissonClient, uriPrefix, createCaptcha);
    }

    @Override
    public List<String> getRedirectUriList() {
        return RedirectUriList;
    }

    @Override
    public String getRedirectUriSuccessfullyVerify(ServletRequest servletRequest) {
        return Location;
    }

    @Override
    public List<String> getIgnoreUriList() {
        return Collections.singletonList(UriEnable);
    }
}
