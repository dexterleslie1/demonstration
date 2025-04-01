package com.future.demo.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Person {
    Long id;
    private String name;
    private int age;
    private String [] hobby;
    private Address address;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String city;
        private String country;
    }
}
