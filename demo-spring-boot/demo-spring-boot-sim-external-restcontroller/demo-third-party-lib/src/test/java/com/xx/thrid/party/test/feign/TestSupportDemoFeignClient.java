package com.xx.thrid.party.test.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        contextId = "testSupportDemoFeignClient",
        value = "demo-spring-boot-test",
        path = "/api/v1")
public interface TestSupportDemoFeignClient {

    @GetMapping("test1")
    ObjectResponse<String> test1() throws BusinessException;

}

