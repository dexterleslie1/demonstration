package com.future.demo.config;

import com.future.common.exception.BusinessException;
import com.future.count.CountService;
import com.future.demo.service.PickupProductRandomlyWhenPurchasingService;
import io.micrometer.core.instrument.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;

@Component
@Slf4j
public class PrometheusCustomMonitor {

    /**
     * 订单从 redis 缓存同步到数据库计数
     */
    private Counter counterOrderSyncCount;
    /**
     * 秒杀订单同步时根据秒杀商品填充商家 id 延迟 histogram 指标
     */
    @Getter
    private Timer timerOrderSyncFillUpOrderMerchantId;
    /**
     * 秒杀订单同步时批量插入订单延迟 histogram 指标
     */
    @Getter
    private Timer timerOrderSyncBatchInsertOrder;
    /**
     * 秒杀订单同步时并发线程数
     */
    @Getter
    private DistributionSummary distributionSummaryOrderSyncThreadConcurrent;
    /**
     * 秒杀订单同步时批量大小
     */
    @Getter
    private DistributionSummary distributionSummaryOrderSyncBatchSize;

    /**
     * 订单建立 listByUserId cassandra 索引计数
     */
    private Counter counterCassandraIndexOrderListByUserId;
    /**
     * 创建订单 Cassandra 索引 listByUserId 并发线程数
     */
    @Getter
    private DistributionSummary distributionSummaryCreateOrderCassandraIndexListByUserIdConcurrency;
    /**
     * 创建订单 Cassandra 索引 listByUserId 批量大小
     */
    @Getter
    private DistributionSummary distributionSummaryCreateOrderCassandraIndexListByUserIdBatchSize;
    /**
     * 创建订单 Cassandra 索引 listByUserId 根据商品填充商家 id 延迟
     */
    @Getter
    private Timer timerCreateOrderCassandraIndexListByUserIdLatencyFillUpOrderMerchantId;
    /**
     * 创建订单 Cassandra 索引 listByUserId 批量插入建立索引延迟
     */
    @Getter
    private Timer timerCreateOrderCassandraIndexListByUserIdLatencyBatchInsertIndex;
    /**
     * 创建订单 Cassandra 索引 listByUserId 向 Kafka 发送 IncreaseCount 消息延迟
     */
    @Getter
    private Timer timerCreateOrderCassandraIndexListByUserIdLatencySendIncreaseCountMessage;
    /**
     * 创建订单 Cassandra 索引 listByUserId 删除订单缓存延迟
     */
    @Getter
    private Timer timerCreateOrderCassandraIndexListByUserIdLatencyRemoveOrderCache;


    /**
     * 订单建立 listByMerchantId cassandra 索引计数
     */
    private Counter counterCassandraIndexOrderListByMerchantId;
    /**
     * 创建订单 Cassandra 索引 listByMerchantId 并发线程数
     */
    @Getter
    private DistributionSummary distributionSummaryCreateOrderCassandraIndexListByMerchantIdConcurrency;
    /**
     * 创建订单 Cassandra 索引 listByMerchantId 批量大小
     */
    @Getter
    private DistributionSummary distributionSummaryCreateOrderCassandraIndexListByMerchantIdBatchSize;
    /**
     * 创建订单 Cassandra 索引 listByMerchantId 根据商品填充商家 id 延迟
     */
    @Getter
    private Timer timerCreateOrderCassandraIndexListByMerchantIdLatencyFillUpOrderMerchantId;
    /**
     * 创建订单 Cassandra 索引 listByMerchantId 批量插入建立索引延迟
     */
    @Getter
    private Timer timerCreateOrderCassandraIndexListByMerchantIdLatencyBatchInsertIndex;
    /**
     * 创建订单 Cassandra 索引 listByMerchantId 向 Kafka 发送 IncreaseCount 消息延迟
     */
    @Getter
    private Timer timerCreateOrderCassandraIndexListByMerchantIdLatencySendIncreaseCountMessage;

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

    /**
     * 计数器统计成功递增
     */
    @Getter
    private Counter counterIncreaseCountStatsSuccessfully;
    /**
     * 计数器递增延迟
     */
    @Getter
    private Timer timerCountServiceIncreaseCountLatency;
    /**
     * 计数器并发线程数
     */
    @Getter
    private DistributionSummary distributionSummaryCountServiceConcurrency;
    /**
     * 计数器批量大小
     */
    @Getter
    private DistributionSummary distributionSummaryCountServiceBatchSize;

