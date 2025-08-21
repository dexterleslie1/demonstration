package com.future.demo.entity;

import lombok.Data;

@Data
public class OrderDetailModel {
    private Long id;
    /*private BigInteger orderId;*/
    private Long orderId;
    private Long userId;
    private Long productId;
    private Long merchantId;
    private Integer amount;
}
