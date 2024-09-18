package com.future.demo;

import com.future.demo.service.LargeTransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class LargeTransactionServiceTests {
    @Resource
    LargeTransactionService largeTransactionService;

    /**
     * 执行大事务
     */
    @Test
    public void test() {
        largeTransactionService.execute();
    }
}
