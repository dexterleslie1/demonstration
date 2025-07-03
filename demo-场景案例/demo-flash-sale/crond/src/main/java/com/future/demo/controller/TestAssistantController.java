package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.service.OrderService;
import com.future.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用于协助测试的api
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TestAssistantController {
    @Resource
    OrderService orderService;
    @Resource
    ProductService productService;

    /**
     * 重新初始化商品信息
     *
     * @return
     */
    @GetMapping(value = "initProduct")
    public ObjectResponse<String> initProduct() throws Exception {
        this.productService.initCaching();
        this.orderService.initProduct();
        return ResponseUtils.successObject("成功初始化商品信息");
    }
}
