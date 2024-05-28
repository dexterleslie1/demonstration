package com.future.demo.mybatis.entity;

import lombok.Data;

@Data
public class EmpModel {
    private Long id;
    private String name;
    private Integer age;
    private AddressModel address;
    private DeptModel dept;
}
