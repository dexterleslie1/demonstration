package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.config.ConfigRedisson;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1/set")
@Slf4j
public class SetController {
    /**
     * set 中元素总数
     */
    public final static Long TotalElementCount = 1000000L;

    @Resource
    private List<RSet<Long>> rSetShardList;
    @Resource
    Environment environment;

    /**
     * 初始化数据
     *
     * @throws InterruptedException
     */
    @PostConstruct
    public void init() throws InterruptedException {
        // 删除之前的数据
        for (RSet<Long> o : rSetShardList)
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
                        RSet<Long> rSet = rSetShardList.get(remaining);
                        rSet.add(count);
                    }
                });
            }
            threadPool.shutdown();
            while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

            if (log.isInfoEnabled())
                log.info("成功初始化 {} 个元素到 RSet {} 中",
                        TotalElementCount, ConfigRedisson.KeyRSet);
        }
    }

    /**
     * 测试 RSet random 性能
     *
     * @return
     */
    @GetMapping("testRSetRandom1")
    public ObjectResponse<Long> testRSetRandom1() {
        Long randomLong = RandomUtil.randomLong(0, TotalElementCount + 1);
        int remaining = randomLong.intValue() % ConfigRedisson.TotalShards;
        RSet<Long> o = rSetShardList.get(remaining);
        Long value = o.random();
        return ResponseUtils.successObject(value);
    }

    /**
     * 测试 RMapCache get 性能
     *
     * @return
     */
    @GetMapping("testRSetRandom2")
    public ListResponse<Long> testRSetRandom2() {
        Long randomLong = RandomUtil.randomLong(0, TotalElementCount + 1);
        int remaining = randomLong.intValue() % ConfigRedisson.TotalShards;
        RSet<Long> o = rSetShardList.get(remaining);
        Set<Long> valueList = o.random(20);
        return ResponseUtils.successList(valueList.stream().toList());
    }
}
