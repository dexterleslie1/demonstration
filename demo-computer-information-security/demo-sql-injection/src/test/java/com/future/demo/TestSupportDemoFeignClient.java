package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        contextId = "testSupportDemoFeignClient",
        value = "demo-spring-boot-test",
        path = "/api/v1")
public interface TestSupportDemoFeignClient {

    @GetMapping("jdbc/testErrorBasedSqlInjection")
    ObjectResponse<String> testErrorBasedSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws BusinessException;

    @GetMapping("jdbc/testBooleanBasedBlindSqlInjection")
    ObjectResponse<String> testBooleanBasedBlindSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws BusinessException;

    @GetMapping("jdbc/testTimeBasedBlindSqlInjection")
    ObjectResponse<Long> testTimeBasedBlindSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws BusinessException;

    @GetMapping("jdbc/testUnionBasedSqlInjection")
    ListResponse<String> testUnionBasedSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws BusinessException;

    @GetMapping("mybatis-plus/testSqlInjection")
    ListResponse<String> testMybatisPlusSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws BusinessException;

    @GetMapping("mybatis-plus/testWhereInSqlInjection")
    ListResponse<String> testMybatisPlusWhereInSqlInjection(
            @RequestParam(name = "idList", required = false) List<String> idList
    ) throws BusinessException;
}

