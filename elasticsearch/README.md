## ES、Lucene、Solr 区别

- es：基于lucene搜索引擎
- solr：基于lucene搜索引擎
- lucene：Lucene是非常优秀的成熟的 开源的 免费的纯 纯java 语言的全文索引检索工具包jar。 是搜索引擎的底层。



## 什么是 tokenizer、analyzer、filter ?

> [什么是 tokenizer、analyzer、filter ?](https://cloud.tencent.com/developer/article/1706529)



## 运行



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



## 中文`IK`和拼音插件

插件下载地址：https://release.infinilabs.com



## 中文`IK`分词器动态词汇扩展

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
>
> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/elasticsearch8-java-client)



#### 基本配置和使用

非 SpringBoot 项目 POM 配置如下：

```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
</properties>

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
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.18.1</version>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.36</version>
</dependency>
```

SpringBoot 项目 POM 配置如下：

```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
</properties>

<dependency>
    <groupId>co.elastic.clients</groupId>
    <artifactId>elasticsearch-java</artifactId>
    <version>8.1.2</version> <!-- 版本号应与Elasticsearch服务器版本一致 -->
</dependency>
<dependency>
    <groupId>jakarta.json</groupId>
    <artifactId>jakarta.json-api</artifactId>
    <version>2.0.1</version>
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
/**
 * 测试基本配置和使用
 *
 * @throws IOException
 */
@Test
public void testBasicUsage() throws IOException {
    String index = "demo_index";
    // 删除索引
    try {
        String finalIndex = index;
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(DeleteIndexRequest.of(o -> o.index(finalIndex)));
        Assert.assertTrue(deleteIndexResponse.acknowledged());
    } catch (Exception ex) {
        // 忽略索引不存在
    }

    // 创建索引
    List<Map<String, Object>> datumList = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("id", "1");
            put("content", "a");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
        }});
        add(new HashMap<String, Object>() {{
            put("id", "2");
            put("content", "a");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
        }});
        add(new HashMap<String, Object>() {{
            put("id", "3");
            put("content", "a1");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
        }});
        add(new HashMap<String, Object>() {{
            put("id", "4");
            put("content", "a2");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
        }});
    }};

    CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index)
            .mappings(m -> m
                    .properties("id", p -> p.long_(l -> l.store(false)))
                    .properties("content", p -> p.text(t -> t
                            .store(false)
                            .analyzer("ik_max_word")))
                    // 用于测试 LocalDateTime 类型
                    .properties("createTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss")))
            ).build();
    CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
    Assert.assertEquals(Boolean.TRUE, createIndexResponse.acknowledged());

    // 插入数据到索引中
    datumList.forEach(datum -> {
        try {
            IndexRequest<Map<String, Object>> indexRequest = IndexRequest.of(o -> {
                return o.index(index)
                        .id(String.valueOf(datum.get("id"))) // 设置文档 ID
                        .document(datum)
                        .refresh(Refresh.True); // 设置文档内容
            });
            IndexResponse indexResponse = client.index(indexRequest);
            Assert.assertEquals(Result.Created, indexResponse.result());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    });
}
```



#### 批量插入

