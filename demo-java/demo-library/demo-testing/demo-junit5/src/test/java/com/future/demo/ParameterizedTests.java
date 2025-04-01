package com.future.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

// 参数化测试
public class ParameterizedTests {
    DemoUtil demoUtil;

    @BeforeEach
    public void beforeEach() {
        demoUtil = new DemoUtil();
    }

    @AfterEach
    public void afterEach() {
        demoUtil = null;
    }

    // 测试@CsvSource
    @ParameterizedTest(name = "加法：{0}+{1}={2}")
    @CsvSource({
            "1,2,3",
            "4,5,9",
            "7,8,15"
    })
    public void testCsvSource(int a, int b, int c) {
        Assertions.assertEquals(c, this.demoUtil.add(a, b), a + "+" + b + "=" + c);
    }

    // 测试@CsvFileSource
    @ParameterizedTest(name = "加法：{0}+{1}={2}")
    @CsvFileSource(resources = "/test.csv")
    public void testCsvFileSource(int a, int b, int c) {
        Assertions.assertEquals(c, this.demoUtil.add(a, b), a + "+" + b + "=" + c);
    }

    // 测试 @MethodSource 从另外一个方法中获取参数
    @ParameterizedTest(name = "加法：{0}+{1}={2}")
    @MethodSource("methodSourceSupplier")
    public void testMethodSource(int a, int b, int c) {
        Assertions.assertEquals(c, this.demoUtil.add(a, b), a + "+" + b + "=" + c);
    }

    static Stream<Arguments> methodSourceSupplier() {
        return Stream.of(
                Arguments.of(1, 2, 3),
                Arguments.of(4, 5, 9),
                Arguments.of(7, 8, 15)
        );
    }

    // 测试 @ValueSource 从一个数组中获取参数
    @ParameterizedTest(name = "测试 @ValueSource 从一个数组中获取参数 {0}")
    @ValueSource(ints = {1, 2, 3})
    public void testValueSource(int a) {
        Assertions.assertTrue(a > 0);
    }
}
