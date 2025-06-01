# ElasticSearch



## ES、Lucene、Solr 区别

- es：基于lucene搜索引擎
- solr：基于lucene搜索引擎
- lucene：Lucene是非常优秀的成熟的 开源的 免费的纯 纯java 语言的全文索引检索工具包jar。 是搜索引擎的底层。



## 什么是 tokenizer、analyzer、filter ?

> [什么是 tokenizer、analyzer、filter ?](https://cloud.tencent.com/developer/article/1706529)



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



## 客户端



### spring-data-elasticsearch

#### 和 elasticsearch 版本兼容列表

>[参考链接](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html)

下表显示了 Spring Data 发布系列使用的 Elasticsearch 和 Spring 版本以及其中包含的 Spring Data Elasticsearch 版本。

| Spring Data Release Train | Spring Data Elasticsearch                                    | Elasticsearch | Spring Framework |
| ------------------------- | ------------------------------------------------------------ | ------------- | ---------------- |
| 2024.1                    | 5.4.x                                                        | 8.15.5        | 6.2.x            |
| 2024.0                    | 5.3.1                                                        | 8.13.4        | 6.1.x            |
| 2023.1 (Vaughan)          | 5.2.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 8.11.1        | 6.1.x            |
| 2023.0 (Ullmann)          | 5.1.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 8.7.1         | 6.0.x            |
| 2022.0 (Turing)           | 5.0.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 8.5.3         | 6.0.x            |
| 2021.2 (Raj)              | 4.4.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 7.17.3        | 5.3.x            |
| 2021.1 (Q)                | 4.3.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 7.15.2        | 5.3.x            |
| 2021.0 (Pascal)           | 4.2.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 7.12.0        | 5.3.x            |
| 2020.0 (Ockham)           | 4.1.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 7.9.3         | 5.3.2            |
| Neumann                   | 4.0.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 7.6.2         | 5.2.12           |
| Moore                     | 3.2.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 6.8.12        | 5.2.12           |
| Lovelace                  | 3.1.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 6.2.2         | 5.1.19           |
| Kay                       | 3.0.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 5.5.0         | 5.0.13           |
| Ingalls                   | 2.1.x[[1](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html#_footnotedef_1)] | 2.4.0         | 4.3.25           |



#### 用法

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/demo-spring-data-elasticsearch)

运行示例需要启动 elasticsearch7。



### Java Transport Client

>提醒：目前项目业务实现使用此客户端。
>
>[Java Transport Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/index.html)

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/elasticsearch-java-transport-client)

运行示例需要启动 elasticsearch7。



### Java High Level REST Client

>注意：无法使用 `elasticsearch-rest-high-level-client` 操作 `elasticsearch8`，可能是因为 `elasticsearch8 docker` 启动不正确导致。
>
>[参考链接](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/elasticsearch-rest-high-level-client)

运行示例需要启动 elasticsearch7。



### Java Low Level REST Client

>[参考链接](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low.html)



### Elasticsearch Java Client

> 提醒：`elasticsearch 8.x` 以上官方推荐使用这个客户端操作 `elasticsearch`。
>
> [参考链接](https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/index.html)

#### 配置和使用

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/elasticsearch8-java-client)

POM 配置

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.11</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>co.elastic.clients</groupId>
    <artifactId>elasticsearch-java</artifactId>
    <version>8.1.2</version> <!-- 版本号应与Elasticsearch服务器版本一致 -->
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.18.1</version>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.36</version>
</dependency>
```

AbstractTestSupport.java

```java
public class AbstractTestSupport {
    protected ElasticsearchClient client = null;
    private RestClient restClient;
    private ElasticsearchTransport transport;

    @Before
    public void setup() {
        // 1. 创建RestClient（底层HTTP客户端）
        restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http") // 单节点配置
                // 多节点示例：new HttpHost("host2", 9200, "http"), ...
        ).build();

        // 2. 创建Transport层（序列化/反序列化）
        transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper() // 使用Jackson作为JSON处理器
        );

        // 3. 创建ElasticsearchClient
        client = new ElasticsearchClient(transport);
    }

    @After
    public void teardown() throws IOException {
        if (transport != null) {
            transport.close();
            transport = null;
        }
        if (restClient != null) {
            restClient.close();
            restClient = null;
        }
    }
}
```

测试

```java
public class ApplicationTests extends AbstractTestSupport {
    public final static String IndexDemo = "index_demo";

    @Test
    public void contextLoads() throws IOException {
        // 删除索引
        try {
            DeleteIndexResponse deleteIndexResponse = client.indices().delete(DeleteIndexRequest.of(o -> o.index(IndexDemo)));
            Assert.assertTrue(deleteIndexResponse.acknowledged());
        } catch (Exception ex) {
            // 忽略索引不存在
        }

        // 创建索引
        List<String[]> datumList1 = Arrays.asList(
                new String[]{"1", "a"},
                new String[]{"2", "a"},
                new String[]{"3", "a1"},
                new String[]{"4", "a2"}
        );
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(IndexDemo)
                .mappings(m -> m
                        .properties("id", p -> p.long_(l -> l.store(false)))
                        .properties("content", p -> p.text(t -> t
                                .store(false)
                                .analyzer("ik_max_word")
                        ))).build();
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
        Assert.assertEquals(Boolean.TRUE, createIndexResponse.acknowledged());

        // 插入数据到索引中
        datumList1.forEach(datum -> {
            try {
                IndexRequest<Document> indexRequest = IndexRequest.of(o -> {
                    return o.index(IndexDemo)
                            .id(datum[0]) // 设置文档 ID
                            .document(new Document(datum[0], datum[1]))
                            .refresh(Refresh.True); // 设置文档内容
                });
                IndexResponse indexResponse = client.index(indexRequest);
                Assert.assertEquals(Result.Created, indexResponse.result());
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        });

        // region 批量插入

        List<BulkOperation> operations = new ArrayList<>();

        for (String[] datum : datumList1) {
            operations.add(BulkOperation.of(b -> b
                    .index(i -> i
                            .index(IndexDemo)
                            .id(datum[0]) // 设置文档 ID
                            .document(new Document(datum[0], datum[1])) // 设置文档内容
                    )
            ));
        }

        BulkRequest request = BulkRequest.of(b -> b
                .operations(operations)
                .refresh(Refresh.True) // 设置刷新策略为 IMMEDIATE
        );

        // 执行批量请求
        BulkResponse response = client.bulk(request);

        /*// 检查是否有错误
        if (response.errors()) {
            System.err.println("批量插入中有错误发生: " + response.items());
        } else {
            System.out.println("批量插入成功，共插入 " + response.items().size() + " 个文档");
        }*/
        Assert.assertFalse(response.errors());
        Assert.assertEquals(datumList1.size(), response.items().size());

        // endregion
    }

    // 定义一个简单的文档类来映射数据
    @Getter
    static class Document {
        private String id;
        private String content;

        public Document(String id, String content) {
            this.id = id;
            this.content = content;
        }
    }
}

```



## 实现好友、群组、群组成员、聊天消息关键字搜索功能

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/elasticsearch-practise-demo)

运行示例需要启动 elasticsearch7。



## 性能实验

### `Java transport client`单条插入和批量插入性能对比

详细的`jmh`代码请参考`https://gitee.com/dexterleslie/demonstration/blob/master/elasticsearch/elasticsearch7/elasticsearch-java-transport-client/src/test/java/com/future/demo/elasticsearch/IndividualAndBulkAddPerfComparisonTests.java`

实验结论：批量插入性能高于单条插入性能。

