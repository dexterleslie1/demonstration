package com.future.demo.module1;

import org.apache.commons.lang3.RandomStringUtils;

public class Module1Util {
    public static String getString() {
        return "这是模块1生成的随机字符串：" + RandomStringUtils.random(20);
    }
}