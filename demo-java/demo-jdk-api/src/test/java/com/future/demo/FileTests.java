package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileTests {
    /**
     * 在 tmp 目录随机创建文件
     */
    @Test
    public void testCreateRandomFileInTempDirectory() throws IOException {
        String temporaryDirectory = System.getProperty("java.io.tmpdir");
        // https://stackoverflow.com/questions/6142901/how-to-create-a-file-in-a-directory-in-java
        String randomFilename = UUID.randomUUID() + ".doc";
        String path = temporaryDirectory + File.separator + randomFilename;
        File randomFile = new File(path);
        File parentFile = randomFile.getParentFile();
        // 随机文件的父路径为 /tmp 目录
        Assert.assertEquals(temporaryDirectory, parentFile.getAbsolutePath());
        // 创建文件成功返回 true，否则如果文件已经存在则返回 false
        boolean result = randomFile.createNewFile();
        Assert.assertTrue(result);
    }
}
