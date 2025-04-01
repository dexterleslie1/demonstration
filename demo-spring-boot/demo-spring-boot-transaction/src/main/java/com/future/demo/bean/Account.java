package com.future.demo.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
    private Long id;
    private String username;
    private int age;
    private BigDecimal balance;
}
