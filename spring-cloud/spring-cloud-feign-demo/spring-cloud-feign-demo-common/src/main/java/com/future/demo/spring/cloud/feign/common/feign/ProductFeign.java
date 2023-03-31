package com.future.demo.spring.cloud.feign.common.feign;

import com.future.demo.spring.cloud.feign.common.entity.Product;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        value = "spring-cloud-feign-demo-provider",
        path = "/api/v1/product")
public interface ProductFeign {
    @GetMapping("{productId}")
    ObjectResponse<Product> info(@PathVariable("productId") Integer productId) throws BusinessException;

    @GetMapping("get")
    Product get(@RequestParam(value = "productId", required = false) Integer productId);

    @PostMapping("add")
    String add(@RequestHeader(value = "customHeader") String customHeader,
               @RequestBody(required = false) Product product);

    @GetMapping("timeout")
    String timeout();
}
