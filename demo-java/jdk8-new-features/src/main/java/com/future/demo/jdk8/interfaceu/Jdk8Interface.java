package com.future.demo.jdk8.interfaceu;

/**
 * 演示jdk8支持接口默认方法和静态方法
 *
 * Oracle官方默认方法和静态方法详解
 * https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html
 */
public interface Jdk8Interface {
    int add(int a, int b);

    default String defaultMethod(String str) {
        return "echo:" + str;
    }

    static String staticMethod(String str) {
        return "echo:" + str;
    }
}
