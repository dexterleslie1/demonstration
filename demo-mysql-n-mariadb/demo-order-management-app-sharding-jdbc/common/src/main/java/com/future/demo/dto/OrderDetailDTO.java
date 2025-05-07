package com.future.demo.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class OrderDetailDTO {
    private Long id;
    private BigInteger orderId;
    private Long productId;
    private Long merchantId;
    private Integer amount;
}
