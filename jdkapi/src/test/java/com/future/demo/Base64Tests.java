package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.util.Base64;
import java.util.Random;

public class Base64Tests {
    /**
     * https://stackoverflow.com/questions/41935207/base64-string-to-byte-in-java
     */
    @Test
    public void test() {
        // region 测试使用jdk base64 api转换byte数组到base64字符串
        Random random = new Random();
        byte[] datum = new byte[1024 * 1024];
        random.nextBytes(datum);

        String base64Str1 = Base64.getEncoder().encodeToString(datum);

        //endregion

        //region 测试base64字符串转换为byte

        byte[] datum1 = Base64.getDecoder().decode(base64Str1);
        Assert.assertArrayEquals(datum, datum1);

        //endregion
    }
}
