package com.future.demo;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

public class FilenameUtilsTests {
    @Test
    public void test() {
        String temporaryDirectoryPath = System.getProperty("java.io.tmpdir");
        String uuidStr = UUID.randomUUID().toString();
        String path = temporaryDirectoryPath + File.separator + uuidStr + ".doc";
        // 文件的名称（不包括文件后缀名），例如：xxx
        String baseName = FilenameUtils.getBaseName(path);
        Assert.assertEquals(uuidStr, baseName);

        // 文件的扩展名，例如：doc
        String filenameExtension = FilenameUtils.getExtension(path);
        Assert.assertEquals("doc", filenameExtension);

        // 文件的名称（包括文件后缀名），例如：xxx.doc
        String filename = FilenameUtils.getName(path);
        Assert.assertEquals(uuidStr + ".doc", filename);

        // 文件父路径，例如：/tmp/
        String fullPath = FilenameUtils.getFullPath(path);
        Assert.assertEquals("/tmp/", fullPath);
    }
}
