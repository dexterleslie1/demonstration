package com.future.demo;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.StringUtils;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteTests {
    // NOTE: 先授权用户AliyunOSSFullAccess权限，否则报错bucket不属于当前用户错误
    @Test
    public void testDeleteAllFile() {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = System.getenv("accessKeyId");
        String accessKeySecret = System.getenv("accessKeySecret");
        String bucketName = System.getenv("bucketName");

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ObjectListing objectListing = ossClient.listObjects(bucketName);
        while(objectListing != null && objectListing.getObjectSummaries() != null && objectListing.getObjectSummaries().size() > 0) {
            List<String> keys = objectListing.getObjectSummaries().stream().map(OSSObjectSummary::getKey).collect(Collectors.toList());
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            deleteObjectsRequest.setKeys(keys);
            ossClient.deleteObjects(deleteObjectsRequest);
            System.out.println("成功删除：" + StringUtils.join(",", keys));
            objectListing = ossClient.listObjects(bucketName);
        }
    }
}
