package com.future.demo.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String address;
    private BigDecimal amount;
    private Long customerId;
    private LocalDateTime createTime;

    /**
     * 订单对应的客户
     */
    private Customer customer;
}
