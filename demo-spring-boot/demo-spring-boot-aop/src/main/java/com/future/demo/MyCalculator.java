package com.future.demo;

import org.springframework.stereotype.Component;

@Component
public class MyCalculator {
    public int add(int a, int b) {
        return a + b;
    }

    public int div(int a, int b) {
        return a / b;
    }
}
