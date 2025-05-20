package com.future.demo.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailModel {
    private Long id;
    private BigDecimal orderId;
    private Long userId;
    private Long productId;
    private Long merchantId;
    private Integer amount;
}
