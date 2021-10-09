package com.future.demo.auto.configuration;

import com.future.demo.starter.TestPropertiesPrinter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
    @Autowired
    TestPropertiesPrinter testPropertiesPrinter;

    @Test
    public void test() {
        String str = testPropertiesPrinter.print();
        log.debug(str);
    }
}
