package com.future.demo;

import com.future.demo.controller.ClothGoodsController;
import com.future.demo.repository.ClothGoodsRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class ConfigPostgresqlInit {

    @Resource
    ClothGoodsRepository clothGoodsRepository;

    @PostConstruct
    public void init() {
        // 获取最大goodsId
        Long maxGoodsId = clothGoodsRepository.findMaxGoodsId();
        if (maxGoodsId != null) {
            ClothGoodsController.idCounter.set(maxGoodsId + 1);
        }
    }

}
