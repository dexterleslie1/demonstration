package com.future.demo.elasticsearch.rest.high.level.client;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ApiTests extends AbstractTestSupport {
    public final static String IndexDemo = "index_demo";

    @Test
    public void test() throws IOException {
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(IndexDemo);
            AcknowledgedResponse response = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            Assert.assertTrue(response.isAcknowledged());
        } catch (Exception ex) {
            // 忽略索引不存在
        }

        List<Object[]> datumList1 = Arrays.asList(
                new Object[] {1, "a"},
                new Object[] {2, "a"},
                new Object[] {3, "a1"},
                new Object[] {4, "a2"}
        );

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("id")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("content")
                            .field("type", "text")
                            .field("store", false)
                            .field("analyzer", "ik_max_word")
                        .endObject()
                    .endObject()
                .endObject();

        CreateIndexRequest request = new CreateIndexRequest(IndexDemo);
        request.mapping(xContentBuilder);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        datumList1.forEach(datum -> {
            try {
                XContentBuilder xContentBuilderTemporary = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", datum[0])
                        .field("content", datum[1])
                        .endObject();
                IndexRequest indexRequest = new IndexRequest(IndexDemo);
                indexRequest.source(xContentBuilderTemporary);
                indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
                IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        });
    }
}
