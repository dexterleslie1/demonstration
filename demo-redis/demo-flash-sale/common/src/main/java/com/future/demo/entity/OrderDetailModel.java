package com.future.demo.entity;

import lombok.Data;

@Data
public class OrderDetailModel {
    private Long id;
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer amount;
}
