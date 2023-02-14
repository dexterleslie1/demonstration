package com.future.demo.mybatis.entity;

import lombok.Data;

@Data
public class UserModel {
    private Long id;
    private String name;
    private String password;
    private int age;
}
