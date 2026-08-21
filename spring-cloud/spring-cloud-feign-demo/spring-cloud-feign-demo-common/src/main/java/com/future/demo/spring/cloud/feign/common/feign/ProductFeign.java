package com.future.demo.spring.cloud.feign.common.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.demo.spring.cloud.feign.common.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        contextId = "productFeign1",
        value = "spring-cloud-feign-demo-provider",
        path = "/api/v1/product")
public interface ProductFeign {
    @GetMapping
    ListResponse<Product> info(@RequestParam("productIds") List<Integer> productIds) throws BusinessException;

    @GetMapping("get")
    Product get(@RequestParam(value = "productId", required = false) Integer productId);

    @PostMapping("add")
    String add(@RequestHeader(value = "customHeader") String customHeader,
               @RequestBody(required = false) Product product);

    @GetMapping("timeout")
    String timeout();

    @GetMapping("testOpenFeignPerfAssist")
    ObjectResponse<String> testOpenFeignPerfAssist();

    /**
     * 协助测试非 http 200 响应
     *
     * @return
     */
    @GetMapping("testAssistantFeignErrorNonHttp200")
    ObjectResponse<String> testAssistantFeignErrorNonHttp200() throws BusinessException;

    /**
     * 协助测试 http 200 响应业务异常
     *
     * @return
     */
    @GetMapping("testAssistantFeignErrorHttp200WithBusinessException")
    ObjectResponse<String> testAssistantFeignErrorHttp200WithBusinessException() throws BusinessException;
}