    /**
     * 商品指标成功创建普通商品
     */
    @Getter
    private Counter counterProductMetricsCreateOrdinarySuccessfully;
    /**
     * 商品指标成功创建秒杀商品
     */
    @Getter
    private Counter counterProductMetricsCreateFlashSaleSuccessfully;

    @Autowired
    CountService futureCountService;
    @Autowired
    PickupProductRandomlyWhenPurchasingService.CountService countService;

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
        timerOrderSyncFillUpOrderMerchantId = Timer.builder("flash.sale.order.sync.timer").serviceLevelObjectives(
                Duration.ofMillis(5),
                Duration.ofMillis(10),
                Duration.ofMillis(20),
                Duration.ofMillis(30),
                Duration.ofMillis(50),
                Duration.ofMillis(75),
                Duration.ofMillis(100),
                Duration.ofMillis(200),
                Duration.ofMillis(300),
                Duration.ofMillis(400),
                Duration.ofMillis(500),
                Duration.ofMillis(750),
                Duration.ofMillis(1000),
                Duration.ofMillis(1500),
                Duration.ofMillis(2000),
                Duration.ofMillis(3000),
                Duration.ofMillis(4000),
                Duration.ofMillis(5000),
                Duration.ofMillis(10000),
                Duration.ofMillis(20000),
                Duration.ofMillis(30000),
                Duration.ofMillis(60000)
        ).tags("type", "fillUpOrderMerchantId").register(registry);
        timerOrderSyncBatchInsertOrder = Timer.builder("flash.sale.order.sync.timer").serviceLevelObjectives(
                Duration.ofMillis(5),
                Duration.ofMillis(10),
                Duration.ofMillis(20),
                Duration.ofMillis(30),
                Duration.ofMillis(50),
                Duration.ofMillis(75),
                Duration.ofMillis(100),
                Duration.ofMillis(200),
                Duration.ofMillis(300),
                Duration.ofMillis(400),
                Duration.ofMillis(500),
                Duration.ofMillis(750),
                Duration.ofMillis(1000),
                Duration.ofMillis(1500),
                Duration.ofMillis(2000),
                Duration.ofMillis(3000),
                Duration.ofMillis(4000),
                Duration.ofMillis(5000),
                Duration.ofMillis(10000),
                Duration.ofMillis(20000),
                Duration.ofMillis(30000),
                Duration.ofMillis(60000)
        ).tags("type", "batchInsertOrder").register(registry);
        distributionSummaryOrderSyncThreadConcurrent = DistributionSummary.builder("flash.sale.order.sync.ds")
                .maximumExpectedValue(64.0)
                .publishPercentileHistogram()
                .tag("type", "threadConcurrent")
                .register(registry);
        distributionSummaryOrderSyncBatchSize = DistributionSummary.builder("flash.sale.order.sync.ds")
                .maximumExpectedValue(1024.0)
                .publishPercentileHistogram()
                .tags("type", "batchSize")
                .register(registry);

