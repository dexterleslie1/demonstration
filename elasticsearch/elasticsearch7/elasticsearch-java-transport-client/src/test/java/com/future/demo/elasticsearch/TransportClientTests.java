package com.future.demo.elasticsearch;

import com.future.common.json.JSONUtil;
import lombok.Data;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TransportClientTests {
    final static String IndexDemo = "demo_index";

    TransportClient client;

    Map<Long, Object[]> idToTitleAndContentMapper = new HashMap<>();

    @Before
    public void before() throws IOException, InterruptedException {
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
        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(IndexDemo).get();
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
                .startObject("province")
                .field("type", "keyword")
                .field("store", false)
                .endObject()
                .startObject("title")
                .field("type", "text")
                .field("store", false)
                .field("index", true)
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("content")
                .field("type", "text")
                .field("store", false)
                .field("index", true)
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("views")
                .field("type", "integer")
                .field("store", false)
                .field("index", true)
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
        idToTitleAndContentMapper.put(1L, new Object[]{"Elasticsearch -发- 版本：6.8.8", "文章参考如下链接，但有些内容可能过时，以实践结果为主：", "广东", 10});
        idToTitleAndContentMapper.put(2L, new Object[]{"35个项目首次参赛的背后——版本北京冬奥会推动中国冬季运动跨越式发展", "7日，当中国高山滑雪选手徐铭甫在北京冬奥会男子滑降比赛中冲过终点时，中国高山滑雪运动也在这一刻取得了历史性的突破——这是历史上中国选手首次参加并完成奥运会高山滑雪男子滑降的比赛。", "广东", 5});
        idToTitleAndContentMapper.put(3L, new Object[]{"北京冬奥会 | 燃！这个冬天，看中国的00后在干什么", "他们不畏中国强手敢打敢拼", "广东", 20});
        idToTitleAndContentMapper.put(4L, new Object[]{"日本运动员发文点赞冬奥志愿者 广东网友热议“好暖”", "太田雄贵在推文中说，“在前往开幕式会场的大巴上，冬奥志愿者们用英文提示大家当天的注意事项，可能因为反复练习，使用的文稿纸张有不少折痕。当志愿者略带紧张地宣读完毕后，车上的乘客纷纷用热烈的掌声赞美他们的努力，现场呈现出一幅很棒的画面”。", "北京", 100});
        idToTitleAndContentMapper.put(5L, new Object[]{"春节假期全国揽收投递快递包裹7.49亿件", "春节期间，全国中国邮政快递业运行情况总体安全稳定，邮政快递服务业务量增幅突破较大。其中，揽收快递包裹4.2亿件，与2019年、2020年、2021年农历同期相比分别增长545%、338%、12.04%；投递快递包裹3.29亿件，与2019年、2020年、2021年农历同期相比分别增长645%、280%、21.6%。", "上海", 50});
        Set<Long> keySet = idToTitleAndContentMapper.keySet();
        keySet.forEach(idTemporary -> {
            try {
                XContentBuilder builderTemporary = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", idTemporary)
                        .field("title", idToTitleAndContentMapper.get(idTemporary)[0])
                        .field("content", idToTitleAndContentMapper.get(idTemporary)[1])
                        .field("province", idToTitleAndContentMapper.get(idTemporary)[2])
                        .field("views", idToTitleAndContentMapper.get(idTemporary)[3])
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
    public void after() throws IOException {
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/transport-client.html
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void test_exists() {
        String indexRandom = "elk_elasticsearch_demo_" + UUID.randomUUID();
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().prepareExists(indexRandom).get();
        Assert.assertFalse(indicesExistsResponse.isExists());

        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(indexRandom).get();
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());
        Assert.assertTrue(createIndexResponse.isAcknowledged());
    }

    @Test
    public void test_id_query() {
        // 根据id查询文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-docs-get.html
        Long id = 1L;
        GetResponse getResponse = client.prepareGet(IndexDemo, "_doc", "1").get();
        Assert.assertEquals(String.valueOf(id), getResponse.getId());
        Assert.assertEquals(IndexDemo, getResponse.getIndex());
        Assert.assertTrue(getResponse.isExists());
        Assert.assertEquals(id, new Long((Integer) getResponse.getSource().get("id")));
        Assert.assertEquals(idToTitleAndContentMapper.get(id)[0], getResponse.getSource().get("title"));
        Assert.assertEquals(idToTitleAndContentMapper.get(id)[1], getResponse.getSource().get("content"));

        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(String.valueOf(id));
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(queryBuilder).get();
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
    }

    @Test
    public void test_term_query() {
        // 根据term关键词查询文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-search.html
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.termQuery("title", "什么")).get();
        Assert.assertEquals(RestStatus.OK, searchResponse.status());
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);

        // 相当于MySQL in查询
        searchResponse = client.prepareSearch(IndexDemo)
                .setQuery(QueryBuilders.termsQuery("title", "版本", "发展")).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        List<String> idList = Arrays.asList("1", "2");
        searchResponse.getHits().forEach(searchHit -> Assert.assertTrue(idList.contains(searchHit.getId())));
    }

    @Test
    public void test_query_string() {
        // 根据queryString查询文档
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.queryStringQuery("什么运动").defaultField("title")).get();
        Assert.assertEquals(RestStatus.OK, searchResponse.status());
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits().value);
    }

    @Test
    public void test_match_query() {
        // match查询会根据你查询的字段类型不一样，采用不同的查询方式
        // 查询的是日期或者数值，他会将你基于字符串的查询内容转换为日期或者数值对待
        // 查询的内容是一个不能被分词的内容(keyword)，match查询不会对你指定的查询关键字进行分词
        // 如果查询的内容是一个可以被分词的内容(text)，match会将你指定的查询内容根据一定的方式分词
        // match查询的底层就是多个term查询

        // 自动分词为： 版本，发展
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.matchQuery("title", "版本发展")).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        List<String> idList = Arrays.asList("1", "2");
        searchResponse.getHits().forEach(searchHit -> Assert.assertTrue(idList.contains(searchHit.getId())));

        // province 是keyword类型，不自动分词，所以查不出结果
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.matchQuery("province", "广东北京")).get();
        Assert.assertEquals(0, searchResponse.getHits().getTotalHits().value);

        // 布尔match查询
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.matchQuery("title", "版本发展").operator(Operator.AND)).get();
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);

        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.matchQuery("title", "版本发展").operator(Operator.OR)).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        searchResponse.getHits().forEach(searchHit -> Assert.assertTrue(idList.contains(searchHit.getId())));

        // multi_match一个查询关键字对应多个field
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.multiMatchQuery("广东", "province", "title")).get();
        Assert.assertEquals(4, searchResponse.getHits().getTotalHits().value);
        searchResponse.getHits().forEach(searchHit -> Assert.assertTrue(searchHit.getSourceAsString().contains("广东")));
    }

    @Test
    public void test_match_all() {
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setTypes("_doc").setQuery(QueryBuilders.matchAllQuery()).get();
        Assert.assertEquals(idToTitleAndContentMapper.size(), searchResponse.getHits().getTotalHits().value);
    }

    @Test
    public void test_prefix_query() {
        // NOTE: 关键字不会被分词 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/query-dsl-prefix-query.html

        // 不分词字段prefix查询
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.prefixQuery("province", "广")).get();
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits().value);
        searchResponse.getHits().forEach(searchHit -> Assert.assertEquals("广东", searchHit.getSourceAsMap().get("province")));

        // 分词字段prefix查询
        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.prefixQuery("content", "推")).get();
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals("4", searchResponse.getHits().getHits()[0].getId());
    }

    @Test
    public void test_wildcard_query() {
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.wildcardQuery("title", "发?")).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);

        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.wildcardQuery("title", "发*")).get();
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits().value);
    }

    @Test
    public void test_range_query() throws IOException {
        String indexname = "demo_test_range_query";

        try {
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(indexname).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        } catch (Exception ex) {

        }

        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(indexname).get();
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());
        Assert.assertTrue(createIndexResponse.isAcknowledged());

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .field("store", false)
                .endObject()
                .startObject("name")
                .field("type", "keyword")
                .field("store", false)
                .endObject()
                .endObject()
                .endObject();

        AcknowledgedResponse acknowledgedResponse = client.admin().indices().preparePutMapping(indexname).setType("_doc").setSource(xContentBuilder).get();
        Assert.assertTrue(acknowledgedResponse.isAcknowledged());

        int total = 10;
        for (int i = 1; i <= total; i++) {
            xContentBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("id", i)
                    .field("name", "name" + i)
                    .endObject();
            IndexResponse indexResponse = client.prepareIndex(indexname, "_doc", String.valueOf(i))
                    .setSource(xContentBuilder)
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .get();
            Assert.assertEquals(DocWriteResponse.Result.CREATED, indexResponse.getResult());
            Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
        }

        SearchResponse searchResponse = client.prepareSearch(indexname).setQuery(QueryBuilders.matchAllQuery()).get();
        Assert.assertEquals(total, searchResponse.getHits().getTotalHits().value);

        // 默认包含lower和upper
        // 排序
        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").from(3))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .get();
        Assert.assertEquals(8, searchResponse.getHits().getTotalHits().value);
        List<Integer> expectedList = Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3);
        List<Integer> actualList = new ArrayList<>();
        searchResponse.getHits().forEach(hit -> {
            actualList.add(Integer.parseInt(hit.getSourceAsMap().get("id").toString()));
        });
        Assert.assertArrayEquals(expectedList.toArray(), actualList.toArray());

        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").from(3))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
                .get();
        Assert.assertEquals(8, searchResponse.getHits().getTotalHits().value);
        List<Integer> expectedList1 = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> actualList1 = new ArrayList<>();
        searchResponse.getHits().forEach(hit -> {
            actualList1.add(Integer.parseInt(hit.getSourceAsMap().get("id").toString()));
        });
        Assert.assertArrayEquals(expectedList1.toArray(), actualList1.toArray());

        // 不包含lower=3
        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").includeLower(false).from(3))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
                .get();
        Assert.assertEquals(7, searchResponse.getHits().getTotalHits().value);
        List<Integer> expectedList2 = Arrays.asList(4, 5, 6, 7, 8, 9, 10);
        List<Integer> actualList2 = new ArrayList<>();
        searchResponse.getHits().forEach(hit -> {
            actualList2.add(Integer.parseInt(hit.getSourceAsMap().get("id").toString()));
        });
        Assert.assertArrayEquals(expectedList2.toArray(), actualList2.toArray());

        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").includeUpper(false).to(5))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
                .get();
        Assert.assertEquals(4, searchResponse.getHits().getTotalHits().value);
        expectedList = Arrays.asList(1, 2, 3, 4);
        List<Integer> actualList3 = new ArrayList<>();
        searchResponse.getHits().forEach(hit -> {
            actualList3.add(Integer.parseInt(hit.getSourceAsMap().get("id").toString()));
        });
        Assert.assertArrayEquals(expectedList.toArray(), actualList3.toArray());

        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").includeUpper(true).to(5))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
                .get();
        Assert.assertEquals(5, searchResponse.getHits().getTotalHits().value);
        expectedList = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> actualList4 = new ArrayList<>();
        searchResponse.getHits().forEach(hit -> {
            actualList4.add(Integer.parseInt(hit.getSourceAsMap().get("id").toString()));
        });
        Assert.assertArrayEquals(expectedList.toArray(), actualList4.toArray());

        // 模拟指定聊天记录id上下文的当前上下文、上一页、下一页

        // 当前上下文
        int currentId = 6;
        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").includeUpper(true).to(currentId))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .setSize(2)
                .get();
        List<Integer> upperList =
                Arrays.asList(searchResponse.getHits().getHits()).stream().map(hit -> Integer.parseInt(hit.getSourceAsMap().get("id").toString())).collect(Collectors.toList());
        upperList = upperList.stream().sorted(((o1, o2) -> o1 - o2)).collect(Collectors.toList());
        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").includeLower(false).from(currentId))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
                .setSize(2)
                .get();
        List<Integer> lowerList =
                Arrays.asList(searchResponse.getHits().getHits()).stream().map(hit -> Integer.parseInt(hit.getSourceAsMap().get("id").toString())).collect(Collectors.toList());
        upperList.addAll(lowerList);
        List<Integer> expectedList3 = Arrays.asList(5, 6, 7, 8);
        Assert.assertArrayEquals(expectedList3.toArray(), upperList.toArray());

        // 当前上下文的上一页
        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").includeUpper(false).to(upperList.get(0)))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .setSize(2)
                .get();
        List<Integer> previousList =
                Arrays.asList(searchResponse.getHits().getHits()).stream().map(hit -> Integer.parseInt(hit.getSourceAsMap().get("id").toString())).collect(Collectors.toList());
        previousList = previousList.stream().sorted(((o1, o2) -> o1 - o2)).collect(Collectors.toList());
        Assert.assertArrayEquals(Arrays.asList(3, 4).toArray(), previousList.toArray());

        // 当前上下文的下一页
        searchResponse = client.prepareSearch(indexname)
                .setQuery(QueryBuilders.rangeQuery("id").includeLower(false).from(upperList.get(upperList.size() - 1)))
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
                .setSize(2)
                .get();
        List<Integer> nextList =
                Arrays.asList(searchResponse.getHits().getHits()).stream().map(hit -> Integer.parseInt(hit.getSourceAsMap().get("id").toString())).collect(Collectors.toList());
        Assert.assertArrayEquals(Arrays.asList(9, 10).toArray(), nextList.toArray());
    }

    @Test
    public void test_regexp_query() {
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.regexpQuery("title", "发[展|文]")).get();
        Assert.assertEquals(2, searchResponse.getHits().getHits()[0].getSourceAsMap().get("id"));
        Assert.assertEquals(4, searchResponse.getHits().getHits()[1].getSourceAsMap().get("id"));
    }

    @Test
    public void test_compound_query() {
        // 复合查询

        // 布尔查询
        // 省份是广东或者北京
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery("province", "广东"))
                .should(QueryBuilders.termQuery("province", "北京"));
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(queryBuilder).get();
        searchResponse.getHits().forEach(hit -> Assert.assertTrue(hit.getSourceAsMap().get("province").equals("广东") || hit.getSourceAsMap().get("province").equals("北京")));

        // 省份是广东时title必须包含“干什么”
        queryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("province", "广东"))
                        .must(QueryBuilders.termQuery("title", "干什么")))
                .should(QueryBuilders.termQuery("province", "北京"));
        searchResponse = client.prepareSearch(IndexDemo).setQuery(queryBuilder)
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC)).get();
        Assert.assertEquals("4", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("3", searchResponse.getHits().getHits()[1].getId());

        // 省份必须是广东或者北京
        // title不能包含跨越
        // content包含中国或者突破
        queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("province", "广东", "北京"))
                // title不能包含“跨越”关键词
                .mustNot(QueryBuilders.termQuery("title", "跨越"))
                // content中包含“中国”或者“突破”关键词
                .must(QueryBuilders.matchQuery("content", "中国突破").operator(Operator.OR));
        searchResponse = client.prepareSearch(IndexDemo).setQuery(queryBuilder).get();
        Assert.assertEquals("3", searchResponse.getHits().getHits()[0].getId());

        // TODO boosting查询
    }

    @Test
    public void test_filter_query() {
        // 根据你的查询条件去查询文档，不去计算得分和相关度，filter会对经常被过滤额数据进行缓存
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("province", "广东"))
                .filter(QueryBuilders.termQuery("title", "35"));
        SearchResponse searchResponse = client.prepareSearch(IndexDemo)
                .setQuery(queryBuilder).get();
        Assert.assertEquals("2", searchResponse.getHits().getHits()[0].getId());
    }

    @Test
    public void test_highlight_query() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("content", "中国");

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("content", 5).preTags("<font color='red'>").postTags("</font>");

        SearchResponse searchResponse = client.prepareSearch(IndexDemo)
                .setQuery(queryBuilder)
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .highlighter(highlightBuilder).get();
//        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
//        searchResponse.getHits().forEach(hit -> {
//            Arrays.asList(hit.getHighlightFields().get("content").getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + fragment.toString()));
//        });
        Assert.assertEquals("5", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("3", searchResponse.getHits().getHits()[1].getId());
        Assert.assertEquals("2", searchResponse.getHits().getHits()[2].getId());
        Assert.assertEquals(1, searchResponse.getHits().getHits()[0].getHighlightFields().get("content").getFragments().length);
        Assert.assertEquals(1, searchResponse.getHits().getHits()[1].getHighlightFields().get("content").getFragments().length);
        Assert.assertEquals(3, searchResponse.getHits().getHits()[2].getHighlightFields().get("content").getFragments().length);
        Assert.assertEquals("春节期间，全国<font color='red'>中国</font>邮政快递业运行情况总体安全稳定", searchResponse.getHits().getHits()[0].getHighlightFields().get("content").getFragments()[0].toString());
        Assert.assertEquals("他们不畏<font color='red'>中国</font>强手敢打敢拼", searchResponse.getHits().getHits()[1].getHighlightFields().get("content").getFragments()[0].toString());
        Assert.assertEquals("7日，当<font color='red'>中国</font>高山滑雪选手徐铭甫在北京冬奥会男子滑降比赛中冲过终点时", searchResponse.getHits().getHits()[2].getHighlightFields().get("content").getFragments()[0].toString());
        Assert.assertEquals("，<font color='red'>中国</font>高山滑雪运动也在这一刻取得了历史性的突破", searchResponse.getHits().getHits()[2].getHighlightFields().get("content").getFragments()[1].toString());
        Assert.assertEquals("这是历史上<font color='red'>中国</font>选手首次参加并完成奥运会高山滑雪男子滑降的比赛", searchResponse.getHits().getHits()[2].getHighlightFields().get("content").getFragments()[2].toString());
    }

    @Test
    public void test_sort_query() {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch(IndexDemo)
                .setQuery(queryBuilder)
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .get();

        List<String> idList = Arrays.asList("5", "4", "3", "2", "1");
        List<String> idList1 = Arrays.asList(searchResponse.getHits().getHits()).stream().map(hit -> hit.getId()).collect(Collectors.toList());
        Assert.assertArrayEquals(idList.toArray(), idList1.toArray());

        queryBuilder = QueryBuilders.matchAllQuery();
        searchResponse = client.prepareSearch(IndexDemo)
                .setQuery(queryBuilder)
                .addSort(SortBuilders.fieldSort("views").order(SortOrder.ASC))
                .get();

        idList = Arrays.asList("2", "1", "3", "5", "4");
        idList1 = Arrays.asList(searchResponse.getHits().getHits()).stream().map(hit -> hit.getId()).collect(Collectors.toList());
        Assert.assertArrayEquals(idList.toArray(), idList1.toArray());
    }

    @Test
    public void test_aggregation_query() {
        // 聚合查询
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-aggs.html
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/_metrics_aggregations.html

        CardinalityAggregationBuilder cardinalityAggregationBuilder = AggregationBuilders.cardinality("cardin").field("province");
        SearchResponse searchResponse = client.prepareSearch(IndexDemo).addAggregation(cardinalityAggregationBuilder).get();
        Cardinality cardinality = searchResponse.getAggregations().get("cardin");
        Assert.assertEquals(3, cardinality.getValue());

        // TODO range聚合范围统计

        // 统计聚合查询，查询field的最大值、最小值、平均值等
        ExtendedStatsAggregationBuilder extendedStatsAggregationBuilder = AggregationBuilders.extendedStats("extStats").field("views");
        searchResponse = client.prepareSearch(IndexDemo).addAggregation(extendedStatsAggregationBuilder).get();
        ExtendedStats extendedStats = searchResponse.getAggregations().get("extStats");
        Assert.assertEquals(5, extendedStats.getCount());
        Assert.assertEquals(5.0, extendedStats.getMin(), 0);
        Assert.assertEquals(100.0, extendedStats.getMax(), 0);

        // 根据province分组统计views
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("groupProvince").field("province");
        termsAggregationBuilder.subAggregation(AggregationBuilders.sum("groupViewsSum").field("views"));
        termsAggregationBuilder.subAggregation(AggregationBuilders.max("groupViewsMax").field("views"));
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("title", "elasticsearch"));
        searchResponse = client.prepareSearch(IndexDemo)
                .setQuery(queryBuilder)
                .addAggregation(termsAggregationBuilder).get();

