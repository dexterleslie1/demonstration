package com.future.demo.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Book {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;
}
