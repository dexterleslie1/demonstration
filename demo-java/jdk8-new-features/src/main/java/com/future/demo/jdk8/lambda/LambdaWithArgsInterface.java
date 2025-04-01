package com.future.demo.jdk8.lambda;

/**
 * 演示lambda用法和有参数lambda用法
 */
// 函数接口约束注解，表示这个接口有且只有一个抽象方法
@FunctionalInterface
public interface LambdaWithArgsInterface {
    int add(int a, int b);

    // 因为接口使用@FunctionalInterface标记，只支持一个接口有且只有一个抽象方法
    // int sub(int a, int b);
    default String defaultMethod(String str) {
        return "echo:" + str;
    }
}
