package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PerformanceTest {
    private Long id;
    private String name;
    private Integer value;
    private String description;
    private LocalDateTime createdAt;
}