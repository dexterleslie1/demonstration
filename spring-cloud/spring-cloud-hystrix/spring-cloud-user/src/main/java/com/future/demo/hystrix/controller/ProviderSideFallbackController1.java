package com.future.demo.hystrix.controller;

import com.future.demo.spring.cloud.common.ObjectResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

// 演示服务提供者断配置服务降级
// 一个接口对应一个fallback
// https://blog.csdn.net/a10534126/article/details/124442783
// https://www.shuzhiduo.com/A/ZOJPebAE5v/
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class ProviderSideFallbackController1 {
    @HystrixCommand(fallbackMethod = "testFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })
    @GetMapping("test1")
    ResponseEntity<ObjectResponse<String>> test(@RequestParam(value = "milliseconds", defaultValue = "0") int milliseconds) {
        log.debug("开始调用ProviderSideFallbackController1#test接口");
        if(milliseconds>0) {
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
                //
            }
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功调用ProviderSideFallbackController1#test接口");
        return ResponseEntity.ok(response);
    }

    ResponseEntity<ObjectResponse<String>> testFallback(int milliseconds) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(5000);
        response.setData("调用ProviderSideFallbackController1#test接口失败");
        return ResponseEntity.ok(response);
    }
}
