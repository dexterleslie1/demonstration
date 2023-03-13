package com.future.demo.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
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
    public final static String IndexDemo = "demo_idx_ik_analyzer_ext";
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
//        PUT my-index-000001
//        {
//            "settings": {
//            "analysis": {
//                "analyzer": {
//                    "my_stop_analyzer": {
//                        "type": "stop",
//                                "stopwords": ["the", "over"]
//                    }
//                }
//            }
//        }
//        }
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
                                .field("keep_separate_first_letter", false)
                                .field("keep_full_pinyin", true)
                                .field("keep_original", true)
                                .field("keep_joined_full_pinyin", true)
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
                            .field("analyzer", "ik_max_word_pinyin")
                            .startObject("fields")
                                .startObject("standard")
                                    .field("type", "text")
                                    .field("analyzer", "standard")
                                .endObject()
                            .endObject()
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
        idToObjectArratMapper.put(1L, new Object[] {"are you right there? The moment."});
        idToObjectArratMapper.put(2L, new Object[] {"杨日琳right，同a事"});
        idToObjectArratMapper.put(3L, new Object[] {"北京冬奥会 | 燃！这个冬天，看中华人民共和国的00后在干什么"});
        idToObjectArratMapper.put(4L, new Object[] {"精讲讲义-第6部分第35章"});
        idToObjectArratMapper.put(5L, new Object[] {"春节假期全国揽收投递快递精品包裹7.49亿件"});
        idToObjectArratMapper.put(6L, new Object[] {"1122"});
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
        // 支持are、the停用词
        String keyword = "yo";
        SearchResponse searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(1, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "are";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(1, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "you";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(1, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "right";
        searchResponse = search(keyword);
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(1, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));
        keyword = "there";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(1, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "The";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(1, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "?";
        searchResponse = search(keyword);
        Assert.assertEquals(0, searchResponse.getHits().getTotalHits().value);

        keyword = "杨";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "日";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "琳";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "同";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "a";
        searchResponse = search(keyword);
//        searchResponse.getHits().forEach(o -> {
//            o.getHighlightFields().forEach((key, value) -> {
//                System.out.println(value.getFragments()[0].string());
//            });
//        });
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(1, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[2].getId()));
        keyword = "事";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));

        keyword = "共和";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));

        keyword = "go";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "gon";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "gong";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "gonghe";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        keyword = "gh";
        searchResponse = search(keyword);
//        searchResponse.getHits().forEach(o -> {
//            o.getHighlightFields().forEach((key, value) -> {
//                System.out.println(value.getFragments()[0].string());
//            });
//        });
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(1, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[2].getId()));
        keyword = "ghg";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(3, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));

        keyword = "精";
        searchResponse = search(keyword);
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(4, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
        Assert.assertEquals(5, Integer.parseInt(searchResponse.getHits().getHits()[1].getId()));

        keyword = "精讲";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(4, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));

        keyword = "日琳";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));

        keyword = "2";
        searchResponse = search(keyword);
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(6, Integer.parseInt(searchResponse.getHits().getHits()[0].getId()));
    }

    public SearchResponse search(String keyword) {
        keyword = QueryParser.escape(keyword);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("content", 128).preTags("##").postTags("##");
        highlightBuilder.field("content.standard", 128).preTags("##").postTags("##");
        return client.prepareSearch(IndexDemo)
                .setQuery(
                        QueryBuilders.boolQuery()
                                .should(QueryBuilders.prefixQuery("content.standard", keyword))
                                .should(QueryBuilders.prefixQuery("content", keyword))
//                                .should(QueryBuilders.matchQuery("content", keyword).analyzer("ik_max_word"))
                                .should(QueryBuilders.wildcardQuery("content.standard", "*" + keyword + "*"))
                )
                .highlighter(highlightBuilder)
                .get();
    }

    // 下面analyze用于协助测试
    @Test
    public void test1() {
//        AnalyzeAction.Response analyzeResponse = client.admin().indices().prepareAnalyze(IndexDemo, "are you right there?").setField("content").get();
//        System.out.println(analyzeResponse.toString());
//        AnalyzeAction.Response analyzeResponse = client.admin().indices().prepareAnalyze(IndexDemo, "中华人民共和国").setField("content").get();
//        System.out.println(analyzeResponse.toString());
//        AnalyzeAction.Response analyzeResponse = client.admin().indices().prepareAnalyze(IndexDemo, "杨日琳are，同a事").setField("content").get();
//        System.out.println(analyzeResponse.toString());
//        AnalyzeAction.Response analyzeResponse = client.admin().indices().prepareAnalyze(IndexDemo, "日琳").setField("content").get();
//        System.out.println(analyzeResponse.toString());
//        AnalyzeAction.Response analyzeResponse = client.admin().indices().prepareAnalyze(IndexDemo, "燃").setField("content").get();
//        System.out.println(analyzeResponse.toString());
//        AnalyzeAction.Response analyzeResponse = client.admin().indices().prepareAnalyze(IndexDemo, "精讲讲义-第6部分第35章 2").setField("content").get();
//        System.out.println(analyzeResponse.toString());
//        AnalyzeAction.Response analyzeResponse = client.admin().indices().prepareAnalyze(IndexDemo, "1122").setAnalyzer("standard").get();
//        System.out.println(analyzeResponse.toString());
    }
}
