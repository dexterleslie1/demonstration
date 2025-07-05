package com.future.demo.dto;

import com.future.demo.entity.ProductModel;
import lombok.Data;

/**
 * 秒杀商品缓存更新事件DTO
 */
@Data
public class FlashSaleProductCacheUpdateEventDTO {
    /**
     * 秒杀商品model
     */
    private ProductModel productModel;
    /**
     * 在秒杀结束多少秒后自动删除缓存中的秒杀商品，默认一分钟后
     */
    private int secondAfterWhichExpiredFlashSaleProductForRemoving;
}
