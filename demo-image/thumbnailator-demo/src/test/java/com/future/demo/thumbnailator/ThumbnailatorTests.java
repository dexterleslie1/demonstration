package com.future.demo.thumbnailator;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
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
        String localWorkDirectory = System.getProperty("user.home");
        String originalImage = "original.jpeg";
        String smallImage = "small.jpeg";
        String mediumImage = "medium.jpeg";
        String noneImage = "1.pcap";

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
            Thumbnails.of(fileNoneImage).size(100, 100).outputFormat("jpg").keepAspectRatio(true).toFile(temporaryFile);
            Assert.fail("预期异常没有抛出");
        } catch (UnsupportedFormatException ex) {
            Assert.assertTrue(ex.getMessage().startsWith("No suitable ImageReader found for"));
        }
    }

    @Test
    public void testPerformance() throws IOException {
        String localWorkDirectory = System.getProperty("user.home");
        File fileOriginalImage1 = new File(localWorkDirectory, "1.JPG");
        File fileOriginalImage2 = new File(localWorkDirectory, "2.JPG");
        resize(fileOriginalImage1, 100, 100);
        resize(fileOriginalImage1, 200, 200);
        resize(fileOriginalImage1, 300, 300);
        resize(fileOriginalImage1, 500, 500);

        resize(fileOriginalImage2, 100, 100);
        resize(fileOriginalImage2, 200, 200);
        resize(fileOriginalImage2, 300, 300);
        resize(fileOriginalImage2, 500, 500);
    }

    /**
     * 用户协助测试图片为何不能正确生成缩略图问题
     *
     * @throws IOException
     */
    @Test
    public void test_helper() throws IOException {
        String directory = System.getProperty("user.home") + "/Documents";
        String filename = "1.jpeg";
        String filenameSmall = "1-small.jpeg";

        InputStream inputStream = new FileInputStream(directory + "/" + filename);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, outputStream);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
        IOUtils.close(inputStream);
        Thumbnails.of(bufferedImage).size(100, 100).keepAspectRatio(true).toFile(directory + "/" + filenameSmall);
        Thumbnails.of(directory + "/" + filename).size(100, 100).keepAspectRatio(true).toFile(directory + "/" + filenameSmall);
    }

    void resize(File originalImage, int width, int height) throws IOException {
        String temporaryDirectory = System.getProperty("java.io.tmpdir");
        File fileTemporary = new File(temporaryDirectory, UUID.randomUUID().toString());

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(originalImage);
            Date startTime = new Date();
            Thumbnails.fromInputStreams(Arrays.asList(inputStream)).size(width, height).keepAspectRatio(true).toFile(fileTemporary);
            Date endTime = new Date();
            long milliseconds = endTime.getTime() - startTime.getTime();
            System.out.println("原图" + originalImage.getAbsolutePath()  + "，大小: " + originalImage.length()/1024 + "kb生成尺寸为宽: " + width + ",高: " + height +
                    "的缩略图" + fileTemporary.getAbsolutePath() + "耗时:" + milliseconds + "毫秒");
        } finally {
            if(inputStream!=null) {
                inputStream.close();
                inputStream = null;
            }
        }
    }
}
