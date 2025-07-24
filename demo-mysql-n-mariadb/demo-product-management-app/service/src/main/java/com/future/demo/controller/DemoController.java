package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.ProductService;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1/demo")
@Slf4j
public class DemoController {

    @Resource
    ProductMapper productMapper;
    @Resource
    SnowflakeService snowflakeService;
    @Resource
    ProductService productService;

    private List<Long> idList;

    @PostConstruct
    public void init() {
        // 创建 10000 个商品
        for (int i = 0; i < 10000; i++) {
            String name = RandomStringUtils.randomAlphanumeric(20);
            Long merchantId = RandomUtil.randomLong(1, Long.MAX_VALUE);
            int stockAmount = 999999999;
            ProductModel model = new ProductModel();
            Long id = snowflakeService.getId("product").getId();
            model.setId(id);
            model.setName(name);
            model.setMerchantId(merchantId);
            model.setStock(stockAmount);
            this.productMapper.insert(model);
        }

        this.idList = this.productMapper.listId(30000);
    }

    /**
     * 扣减商品库存
     *
     * @return
     */
    @GetMapping("decreaseProductStockAmount")
    public ObjectResponse<String> decreaseProductStockAmount() {
        int randomInt = RandomUtil.randomInt(0, idList.size());
        Long id = idList.get(randomInt);
        productService.decreaseStockAmount(id);
        return ResponseUtils.successObject("成功扣减商品库存");
    }

    /**
     * 新增商品
     *
     * @return
     */
    @GetMapping("addProduct")
    public ObjectResponse<String> addProduct() {
        Long id = productService.add();
        return ResponseUtils.successObject(String.valueOf(id));
    }
}
