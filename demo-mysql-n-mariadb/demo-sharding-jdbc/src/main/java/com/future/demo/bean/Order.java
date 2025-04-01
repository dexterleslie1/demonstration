package com.future.demo.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private LocalDateTime createTime;
    private Long userId;
    private Long merchantId;
    private BigDecimal totalAmount;
    private Integer totalCount;
    private Status status;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receivedTime;
    private LocalDateTime cancelTime;
    private DeleteStatus deleteStatus;
}
