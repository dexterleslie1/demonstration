package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

public class Tests {
    @Test
    public void test() {
        String str = PluginUtil.getRandomStr();
        Assert.assertTrue(str.startsWith("插件"));
    }
}
