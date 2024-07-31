# `aws sdk`用法

## `aws-sdk-for-java`用法

>[Developer guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
>
>[Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html#get-started-setup-javamaven)

示例的详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/aws/demo-aws-sdk-for-java)

使用步骤：

1. 初始化身份认证器

   ```java
   String accessKeyId = System.getenv("accessKeyId");
           String accessKeySecret = System.getenv("accessKeySecret");
           Region region = Region.AP_NORTHEAST_1;
   
           Wafv2Client wafv2Client = null;
           try {
               AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, accessKeySecret);
   ```

   

2. 初始化相应的`api sdk`，例如：`wafv2`、`ec2`等

   ```java
   // 省略...
   wafv2Client = Wafv2Client.builder()
                       .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                       .region(region).build();
   ```