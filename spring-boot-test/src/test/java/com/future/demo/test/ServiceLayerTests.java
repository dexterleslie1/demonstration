package com.future.demo.test;

import com.future.demo.Application;
import com.future.demo.TestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

// https://stackoverflow.com/questions/42249791/resolving-port-already-in-use-in-a-spring-boot-test-defined-port
@DirtiesContext
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ServiceLayerTests {

    @SpyBean
    TestService testService;

    @Test
    public void test() {
        int c = this.testService.add(1, 2);
        Assert.assertEquals(3, c);

        Mockito.when(this.testService.add(1, 2)).thenReturn(5);
        c = this.testService.add(1, 2);
        Assert.assertEquals(5, c);
    }
}
