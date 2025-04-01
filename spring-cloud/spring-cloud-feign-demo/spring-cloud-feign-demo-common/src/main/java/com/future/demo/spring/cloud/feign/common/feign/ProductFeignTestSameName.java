package com.future.demo.spring.cloud.feign.common.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.demo.spring.cloud.feign.common.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 演示通过contextId解决重复feign问题
 * https://stackoverflow.com/questions/69950791/how-to-make-multiple-feignclient-s-to-use-same-serviceid-name
 */
@FeignClient(
        contextId = "productFeign2",
        value = "spring-cloud-feign-demo-provider",
        path = "/api/v1/product")
public interface ProductFeignTestSameName {
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
