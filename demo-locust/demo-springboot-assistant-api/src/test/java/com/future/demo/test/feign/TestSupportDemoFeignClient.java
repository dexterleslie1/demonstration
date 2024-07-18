package com.future.demo.test.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        contextId = "testSupportDemoFeignClient",
        value = "demo-spring-boot-test",
        path = "/api/v1")
public interface TestSupportDemoFeignClient {

    @GetMapping("testGet")
    ObjectResponse<String> testGet() throws BusinessException;
}

