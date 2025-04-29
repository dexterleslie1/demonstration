# ElasticSearch



## 运行 `elasticsearch`



### 使用`docker compose`运行`elasticsearch7`

>详细设置请参考本站 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/elasticsearch/elasticsearch7)

编译镜像

```bash
docker compose build
```

启用`elasticsearch7`和`kibana`

```bash
docker compose up -d
```

关闭`elasticsearch7`和`kibana`

```bash
docker compose down -v
```

访问`kibana`地址`http://localhost:5601`



### 使用`docker compose`运行`elasticsearch8`

>详细设置请参考本站 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/elasticsearch/elasticsearch8)

编译镜像

```bash
docker compose build
```

启用`elasticsearch8`和`kibana`

```bash
docker compose up -d
```

关闭`elasticsearch8`和`kibana`

```bash
docker compose down -v
```

访问`kibana`地址`http://localhost:5601`



## 中文 IK 和拼音插件

插件下载地址：`https://release.infinilabs.com`



## 中文 IK 分词器动态词汇扩展

>详细用法请参考本站 [示例1](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/elasticsearch7) 和 [示例2](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/demo-ik-analyzer-extend)
>
>[参考链接](https://blog.csdn.net/qq_43692950/article/details/122274613)

在示例1中启动 elasticsearch7 服务

```bash
docker compose up -d
```

使用示例2运行单元测试，因为使用 http 方式扩展 ik 分词器并且添加“日琳”和“精讲”词汇，所以单元测试能够通过。