```java
/**
 * 测试批量插入
 *
 * @throws IOException
 */
@Test
public void testBulkInsertion() throws IOException {
    String index = "demo_index";
    // 删除索引
    try {
        String finalIndex = index;
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(DeleteIndexRequest.of(o -> o.index(finalIndex)));
        Assert.assertTrue(deleteIndexResponse.acknowledged());
    } catch (Exception ex) {
        // 忽略索引不存在
    }

    // 创建索引
    List<Map<String, Object>> datumList = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("id", "1");
            put("content", "a");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
        }});
        add(new HashMap<String, Object>() {{
            put("id", "2");
            put("content", "a");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
        }});
        add(new HashMap<String, Object>() {{
            put("id", "3");
            put("content", "a1");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
        }});
        add(new HashMap<String, Object>() {{
            put("id", "4");
            put("content", "a2");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
        }});
    }};

    // region 批量插入

    List<BulkOperation> operations = new ArrayList<>();

    for (Map<String, Object> datum : datumList) {
        String finalIndex3 = index;
        operations.add(BulkOperation.of(b -> b
                .index(i -> i
                        .index(finalIndex3)
                        .id(String.valueOf(datum.get("id"))) // 设置文档 ID
                        .document(datum) // 设置文档内容
                )
        ));
    }

    List<BulkOperation> finalOperations1 = operations;
    BulkRequest request = BulkRequest.of(b -> b
            .operations(finalOperations1)
            .refresh(Refresh.True) // 设置刷新策略为 IMMEDIATE
    );

    // 执行批量请求
    BulkResponse response = client.bulk(request);
    Assert.assertFalse(response.errors());
    Assert.assertEquals(datumList.size(), response.items().size());
    response.items().forEach(o -> Assert.assertNull(o.error()));

    // endregion

    // region 测试批量插入错误处理

    index = "demo_index_errors";
    try {
        String finalIndex2 = index;
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(DeleteIndexRequest.of(o -> o.index(finalIndex2)));
        Assert.assertTrue(deleteIndexResponse.acknowledged());
    } catch (Exception ex) {
        // 忽略索引不存在
    }

    CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index)
            .mappings(m -> m
                    .properties("id", p -> p.long_(l -> l.store(false)))
                    .properties("content", p -> p.text(t -> t
                            .store(false)
                            .analyzer("ik_max_word")))
                    // LocalDateTime 类型没有指定 format 是为了测试批量插入时错误
                    .properties("createTime", p -> p.date(t -> t.store(false)))
            ).build();
    CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
    Assert.assertEquals(Boolean.TRUE, createIndexResponse.acknowledged());

    operations = new ArrayList<>();

    for (Map<String, Object> datum : datumList) {
        String finalIndex4 = index;
        operations.add(BulkOperation.of(b -> b
                .index(i -> i
                        .index(finalIndex4)
                        .id(String.valueOf(datum.get("id"))) // 设置文档 ID
                        .document(datum) // 设置文档内容
                )
        ));
    }

    List<BulkOperation> finalOperations = operations;
    request = BulkRequest.of(b -> b
            .operations(finalOperations)
            .refresh(Refresh.True) // 设置刷新策略为 IMMEDIATE
    );

    // 执行批量请求
    response = client.bulk(request);
    Assert.assertTrue(response.errors());
    Assert.assertEquals(datumList.size(), response.items().size());
    response.items().forEach(o -> Assert.assertNotNull(o.error()));
    // reason 错误原因样例：failed to parse field [createTime] of type [date] in document with id '1'. Preview of field's value: '2025-06-02 19:32:17'
    response.items().forEach(o -> Assert.assertTrue(o.error().reason().contains("failed to parse field [createTime] of type [date] in document with id")));

    // endregion
}
```



#### `term` 等值查询

```java
/**
 * 用户查询指定日期范围+指定状态的订单
 *
 * @param userId
 * @param status
 * @param startTime
 * @param endTime
 * @return
 */
public List<OrderDTO> listByUserIdAndStatus(
        Long userId,
        Status status,
        LocalDateTime startTime,
        LocalDateTime endTime) throws IOException {

    // 1. 构建查询条件
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Query boolQuery = QueryBuilders.bool()
            .filter(QueryBuilders.term().field("userId").value(userId).build()._toQuery())
            .filter(QueryBuilders.term().field("status").value(status.name()).build()._toQuery())
            .filter(QueryBuilders.terms().field("deleteStatus")
                    .terms(t -> t.value(Arrays.stream(DeleteStatus.values()).map(o -> FieldValue.of(o.name())).collect(Collectors.toList())))
                    .build()._toQuery())
            .filter(QueryBuilders.range()
                    .field("createTime")
                    .gte(JsonData.of(dateTimeFormatter.format(startTime)))
                    .lte(JsonData.of(dateTimeFormatter.format(endTime)))
                    .build()._toQuery()
            )
            .build()._toQuery();

    // 2. 构建排序和分页
    SearchRequest searchRequest = SearchRequest.of(s -> s
            .index("t_order")
            .query(boolQuery)
            .sort(so -> so.field(f -> f.field("id").order(SortOrder.Desc)))
            .from(0)
            .size(20)
    );

    // 3. 执行查询
    SearchResponse<OrderModel> response = client.search(searchRequest, OrderModel.class);

    // 4. 解析结果
    List<OrderModel> orderModelList = response.hits().hits().stream()
            .map(Hit::source)
            .collect(Collectors.toList());

    return this.convertOrderEntityToOrderDTO(orderModelList);
}
```



