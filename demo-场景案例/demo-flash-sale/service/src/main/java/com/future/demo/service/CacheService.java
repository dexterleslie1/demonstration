package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.entity.ProductModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CacheService {
    /**
     * 普通商品列表
     */
    private List<ProductModel> ordinaryProductList;
    /**
     * 秒杀商品列表
     */
    private List<ProductModel> flashSaleProductList;

    /**
     * 设置商品列表
     *
     * @param modelList
     */
    public void setProductList(List<ProductModel> modelList) {
        if (modelList.isEmpty())
            return;

        // 设置普通商品列表
        ordinaryProductList = modelList.stream().filter(o -> !o.isFlashSale()).collect(Collectors.toList());
        if (log.isDebugEnabled())
            log.debug("设置普通商品列表缓存 {}", ordinaryProductList);

        // 设置秒杀商品列表
        flashSaleProductList = modelList.stream().filter(ProductModel::isFlashSale).collect(Collectors.toList());
        if (log.isDebugEnabled())
            log.debug("设置秒杀商品列表缓存 {}", flashSaleProductList);
    }

    /**
     * 随机抽取普通商品ID
     *
     * @return
     */
    public long getOrdinaryProductIdRandomly() {
        if (ordinaryProductList == null || ordinaryProductList.isEmpty())
            throw new IllegalArgumentException("缓存中没有普通商品列表");

        return ordinaryProductList.get(RandomUtil.randomInt(0, ordinaryProductList.size())).getId();
    }

    /**
     * 随机抽取秒杀商品ID
     *
     * @return
     */
    public long getFlashSaleProductIdRandomly() {
        if (flashSaleProductList == null || flashSaleProductList.isEmpty())
            throw new IllegalArgumentException("缓存中没有秒杀商品列表");

        return flashSaleProductList.get(RandomUtil.randomInt(0, flashSaleProductList.size())).getId();
    }
}
