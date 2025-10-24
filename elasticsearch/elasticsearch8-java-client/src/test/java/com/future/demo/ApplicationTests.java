package com.future.demo;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.BooleanProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.PropertyVariant;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.get.GetResult;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class ApplicationTests extends AbstractTestSupport {

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
                put("userId", 1L);
                put("productId", 1L);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
            }});
            add(new HashMap<String, Object>() {{
                put("id", "2");
                put("content", "a1");
                put("userId", 1L);
                put("productId", 2L);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
            }});
            add(new HashMap<String, Object>() {{
                put("id", "3");
                put("content", "a2");
                put("userId", 2L);
                put("productId", 1L);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
            }});
            add(new HashMap<String, Object>() {{
                put("id", "4");
                put("content", "a3");
                put("userId", 2L);
                put("productId", 3L);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
            }});

            // 协助中文搜索测试准备数据
            add(new HashMap<String, Object>() {{
                put("id", "5");
                put("content", "中华人民共和国");
                put("userId", 11L);
                put("productId", 22L);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
            }});
            add(new HashMap<String, Object>() {{
                put("id", "6");
                put("content", "基本配置和使用");
                put("userId", 11L);
                put("productId", 22L);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
            }});
            add(new HashMap<String, Object>() {{
                put("id", "7");
                put("content", "`elasticsearch 8.x` 以上官方推荐使用人民这个客户端操作 `elasticsearch`");
                put("userId", 11L);
                put("productId", 22L);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                put("createTime", dateTimeFormatter.format(LocalDateTime.now()));
            }});
        }};

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

        // region 根据 id 查询

        String docId = "1"; // 要查询的文档 ID
        GetRequest getRequest = GetRequest.of(g -> g
                .index(index)       // 指定索引名
                .id(docId)          // 指定文档 ID
        );
        GetResponse<Map> getResponse = client.get(getRequest, Map.class); // 指定返回类型为 Map
        // 验证查询结果
        Assert.assertTrue(getResponse.found());  // 确认文档存在
        Assert.assertEquals("1", getResponse.source().get("id")); // 验证文档内容
        Assert.assertEquals("a", getResponse.source().get("content"));

        // 批量查询多个文档
        MgetRequest multiGetRequest = MgetRequest.of(m -> m
                .index(index)
                .ids("1", "2", String.valueOf(Long.MAX_VALUE))  // 指定多个文档 ID
        );
        MgetResponse<Map> multiGetResponse = client.mget(multiGetRequest, Map.class);
        Assert.assertEquals(3, multiGetResponse.docs().size());
        // 获取文档不会失败即使文档 id 不存在
        Assert.assertFalse(multiGetResponse.docs().get(0).isFailure());
        Assert.assertFalse(multiGetResponse.docs().get(1).isFailure());
        Assert.assertFalse(multiGetResponse.docs().get(2).isFailure());
        GetResult getResult = (GetResult) multiGetResponse.docs().get(0)._get();
        Assert.assertEquals("1", getResult.id());
        Map mapSource = (Map) getResult.source();
        Assert.assertEquals("a", mapSource.get("content"));
        getResult = (GetResult) multiGetResponse.docs().get(1)._get();
        Assert.assertEquals("2", getResult.id());
        mapSource = (Map) getResult.source();
        Assert.assertEquals("a1", mapSource.get("content"));
        getResult = (GetResult) multiGetResponse.docs().get(2)._get();
        Assert.assertEquals("9223372036854775807", getResult.id());
        mapSource = (Map) getResult.source();
        // 文档 id 不存在 mapSource=null
        Assert.assertNull(mapSource);

        // endregion

        // region term 查询

        // 查询 userId=1 的所有文档
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .term(t -> t
                                .field("userId")
                                .value(1L)  // 注意类型匹配（Long）
                        )
                )
        );
        SearchResponse<Map> response = client.search(searchRequest, Map.class);
        Assert.assertEquals(2, response.hits().hits().size());
        Assert.assertEquals("1", response.hits().hits().get(0).id());
        Assert.assertEquals("2", response.hits().hits().get(1).id());

        // 查询 productId=1 的所有文档
        searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .term(t -> t
                                .field("productId")
                                .value(1L)
                        )
                )
        );
        response = client.search(searchRequest, Map.class);
        Assert.assertEquals(2, response.hits().hits().size());
        Assert.assertEquals("1", response.hits().hits().get(0).id());
        Assert.assertEquals("3", response.hits().hits().get(1).id());

        // 查询 productId=1 的所有文档 order by id 降序
        searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .term(t -> t
                                .field("productId")
                                .value(1L)
                        )
                )
                .sort(so -> so
                        .field(f -> f
                                .field("id")  // 使用 id 作为排序字段
                                .order(SortOrder.Desc)  // 降序排序
                        )
                )
        );
        response = client.search(searchRequest, Map.class);
        Assert.assertEquals(2, response.hits().hits().size());
        Assert.assertEquals("3", response.hits().hits().get(0).id());
        Assert.assertEquals("1", response.hits().hits().get(1).id());

        // 查询 userId=1 AND productId=1 的文档
        SearchRequest boolRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .bool(b -> b
                                .must(
                                        // 条件1：userId=1
                                        q1 -> q1.term(t -> t.field("userId").value(1L))
                                ).must(
                                        // 条件2：productId=1
                                        q2 -> q2.term(t -> t.field("productId").value(1L))
                                )
                        )
                )
        );
        SearchResponse<Map> boolResponse = client.search(boolRequest, Map.class);
        Assert.assertEquals(1, boolResponse.hits().hits().size());
        Assert.assertEquals("1", boolResponse.hits().hits().get(0).id());

        // 查询 userId=1 AND productId=1 的文档另一种写法
        // 1. 构建查询条件
        Query boolQuery = QueryBuilders.bool()
                .filter(QueryBuilders.term().field("userId").value(1L).build()._toQuery())
                .filter(QueryBuilders.term().field("productId").value(1L).build()._toQuery())
                .build()._toQuery();
        // 2. 构建排序和分页
        searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(boolQuery)
                .sort(so -> so.field(f -> f.field("id").order(SortOrder.Desc)))
                .size(10000)
        );
        // 3. 执行查询
        response = client.search(searchRequest, Map.class);
        Assert.assertEquals(1, response.hits().hits().size());
        Assert.assertEquals("1", response.hits().hits().get(0).id());

        // endregion

        // region range 查询

        // 查询 id 大于等于 3 和小于等于 4 的文档
        searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .range(t -> t
                                .field("id")
                                .gte(JsonData.of(3L))
                                .lte(JsonData.of(4L))
                        )
                )
        );
        response = client.search(searchRequest, Map.class);
        Assert.assertEquals(2, response.hits().hits().size());
        Assert.assertEquals("3", response.hits().hits().get(0).id());
        Assert.assertEquals("4", response.hits().hits().get(1).id());

        // endregion

        // region 测试中文搜索

        // 根据 "人民" 中文关键字查询
        searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .match(t -> t
                                .field("content")
                                .query("人民")
                        )
                )
        );
        response = client.search(searchRequest, Map.class);
        Assert.assertEquals(2, response.hits().hits().size());
        Assert.assertEquals("5", response.hits().hits().get(0).id());
        Assert.assertEquals("7", response.hits().hits().get(1).id());

        // 根据 "人民" 中文拼音关键字查询
        searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .match(t -> t
                                .field("content.pinyin")
                                .query("renmin")
                        )
                )
        );
        response = client.search(searchRequest, Map.class);
        Assert.assertEquals(2, response.hits().hits().size());
        Assert.assertEquals("5", response.hits().hits().get(0).id());
        Assert.assertEquals("7", response.hits().hits().get(1).id());

        // 根据 "人民" 中文拼音首字母关键字查询
        /*searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .match(t -> t
                                .field("content.pinyin")
                                .query("rm")
                        )
                )
        );
        response = client.search(searchRequest, Map.class);
        Assert.assertEquals(2, response.hits().hits().size());
        Assert.assertEquals("5", response.hits().hits().get(0).id());
        Assert.assertEquals("7", response.hits().hits().get(1).id());*/

        // endregion
    }

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
}