#### `term in` 查询

```java
private List<OrderDetailModel> listOrderDetailByOrderIds(List<Long> orderIdList) throws IOException {
    // 1. 构建查询条件
    Query termsQuery = QueryBuilders.terms()
            .field("orderId")
            .terms(t -> t.value(orderIdList.stream().map(FieldValue::of).collect(Collectors.toList())))
            .build()._toQuery();

    // 2. 构建排序和分页
    SearchRequest searchRequest = SearchRequest.of(s -> s
            .index("t_order_detail")
            .query(termsQuery)
    );

    // 3. 执行查询
    SearchResponse<OrderDetailModel> response = client.search(searchRequest, OrderDetailModel.class);

    // 4. 解析结果
    return response.hits().hits().stream()
            .map(Hit::source)
            .collect(Collectors.toList());
}
```



#### LocalDateTime 类型处理

POM 配置添加如下依赖

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.18.1</version>
</dependency>
```

客户端初始化添加如下逻辑：

```java
// 支持 LocalDateTime 类型处理
JacksonJsonpMapper mapper = new JacksonJsonpMapper(new ObjectMapper().registerModule(new JavaTimeModule()));
transport = new RestClientTransport(
        restClient,
        mapper // 使用Jackson作为JSON处理器
);
```

测试

```java
/**
 * 测试 LocalDateTime 类型处理
 *
 * @throws IOException
 */
