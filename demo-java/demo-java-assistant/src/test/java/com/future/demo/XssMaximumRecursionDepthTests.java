package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

/**
 * 用于找出-Xss参数不同大小支持的最大递归深度
 * 1、通过调整-Xss参数测试最大的递归深度，例如：-Xss10m
 */
@Slf4j
public class XssMaximumRecursionDepthTests {
    private final static Random RANDOM = new Random(System.currentTimeMillis());

    @Test
    //@Ignore
    public void test() {
        testRecursion(-1);
    }

    void testRecursion(int depth) {
        try {
            // 基本类型值在存放在栈空间中（栈帧）
            long randomLong = RANDOM.nextLong();
            int randomInt = RANDOM.nextInt();
            double randomDouble = RANDOM.nextDouble();

            testRecursion(depth + 1);

            use(randomLong, randomInt, randomDouble);
        } catch (StackOverflowError ignore) {
            log.debug("最大递归深度为 {} 级", depth);
        }
    }

    void use(long randomLong, int randomInt, double randomDouble) {

    }
}
