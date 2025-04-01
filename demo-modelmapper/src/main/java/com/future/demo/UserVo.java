package com.future.demo;

import lombok.Data;

@Data
public class UserVo {
    private Long id;
    private String name;

    // 用于测试自定义属性转换
    private int ageTest;
    // 用于测试自定义属性转换
    private CategoryVo category = null;

    @Data
    public static class CategoryVo {
        private Long id;
    }
}
