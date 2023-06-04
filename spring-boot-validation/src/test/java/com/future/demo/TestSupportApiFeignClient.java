package com.future.demo;

import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        contextId = "testSupportApiFeignClient",
        value = "testSupportApiFeignClient",
        path = "/api/v1")
public interface TestSupportApiFeignClient {

    @GetMapping("testSingleParam")
    ObjectResponse<String> testSingleParam(@RequestParam(value = "p1") String p1) throws BusinessException;

}

