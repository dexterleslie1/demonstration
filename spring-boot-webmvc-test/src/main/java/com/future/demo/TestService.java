package com.future.demo;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public int add(int a, int b) {
        int c = a+b;
        return c;
    }
}
