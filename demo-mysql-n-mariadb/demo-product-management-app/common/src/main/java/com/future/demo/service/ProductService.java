package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.ProductMapper;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductService {

    @Autowired
    SnowflakeService snowflakeService;
    @Autowired
    ProductMapper productMapper;

    /**
     * 扣减商品库存
     *
     * @param id
     */
    public void decreaseStockAmount(Long id) {
        productMapper.decreaseStock(id, 1);
    }

    /**
     * 新增商品
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public Long add() {
        String name = RandomStringUtils.randomAlphanumeric(20);
        Long merchantId = RandomUtil.randomLong(1, Long.MAX_VALUE);
        int stockAmount = 999999999;
        ProductModel model = new ProductModel();
        Long id = snowflakeService.getId("product").getId();
        model.setId(id);
        model.setName(name);
        model.setMerchantId(merchantId);
        model.setStock(stockAmount);
        productMapper.insert(model);
        return id;
    }
}
