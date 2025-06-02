package com.future.demo;

import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
