package com.future.demo.config;

import com.future.demo.mapper.CommonMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;

@Component
public class PrometheusCustomMonitor {

    /**
     * 订单从 redis 缓存同步到数据库计数
     */
    private Counter counterOrderSyncCount;
    /**
     * 订单建立 listByUserId cassandra 索引计数
     */
    private Counter counterCassandraIndexOrderListByUserId;
    /**
     * 订单建立 listByMerchantId cassandra 索引计数
     */
    private Counter counterCassandraIndexOrderListByMerchantId;
    /**
     * 普通下单缓存中没有商品列表
     */
    @Getter
    private Counter counterOrdinaryPurchaseNoProductListInCache;
    /**
     * 普通下单时购买秒杀商品不被支持
     */
    @Getter
    private Counter counterOrdinaryPurchaseFlashSaleProductNotSupported;
    /**
     * 普通下单时库存不足
     */
    @Getter
    private Counter counterOrdinaryPurchaseInsufficientStock;
    /**
     * 普通下单成功
     */
    @Getter
    private Counter counterOrdinaryPurchaseSuccessfully;
    /**
     * 秒杀下单缓存中没有秒杀商品列表
     */
    @Getter
    private Counter counterFlashSalePurchaseNoProductListInCache;
    /**
     * 秒杀下单商品不存在
     */
    @Getter
    private Counter counterFlashSalePurchaseProductNotExists;
    /**
     * 秒杀下单秒杀未开始
     */
    @Getter
    private Counter counterFlashSalePurchaseNotStartedYet;
    /**
     * 秒杀下单已结束
     */
    @Getter
    private Counter counterFlashSalePurchaseEnded;
    /**
     * 秒杀下单库存不足
     */
    @Getter
    private Counter counterFlashSalePurchaseInsufficientStock;
    /**
     * 秒杀下单重复下单
     */
    @Getter
    private Counter counterFlashSalePurchaseAlreadyPurchased;
    /**
     * 秒杀下单未知异常
     */
    @Getter
    private Counter counterFlashSalePurchaseUnknownException;
    /**
     * 秒杀下单成功
     */
    @Getter
    private Counter counterFlashSalePurchaseSuccessfully;

    @Resource
    CommonMapper commonMapper;

    private final MeterRegistry registry;

    @Autowired
    public PrometheusCustomMonitor(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    private void init() {
        // 会自动转换名为为 flash_sale_order_sync_count_total 的指标
        // 指标 flash_sale_order_sync_count_total 的 tags 为 order='master'，模拟订单主表
        counterOrderSyncCount = registry.counter("flash.sale.order.sync.count", "order", "master");
        counterCassandraIndexOrderListByUserId = registry.counter("flash.sale.cassandra.index.create", "type", "orderListByUserId");
        counterCassandraIndexOrderListByMerchantId = registry.counter("flash.sale.cassandra.index.create", "type", "orderListByMerchantId");

        // 普通下单
        counterOrdinaryPurchaseNoProductListInCache = registry.counter("purchase.stats", "type", "ordinaryPurchaseNoProductListInCache");
        counterOrdinaryPurchaseFlashSaleProductNotSupported = registry.counter("purchase.stats", "type", "ordinaryPurchaseFlashSaleProductNotSupported");
        counterOrdinaryPurchaseInsufficientStock = registry.counter("purchase.stats", "type", "ordinaryPurchaseInsufficientStock");
        counterOrdinaryPurchaseSuccessfully = registry.counter("purchase.stats", "type", "ordinaryPurchaseSuccessfully");
        // 秒杀下单
        counterFlashSalePurchaseNoProductListInCache = registry.counter("purchase.stats", "type", "flashSalePurchaseNoProductListInCache");
        counterFlashSalePurchaseProductNotExists = registry.counter("purchase.stats", "type", "flashSalePurchaseProductNotExists");
        counterFlashSalePurchaseNotStartedYet = registry.counter("purchase.stats", "type", "flashSalePurchaseNotStartedYet");
        counterFlashSalePurchaseEnded = registry.counter("purchase.stats", "type", "flashSalePurchaseEnded");
        counterFlashSalePurchaseInsufficientStock = registry.counter("purchase.stats", "type", "flashSalePurchaseInsufficientStock");
        counterFlashSalePurchaseAlreadyPurchased = registry.counter("purchase.stats", "type", "flashSalePurchaseAlreadyPurchased");
        counterFlashSalePurchaseUnknownException = registry.counter("purchase.stats", "type", "flashSalePurchaseUnknownException");
        counterFlashSalePurchaseSuccessfully = registry.counter("purchase.stats", "type", "flashSalePurchaseSuccessfully");

        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "order")), commonMapper, (o) -> o.getCountByFlag("order"));
        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "product")), commonMapper, (o) -> o.getCountByFlag("product"));
        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "cassandra-index-orderListByUserId")), commonMapper, (o) -> o.getCountByFlag("orderListByUserId"));
        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "cassandra-index-orderListByMerchantId")), commonMapper, (o) -> o.getCountByFlag("orderListByMerchantId"));
    }

    public void incrementOrderSyncCount(int count) {
        counterOrderSyncCount.increment(count);
    }

    public void incrementCassandraIndexOrderListByUserId(int count) {
        this.counterCassandraIndexOrderListByUserId.increment(count);
    }

    public void incrementCassandraIndexOrderListByMerchantId(int count) {
        this.counterCassandraIndexOrderListByMerchantId.increment(count);
    }
}
