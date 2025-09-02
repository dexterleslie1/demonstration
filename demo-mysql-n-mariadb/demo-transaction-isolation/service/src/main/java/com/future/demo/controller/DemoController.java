package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.service.PerformanceTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DemoController {
    @Resource
    PerformanceTestService performanceTestService;

    @GetMapping("selectByValueRangeWithIsolationReadUncommitted")
    public ObjectResponse<String> selectByValueRangeWithIsolationReadUncommitted() {
        performanceTestService.selectByValueRangeWithIsolationReadUncommitted();
        return ResponseUtils.successObject("成功调用");
    }

    @GetMapping("selectByValueRangeWithIsolationReadCommitted")
    public ObjectResponse<String> selectByValueRangeWithIsolationReadCommitted() {
        performanceTestService.selectByValueRangeWithIsolationReadCommitted();
        return ResponseUtils.successObject("成功调用");
    }

    @GetMapping("selectByValueRangeWithIsolationRepeatableRead")
    public ObjectResponse<String> selectByValueRangeWithIsolationRepeatableRead() {
        performanceTestService.selectByValueRangeWithIsolationRepeatableRead();
        return ResponseUtils.successObject("成功调用");
    }

    @GetMapping("selectByValueRangeWithIsolationSerializable")
    public ObjectResponse<String> selectByValueRangeWithIsolationSerializable() {
        performanceTestService.selectByValueRangeWithIsolationSerializable();
        return ResponseUtils.successObject("成功调用");
    }

    @GetMapping("updateValue")
    public ObjectResponse<String> updateValue() {
        performanceTestService.updateValue();
        return ResponseUtils.successObject("成功调用");
    }
}
