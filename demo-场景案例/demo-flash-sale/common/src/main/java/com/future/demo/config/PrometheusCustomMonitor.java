package com.future.demo.config;

import com.future.demo.mapper.CassandraCountMapper;
import com.future.demo.mapper.CommonMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
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

    @Resource
    CommonMapper commonMapper;
    @Resource
    CassandraCountMapper cassandraCountMapper;

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

        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "order")), commonMapper, (o) -> o.getCountByFlag("order"));
        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "cassandra-index-orderListByUserId")), cassandraCountMapper, (o) -> o.getCountByFlag("orderListByUserId"));
        registry.gauge("flash.sale.total.count", Collections.singletonList(new ImmutableTag("type", "cassandra-index-orderListByMerchantId")), cassandraCountMapper, (o) -> o.getCountByFlag("orderListByMerchantId"));
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
