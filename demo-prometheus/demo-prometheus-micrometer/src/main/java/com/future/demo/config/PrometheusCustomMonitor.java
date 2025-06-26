package com.future.demo.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PrometheusCustomMonitor {

    private Counter orderCounterMaster;
    private Counter orderCounterDetail;

    private final MeterRegistry registry;

    @Autowired
    public PrometheusCustomMonitor(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    private void init() {
        // 会自动转换名为为 my_order_request_count 的指标
        // 指标 my_order_request_count 的 tags 为 order='master'，模拟订单主表
        orderCounterMaster = registry.counter("my.order.request.count", "order", "master");
        // 指标 my_order_request_count 的 tags 为 order='detail'，模拟订单明细表
        orderCounterDetail = registry.counter("my.order.request.count", "order", "detail");
    }

    public void incrementOrderCountMaster() {
        orderCounterMaster.increment();
    }

    public void incrementOrderCountDetail() {
        orderCounterDetail.increment(2);
    }
}
