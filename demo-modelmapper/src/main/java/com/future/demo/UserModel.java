package com.future.demo;

import lombok.Data;

@Data
public class UserModel {
    private Long id;
    private String name;

    // 用于测试自定义属性转换
    private int age;
    // 用于测试自定义属性转换
    private Long categoryId;
}
