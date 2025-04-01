package com.future.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {Application.class})
public class ApplicationTests {
    @Autowired
    TestComponent testComponent;

    @Test
    public void test() throws Exception {
        this.testComponent.test();
    }
}
