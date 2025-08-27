package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.config.PrometheusCustomMonitor;
import com.future.demo.constant.Const;
import com.future.demo.entity.ProductModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 当下单时随机选择商品
 */
@Service
@Slf4j
public class PickupProductRandomlyWhenPurchasingService {

    PrometheusCustomMonitor prometheusCustomMonitor;
    RedissonClient redissonClient;
    @Getter
    RSet<Long> rSetOrdinaryProductId;
    @Getter
    RSet<Long> rSetFlashSaleProductId;

    List<Long> productIdListOrdinary;
    List<Long> productIdListFlashSale;

    ScheduledExecutorService scheduledExecutorService;

    /**
     * @param redissonClient
     */
    public PickupProductRandomlyWhenPurchasingService(@Autowired RedissonClient redissonClient,
                                                      @Autowired PrometheusCustomMonitor prometheusCustomMonitor) {
        this.redissonClient = redissonClient;
        this.prometheusCustomMonitor = prometheusCustomMonitor;
        rSetOrdinaryProductId = redissonClient.getSet(Const.KeyRSetProductIdOrdinaryForPickupRandomlyWhenPurchasing);
        rSetFlashSaleProductId = redissonClient.getSet(Const.KeyRSetProductIdFlashSaleForPickupRandomlyWhenPurchasing);

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new CustomizableThreadFactory("rset-product-id-list-fixed-rate-random-picker-"));
        scheduledExecutorService.scheduleAtFixedRate(new R(), 0, 10, TimeUnit.SECONDS);
    }

    private class R implements Runnable {

        @Override
        public void run() {
            try {
                // 随机最多获取 40960 个商品
                productIdListOrdinary = rSetOrdinaryProductId.random(40960).stream().toList();
                if (log.isDebugEnabled()) {
                    log.debug("rSetOrdinaryProductId.readAll().stream().toList() size {}", productIdListOrdinary.size());
                }
                productIdListFlashSale = rSetFlashSaleProductId.random(40960).stream().toList();
                if (log.isDebugEnabled()) {
                    log.debug("rSetFlashSaleProductId.readAll().stream().toList() size {}", productIdListFlashSale.size());
                }
            } catch (Exception ex) {
                // 不能抛出异常，否则 ScheduledExecutorService 停止工作
                log.error(ex.getMessage(), ex);
            }
        }
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        scheduledExecutorService.shutdown();
        while (!scheduledExecutorService.awaitTermination(100, TimeUnit.MILLISECONDS)) ;
    }

    /**
     * 设置商品列表
     *
     * @param modelList
     */
    public void setProductList(List<ProductModel> modelList) {
        // 设置普通商品的 RSet
        List<Long> productIdListOrdinary = modelList.stream().filter(o -> !o.isFlashSale()).map(ProductModel::getId).distinct().toList();
        List<Long> productIdListFlashSale = modelList.stream().filter(ProductModel::isFlashSale).map(ProductModel::getId).distinct().toList();
        if (!productIdListOrdinary.isEmpty()) {
            rSetOrdinaryProductId.addAll(productIdListOrdinary);
            if (log.isDebugEnabled())
                log.debug("成功设置 RSet {} productIdList {}",
                        Const.KeyRSetProductIdOrdinaryForPickupRandomlyWhenPurchasing, productIdListOrdinary);
        }
        // 设置秒杀商品的 RSet
        if (!productIdListFlashSale.isEmpty()) {
            rSetFlashSaleProductId.addAll(productIdListFlashSale);
            if (log.isDebugEnabled())
                log.debug("成功设置 RSet {} productIdList {}",
                        Const.KeyRSetProductIdFlashSaleForPickupRandomlyWhenPurchasing, productIdListFlashSale);
        }

        if (log.isDebugEnabled())
            log.debug("成功向缓存中添加商品用于下单时随机抽取商品 {}", modelList);
    }

    /**
     * 随机抽取普通商品ID
     *
     * @return
     */
    public long getOrdinaryProductIdRandomly() {
        if (productIdListOrdinary == null || productIdListOrdinary.isEmpty()) {
            prometheusCustomMonitor.getCounterOrdinaryPurchaseNoProductListInCache().increment();
            throw new IllegalArgumentException("缓存中没有普通商品列表");
        }

        int randomIndex = RandomUtil.randomInt(0, productIdListOrdinary.size());
        return productIdListOrdinary.get(randomIndex);
    }

    /**
     * 随机抽取秒杀商品ID
     *
     * @return
     */
    public long getFlashSaleProductIdRandomly() {
        if (productIdListFlashSale == null || productIdListFlashSale.isEmpty()) {
            prometheusCustomMonitor.getCounterFlashSalePurchaseNoProductListInCache().increment();
            throw new IllegalArgumentException("缓存中没有秒杀商品列表");
        }

        int randomIndex = RandomUtil.randomInt(0, productIdListFlashSale.size());
        return productIdListFlashSale.get(randomIndex);
    }

    @Service
    public static class CountService {
        RSet<Long> rSetOrdinaryProductId;
        RSet<Long> rSetFlashSaleProductId;

        /**
         * @param redissonClient
         */
        public CountService(@Autowired RedissonClient redissonClient) {
            rSetOrdinaryProductId = redissonClient.getSet(Const.KeyRSetProductIdOrdinaryForPickupRandomlyWhenPurchasing);
            rSetFlashSaleProductId = redissonClient.getSet(Const.KeyRSetProductIdFlashSaleForPickupRandomlyWhenPurchasing);
        }

        /**
         * 获取随机选择商品缓存中普通商品总数
         *
         * @return
         */
        public int getOrdinaryProductIdCount() {
            return rSetOrdinaryProductId.size();
        }

        /**
         * 获取随机选择商品缓存中秒杀商品总数
         *
         * @return
         */
        public int getFlashSaleProductIdCount() {
            return rSetFlashSaleProductId.size();
        }
    }
}