//        searchResponse.getAggregations().forEach(System.out::println);

        // 判断是否有三个省份
        Assert.assertEquals(3, ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBuckets().size());
        Set<String> provinceSet = idToTitleAndContentMapper.values().stream().map(objects -> (String) objects[2]).collect(Collectors.toSet());
        ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBuckets().forEach(bucket -> Assert.assertTrue(provinceSet.contains(bucket.getKeyAsString())));

        // 广东doc=2
        Assert.assertEquals(2, ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("广东").getDocCount());
        // 上海doc=1
        Assert.assertEquals(1, ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("上海").getDocCount());
        // 北京doc=1
        Assert.assertEquals(1, ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("北京").getDocCount());

        // 广东=25
        Assert.assertEquals(25.0, ((InternalSum) ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("广东").getAggregations().get("groupViewsSum")).getValue(), 0);
        // 上海=50
        Assert.assertEquals(50.0, ((InternalSum) ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("上海").getAggregations().get("groupViewsSum")).getValue(), 0);
        // 北京=100
        Assert.assertEquals(100.0, ((InternalSum) ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("北京").getAggregations().get("groupViewsSum")).getValue(), 0);

        Assert.assertEquals(20.0, ((InternalMax) ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("广东").getAggregations().get("groupViewsMax")).getValue(), 0);
        Assert.assertEquals(50.0, ((InternalMax) ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("上海").getAggregations().get("groupViewsMax")).getValue(), 0);
        Assert.assertEquals(100.0, ((InternalMax) ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("北京").getAggregations().get("groupViewsMax")).getValue(), 0);
    }

    @Test
    public void test_aggregation_query_group() throws IOException {
        String indexname = "demo_test_aggregation_query_group";

        try {
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(indexname).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        } catch (Exception ex) {

        }

        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(indexname).get();
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());
        Assert.assertTrue(createIndexResponse.isAcknowledged());

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("groupId")
                .field("type", "long")
                .field("store", false)
                .endObject()
                .endObject()
                .endObject();

        AcknowledgedResponse acknowledgedResponse = client.admin().indices().preparePutMapping(indexname).setType("_doc").setSource(xContentBuilder).get();
        Assert.assertTrue(acknowledgedResponse.isAcknowledged());

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("byGroupId").field("groupId");

        SearchResponse searchResponse = client.prepareSearch(indexname).addAggregation(termsAggregationBuilder).get();
        Assert.assertEquals(0, ((LongTerms) searchResponse.getAggregations().get("byGroupId")).getBuckets().size());

        List<Long> groupIdList = Arrays.asList(1L, 2L, 2L, 3L, 5L, 5L);
        groupIdList.forEach(groupId -> {
            XContentBuilder xContentBuilder1 = null;
            try {
                xContentBuilder1 = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("groupId", groupId)
                        .endObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            IndexResponse indexResponse = client.prepareIndex(indexname, "_doc")
                    .setSource(xContentBuilder1)
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .get();
            Assert.assertEquals(DocWriteResponse.Result.CREATED, indexResponse.getResult());
            Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
        });

        termsAggregationBuilder = AggregationBuilders.terms("byGroupId").field("groupId");

        searchResponse = client.prepareSearch(indexname).addAggregation(termsAggregationBuilder).get();
        Assert.assertEquals(4, ((LongTerms) searchResponse.getAggregations().get("byGroupId")).getBuckets().size());
        Assert.assertEquals(1, ((LongTerms) searchResponse.getAggregations().get("byGroupId")).getBucketByKey("1").getDocCount());
        Assert.assertEquals(2, ((LongTerms) searchResponse.getAggregations().get("byGroupId")).getBucketByKey("2").getDocCount());
        Assert.assertEquals(1, ((LongTerms) searchResponse.getAggregations().get("byGroupId")).getBucketByKey("3").getDocCount());
        Assert.assertEquals(2, ((LongTerms) searchResponse.getAggregations().get("byGroupId")).getBucketByKey("5").getDocCount());
    }

    @Test
    public void test_delete_document() throws InterruptedException {
        // 根据id删除
        DeleteResponse deleteResponse = client.prepareDelete(IndexDemo, "_doc", String.valueOf(1L))
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                .get();
        Assert.assertEquals(DocWriteResponse.Result.DELETED, deleteResponse.getResult());

        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.matchAllQuery()).get();
        Assert.assertEquals(idToTitleAndContentMapper.size() - 1, searchResponse.getHits().getTotalHits().value);
        searchResponse.getHits().forEach(hit -> Assert.assertNotEquals("1", hit.getId()));

        // 根据查询删除
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-delete-by-query.html
        DeleteByQueryRequestBuilder deleteByQueryRequestBuilder = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE);
        deleteByQueryRequestBuilder.filter(QueryBuilders.termQuery("id", 2));
        // 相当于 WriteRequest.RefreshPolicy.IMMEDIATE
        deleteByQueryRequestBuilder.request().setRefresh(true);
        deleteByQueryRequestBuilder.source(IndexDemo);
        BulkByScrollResponse bulkByScrollResponse = deleteByQueryRequestBuilder.get();
        Assert.assertEquals(1, bulkByScrollResponse.getDeleted());

        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.matchAllQuery()).get();
        Assert.assertEquals(idToTitleAndContentMapper.size() - 2, searchResponse.getHits().getTotalHits().value);
        searchResponse.getHits().forEach(hit -> {
            Assert.assertNotEquals("1", hit.getId());
            Assert.assertNotEquals("2", hit.getId());
        });
    }

    @Test
    public void test_bulk_add_document() throws InterruptedException {
        DeleteByQueryRequestBuilder deleteByQueryRequestBuilder = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE);
        deleteByQueryRequestBuilder.filter(QueryBuilders.matchAllQuery());
        // 相当于 WriteRequest.RefreshPolicy.IMMEDIATE
        deleteByQueryRequestBuilder.request().setRefresh(true);
        deleteByQueryRequestBuilder.source(IndexDemo);
        BulkByScrollResponse bulkByScrollResponse = deleteByQueryRequestBuilder.get();
        Assert.assertEquals(idToTitleAndContentMapper.size(), bulkByScrollResponse.getDeleted());

        SearchResponse searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.matchAllQuery()).get();
        Assert.assertEquals(0, searchResponse.getHits().getTotalHits().value);

        // 批量新增文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk.html
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        idToTitleAndContentMapper.forEach((key, value) -> {
            try {
                bulkRequestBuilder.add(client.prepareIndex(IndexDemo, "_doc", String.valueOf(key))
                        .setSource(XContentFactory.jsonBuilder()
                                .startObject()
                                .field("id", key)
                                .field("title", idToTitleAndContentMapper.get(key)[0])
                                .field("content", idToTitleAndContentMapper.get(key)[1])
                                .field("province", idToTitleAndContentMapper.get(key)[2])
                                .field("views", idToTitleAndContentMapper.get(key)[3])
                                .endObject()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Assert.assertFalse(bulkRequestBuilder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).get().hasFailures());

        searchResponse = client.prepareSearch(IndexDemo).setQuery(QueryBuilders.matchAllQuery()).get();
        Assert.assertEquals(idToTitleAndContentMapper.size(), searchResponse.getHits().getTotalHits().value);
    }

    @Test
    public void test_nested_data_type() throws IOException, InterruptedException {
        // 测试nested数据类型
        // 使用博客和评论场景模拟
        // https://blog.csdn.net/laoyang360/article/details/82950393

        String index = "demo_elk_es_nested_data_type";

        if (client.admin().indices().prepareExists(index).get().isExists()) {
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(index).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        }

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("title")
                .field("type", "text")
                .endObject()
                .startObject("body")
                .field("type", "text")
                .endObject()
                .startObject("tags")
                .field("type", "keyword")
                .endObject()
                .startObject("published_on")
                .field("type", "keyword")
                .endObject()
                .startObject("comments")
                .field("type", "nested")
                .startObject("properties")
                .startObject("name")
                .field("type", "text")
                .endObject()
                .startObject("comment")
                .field("type", "text")
                .endObject()
                .startObject("age")
                .field("type", "short")
                .endObject()
                .startObject("rating")
                .field("type", "short")
                .endObject()
                .startObject("commented_on")
                .field("type", "keyword")
                .endObject()
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(index).addMapping("blog", xContentBuilder).get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .field("title", "Invest Money")
                .field("body", "Please start investing money as soon...")
                .field("tags", Arrays.asList("money", "invest"))
                .field("published_on", "18 Oct 2017")
                .startArray("comments")
                .startObject()
                .field("name", "William")
                .field("age", 34)
                .field("rating", 8)
                .field("comment", "Nice article..")
                .field("commented_on", "30 Nov 2017")
                .endObject()
                .startObject()
                .field("name", "John")
                .field("age", 38)
                .field("rating", 9)
                .field("comment", "I started investing after reading this.")
                .field("commented_on", "25 Nov 2017")
                .endObject()
                .startObject()
                .field("name", "Smith")
                .field("age", 33)
                .field("rating", 7)
                .field("comment", "Very good post")
                .field("commented_on", "20 Nov 2017")
                .endObject()
                .endArray()
                .endObject();
        IndexResponse indexResponse = client.prepareIndex(index, "blog", "1").setSource(xContentBuilder).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).get();
        Assert.assertEquals(DocWriteResponse.Result.CREATED, indexResponse.getResult());
        Assert.assertEquals(RestStatus.CREATED, indexResponse.status());

        QueryBuilder queryBuilder = QueryBuilders.nestedQuery("comments", QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("comments.name", "john"))
                .must(QueryBuilders.termQuery("comments.age", 34)), ScoreMode.None);
        SearchResponse searchResponse = client.prepareSearch(index).setQuery(queryBuilder).get();
        Assert.assertEquals(0, searchResponse.getHits().getTotalHits().value);

        queryBuilder = QueryBuilders.nestedQuery("comments", QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("comments.name", "john"))
                .must(QueryBuilders.termQuery("comments.age", 38)), ScoreMode.None);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("comments.name", 15).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(index).setQuery(queryBuilder).highlighter(highlightBuilder).get();
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals("<font color='red'>John</font>", searchResponse.getHits().getHits()[0].getHighlightFields().get("comments.name").getFragments()[0].string());
    }

    @Test
    public void testScroll() throws IOException {
        String index = "demo_index_scroll";

        if (client.admin().indices().prepareExists(index).get().isExists()) {
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(index).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        }

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .endObject()
                .endObject()
                .endObject();
        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(index).addMapping("_doc", xContentBuilder).get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        int total = 101;
        int size = 3;
        List<Long> expectedList = new ArrayList<>();
        for (long i = 1; i <= total; i++) {
            xContentBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("id", i)
                    .endObject();
            IndexResponse indexResponse = client.prepareIndex(index, "_doc", String.valueOf(i))
                    .setSource(xContentBuilder).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).get();
            Assert.assertEquals(DocWriteResponse.Result.CREATED, indexResponse.getResult());
            Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
            expectedList.add(i);
        }

        List<Long> actualList = new ArrayList<>();
        int count = 0;
        int totalCount = 0;
        SearchResponse scrollResp = client.prepareSearch(index)
