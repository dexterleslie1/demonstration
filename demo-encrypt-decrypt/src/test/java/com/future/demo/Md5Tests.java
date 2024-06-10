package com.future.demo;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

public class Md5Tests {
    @Test
    public void test() {
        String randomStr = RandomStringUtils.random(1024);
        String md5Str = DigestUtils.md5Hex(randomStr);
        // md5算法固定输出为128位
        Assert.assertEquals(32, md5Str.length());
    }
}
