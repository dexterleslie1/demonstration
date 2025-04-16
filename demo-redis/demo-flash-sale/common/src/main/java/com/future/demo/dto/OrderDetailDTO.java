package com.future.demo.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer amount;
}