//                .addSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
                .setScroll(new TimeValue(60000))
                .setQuery(QueryBuilders.matchAllQuery())
                .setSize(size).get();
        do {
            count++;
            totalCount = totalCount + scrollResp.getHits().getHits().length;
            if (scrollResp.getHits() != null) {
                scrollResp.getHits().forEach(hit -> actualList.add(Long.parseLong(hit.getSourceAsMap().get("id").toString())));
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(60000)).execute().actionGet();
        } while (scrollResp.getHits().getHits().length != 0);

        int page;
        if (total % size == 0) {
            page = total / size;
        } else {
            page = (total + size) / size;
        }
        Assert.assertEquals(page, count);
        Assert.assertEquals(total, totalCount);
        Assert.assertEquals(expectedList.size(), actualList.size());
        expectedList.forEach(id -> Assert.assertTrue(actualList.contains(id)));
    }

    private final static Random R = new Random(System.currentTimeMillis());
    private final static int MaximumMessageId = 10000000;

    /**
     * 测试是否因为删除延迟导致插入数据丢失
     * 结论：不存在删除延迟导致插入数据丢失问题
     *
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void testIfDeletionDelayLeadToInsertionDatumMissing() throws InterruptedException, IOException {
        // 创建消息索引
        String indexChatMessage = "chat_message";
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().prepareExists(indexChatMessage).get();

        if (indicesExistsResponse.isExists()) {
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(indexChatMessage).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        }

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

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .field("store", false)
                .endObject()
                // 此索引所属的用户id
                .startObject("userId")
                .field("type", "long")
                .field("store", false)
                .endObject()
                .startObject("content")
                .field("type", "text")
                .field("store", false)
                .field("analyzer", "ik_max_word")
                .endObject()
                // 专门用于存储文件、图片、视频类型消息原始content JSON
                .startObject("contentJSON")
                .field("type", "keyword")
                .field("store", false)
                .endObject()
                .endObject()
                .endObject();

        client.admin().indices()
                .prepareCreate(indexChatMessage)
                .addMapping("_doc", xContentBuilder)
                .setSettings(xContentBuilderSettings)
                .get();

        int total = 1000;

        // 生成随机的消息id
        List<Integer> randomMessageIdList = new ArrayList<>();
        while (randomMessageIdList.size() < total) {
            int randomInt = R.nextInt(MaximumMessageId);

            if (randomInt <= 0 || randomMessageIdList.contains(randomInt)) {
                continue;
            }

            randomMessageIdList.add(randomInt);
        }
        Assert.assertEquals(total, randomMessageIdList.size());

        String content = "nTA132Xw5OlUzEMbwqEHtGPx2ktroW2ZFVUAM6i9bSMnbWuGZTCwYS7mZ3vVFbvdUpQo7sXN3LKsQEYNDL3lgzfO3W5ubIjf9uWMNz97hoT7600x5HTyRHDxT0mQalVvVDMZXaAFItzDAlwWnRW9XG2fGvjQOsVIkv3gWOsdc3sFC4H6bcFTFk3pptD13IqMxafNBSfS2D6Dkb4wj1ApSEVxOyO8Fp1zHhOzzZrSM31MYF3EWVfeGxQhptBRQKVKkU8jpzV07oH5rOk0pGQJQ6nBWXk0FNJjO1PFtcuS1Mmqft4iF2w3H1Jx1HgekowZE3iKSTuULs571bdeIvgxq1zkba5ulCxAdevGXK1WpA8BeVKuHnSyelZ2kGpcWLTfmvLdtWkV0dCNlVTkZnjZrP7SCZBYvlV9dYELRnDRRd9AVyaQUcQ9TwdHb6QoOaRPOub8UTjVBSqzGPq5gqaahOMijdoQz4VmSe1Ft8K0nimZeCff6Fni8VDwqB4xZp1tiHqlFK02QTJnm0SKUm32Tb8YssVOrBrGznBYPkRI1ptvDhcDpfq0MRTA4wFHV0mQoNlDwP8EHxORswtUXyozymXRTOZO4ra3BTJspXDT8WrtEI8SIgGmE1FpEkRztZsc9N4kAGTPJmjXFzIFhL1xm9tXd9Xu5UUGSBNVknRTbUZnIXohi0MUATuCduusFMzlaDz2TWxm660fc0jW3R7kWtPgJFEL78fu9NpQrPmVbInkcBoEDYR3LH8pWkZY6d430JSZkF42QHUqDbtVAwk6VwvFQIwWyGoMI0HBPaJ4p14vj1f0t0cBrX0P95IBfQxbkN4RjdUtHVqNREPMuLgS46VJJxoZzDlPhPw61YR8RczkrA9yBK0Ml2yTwivIpb4gzhoQH6zBwMWAwHsGovFFMAa8uPK81cXbmluCaGKvwnEBzQviuzcPPH7g9w7fzED1Q2luf8JTtak86MVUjhF96wYnXg4qp3gB9lroT4oF";

        int concurrentThreads = 100;
        int userIdSize = 5;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < total; j++) {
                        int messageId = randomMessageIdList.get(j);

                        // 删除之前的messageId对应的索引
                        DeleteByQueryRequestBuilder deleteByQueryRequestBuilder = new DeleteByQueryRequestBuilder(this.client, DeleteByQueryAction.INSTANCE);
                        deleteByQueryRequestBuilder.filter(QueryBuilders.termQuery("id", messageId));
                        deleteByQueryRequestBuilder.source(indexChatMessage);
                        deleteByQueryRequestBuilder.get();

                        for (int k = 1; k <= userIdSize; k++) {
                            long userId = k;
                            ESIndexChatMessageDTO esIndexChatMessageDTO = new ESIndexChatMessageDTO();
                            esIndexChatMessageDTO.setId(messageId);
                            esIndexChatMessageDTO.setUserId(userId);
                            esIndexChatMessageDTO.setContent(content);
                            esIndexChatMessageDTO.setContentJSON(content);
                            String idStr = messageId + "#" + userId;
                            IndexResponse indexResponse = client.prepareIndex(indexChatMessage, "_doc", idStr)
                                    .setSource(JSONUtil.ObjectMapperInstance.writeValueAsString(esIndexChatMessageDTO), XContentType.JSON)
                                    .get();
                            Assert.assertEquals("id=" + messageId + "聊天消息索引数据同步失败", DocWriteResponse.Result.CREATED, indexResponse.getResult());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        Thread.sleep(3000);

        // 检查数据是否完整
        SearchResponse searchResponse = client.prepareSearch(indexChatMessage).setTypes("_doc").setQuery(QueryBuilders.matchAllQuery()).get();
        Assert.assertEquals(randomMessageIdList.size() * userIdSize, searchResponse.getHits().getTotalHits().value);
    }

    /**
     * 聊天消息索引DTO
     */
    @Data
    private static class ESIndexChatMessageDTO {
        private long id;
        private long userId;
        private String content;
        private String contentJSON;
    }
}
