package com.future.demo;

import org.junit.jupiter.api.*;

// 自定义显示名称
@DisplayName("测试算术运算")
public class DisplayNameTests {

    DemoUtil demoUtil;

    @BeforeEach
    public void beforeEach() {
        demoUtil = new DemoUtil();
    }

    @AfterEach
    public void afterEach() {
        demoUtil = null;
    }

    @Test
    @DisplayName("测试加法")
    public void testAdd() {
        Assertions.assertEquals(3, demoUtil.add(1, 2), "1+2=3");
        Assertions.assertNotEquals(2, demoUtil.add(1, 2), "1+2!=2");
    }

    @Test
    @DisplayName("测试减法")
    public void testSub() {
        Assertions.assertEquals(2, demoUtil.sub(3, 1), "3-1=2");
        Assertions.assertNotEquals(3, demoUtil.sub(3, 1), "3-1!=3");
    }
}
