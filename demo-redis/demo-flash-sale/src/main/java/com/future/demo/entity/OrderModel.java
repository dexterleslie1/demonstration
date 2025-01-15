package com.future.demo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OrderModel {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer amount;
    private Date createTime;
}
