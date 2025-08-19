package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Employee {
    private Long id;
    private String name;
    private Integer age;
    private String position;
    private LocalDateTime hireTime;
    private String remark;
}
