package com.future.demo.elasticsearch;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class DemoTests {
    final static String IndexDemo = "elasticsearch_java_transport_client_demo";
    final static String IndexDemo1 = "elasticsearch_java_transport_client_demo1";

    TransportClient client;

    @Before
    public void before() throws IOException, InterruptedException {
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster")
                .build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        // 删除之前的已存在索引
        try {
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(IndexDemo).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        } catch (Exception ex) {
            // 忽略索引不存在情况
        }
        try {
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(IndexDemo1).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        } catch (Exception ex) {
        }

        // 准备测试好友测试数据
        List<Object[]> datumList = Arrays.asList(
                new Object[] {1, "关键字1"},
                new Object[] {2, "关键字2"},
                new Object[] {3, "关键字3"},
                new Object[] {4, "关键字4"},
                new Object[] {5, "关键字5"},
                new Object[] {6, "关键字6"},
                new Object[] {7, "关键字7"},
                new Object[] {8, "关键字8"},
                new Object[] {9, "关键字9"},
                new Object[] {10, "可就如诶人"},
                new Object[] {11, "938可佛渡日哦"}
        );
        List<Object[]> datumList1 = Arrays.asList(
                new Object[] {1, "a"},
                new Object[] {2, "a"},
                new Object[] {3, "a1"},
                new Object[] {4, "a2"}
        );

        // keyword字段索引转换为小写
        // https://stackoverflow.com/questions/43492477/elasticsearch-keyword-and-lowercase-and-aggregation
        XContentBuilder xContentBuilderSettings = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("analysis")
                        .startObject("normalizer")
                            .startObject("keyword_lowercase")
                                .field("type", "custom")
                                .field("filter", "lowercase")
                            .endObject()
                        .endObject()
                        .startObject("analyzer")
                            .startObject("ik_smart_pinyin")
                                .field("type", "custom")
                                .field("tokenizer", "ik_smart")
                                .field("filter", Arrays.asList("my_pinyin", "word_delimiter"))
                            .endObject()
                            .startObject("ik_max_word_pinyin")
                                .field("type", "custom")
                                .field("tokenizer", "ik_max_word")
                                .field("filter", Arrays.asList("my_pinyin", "word_delimiter"))
                            .endObject()
                        .endObject()
                        .startObject("filter")
                            .startObject("my_pinyin")
                                .field("type", "pinyin")
                                .field("keep_separate_first_letter", true)
                                .field("keep_full_pinyin", true)
                                .field("keep_original", true)
                                .field("limit_first_letter_length", 16)
                                .field("lowercase", true)
                                .field("remove_duplicated_term", true)
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();

        // 创建好友关系索引
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("id")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("name")
                            .field("type", "text")
                            .field("store", true)
                            .field("analyzer", "ik_max_word")
                        .endObject()
                    .endObject()
                .endObject();
        CreateIndexResponse createIndexResponse = client.admin().indices()
                .prepareCreate(IndexDemo)
                .addMapping("_doc", xContentBuilder)
                .setSettings(xContentBuilderSettings)
                .get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        datumList.forEach(datum -> {
            try {
                XContentBuilder xContentBuilderTemporary = XContentFactory.jsonBuilder()
                        .startObject()
                            .field("id", datum[0])
                            .field("name", datum[1])
                        .endObject();
                IndexResponse indexResponse = client.prepareIndex(IndexDemo, "_doc", String.valueOf(datum[0]))
                        .setSource(xContentBuilderTemporary)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                        .get();
                Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        });

        xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("id")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("content")
                            .field("type", "text")
                            .field("store", false)
                            .field("analyzer", "ik_smart")
                        .endObject()
                    .endObject()
                .endObject();
        createIndexResponse = client.admin().indices()
                .prepareCreate(IndexDemo1)
                .addMapping("_doc", xContentBuilder)
                .setSettings(xContentBuilderSettings)
                .get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        datumList1.forEach(datum -> {
            try {
                XContentBuilder xContentBuilderTemporary = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", datum[0])
                        .field("content", datum[1])
                        .endObject();
                IndexResponse indexResponse = client.prepareIndex(IndexDemo1, "_doc", String.valueOf(datum[0]))
                        .setSource(xContentBuilderTemporary)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                        .get();
                Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        });
    }

    @After
    public void after() {
        if(client != null) {
            client.close();
        }
    }

    @Test
    public void test() {
        // 演示count聚合
        String keyword = "关键";
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.prefixQuery("name", keyword));
        ValueCountAggregationBuilder valueCountAggregationBuilder = AggregationBuilders.count("countById").field("id");
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setTypes("_doc")
                .setQuery(queryBuilder).addAggregation(valueCountAggregationBuilder).setSize(0).get();
        Assert.assertEquals(9, ((InternalValueCount) searchResponse.getAggregations().get("countById")).getValue());
        Assert.assertEquals(0, searchResponse.getHits().getHits().length);

        // 演示字母a搜索
        keyword = "a";
        queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.prefixQuery("content", keyword));
        searchResponse = client.prepareSearch(IndexDemo1).setTypes("_doc").setQuery(queryBuilder).get();
        Assert.assertEquals(2, searchResponse.getHits().getHits().length);
    }
}
