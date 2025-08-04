package com.future.demo.dto;

import lombok.Data;

/**
 * 修改商品库存请求，用于协助批量修改库存
 */
@Data
public class UpdateProductStockAmountReq {
    private Long productId;
    private Integer stockAmount;
}
