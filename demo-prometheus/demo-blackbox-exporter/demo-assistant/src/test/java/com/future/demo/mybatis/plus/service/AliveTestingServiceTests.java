package com.future.demo.mybatis.plus.service;

import com.future.demo.mybatis.plus.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class AliveTestingServiceTests {
    @Autowired
    AliveTestingService aliveTestingService;

    @Test
    public void testCheckAlive() {
        this.aliveTestingService.checkAlive();
    }
}
