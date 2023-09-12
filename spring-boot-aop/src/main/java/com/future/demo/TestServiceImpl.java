package com.future.demo;

import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestServiceI {
    @Override
    public String test1(Long param1, String param2) {
        return "param1=" + param1 + ",param2=" + param2;
    }
}
