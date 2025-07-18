package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.config.ConfigRedisson;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1/map")
@Slf4j
public class MapController {
    /**
     * map 中元素总数
     */
    public final static Long TotalElementCount = 1000000L;

    @Resource
    private List<RMap<Long, String>> rMapShardList;
    @Resource
    private List<RMapCache<Long, String>> rMapCacheShardList;
    @Resource
    private List<RLocalCachedMap<Long, String>> rLocalCachedMapShardList;
    @Resource
    Environment environment;

    /**
     * 初始化 map 数据
     *
     * @throws InterruptedException
     */
    @PostConstruct
    public void init() throws InterruptedException {
        // 删除之前的数据
        for (RMap<Long, String> o : rMapShardList)
            o.delete();
        for (RMapCache<Long, String> o : rMapCacheShardList)
            o.delete();
        for (RLocalCachedMap<Long, String> o : rLocalCachedMapShardList)
            o.delete();

        String[] activeProfiles = environment.getActiveProfiles();
        // 单元测试不执行初始化
        if (activeProfiles == null || activeProfiles.length == 0 || !activeProfiles[0].equals("test")) {
            AtomicInteger counter = new AtomicInteger();
            ExecutorService threadPool = Executors.newCachedThreadPool();
            int concurrentThreads = 32;
            for (int i = 0; i < concurrentThreads; i++) {
                threadPool.submit(() -> {
                    long count;
                    while ((count = counter.getAndIncrement()) < TotalElementCount) {
                        int remaining = (int) count % ConfigRedisson.TotalShards;
                        RMap<Long, String> rMap = rMapShardList.get(remaining);
                        RMapCache<Long, String> rMapCache = rMapCacheShardList.get(remaining);
                        RLocalCachedMap<Long, String> rLocalCachedMap = rLocalCachedMapShardList.get(remaining);
                        String uuidStr = UUID.randomUUID().toString();
                        rMap.put(count, uuidStr);
                        rMapCache.put(count, uuidStr);
                        rLocalCachedMap.put(count, uuidStr);
                    }
                });
            }
            threadPool.shutdown();
            while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

            if (log.isInfoEnabled())
                log.info("成功初始化 {} 个元素到 RMap {} RMapCache {} RLocalCachedMap {} 中",
                        TotalElementCount, ConfigRedisson.KeyRMap, ConfigRedisson.KeyRMapCache, ConfigRedisson.KeyRLocalCachedMap);
        }
    }

    /**
     * 测试 RMap get 性能
     *
     * @return
     */
    @GetMapping("testRMapGet")
    public ObjectResponse<String> testRMapGet() {
        Long randomLong = RandomUtil.randomLong(0, TotalElementCount + 1);
        int remaining = randomLong.intValue() % ConfigRedisson.TotalShards;
        RMap<Long, String> o = rMapShardList.get(remaining);
        String value = o.get(randomLong);
        return ResponseUtils.successObject(value);
    }

    /**
     * 测试 RMapCache get 性能
     *
     * @return
     */
    @GetMapping("testRMapCacheGet")
    public ObjectResponse<String> testRMapCacheGet() {
        Long randomLong = RandomUtil.randomLong(0, TotalElementCount + 1);
        int remaining = randomLong.intValue() % ConfigRedisson.TotalShards;
        RMapCache<Long, String> o = rMapCacheShardList.get(remaining);
        String value = o.get(randomLong);
        return ResponseUtils.successObject(value);
    }

    /**
     * 测试 RLocalCachedMap get 性能
     *
     * @return
     */
    @GetMapping("testRLocalCachedMapGet")
    public ObjectResponse<String> testRLocalCachedMapGet() {
        Long randomLong = RandomUtil.randomLong(0, TotalElementCount + 1);
        int remaining = randomLong.intValue() % ConfigRedisson.TotalShards;
        RLocalCachedMap<Long, String> o = rLocalCachedMapShardList.get(remaining);
        String value = o.get(randomLong);
        return ResponseUtils.successObject(value);
    }
}
