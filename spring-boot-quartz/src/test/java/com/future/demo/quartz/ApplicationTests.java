package com.future.demo.quartz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
// 测试时不执行quartz
@TestPropertySource(properties = "app.scheduling.enable=false")
public class ApplicationTests {
    @Test
    public void test() {
        System.out.println("place holder");
    }
}
