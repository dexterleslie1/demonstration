package com.future.demo.spring.boot.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSupportConfig.class}, properties = "spring.boot.test.demo.testing=true")
public class ServiceLayerTests {
    @Autowired
    TestService testService;

    @Test
    public void test() {
        int c = this.testService.add(1, 2);
        Assert.assertEquals(0, c);

        Mockito.when(this.testService.add(1, 2)).thenReturn(3);
        c = this.testService.add(1, 2);
        Assert.assertEquals(3, c);

        Mockito.when(this.testService.add(1, 2)).thenReturn(5);
        c = this.testService.add(1, 2);
        Assert.assertEquals(5, c);
    }
}
