package com.future.demo.spring.cloud.feign.consumer.controller;

import com.future.demo.spring.cloud.feign.common.entity.Product;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeign;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignWithConfig;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignWithSpecifyUrl;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/v1/external/product")
public class ApiController {
    @Autowired
    ProductFeign productFeign;
    @Autowired
    ProductFeignWithSpecifyUrl productFeignWithSpecifyUrl;
    @Resource
    ProductFeignWithConfig productFeignWithConfig;

    @GetMapping("{productId}")
    public ObjectResponse<Product> info(@PathVariable("productId") Integer productId) throws BusinessException {
        ObjectResponse<Product> response = this.productFeign.info(productId);
        ObjectResponse<Product> response2 = this.productFeignWithSpecifyUrl.info(productId);
        log.info("product2={}", response2);
        ObjectResponse<Product> response3 = this.productFeignWithConfig.info(productId);
        log.info("product3={}", response3);
        return response;
    }

    @GetMapping("get")
    public Product get(@RequestParam(value = "productId", required = false) Integer productId) {
        Product product = this.productFeign.get(productId);
        Product product2 = this.productFeignWithSpecifyUrl.get(productId);
        log.info("product2=" + product2);
        return product;
    }

    @PostMapping("add")
    public String add(Product product) {
        String result = this.productFeign.add("customHeaderValue1", product);
        String result2 = this.productFeignWithSpecifyUrl.add("customHeaderValue1", product);
        log.info("result2=" + result2);
        return result;
    }

    @GetMapping("timeout")
    public String timeout() {
        return this.productFeign.timeout();
    }
}
