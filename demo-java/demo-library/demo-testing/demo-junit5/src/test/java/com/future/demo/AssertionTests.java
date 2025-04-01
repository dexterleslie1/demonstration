package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;

public class AssertionTests {
    @Test
    public void test() {
        // 测试assertSame和assertNotSame
        Object obj1 = new Object();
        Object obj2 = obj1;
        Object obj3 = new Object();
        Assertions.assertSame(obj1, obj2, "obj1和obj2是同一个对象");
        Assertions.assertNotSame(obj1, obj3, "obj1和obj3不是同一个对象");

        // 测试assertTrue和assertFalse
        Assertions.assertTrue(1 > 0, "1大于0");
        Assertions.assertFalse(1 < 0, "1大于0");

        // 测试assertArrayEquals
        String[] strArr1 = Arrays.asList("a", "b").toArray(new String[]{});
        String[] strArr2 = Arrays.asList("a", "b").toArray(new String[]{});
        Assertions.assertArrayEquals(strArr1, strArr2);

        // 测试assertIterableEquals
        Assertions.assertIterableEquals(Arrays.asList("a", "b"), Arrays.asList("a", "b"));
        Assertions.assertIterableEquals(Arrays.asList(1, 2), Arrays.asList(1, 2));

        // 测试assertLinesMatch
        Assertions.assertLinesMatch(Arrays.asList("a", "b"), Arrays.asList("a", "b"));

        // 测试assertThrows和assertDoesNotThrow
        Assertions.assertThrows(ArithmeticException.class, () -> {
            int a = 1 / 0;
        });
        Assertions.assertDoesNotThrow(() -> {
            int a = 10 / 1;
        });

        // 测试assertTimeout
        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
           Thread.sleep(500);
        });
    }
}
