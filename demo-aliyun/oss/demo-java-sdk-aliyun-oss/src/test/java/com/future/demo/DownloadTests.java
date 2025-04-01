package com.future.demo;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * 下载小文件测试
 */
public class DownloadTests {
    /**
     *
     */
    @Test
    public void download() throws IOException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = System.getenv("accessKeyId");
        String accessKeySecret = System.getenv("accessKeySecret");
        String bucketName = System.getenv("bucketName");
        String objectName = "certificates/chat-apns-iOS-dev.p12";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
        File file = File.createTempFile("cert", ".tmp");
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), file);

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    @Test
    public void testObjectMetadata() throws ParseException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
        String accessKeyId = System.getenv("accessKeyId");
        String accessKeySecret = System.getenv("accessKeySecret");
        String bucketName = System.getenv("bucketName");
        String objectName = "test.txt";

        String content = "Hello OSS";

        // 创建上传文件的元信息，可以通过文件元信息设置HTTP header。
        ObjectMetadata meta = new ObjectMetadata();

        String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(content.getBytes()));
        // 开启文件内容MD5校验。开启后OSS会把您提供的MD5与文件的MD5比较，不一致则抛出异常。
        meta.setContentMD5(md5);
        // 指定上传的内容类型。内容类型决定浏览器将以什么形式、什么编码读取文件。如果没有指定则根据文件的扩展名生成，如果没有扩展名则为默认值application/octet-stream。
        meta.setContentType("text/plain");
        // 设置内容被下载时的名称。
        meta.setContentDisposition("attachment; filename=\"DownloadFilename\"");
        // 设置上传文件的长度。如超过此长度，则上传文件会被截断，上传的文件长度为设置的长度。如小于此长度，则为上传文件的实际长度。
        meta.setContentLength(content.length());
        // 设置内容被下载时网页的缓存行为。
        meta.setCacheControl("Download Action");
        // 设置缓存过期时间，格式是格林威治时间（GMT）。
        meta.setExpirationTime(DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
        // 设置内容被下载时的编码格式。
        meta.setContentEncoding("utf-8");
        // 设置header。
        meta.setHeader("header1", "value1");
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 设置自定义元信息property值为property-value。建议使用Base64编码。
        meta.addUserMetadata("metadata1", "mvalue1");

        // 上传文件。
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()), meta);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
