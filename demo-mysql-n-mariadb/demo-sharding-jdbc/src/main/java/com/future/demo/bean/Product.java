package com.future.demo.bean;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private LocalDateTime createTime;
    private Long descriptionId;
    private String description;
}
