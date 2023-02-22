package com.future.demo.spring.cloud.feign.common.feign;

import com.future.demo.spring.cloud.feign.common.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "spring-cloud-feign-demo-provider")
@RequestMapping("/api/v1/product")
public interface ProductFeign {
    @GetMapping("{productId}")
    Product info(@PathVariable("productId") Integer productId);

    @GetMapping("get")
    Product get(@RequestParam(value = "productId", required = false) Integer productId);

    @PostMapping("add")
    String add(@RequestHeader(value = "customHeader") String customHeader,
               @RequestBody(required = false) Product product);

    @GetMapping("timeout")
    String timeout();
}
