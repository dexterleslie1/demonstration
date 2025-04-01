package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 用于找出-Xss参数不同大小支持的最大递归深度
 * 1、通过调整-Xss参数测试最大的递归深度，例如：-Xss10m
 */
@Slf4j
public class XssMaximumRecursionDepthTests {
    @Test
    @Ignore
    public void test() {
        testRecursion(-1);
    }

    void testRecursion(int depth) {
        try {
            testRecursion(depth + 1);
        } catch (StackOverflowError ignore) {
            log.debug("最大递归深度为 {} 级", depth);
        }
    }
}
