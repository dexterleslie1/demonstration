package com.future.demo.jdk8.builtin.function;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
public class BuiltinFunctionTests {
    @Test
    public void test() {
        // region 内置函数式接口 Supplier

        Supplier<String> supplier = () -> "Hello world!";
        String str = supplier.get();
        Assert.assertEquals("Hello world!", str);

        // endregion

        // region 内置函数式接口 Consumer

        Consumer<String> consumer = param1 -> log.debug(param1.toUpperCase());
        consumer.accept("Hello world!");

        // endregion

        // region 内置函数式接口 Function

        // 使用 Function 接口实现 (a+b)*c
        Function<BigDecimal[], BigDecimal[]> function1 = param1 -> new BigDecimal[]{param1[2], param1[0].add(param1[1])};
        Function<BigDecimal[], BigDecimal> function2 = param1 -> param1[0].multiply(param1[1]);
        BigDecimal result = function1.andThen(function2).apply(new BigDecimal[]{new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3")});
        Assert.assertEquals(new BigDecimal("9"), result);

        // endregion

        // region 内置函数式接口 Predicate

        Predicate<String> predicate = param1 -> param1.length() > 3;
        boolean b = predicate.test("Hello");
        Assert.assertTrue(b);
        b = predicate.test("He");
        Assert.assertFalse(b);

        // endregion
    }
}
