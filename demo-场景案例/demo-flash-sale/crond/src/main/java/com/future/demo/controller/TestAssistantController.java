package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.service.MerchantService;
import com.future.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用于协助测试的api
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TestAssistantController {

    @Resource
    MerchantService merchantService;
    @Resource
    ProductService productService;

    /**
     * 新增普通商品
     *
     * @return
     * @throws Exception
     */
    @GetMapping("addOrdinaryProduct")
    public ObjectResponse<Long> addOrdinaryProduct(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "stockAmount", required = false) Integer stockAmount
    ) throws Exception {
        if (StringUtils.isBlank(name))
            name = RandomStringUtils.randomAlphanumeric(20);
        if (merchantId == null || merchantId <= 0)
            merchantId = this.merchantService.getIdRandomly();
        if (stockAmount == null || stockAmount < 0)
            stockAmount = 300;
        Long productId = this.productService.add(name, merchantId, stockAmount, false, null, null);
        return ResponseUtils.successObject(productId);
    }

    /**
     * 新增秒杀商品
     *
     * @return
     * @throws Exception
     */
    @GetMapping("addFlashSaleProduct")
    public ObjectResponse<Long> addFlashSaleProduct(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "stockAmount", required = false) Integer stockAmount,
            @RequestParam(value = "flashSaleStartTime", required = false) String flashSaleStartTimeStr,
            @RequestParam(value = "flashSaleEndTime", required = false) String flashSaleEndTimeStr
    ) throws Exception {
        if (StringUtils.isBlank(name))
            name = RandomStringUtils.randomAlphanumeric(20);
        if (merchantId == null || merchantId <= 0)
            merchantId = this.merchantService.getIdRandomly();
        if (stockAmount == null || stockAmount < 0)
            stockAmount = 300;

        LocalDateTime flashSaleStartTime = null;
        if (!StringUtils.isBlank(flashSaleStartTimeStr)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            flashSaleStartTime = LocalDateTime.parse(flashSaleStartTimeStr, dateTimeFormatter);
        }
        if (flashSaleStartTime == null)
            flashSaleStartTime = this.productService.getFlashSaleStartTimeRandomly(0);
        LocalDateTime flashSaleEndTime = null;
        if (!StringUtils.isBlank(flashSaleEndTimeStr)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            flashSaleEndTime = LocalDateTime.parse(flashSaleEndTimeStr, dateTimeFormatter);
        }
        if (flashSaleEndTime == null)
            flashSaleEndTime = this.productService.getFlashSaleEndTimeRandomly(flashSaleStartTime);
        Long productId = this.productService.add(name, merchantId, stockAmount, true, flashSaleStartTime, flashSaleEndTime);
        return ResponseUtils.successObject(productId);
    }
}
