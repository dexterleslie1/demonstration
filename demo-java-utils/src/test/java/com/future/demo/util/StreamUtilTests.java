package com.future.demo.util;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.Random;

public class StreamUtilTests {
    /**
     * JAVA根据URL网址获取输入流
     * https://www.cnblogs.com/jiehanshi/p/11528767.html
     *
     * 网络流中 InputStream.available() = 0 问题探究
     * https://www.cnblogs.com/zjfjava/p/10829241.html
     * InputStream的available()方法的作用是返回此输入流在不受阻塞情况下能读取的字节数。网络流与文件流不同的关键就在于是否“受阻”二字，网络socket流在读取时如果没有内容read()方法是会受阻的，所以从socket初始化的输入流的available也是为零的，所以要read一字节后再使用，这样可用的字节数就等于 available + 1。但文件读取时read()一般是不会受阻的，因为文件流的可用字节数 available = file.length()，而文件的内容长度在创建File对象时就已知了。
     *
     * @throws Exception
     */
    @Test
    public void testInputStreamAvailable() throws Exception {
        // 创建测试临时文件
        String filename = "inputstream-available-test.bin";
        String temporaryDirectory = System.getProperty("java.io.tmpdir");
        File temporaryFile = new File(temporaryDirectory, filename);
        if(!temporaryFile.exists()) {
            boolean b = temporaryFile.createNewFile();
            if(!b) {
                throw new Exception("创建文件 " + temporaryFile.getAbsolutePath() + "失败");
            }
        }

        // 向临时文件写入1MB数据
        byte []bytes = new byte[1024*1024];
        Random random = new Random();
        random.nextBytes(bytes);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(temporaryFile);
            IOUtils.copy(new ByteArrayInputStream(bytes), outputStream);
        } finally {
            if(outputStream!=null) {
                outputStream.close();
                outputStream = null;
            }
        }

        // 测试FileInputStream#available方法
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(temporaryFile);
            long length = inputStream.available();
            Assert.assertEquals(bytes.length, length);
        } finally {
            if(inputStream!=null) {
                inputStream.close();
                inputStream = null;
            }
        }

        // 测试网络流#available方法
        inputStream = null;
        outputStream = null;
        try {
            URL url = new URL("https://common.cnblogs.com/images/wechat.png");
            inputStream = url.openStream();
            long length = inputStream.available();
            outputStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, outputStream);
            // available方法返回length小于等于文件大小
            Assert.assertTrue(length<=((ByteArrayOutputStream)outputStream).toByteArray().length);
        } finally {
            if(inputStream!=null) {
               inputStream.close();
               inputStream = null;
            }
        }
    }
}
