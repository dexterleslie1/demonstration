package com.future.demo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SharedStore {
    public List<String> sharedList = new ArrayList<>();
}