package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.entity.IdCacheAssistantEntity;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapperidca.IdCacheAssistantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 在订单数据超多时，一次加载所有订单id到内存以协助getById测试是不现实的，
 * 所以使用此机制辅助此场景测试
 */
@Service
@Slf4j
public class IdCacheAssistantService {
    @Resource
    IdCacheAssistantMapper idCacheAssistantMapper;
    @Resource
    OrderMapper orderMapper;

    private Long idMin;
    private Long idMax;

    private boolean stop = false;
    private ExecutorService threadPool = null;

    private Long[] orderIdArrRandom = null;

    @PostConstruct
    public void init() {
        // 加载id缓存辅助的最大和最小id值
        this.idMin = this.idCacheAssistantMapper.getIdMin();
        this.idMax = this.idCacheAssistantMapper.getIdMax();

        if (this.idMin != null && this.idMax != null) {
            log.info("id缓存辅助最小id为{}，最大id为{}", this.idMin, this.idMax);

            threadPool = Executors.newFixedThreadPool(1);
            threadPool.submit(() -> {
                int pageSize = 10000 * 20;
                AtomicLong counter = new AtomicLong();
                while (!stop) {
                    long idRandom = RandomUtil.randomLong(idMin, idMax);

                    Long[] orderIdArrRandomResult = this.idCacheAssistantMapper.listOrderId(idRandom, pageSize);
                    if (orderIdArrRandomResult.length == pageSize) {
                        orderIdArrRandom = orderIdArrRandomResult;
                        counter.incrementAndGet();

                        if (counter.get() % 10 == 0 && counter.get() > 0) {
                            log.info("自应用启动以来，成功更换{}次id缓存辅助数据", counter.get());
                        }
                    }

                    int randInt = RandomUtil.randomInt(0, 5000);
                    if (randInt != 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(randInt);
                        } catch (InterruptedException e) {
                            log.info(e.getMessage(), e);
                        }
                    }
                }
            });
            log.info("成功启动id缓存辅助机制");
        } else {
            log.info("没有初始化id缓存辅助数据，所以没有启动id缓存辅助机制");
        }
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        if (threadPool != null) {
            this.stop = true;
            threadPool.shutdown();
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * 随机获取一个订单ID
     *
     * @return
     */
    public Long getRandomly() {
        if (this.orderIdArrRandom == null || this.orderIdArrRandom.length == 0) {
            throw new IllegalStateException("id缓存辅助机制没有启动");
        }
        return this.orderIdArrRandom[RandomUtil.randomInt(0, this.orderIdArrRandom.length)];
    }

    /**
     * 初始化id缓存辅助数据
     */
    public void initData() {
        this.idCacheAssistantMapper.truncate();

        int pageSize = 10000 * 10;

        Long orderIdMin = this.orderMapper.getIdMin();
        if (orderIdMin != null) {
            long counter = 0;

            Long lowerBoundary = this.orderMapper.getIdMin();

            // 先插入最小的id，否则会被丢失
            IdCacheAssistantEntity entity = new IdCacheAssistantEntity();
            entity.setOrderId(lowerBoundary);
            this.idCacheAssistantMapper.addBatch(Collections.singletonList(entity));
            counter = counter + 1;

            while (true) {
                Long[] ids = this.orderMapper.listRangeIds(lowerBoundary, pageSize);

                if (ids != null && ids.length > 1) {
                    List<IdCacheAssistantEntity> entityList = Arrays.stream(ids).map(o -> {
                        IdCacheAssistantEntity entityTemporary = new IdCacheAssistantEntity();
                        entityTemporary.setOrderId(o);
                        return entityTemporary;
                    }).collect(Collectors.toList());
                    idCacheAssistantMapper.addBatch(entityList);

                    counter = counter + ids.length;
                    log.info("已初始化{}个id缓存辅助数据", counter);

                    lowerBoundary = ids[ids.length - 1];
                } else {
                    log.info("初始化id缓存辅助数据，共{}个", counter);
                    break;
                }
            }
        } else {
            log.info("无法初始化id缓存辅助数据，因为订单表没有数据");
        }
    }
}
