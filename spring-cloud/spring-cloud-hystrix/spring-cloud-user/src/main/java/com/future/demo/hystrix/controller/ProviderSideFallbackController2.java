package com.future.demo.hystrix.controller;

import com.future.demo.spring.cloud.common.ObjectResponse;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
// 可以统一fallback逻辑
@DefaultProperties(defaultFallback = "testFallback", commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
})
public class ProviderSideFallbackController2 {
    @HystrixCommand
    @GetMapping("test2")
    ResponseEntity<ObjectResponse<String>> test(@RequestParam(value = "milliseconds", defaultValue = "0") int milliseconds) {
        log.debug("开始调用ProviderSideFallbackController2#test接口");
        if(milliseconds>0) {
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
                //
            }
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功调用ProviderSideFallbackController2#test接口");
        return ResponseEntity.ok(response);
    }

    ResponseEntity<ObjectResponse<String>> testFallback() {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(5000);
        response.setData("调用ProviderSideFallbackController2#test接口失败");
        return ResponseEntity.ok(response);
    }
}
