package com.future.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ConfigMy {
    /**
     * 订单主表总数，gauge从此数据源获取，模拟从数据库读取订单总数
     *
     * @return
     */
    @Bean
    public AtomicInteger orderMasterTotalCount() {
        return new AtomicInteger();
    }

    /**
     * 订单明细表总数，gauge从此数据源获取，模拟从数据库读取订单总数
     *
     * @return
     */
    @Bean
    public AtomicInteger orderDetailTotalCount() {
        return new AtomicInteger();
    }
}