        counterCassandraIndexOrderListByUserId = registry.counter("flash.sale.cassandra.index.create", "type", "orderListByUserId");
        distributionSummaryCreateOrderCassandraIndexListByUserIdConcurrency = DistributionSummary.builder("flash.sale.cassandra.index.create.ds")
                .maximumExpectedValue(64.0)
                .publishPercentileHistogram()
                .tags("flag", "listByUserId", "type", "concurrency")
                .register(registry);
        distributionSummaryCreateOrderCassandraIndexListByUserIdBatchSize = DistributionSummary.builder("flash.sale.cassandra.index.create.ds")
                .maximumExpectedValue(1024.0)
                .publishPercentileHistogram()
                .tags("flag", "listByUserId", "type", "batchSize")
                .register(registry);
        timerCreateOrderCassandraIndexListByUserIdLatencyFillUpOrderMerchantId = Timer.builder("flash.sale.cassandra.index.create.timer")
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                ).tags("flag", "listByUserId", "type", "fillUpOrderMerchantId").register(registry);
        timerCreateOrderCassandraIndexListByUserIdLatencyBatchInsertIndex = Timer.builder("flash.sale.cassandra.index.create.timer")
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                ).tags("flag", "listByUserId", "type", "batchInsertIndex").register(registry);
        timerCreateOrderCassandraIndexListByUserIdLatencySendIncreaseCountMessage = Timer.builder("flash.sale.cassandra.index.create.timer")
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                ).tags("flag", "listByUserId", "type", "sendIncreaseCountMessage").register(registry);
        timerCreateOrderCassandraIndexListByUserIdLatencyRemoveOrderCache = Timer.builder("flash.sale.cassandra.index.create.timer")
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                ).tags("flag", "listByUserId", "type", "removeOrderCache").register(registry);

        counterCassandraIndexOrderListByMerchantId = registry.counter("flash.sale.cassandra.index.create", "type", "orderListByMerchantId");
        distributionSummaryCreateOrderCassandraIndexListByMerchantIdConcurrency = DistributionSummary.builder("flash.sale.cassandra.index.create.ds")
                .maximumExpectedValue(64.0)
                .publishPercentileHistogram()
                .tags("flag", "listByMerchantId", "type", "concurrency")
                .register(registry);
        distributionSummaryCreateOrderCassandraIndexListByMerchantIdBatchSize = DistributionSummary.builder("flash.sale.cassandra.index.create.ds")
                .maximumExpectedValue(1024.0)
                .publishPercentileHistogram()
                .tags("flag", "listByMerchantId", "type", "batchSize")
                .register(registry);
        timerCreateOrderCassandraIndexListByMerchantIdLatencyFillUpOrderMerchantId = Timer.builder("flash.sale.cassandra.index.create.timer")
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                ).tags("flag", "listByMerchantId", "type", "fillUpOrderMerchantId").register(registry);
        timerCreateOrderCassandraIndexListByMerchantIdLatencyBatchInsertIndex = Timer.builder("flash.sale.cassandra.index.create.timer")
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                ).tags("flag", "listByMerchantId", "type", "batchInsertIndex").register(registry);
        timerCreateOrderCassandraIndexListByMerchantIdLatencySendIncreaseCountMessage = Timer.builder("flash.sale.cassandra.index.create.timer")
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                ).tags("flag", "listByMerchantId", "type", "sendIncreaseCountMessage").register(registry);

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

        // 计数器
        counterIncreaseCountStatsSuccessfully = registry.counter("increase.count.stats", "type", "success");
        timerCountServiceIncreaseCountLatency = Timer.builder("count.service.timer")
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                ).tags("type", "increaseCount").register(registry);
        distributionSummaryCountServiceConcurrency = DistributionSummary.builder("count.service.ds")
                .maximumExpectedValue(64.0)
                .publishPercentileHistogram()
                .tags("type", "concurrency")
                .register(registry);
        distributionSummaryCountServiceBatchSize = DistributionSummary.builder("count.service.ds")
                .maximumExpectedValue(1024.0)
                .publishPercentileHistogram()
                .tags("type", "batchSize")
                .register(registry);

        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "order")), futureCountService, (o) -> {
            try {
                return o.getCountByFlag("order");
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "product")), futureCountService, (o) -> {
            try {
                return o.getCountByFlag("product");
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "cassandra-index-orderListByUserId")), futureCountService, (o) -> {
            try {
                return o.getCountByFlag("orderListByUserId");
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "cassandra-index-orderListByMerchantId")), futureCountService, (o) -> {
            try {
                return o.getCountByFlag("orderListByMerchantId");
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });

        // 商品指标
        counterProductMetricsCreateOrdinarySuccessfully = registry.counter("product.metrics", "type", "createOrdinarySuccessfully");
        counterProductMetricsCreateFlashSaleSuccessfully = registry.counter("product.metrics", "type", "createFlashSaleSuccessfully");

        // 下单时随机选择商品缓存
        registry.gauge("product.pickup.randomly.stats", Collections.singletonList(new ImmutableTag("type", "ordinary")),
                countService, PickupProductRandomlyWhenPurchasingService.CountService::getOrdinaryProductIdCount);
        registry.gauge("product.pickup.randomly.stats", Collections.singletonList(new ImmutableTag("type", "flashSale")),
                countService, PickupProductRandomlyWhenPurchasingService.CountService::getFlashSaleProductIdCount);
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
