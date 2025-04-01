package com.future.demo.elasticsearch;

import org.elasticsearch.action.DocWriteResponse;
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
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IkPluginTests {
    public final static String IndexDemo = "demo_index";
    TransportClient client;

    Map<Long, Object[]> idToObjectArratMapper = new HashMap<>();

    @Before
    public void setup() throws IOException {
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/transport-client.html
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster")
                .build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        // 删除索引
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-admin-indices.html
        try {
            client.admin().indices().prepareDelete(IndexDemo).get();
        } catch (IndexNotFoundException ex) {
            // 忽略索引不存在错误
        }

        // 准备索引数据
        // 创建索引
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-admin-indices.html
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
                            // 拼音和拼音首字母搜索
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
        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(IndexDemo).setSettings(xContentBuilderSettings).get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        // 设置_mappings
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-admin-indices.html
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("id")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("content")
                            .field("type", "text")
                            .field("store", false)
                            .field("index", true)
                            .field("analyzer", "ik_smart_pinyin")
                        .endObject()
                        .startObject("contentWithoutPinyin")
                            .field("type", "text")
                            .field("store", false)
                            .field("index", true)
                            .field("analyzer", "ik_smart")
                        .endObject()
                    .endObject()
                .endObject();
        AcknowledgedResponse acknowledgedResponse = client.admin().indices().preparePutMapping(IndexDemo)
                .setType("_doc")
                .setSource(builder).get();
        Assert.assertTrue(acknowledgedResponse.isAcknowledged());

        // 创建文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-docs-index.html
        // NOTE: 在版本6.8.0 content内容为“我是黎明前的黑暗”时，批量创建索引api会报错，解决办法升级到7.8.0
        idToObjectArratMapper.put(1L, new Object[] {"Elasticsearch 奥克兰-发- 版本：6.8.8"});
        idToObjectArratMapper.put(2L, new Object[] {"35个项目首次参赛的背后——版本北京冬奥会推动中国冬季运动跨越式发展"});
        idToObjectArratMapper.put(3L, new Object[] {"北京冬奥会 | 燃！这个冬天，看中国的00后在干什么"});
        idToObjectArratMapper.put(4L, new Object[] {"日本运动员发文点赞冬奥志愿者 广东网友热议“好暖”"});
        idToObjectArratMapper.put(5L, new Object[] {"春节假期全国揽收投递快递包裹7.49亿件"});
        Set<Long> keySet = idToObjectArratMapper.keySet();
        keySet.forEach(idTemporary -> {
            try {
                XContentBuilder builderTemporary = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", idTemporary)
                        .field("content", idToObjectArratMapper.get(idTemporary)[0])
                        .field("contentWithoutPinyin", idToObjectArratMapper.get(idTemporary)[0])
                        .endObject();
                IndexResponse indexResponse = client.prepareIndex(IndexDemo, "_doc", String.valueOf(idTemporary))
                        .setSource(builderTemporary)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                        .get();
                Assert.assertEquals(DocWriteResponse.Result.CREATED, indexResponse.getResult());
                Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
                Assert.assertEquals(String.valueOf(idTemporary), indexResponse.getId());
                Assert.assertEquals(IndexDemo, indexResponse.getIndex());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @After
    public void teardown() {
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/transport-client.html
        if(client != null) {
            client.close();
        }
    }

    @Test
    public void test() {
        // 演示拼音首字母搜索
        String keyword = "zg";
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.prefixQuery("content", keyword)).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));

        // 演示拼音搜索
        keyword = "zhong";
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.prefixQuery("content", keyword)).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));

        // 演示中文单词搜索
        keyword = "中国";
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.prefixQuery("content", keyword)).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));

        // 测试不根据拼音查询
        keyword = "中";
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.prefixQuery("contentWithoutPinyin", keyword)).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));
        keyword = "中国";
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.prefixQuery("contentWithoutPinyin", keyword)).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));
        keyword = "zg";
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.prefixQuery("contentWithoutPinyin", keyword)).get();
        Assert.assertEquals(0, searchResponse.getHits().getTotalHits().value);
    }
}
