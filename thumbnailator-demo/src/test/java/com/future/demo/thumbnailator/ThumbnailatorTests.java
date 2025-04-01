package com.future.demo.thumbnailator;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.UUID;

public class ThumbnailatorTests {
    /**
     * java使用thumbnailator缩放图片简单示范
     * https://blog.csdn.net/zengchao9/article/details/84744236
     * 
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        String localWorkDirectory = "/Users/macos";
        String originalImage = "original.jpeg";
        String smallImage = "small.jpeg";
        String mediumImage = "medium.jpeg";
        String noneImage = "11.pcap";

        File originalFile = new File(localWorkDirectory, originalImage);
        File smallFile = new File(localWorkDirectory, smallImage);
        File mediumFile = new File(localWorkDirectory, mediumImage);
        if(!originalFile.exists()) {
            throw new Exception("图片 " + originalFile.getAbsolutePath() + " 不存在");
        }

        Thumbnails.of(originalFile).size(100, 100).keepAspectRatio(true).toFile(smallFile);
        Thumbnails.of(originalFile).size(500, 500).keepAspectRatio(true).toFile(mediumFile);

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(originalFile);
            Thumbnails.fromInputStreams(Arrays.asList(fileInputStream)).size(100, 100).keepAspectRatio(true).toFile(smallFile);

            fileInputStream.close();

            fileInputStream = new FileInputStream(originalFile);
            Thumbnails.fromInputStreams(Arrays.asList(fileInputStream)).size(100, 100).keepAspectRatio(true).toFile(mediumFile);
        } finally {
            if(fileInputStream!=null) {
                fileInputStream.close();
                fileInputStream = null;
            }
        }

        // 测试非图片类型文件
        String temporaryFilename = UUID.randomUUID().toString();
        File temporaryFile = new File(localWorkDirectory, temporaryFilename);
        File fileNoneImage = new File(localWorkDirectory, noneImage);
        try {
            Thumbnails.of(fileNoneImage).size(100, 100).keepAspectRatio(true).toFile(temporaryFile);
            Assert.fail("预期异常没有抛出");
        } catch (UnsupportedFormatException ex) {
        }
    }
}
