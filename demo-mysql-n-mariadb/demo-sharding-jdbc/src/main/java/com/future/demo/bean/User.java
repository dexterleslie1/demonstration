package com.future.demo.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String name;
    private BigDecimal balance;
    private LocalDateTime createTime;
}
