package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        contextId = "testSupportApiFeign",
        value = "app-test-service",
        path = "/api/v1")
public interface TestSupportApiFeign {

    @GetMapping("test401Error")
    ObjectResponse<String> test401Error() throws BusinessException;

    @GetMapping("testHttp200")
    ObjectResponse<String> testHttp200() throws BusinessException;
}
