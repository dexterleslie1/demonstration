package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// 入门示例
public class GettingStartedTests {
    @Test
    public void test() {
        DemoUtil demoUtil = new DemoUtil();

        // 断言
        Assertions.assertEquals(10, demoUtil.add(5, 5), "5 + 5 = 10");
        Assertions.assertNotEquals(9, demoUtil.add(2, 8), "2 + 8 != 9");
    }
}
