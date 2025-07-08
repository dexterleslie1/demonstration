package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DemoController {
    /**
     * 用于测试有debug日志时的性能
     * @return
     */
    @GetMapping("testPerfWithDebugLog")
    public ObjectResponse<String> testPerfWithDebugLog() {
        if (log.isDebugEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.debug("当前时间：{}", localDateTimeNow);
        }
        if (log.isDebugEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.debug("当前时间：{}", localDateTimeNow);
        }
        if (log.isDebugEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.debug("当前时间：{}", localDateTimeNow);
        }
        if (log.isDebugEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.debug("当前时间：{}", localDateTimeNow);
        }
        if (log.isDebugEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.debug("当前时间：{}", localDateTimeNow);
        }
        return ResponseUtils.successObject(UUID.randomUUID().toString());
    }

    /**
     * 用于测试有没有日志时的性能
     * @return
     */
    @GetMapping("testPerfWithoutLog")
    public ObjectResponse<String> testPerfWithoutLog() {
        if (log.isTraceEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.trace("当前时间：{}", localDateTimeNow);
        }
        if (log.isTraceEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.trace("当前时间：{}", localDateTimeNow);
        }
        if (log.isTraceEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.trace("当前时间：{}", localDateTimeNow);
        }
        if (log.isTraceEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.trace("当前时间：{}", localDateTimeNow);
        }
        if (log.isTraceEnabled()) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            log.trace("当前时间：{}", localDateTimeNow);
        }
        return ResponseUtils.successObject(UUID.randomUUID().toString());
    }
}
