package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.config.PrometheusCustomMonitor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1")
public class DemoController {
    @Resource
    PrometheusCustomMonitor monitor;

    @GetMapping("testCounter")
    public ObjectResponse<String> testCounter() {
        this.monitor.incrementOrderCountMaster();
        this.monitor.incrementOrderCountDetail();
        return ResponseUtils.successObject("成功下单");
    }
}
