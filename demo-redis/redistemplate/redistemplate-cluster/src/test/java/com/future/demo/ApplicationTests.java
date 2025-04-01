package com.future.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {

    @Autowired
    TestComponent testComponent;

    @Test
    public void test() throws Exception {
        this.testComponent.test();
    }

    @Test
    public void testRead() throws Exception {
        this.testComponent.testRead();
    }
}
