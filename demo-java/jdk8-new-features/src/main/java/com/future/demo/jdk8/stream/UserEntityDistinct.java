package com.future.demo.jdk8.stream;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserEntityDistinct {
    private String name;
    private int age;

    private List<NestedClass> myList;

    @Data
    @AllArgsConstructor
    public static class NestedClass {
        private Long id;
    }
}
