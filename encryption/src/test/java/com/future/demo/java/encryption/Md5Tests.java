package com.future.demo.java.encryption;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class Md5Tests {
    @Test
    public void test() {
        Random random = new Random();
        byte []datum = new byte[1024*1024];
        random.nextBytes(datum);
        String md5Str = DigestUtils.md5Hex(datum);
        Assert.assertNotNull(md5Str);
    }
}
