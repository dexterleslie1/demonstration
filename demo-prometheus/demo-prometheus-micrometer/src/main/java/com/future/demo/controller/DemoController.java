package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.config.PrometheusCustomMonitor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1")
public class DemoController {
    @Resource
    PrometheusCustomMonitor monitor;
    @Resource
    AtomicInteger orderMasterTotalCount;
    @Resource
    AtomicInteger orderDetailTotalCount;

    @GetMapping("testCounter")
    public ObjectResponse<String> testCounter() {
        this.monitor.incrementOrderCountMaster();
        this.monitor.incrementOrderCountDetail();
        return ResponseUtils.successObject("成功下单");
    }

    /**
     * 测试设置订单主表总记录数
     *
     * @return
     */
    @GetMapping("testSetOrderMasterTotalCount")
    public ObjectResponse<String> testSetOrderMasterTotalCount() {
        this.orderMasterTotalCount.incrementAndGet();
        return ResponseUtils.successObject("调用成功");
    }

    /**
     * 测试设置订单明细表总记录数
     *
     * @return
     */
    @GetMapping("testSetOrderDetailTotalCount")
    public ObjectResponse<String> testSetOrderDetailTotalCount() {
        this.orderDetailTotalCount.incrementAndGet();
        return ResponseUtils.successObject("调用成功");
    }
}