@Test
public void testLocalDateTimeDataType() throws IOException {
    // region 测试自定义 Bean 中的 LocalDateTime 类型处理

    String index = "demo_index";
    // 删除索引
    try {
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(DeleteIndexRequest.of(o -> o.index(index)));
        Assert.assertTrue(deleteIndexResponse.acknowledged());
    } catch (Exception ex) {
        // 忽略索引不存在
    }

    // 创建索引
    List<MyBean> datumList = new ArrayList<MyBean>() {{
        add(new MyBean(1L, "a", LocalDateTime.now()));
        add(new MyBean(2L, "a", LocalDateTime.now()));
        add(new MyBean(3L, "a1", LocalDateTime.now()));
        add(new MyBean(4L, "a2", LocalDateTime.now()));
    }};

    CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index)
            .mappings(m -> m
                    .properties("id", p -> p.long_(l -> l.store(false)))
                    .properties("content", p -> p.text(t -> t
                            .store(false)
                            .analyzer("ik_max_word")))
                    // 用于测试 LocalDateTime 类型
                    .properties("createTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss")))
            ).build();
    CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
    Assert.assertEquals(Boolean.TRUE, createIndexResponse.acknowledged());

    // 插入数据到索引中
    datumList.forEach(datum -> {
        try {
            IndexRequest<MyBean> indexRequest = IndexRequest.of(o -> {
                return o.index(index)
                        .id(String.valueOf(datum.getId())) // 设置文档 ID
                        .document(datum)
                        .refresh(Refresh.True); // 设置文档内容
            });
            IndexResponse indexResponse = client.index(indexRequest);
            Assert.assertEquals(Result.Created, indexResponse.result());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    });

    // endregion
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public static class MyBean {
    private Long id;
    private String content;
    // 处理 LocalDateTime 类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```



## 实现好友、群组、群组成员、聊天消息关键字搜索功能

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/elasticsearch-practise-demo)

运行示例需要启动 elasticsearch7。



## 性能实验

### `Java transport client`单条插入和批量插入性能对比

详细的`jmh`代码请参考`https://gitee.com/dexterleslie/demonstration/blob/master/elasticsearch/elasticsearch7/elasticsearch-java-transport-client/src/test/java/com/future/demo/elasticsearch/IndividualAndBulkAddPerfComparisonTests.java`

实验结论：批量插入性能高于单条插入性能。



## 数据类型

### `keyword`

`keyword` 类型用于存储需要完全匹配的字符串，例如：枚举值（如订单状态：`Unpay`、`Undelivery` 等），ID、代码、标签等，分类字段（如产品类别、用户角色）。与 `text` 类型不同，`keyword` 类型不会启用分析器，不会对值进行分词或处理。



### `text`

`text` 是 Elasticsearch 中用于存储和分析文本数据的核心字段类型，专为全文搜索设计。支持对文本内容进行分词、索引和模糊匹配，例如：搜索文章、博客、产品描述等长文本内容。实现关键词搜索、自动补全、拼写纠正等功能。通过分析器（Analyzer）将文本拆分为词项（Terms），便于索引和查询。



### `date`

`date` 类型用于存储日期和时间信息，例如订单的付款时间、创建时间等。Elasticsearch 的 `date` 类型支持日期范围查询、日期聚合、日期直方图等操作。

```json
{
  "mappings": {
    "properties": {
      "payTime": {
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss||epoch_millis",  // 支持多种格式
        "null_value": null  // 可选：指定 null 值的处理方式
      }
    }
  }
}
```

date 类型支持多种日期格式，例如：

- `yyyy-MM-dd HH:mm:ss`（如 `2023-10-01 12:00:00`）
- `epoch_millis`（Unix 时间戳，毫秒级）
- `strict_date_optional_time`（严格格式，支持 ISO 8601）

可以通过 `format` 参数指定一个或多个格式。



## 子字段

Elasticsearch 8 中的**子字段（Sub-fields）**是字段映射（mapping）中的一个重要概念，它允许你为同一个字段值定义多种不同的索引方式，从而支持不同的搜索和分析需求。以下是关于子字段的详细说明：

---

### **一、子字段的核心概念**
子字段是**父字段的附属字段**，它们：
1. **共享相同的原始值**（来自同一个字段内容）
2. **拥有独立的索引配置**（不同的分词器、数据类型等）
3. **用于不同的搜索场景**（精确匹配、全文搜索、聚合等）

#### **典型应用场景**：
- 一个文本字段同时需要**分词搜索**和**精确匹配**
- 一个数字字段需要**不同精度**的存储
- 支持**多语言处理**（如中英文混合字段）

---

### **二、子字段的常见类型**
#### 1. **多分词策略子字段**
```json
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",  // 主字段用中文分词
        "fields": {
          "english": {
            "type": "text",
            "analyzer": "english"    // 子字段用英文分词
          },
          "keyword": {
            "type": "keyword"       // 子字段用于精确匹配
          }
        }
      }
    }
  }
}
```
- 搜索时通过 `title.english` 或 `title.keyword` 指定子字段

#### 2. **多数据类型子字段**
```json
{
  "mappings": {
    "properties": {
      "price": {
        "type": "float",           // 主字段存储原始值
        "fields": {
          "approx": {
            "type": "integer"      // 子字段存储近似整数值
          }
        }
      }
    }
  }
}
```

#### 3. **多语言支持子字段**
```json
{
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "fields": {
          "cn": { "type": "text", "analyzer": "ik_smart" },
          "en": { "type": "text", "analyzer": "english" }
        }
      }
    }
  }
}
```

---

### **三、子字段的搜索与聚合**
#### 1. **查询时指定子字段**
```json
GET /products/_search
{
  "query": {
    "match": {
      "title.keyword": "华为手机"  // 精确匹配
    }
  }
}
```

#### 2. **多子字段组合查询**
```json
GET /articles/_search
{
  "query": {
    "multi_match": {
      "query": "人工智能",
      "fields": ["content", "content.cn^2"]  // 中文子字段权重更高
    }
  }
}
```

#### 3. **子字段聚合**
```json
GET /products/_search
{
  "aggs": {
    "price_groups": {
      "histogram": {
        "field": "price.approx",  // 使用子字段近似值
        "interval": 1000
      }
    }
  }
}
```

---

### **四、Elasticsearch 8 中子字段的新特性**
1. **更严格的动态映射控制**  
   8.x 版本默认禁用动态映射，子字段需要明确定义：
   ```json
   {
     "mappings": {
       "dynamic": "strict",  // 禁止自动创建字段
       "properties": {...}
     }
   }
   ```

2. **对 `keyword` 子字段的优化**  
   自动生成的 `keyword` 子字段（如 `field.keyword`）在 8.x 中默认禁用，需显式声明：
   ```json
   {
     "mappings": {
       "properties": {
         "name": {
           "type": "text",
           "fields": {
             "raw": { "type": "keyword" }  // 必须手动定义
           }
         }
       }
     }
   }
   ```

3. **支持 `flattened` 类型的子字段**  
   用于索引嵌套 JSON 对象：
   ```json
   {
     "mappings": {
       "properties": {
         "metadata": {
           "type": "flattened",
           "fields": {
             "exact": { "type": "keyword" }
           }
         }
       }
     }
   }
   ```

---

### **五、子字段 vs 多字段（Multi-fields）**
在 Elasticsearch 7.x 之前称为 "multi-fields"，8.x 后统一称为子字段（sub-fields），但核心概念相同。

---

### **六、实际案例：电商商品映射**
```json
PUT /products
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fields": {
          "pinyin": {
            "type": "text",
            "analyzer": "pinyin_analyzer"
          },
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "price": {
        "type": "scaled_float",
        "scaling_factor": 100,
        "fields": {
          "usd": {
            "type": "float"
          }
        }
      }
    }
  }
}
```
- `name` 字段：支持中文分词、拼音搜索和精确匹配
- `price` 字段：存储精确值（如 19.99 → 保存为 1999）和原始浮点数

---

### **七、验证子字段映射**
```bash
GET /products/_mapping/field/name*
```
返回结果示例：
```json
{
  "products": {
    "mappings": {
      "name": {
        "full_name": "name",
        "mapping": {
          "analyzer": "ik_max_word",
          "fields": {
            "pinyin": { "type": "text", "analyzer": "pinyin_analyzer" },
            "keyword": { "type": "keyword", "ignore_above": 256 }
          },
          "type": "text"
        }
      }
    }
  }
}
```

---

### **总结**
| 特性           | 说明                                                |
| -------------- | --------------------------------------------------- |
| **共享原始值** | 所有子字段从同一字段获取数据                        |
| **独立配置**   | 每个子字段可设置不同的数据类型/分词器               |
| **搜索灵活性** | 通过 `field.subfield` 语法指定搜索方式              |
| **存储开销**   | 会增加索引大小（但远小于冗余存储多个字段）          |
| **ES8 变化**   | 需显式定义子字段，默认不再自动生成 `keyword` 子字段 |

子字段是 Elasticsearch 实现**一源多用**的核心机制，合理使用能显著提升搜索效率和灵活性。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/elasticsearch8-java-client)

```java
CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index)
        .mappings(m -> m
                .properties("id", p -> p.long_(l -> l.store(false)))
                .properties("content", p -> p.text(t -> t
                        .store(false)
                        .analyzer("ik_max_word")
                        // content 字段添加子字段支持拼音搜索
                        .fields("pinyin", ft -> ft.text(t1 ->
                                t1.analyzer("pinyin")
                                        .store(false)))
                ))
                .properties("userId", p -> p.long_(l -> l.store(false)))
                .properties("productId", p -> p.long_(l -> l.store(false)))
                // 用于测试 LocalDateTime 类型
                .properties("createTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss")))
        ).build();
CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
Assert.assertEquals(Boolean.TRUE, createIndexResponse.acknowledged());
```



## `Rest API`

### 统计索引中文档总数

```json
GET /t_order/_count
{
  "query": {
    "match_all": {}
  }
}

GET /t_order_detail/_count
{
  "query": {
    "match_all": {}
  }
}
```

