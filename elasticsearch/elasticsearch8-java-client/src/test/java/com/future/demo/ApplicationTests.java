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
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
