package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductModel {
    private Long id;
    private String name;
    private Integer stock;
    private Long merchantId;
    private boolean flashSale;
    private LocalDateTime flashSaleStartTime;
    private LocalDateTime flashSaleEndTime;
}
