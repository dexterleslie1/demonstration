package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

public class RuntimeApiTests {
    /**
     * 获取系统当前 CPU 数量
     */
    @Test
    public void testAvailableProcessors() {
        Runtime runtime = Runtime.getRuntime();
        int availableProcessors = runtime.availableProcessors();
        Assert.assertEquals(8, availableProcessors);
    }
}
