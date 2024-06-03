package com.future.demo;

import org.junit.Assert;
import org.springframework.core.io.*;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.Random;

public class Application {
    public static void main(String args[]) throws Exception {
        // 测试ClassPathResource
        ClassPathResource resource = new ClassPathResource("file-none-exists.properties");
        Assert.assertFalse(resource.exists());

        // 测试使用InputStream读取classpath资源
        // 不抛出异常认为成功读取数据
        InputStream inputStream = null;
        try {
            resource = new ClassPathResource("file.properties");
            System.out.println("ClassPathResource path: " + resource.getURL().getPath());
            inputStream = resource.getInputStream();
            StreamUtils.copyToByteArray(inputStream);
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }

        resource = new ClassPathResource("file.properties");
        Assert.assertTrue(resource.exists());
        try {
            // 调用ClassPathResource.getFile()方法会预期抛出FileNotFoundException
            // 当资源是打包在 JAR、WAR、EAR 或其他归档文件内部时，getFile() 方法将无法直接访问它，因为这些资源并不是以文件系统上的独立文件形式存在的。在这种情况下，尝试调用 getFile() 将会失败，因为文件系统中没有实际的文件与之对应。
            resource.getFile();
            Assert.fail("没有抛出预期异常");
        } catch (FileNotFoundException ex) {
            // 预期异常
        }

        // 测试FileSystemResource
        resource = new ClassPathResource("file.properties");
        String path = resource.getURL().getPath();
        // ClassPathResource不会存在于文件系统上
        FileSystemResource fileSystemResource = new FileSystemResource(path);
        Assert.assertFalse(fileSystemResource.exists());

        // 测试UrlResource
        UrlResource urlResource = new UrlResource("https://docs.spring.io/spring/docs/4.0.0.M1/spring-framework-reference/pdf/spring-framework-reference.pdf");
        File temporaryFile = File.createTempFile("file", ".tmp");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(temporaryFile);
            StreamUtils.copy(urlResource.getInputStream(), outputStream);

            inputStream = new FileInputStream(temporaryFile);
            Assert.assertEquals(urlResource.contentLength(), inputStream.available());
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(outputStream!=null) {
                outputStream.close();
                outputStream = null;
            }

            if(inputStream!=null) {
                inputStream.close();
                inputStream = null;
            }
        }

        // 测试ByteArrayResource
        byte [] randomBytes = new byte[1024*100];
        Random random = new Random();
        random.nextBytes(randomBytes);
        ByteArrayResource byteArrayResource = new ByteArrayResource(randomBytes);
        Assert.assertEquals(randomBytes, byteArrayResource.getByteArray());

        // 测试ResourceLoader
        ClassPathResource classPathResource = new ClassPathResource("file.properties");
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        // 读取classpath资源
        Resource resourceL = resourceLoader.getResource("file.properties");
        Assert.assertTrue(resourceL instanceof ClassPathResource);

        // 读取classpath资源路径前缀使用classpath:
        resourceL = resourceLoader.getResource("classpath:file.properties");
        Assert.assertTrue(resourceL instanceof ClassPathResource);

        // 使用绝对路径读取classpath资源
        // absolutePath例子：file:/home/xxx/workspace-git/demonstration/demo-spring-boot/demo-spring-resource/target/demo.jar!/file.properties
        String absolutePath = classPathResource.getURL().getPath();
        resourceL = resourceLoader.getResource(absolutePath);
        Assert.assertTrue(resourceL instanceof FileUrlResource);

        // 读取网络的资源
        resourceL = resourceLoader.getResource("https://docs.spring.io/spring/docs/4.0.0.M1/spring-framework-reference/pdf/spring-framework-reference.pdf");
        Assert.assertTrue(resourceL instanceof UrlResource);
    }
}