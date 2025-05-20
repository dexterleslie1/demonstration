package com.future.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {
    private Long id;
    private BigDecimal orderId;
    private Long productId;
    private Long merchantId;
    private Integer amount;
}
