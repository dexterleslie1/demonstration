package com.future.demo.controller;

import lombok.Data;

import java.util.List;

@Data
public class MyBean {
    private String field1;
    private String field2;
    private String param1;
    private List<MyBeanInner> dataList;

    @Data
    public static class MyBeanInner {
        private String field1;
        private String field2;
    }
}
