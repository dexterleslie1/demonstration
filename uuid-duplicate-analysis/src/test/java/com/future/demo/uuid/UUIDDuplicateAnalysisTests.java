package com.future.demo.uuid;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class UUIDDuplicateAnalysisTests {
    @Autowired
    private RedisTemplate redisTemplate = null;
    @Autowired
    private CacheManager cacheManager = null;

    private Cache uuidCache = null;

    @PostConstruct
    public void init() {
        this.uuidCache = this.cacheManager.getCache("uuidCache");
    }

    @Test
    public void test() throws InterruptedException {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        int concurrentThreads = 50;
        ExecutorService executorService = Executors.newCachedThreadPool();

        int runningDurationMilliseconds = 15*60*1000;

        for(int i=0; i<concurrentThreads; i++) {
            final Date startTime = new Date();
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Date timeNow = new Date();
                    while(timeNow.getTime()-startTime.getTime()<=runningDurationMilliseconds) {
                        String uuid = UUID.randomUUID().toString();
                        if(valueOperations.get(uuid)!=null) {
                            log.warn("检测到uuid={}重复", uuid);
                        } else if(uuidCache.get(uuid)!=null) {
                            log.warn("检测到uuid={}重复", uuid);
                        }
                        valueOperations.set(uuid, "");
                        uuidCache.put(new Element(uuid, ""));
                        timeNow = new Date();
                    }
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));
    }
}
