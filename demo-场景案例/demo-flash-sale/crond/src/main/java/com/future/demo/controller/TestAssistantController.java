package com.future.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于协助测试的api
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TestAssistantController {
//    @Resource
//    OrderService orderService;
//    @Resource
//    ProductService productService;
//    @Resource
//    MerchantService merchantService;
//
//    /**
//     * 重新初始化商品信息
//     *
//     * @return
//     */
//    @GetMapping(value = "initProduct")
//    public ObjectResponse<String> initProduct() throws Exception {
//        this.productService.initCaching();
//        this.orderService.initProduct();
//        return ResponseUtils.successObject("成功初始化商品信息");
//    }
}
