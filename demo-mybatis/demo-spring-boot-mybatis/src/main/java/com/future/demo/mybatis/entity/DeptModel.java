package com.future.demo.mybatis.entity;

import lombok.Data;

import java.util.List;

@Data
public class DeptModel {
    private Long id;
    private String name;

    private List<EmpModel> empList;
}
