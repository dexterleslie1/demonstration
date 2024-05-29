package com.future.demo.test.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        contextId = "testSupportDemoFeignClient",
        value = "demo-spring-boot-test",
        path = "/api/v1")
public interface TestSupportDemoFeignClient {

    @GetMapping("add")
    ObjectResponse<Integer> add(@RequestParam(value = "a") int a,
                                @RequestParam(value = "b") int b,
                                @RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;

    @GetMapping("minus")
    ObjectResponse<Integer> minus(@RequestParam(value = "a") int a,
                                  @RequestParam(value = "b") int b,
                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;

    @PostMapping("addUser")
    ObjectResponse<String> addUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;

}

