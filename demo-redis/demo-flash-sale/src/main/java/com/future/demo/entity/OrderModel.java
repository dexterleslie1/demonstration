package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderModel {
    private Long id;
    private Long userId;
    /*private Long productId;
    private Integer amount;*/
    private LocalDateTime createTime;
}
