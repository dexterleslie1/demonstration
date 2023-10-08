package com.future.demo.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Md5UtilTests {
    /**
     * https://www.geeksforgeeks.org/how-to-generate-md5-checksum-for-files-in-java/
     * https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void test() throws IOException, NoSuchAlgorithmException {
        byte []bytes = new byte[1024*1024*512];
        Random random = new Random();
        random.nextBytes(bytes);

        long startTime = System.currentTimeMillis();
        String md5 = DigestUtils.md5Hex(bytes);
        long endTime = System.currentTimeMillis();
        long milliseconds1 = endTime - startTime;

        startTime = System.currentTimeMillis();
        String md5T = Md5Util.md5(new ByteArrayInputStream(bytes));
        endTime = System.currentTimeMillis();
        long milliseconds2 = endTime - startTime;

        Assert.assertEquals(md5, md5T);
        System.out.println("DigestUtils.md5Hex方法md5耗时: " + milliseconds1 + "毫秒，Md5Util.md5方法耗时: " + milliseconds2 + "毫秒");
    }
}
