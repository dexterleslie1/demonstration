package com.future.demo.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PrometheusCustomMonitor {

    private Counter counterOrderSyncCount;

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
    }

    public void incrementOrderSyncCount(int count) {
        counterOrderSyncCount.increment(count);
    }
}
