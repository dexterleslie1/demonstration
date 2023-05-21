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
import org.springframework.test.context.junit4.SpringRunner;

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
