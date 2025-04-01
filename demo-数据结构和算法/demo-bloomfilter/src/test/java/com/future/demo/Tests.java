package com.future.demo;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Assert;
import org.junit.Test;

public class Tests {

    @Test
    public void test() {
        int expectedInsertions = 1000000000;
        double fpp = 0.01;
        BloomFilter<String> filter =
                BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedInsertions, fpp);

        // 向布隆过滤器中添加元素
        filter.put("example1");
        filter.put("example2");

        // 检查元素是否存在
        Assert.assertTrue(filter.mightContain("example1")); // 应该输出true
        Assert.assertTrue(filter.mightContain("example2")); // 应该输出true
        Assert.assertFalse(filter.mightContain("example3")); // 可能会输出true（误判）
    }
}
