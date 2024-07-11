package com.future.demo.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationTests {
    @Autowired
    MinioClient minioClient;

    @Test
    public void test() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException, XmlParserException, ErrorResponseException, InvalidBucketNameException, RegionConflictException, InvalidExpiresRangeException {
        // 创建bucket
        String bucketName = "test-bucket";
        boolean exists = this.minioClient.bucketExists(bucketName);
        if (!exists) {
            this.minioClient.makeBucket(bucketName);
        }
        exists = this.minioClient.bucketExists(bucketName);
        Assert.assertTrue(exists);

        // put object到bucket
        String objectName = "1.txt";
        ClassPathResource resource = new ClassPathResource("1.txt");
        this.minioClient.putObject(bucketName, objectName, resource.getFile().getAbsolutePath(), null);
        Iterable<Result<Item>> iterable = this.minioClient.listObjects(bucketName);
        Assert.assertTrue(StreamSupport.stream(iterable.spliterator(), false).count() >= 1);
        // 判断对象是否存在
        ObjectStat objectStat = this.minioClient.statObject(bucketName, objectName);
        Assert.assertNotNull(objectStat);

        // 获取bucket中object内容
        InputStream inputStream = this.minioClient.getObject(bucketName, objectName);
        String content = IOUtils.toString(inputStream, "utf-8");
        inputStream.close();
        Assert.assertEquals("这是一个测试文件", content);

        // 获取bucket中object presigned
        String presignedUrl = this.minioClient.presignedGetObject(bucketName, objectName);
        UrlResource urlResource = new UrlResource(presignedUrl);
        inputStream = urlResource.getInputStream();
        content = IOUtils.toString(inputStream, "utf-8");
        inputStream.close();
        Assert.assertEquals("这是一个测试文件", content);

        // 查询并删除bucket中object
        iterable.forEach(itemResult -> {
            try {
                this.minioClient.removeObject(bucketName, itemResult.get().objectName());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        // 判断对象是否存在
        try {
            this.minioClient.statObject(bucketName, objectName);
            Assert.fail("预期异常没有抛出");
        } catch (ErrorResponseException ex) {
            ErrorCode errorCodeResponse = ex.errorResponse().errorCode();
            Assert.assertTrue(ErrorCode.NO_SUCH_KEY == errorCodeResponse || ErrorCode.NO_SUCH_OBJECT == errorCodeResponse);
        }

        // 查询所有bucket
        String randomBucketName = UUID.randomUUID().toString();
        this.minioClient.makeBucket(randomBucketName);
        List<Bucket> buckets = this.minioClient.listBuckets();
        Assert.assertTrue(buckets.size() > 0);

        // 测试path
        byte[] randomBytes = new byte[1024 * 1024];
        Random random = new Random();
        random.nextBytes(randomBytes);
        String path = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String objectNameWithPath = path + "/" + objectName;
        InputStream byteArrayInputStream = new ByteArrayInputStream(randomBytes);
        this.minioClient.putObject(bucketName, objectNameWithPath, byteArrayInputStream, new PutObjectOptions(randomBytes.length, -1));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        inputStream = this.minioClient.getObject(bucketName, objectNameWithPath);
        IOUtils.copy(inputStream, outputStream);
        byte[] bytesRead = outputStream.toByteArray();
        inputStream.close();
        Assert.assertArrayEquals(randomBytes, bytesRead);

        // 测试objectStat
        objectStat = this.minioClient.statObject(bucketName, objectNameWithPath);
        Assert.assertEquals(randomBytes.length, objectStat.length());

        // 测试获取对象列表
        List<Bucket> bucketList = this.minioClient.listBuckets();
        Assert.assertTrue(bucketList.stream().map(Bucket::name).collect(Collectors.toList()).contains(bucketName));
        Iterable<Result<Item>> resultIterable = this.minioClient.listObjects(bucketName);
        List<Item> resultList = new ArrayList<>();
        for (Result<Item> result : resultIterable) {
            resultList.add(result.get());
        }
        Assert.assertTrue(resultList.stream().map(Item::objectName).collect(Collectors.toList()).contains(objectNameWithPath));
    }
}
