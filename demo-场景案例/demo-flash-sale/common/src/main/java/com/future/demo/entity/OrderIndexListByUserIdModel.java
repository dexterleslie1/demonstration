package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderIndexListByUserIdModel {
    private Long userId;
    private Status status;
    private LocalDateTime createTime;
    private Long orderId;
}
