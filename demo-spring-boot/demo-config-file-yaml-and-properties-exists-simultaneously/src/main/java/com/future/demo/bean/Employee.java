package com.future.demo.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    private Long id;
    private String empName;
    private Integer age;
    private Double empSalary;
}
