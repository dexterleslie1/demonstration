package com.future.demo.spring.cloud.feign.common.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.demo.spring.cloud.feign.common.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        value = "spring-cloud-feign-demo-provider-config",
        path = "/api/v1/product")
public interface ProductFeignWithConfig {
    @GetMapping
    ListResponse<Product> info(@RequestParam("productIds") List<Integer> productIds);

    @GetMapping("get")
    Product get(@RequestParam(value = "productId", required = false) Integer productId);

    @PostMapping("add")
    String add(@RequestHeader(value = "customHeader") String customHeader,
               @RequestBody(required = false) Product product);

    @GetMapping("timeout")
    String timeout();

    @GetMapping("test401Error")
    ObjectResponse<String> test401Error() throws BusinessException;
}
