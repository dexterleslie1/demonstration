package com.future.demo;

import org.junit.jupiter.api.*;

// 生命周期
public class LifecycleTests {

    DemoUtil demoUtil;

    @BeforeAll
    static void beforeAll() {
        System.out.println("在所有测试用例前执行一次");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("在所有测试用例后执行一次");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("在每个测试用例前执行一次");
        demoUtil = new DemoUtil();
    }

    @AfterEach
    public void afterEach() {
        System.out.println("在每个测试用例后执行一次");
        demoUtil = null;
    }

    @Test
    public void testAdd() {
        Assertions.assertEquals(3, demoUtil.add(1, 2), "1+2=3");
        Assertions.assertNotEquals(2, demoUtil.add(1, 2), "1+2!=2");
    }

    @Test
    public void testSub() {
        Assertions.assertEquals(2, demoUtil.sub(3, 1), "3-1=2");
        Assertions.assertNotEquals(3, demoUtil.sub(3, 1), "3-1!=3");
    }
}
