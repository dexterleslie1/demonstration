package com.future.demo;

import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        contextId = "testSupportApiFeign",
        value = "app-test-service",
        path = "/api/v1")
public interface TestSupportApiFeign {

    @GetMapping("test401Error")
    public ObjectResponse<String> test401Error() throws BusinessException;

}