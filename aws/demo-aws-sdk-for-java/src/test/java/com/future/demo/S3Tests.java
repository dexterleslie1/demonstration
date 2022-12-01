package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StreamUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

@Slf4j
public class S3Tests {
    @Test
    public void test() throws IOException {
        String accessKeyId = System.getenv("accessKeyId");
        String accessKeySecret = System.getenv("accessKeySecret");
        Region region = Region.AP_EAST_1;

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, accessKeySecret);
        S3Client s3Client = null;
        try {
            s3Client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(region).build();

            String bucket = "yyd-demo-s3-java-sdk";
            /**
             * 普通上传
             */
            // 目录结构 folder1/folder2/1.txt
            String key = "folder1/folder2/1.txt";
            String content = "hello world";
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(key).build();
            s3Client.putObject(putObjectRequest, RequestBody.fromString(content));

            boolean objectExists;
            try {
                s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
                objectExists = true;
            } catch (NoSuchKeyException e) {
                objectExists = false;
            }
            Assert.assertTrue(objectExists);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest);
            byte []datum = StreamUtils.copyToByteArray(inputStream);
            inputStream.close();
            inputStream = null;
            Assert.assertEquals(content, new String(datum));

            // 删除对象
            deleteObject(s3Client, bucket, key);

            /**
             * 分片上传
             */
            key = "multipart.txt";
            int mB = 1024 * 1024;
            // First create a multipart upload and get the upload id
            CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);
            String uploadId = response.uploadId();
            log.debug("uploadId={}", uploadId);

            // Upload all the different parts of the object
            UploadPartRequest uploadPartRequest1 = UploadPartRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(1).build();

            byte [] datumPart1 = getRandomByteBuffer(5 * mB);
            String etag1 = s3Client.uploadPart(uploadPartRequest1, RequestBody.fromBytes(datumPart1)).eTag();

            CompletedPart part1 = CompletedPart.builder().partNumber(1).eTag(etag1).build();

            UploadPartRequest uploadPartRequest2 = UploadPartRequest.builder().bucket(bucket).key(key)
                    .uploadId(uploadId)
                    .partNumber(2).build();
            byte [] datumPart2 = "world1".getBytes();
            String etag2 = s3Client.uploadPart(uploadPartRequest2, RequestBody.fromBytes(datumPart2)).eTag();
            CompletedPart part2 = CompletedPart.builder().partNumber(2).eTag(etag2).build();

            // Finally call completeMultipartUpload operation to tell S3 to merge all uploaded
            // parts and finish the multipart operation.
            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                    .parts(part1, part2)
                    .build();

            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    CompleteMultipartUploadRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .uploadId(uploadId)
                            .multipartUpload(completedMultipartUpload)
                            .build();

            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

            /**
             * 下载对象
             */
            getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            inputStream = s3Client.getObject(getObjectRequest);
            datum = StreamUtils.copyToByteArray(inputStream);
            byte[] allByteArray = new byte[datumPart1.length + datumPart2.length];
            ByteBuffer buff = ByteBuffer.wrap(allByteArray);
            buff.put(datumPart1);
            buff.put(datumPart2);
            byte[] combined = buff.array();
            Assert.assertArrayEquals(datum, combined);
            inputStream.close();
            inputStream = null;
        } finally {
            if(s3Client != null) {
                s3Client.close();
                s3Client = null;
            }
        }
    }

    /**
     * 删除object
     *
     * @param bucket
     * @param key
     */
    private void deleteObject(S3Client s3Client, String bucket, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    private Random random = new Random();
    private byte[] getRandomByteBuffer(int totalBytes) {
        byte []datum = new byte[totalBytes];
        random.nextBytes(datum);
        return datum;
    }
}
