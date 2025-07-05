package com.future.demo.tasks;

import com.future.demo.service.MerchantService;
import com.future.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class TaskCreateProduct {
    @Resource
    ProductService productService;
    @Resource
    MerchantService merchantService;

    /**
     * 创建普通商品
     */
    /*@Scheduled(cron = "0/1 * * * * ?")
    public void executeCreateOrdinary() throws Exception {
        for (int i = 0; i < 1024; i++) {
            String name = RandomStringUtils.randomAlphanumeric(20);
            Long merchantId = merchantService.getIdRandomly();
            productService.add(name, merchantId, 300, false, null, null);
        }
    }*/
}
