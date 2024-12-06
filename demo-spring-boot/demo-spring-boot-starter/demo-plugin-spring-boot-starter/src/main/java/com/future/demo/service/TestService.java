package com.future.demo.service;

import com.future.demo.properties.TestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    TestProperties testProperties;

    public TestProperties getTestProperties() {
        return testProperties;
    }
}
