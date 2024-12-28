package com.future.demo.spring.cloud.feign.consumer.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.demo.spring.cloud.feign.common.entity.Product;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeign;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignTestSameName;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignWithConfig;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignWithSpecifyUrl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/external/product")
public class ApiController {
    @Resource
    ProductFeign productFeign;
    @Resource
    ProductFeignTestSameName productFeignTestSameName;
    @Resource
    ProductFeignWithSpecifyUrl productFeignWithSpecifyUrl;
    @Resource
    ProductFeignWithConfig productFeignWithConfig;

    @GetMapping("{productId}")
    public ObjectResponse<Product> info(@PathVariable("productId") Integer productId) throws BusinessException {
        ObjectResponse<Product> response = this.productFeign.info(productId);
        /*ObjectResponse<Product> response2 = this.productFeignWithSpecifyUrl.info(productId);
        log.info("product2={}", response2);
        ObjectResponse<Product> response3 = this.productFeignWithConfig.info(productId);
        log.info("product3={}", response3);
        ObjectResponse<Product> response5 = this.productFeignTestSameName.info(productId);
        log.info("product5={}", response5);*/
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

    // 测试feign调用接口返回401 http错误是否能够正常解析返回错误信息
    @GetMapping("test401Error")
    public ObjectResponse<String> test401Error() throws BusinessException {
        return this.productFeignWithConfig.test401Error();
    }
}
