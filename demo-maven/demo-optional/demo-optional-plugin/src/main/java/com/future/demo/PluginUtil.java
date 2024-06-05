package com.future.demo;

import org.apache.commons.lang3.RandomStringUtils;

public class PluginUtil {
    public static String getRandomStr() {
        return "插件随机生成字符串：" + RandomStringUtils.random(20);
    }
}
