package com.future.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreOrderDto {
    private Long productId;
    private Long userId;
    private Integer amount;
}
