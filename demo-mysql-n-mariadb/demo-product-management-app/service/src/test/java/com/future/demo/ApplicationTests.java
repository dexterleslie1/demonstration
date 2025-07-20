package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.ProductMapper;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Resource
    ProductMapper productMapper;
    @Resource
    SnowflakeService snowflakeService;

    @Test
    public void contextLoads() {
        String name = RandomStringUtils.randomAlphanumeric(20);
        Long merchantId = RandomUtil.randomLong(1, Long.MAX_VALUE);
        int stockAmount = 300;
        ProductModel model = new ProductModel();
        Long id = snowflakeService.getId("product").getId();
        model.setId(id);
        model.setName(name);
        model.setMerchantId(merchantId);
        model.setStock(stockAmount);
        int affectRows = this.productMapper.insert(model);
        Assertions.assertEquals(1, affectRows);

        affectRows = this.productMapper.decreaseStock(id, 1);
        Assertions.assertEquals(1, affectRows);
        model = this.productMapper.getById(id);
        Assertions.assertEquals(stockAmount - 1, model.getStock());
    }

    void reset() {
    }
}
