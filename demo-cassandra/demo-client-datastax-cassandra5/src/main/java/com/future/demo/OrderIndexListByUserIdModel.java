package com.future.demo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderIndexListByUserIdModel {
    private Long userId;
    private String status;
    private LocalDateTime createTime;
    private Long orderId;
}
