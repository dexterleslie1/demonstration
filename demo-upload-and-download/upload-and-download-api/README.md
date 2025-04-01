##### 演示范围下载(range)、演示上传和下载文件，使用postman测试上传和下载文件

NOTE: MultipartFile上传时会先把上传的文件流读入到临时目录保存，读取完毕后调用Controller上传接口并传入准备好的MultipartFile[]参数

- TODO: 支持流量器断点上传和断点下载，流量器兼容性测试
- TODO: 使用webuploader分片上传
- TODO: 支持分片下载
- TODO: 秒传
- TODO: 文件服务器限流逻辑

##### OSS需求
- 上传进度感知
- 流式上传
- 分片上传
- 秒传
- 流式下载
- 范围下载
- 断点下载

#### 参考资料
##### springboot多文件上传
https://www.cnblogs.com/it-deepinmind/p/12598286.html

- TODO: 上传文件超出大小axios没有response
- TODO: STS、url签名演示
- TODO: 华为手机浏览器无法断点下载
- TODO: 怎么根据不同url控制上传文件大小



