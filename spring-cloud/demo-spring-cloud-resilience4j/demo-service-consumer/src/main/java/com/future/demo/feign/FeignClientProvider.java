package com.future.demo.feign;

import com.future.common.http.ObjectResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "demo-service-provider", path = "/api/v1")
public interface FeignClientProvider {
    @GetMapping("test1")
    // 配置 Feign 客户端 circuitbreaker resilience4j 服务熔断和降级
    @CircuitBreaker(name = "demo-service-provider", fallbackMethod = "test1Fallback")
    /*// 配置 Feign 客户端 resilience4j bulkhead（舱壁隔离）
    @Bulkhead(name = "demo-service-provider", fallbackMethod = "test1Fallback", type = Bulkhead.Type.SEMAPHORE)*/
    public ObjectResponse<String> test1(@RequestParam(value = "flag", defaultValue = "") String flag) throws Throwable;

    // 服务降级 fallback 方法
    default public ObjectResponse<String> test1Fallback(Throwable throwable) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(throwable.getMessage());
        return response;
    }
}
