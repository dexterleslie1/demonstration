package com.future.demo;

import org.junit.Assert;
import org.springframework.core.io.*;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.Properties;
import java.util.Random;

public class Application {
    public static void main(String args[]) throws Exception {
        // 测试ClassPathResource
        ClassPathResource resource = new ClassPathResource("file-none-exists.properties");
        Assert.assertFalse(resource.exists());

        // 注意：在jar发布包中，不能使用ResourceUtils.getFile()获取文件，因为它会抛出FileNotFoundException
        // 使用ClassPathResource.getInputStream()方法获取InputStream
        // File file = ResourceUtils.getFile("classpath:file.properties");
        // System.out.println("使用ResourceUtils.getFile(\"classpath:file.properties\")读取文件：" + file);

        // 模拟读取第三方库的classpath资源
        resource = new ClassPathResource("external.properties");
        Assert.assertTrue(resource.exists());
        // 2. 从Resource中获取InputStream
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();

            // 3. 使用Properties类加载配置文件
            Properties properties = new Properties();
            properties.load(inputStream);

            // 4. 读取并打印配置值
            String value = properties.getProperty("key1");
            System.out.println("从external.properties读取的key1=" + value);

        } catch (IOException e) {
            throw e;
        } finally {
            // 5. 关闭InputStream
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }

        // 测试使用InputStream读取classpath资源
        // 不抛出异常认为成功读取数据
        inputStream = null;
        try {
            resource = new ClassPathResource("file.properties");
            System.out.println("ClassPathResource path: " + resource.getURL().getPath());
            inputStream = resource.getInputStream();
            StreamUtils.copyToByteArray(inputStream);
        } finally {
            if (inputStream != null) {
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
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }

            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
        }

        // 测试ByteArrayResource
        byte[] randomBytes = new byte[1024 * 100];
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