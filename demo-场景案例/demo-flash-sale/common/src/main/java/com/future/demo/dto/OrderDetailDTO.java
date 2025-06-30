package com.future.demo.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Long merchantId;
    private Integer amount;
}
