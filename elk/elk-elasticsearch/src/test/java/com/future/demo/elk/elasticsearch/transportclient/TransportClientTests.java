package com.future.demo.elk.elasticsearch.transportclient;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

public class TransportClientTests {
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
            client.admin().indices().prepareDelete("demo_index").get();
        } catch (IndexNotFoundException ex) {
            // 忽略索引不存在错误
        }

        // 准备索引数据
        // 创建索引
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-admin-indices.html
        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate("demo_index").get();
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
        AcknowledgedResponse acknowledgedResponse = client.admin().indices().preparePutMapping("demo_index")
                .setType("_doc")
                .setSource(builder).get();
        Assert.assertTrue(acknowledgedResponse.isAcknowledged());

        // 创建文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-docs-index.html
        idToTitleAndContentMapper.put(1L, new Object[] {"Elasticsearch -发- 版本：6.8.8", "文章参考如下链接，但有些内容可能过时，以实践结果为主：", "广东", 10});
        idToTitleAndContentMapper.put(2L, new Object[] {"35个项目首次参赛的背后——版本北京冬奥会推动中国冬季运动跨越式发展", "7日，当中国高山滑雪选手徐铭甫在北京冬奥会男子滑降比赛中冲过终点时，中国高山滑雪运动也在这一刻取得了历史性的突破——这是历史上中国选手首次参加并完成奥运会高山滑雪男子滑降的比赛。", "广东", 5});
        idToTitleAndContentMapper.put(3L, new Object[] {"北京冬奥会 | 燃！这个冬天，看中国的00后在干什么", "他们不畏中国强手敢打敢拼", "广东", 20});
        idToTitleAndContentMapper.put(4L, new Object[] {"日本运动员发文点赞冬奥志愿者 广东网友热议“好暖”", "太田雄贵在推文中说，“在前往开幕式会场的大巴上，冬奥志愿者们用英文提示大家当天的注意事项，可能因为反复练习，使用的文稿纸张有不少折痕。当志愿者略带紧张地宣读完毕后，车上的乘客纷纷用热烈的掌声赞美他们的努力，现场呈现出一幅很棒的画面”。", "北京", 100});
        idToTitleAndContentMapper.put(5L, new Object[] {"春节假期全国揽收投递快递包裹7.49亿件", "春节期间，全国中国邮政快递业运行情况总体安全稳定，邮政快递服务业务量增幅突破较大。其中，揽收快递包裹4.2亿件，与2019年、2020年、2021年农历同期相比分别增长545%、338%、12.04%；投递快递包裹3.29亿件，与2019年、2020年、2021年农历同期相比分别增长645%、280%、21.6%。", "上海", 50});
        Set<Long> keySet = idToTitleAndContentMapper.keySet();
        for(Long idTemporary : keySet) {
            builder = XContentFactory.jsonBuilder()
                    .startObject()
                        .field("id", idTemporary)
                        .field("title", idToTitleAndContentMapper.get(idTemporary)[0])
                        .field("content", idToTitleAndContentMapper.get(idTemporary)[1])
                        .field("province", idToTitleAndContentMapper.get(idTemporary)[2])
                        .field("views", idToTitleAndContentMapper.get(idTemporary)[3])
                    .endObject();
            IndexResponse indexResponse = client.prepareIndex("demo_index", "_doc", String.valueOf(idTemporary))
                    .setSource(builder).get();
            Assert.assertEquals(DocWriteResponse.Result.CREATED, indexResponse.getResult());
            Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
            Assert.assertEquals(String.valueOf(idTemporary), indexResponse.getId());
            Assert.assertEquals("demo_index", indexResponse.getIndex());
        }

        Thread.sleep(1000);
    }

    @After
    public void after() throws IOException {
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/transport-client.html
        if(client != null) {
            client.close();
        }
    }

    @Test
    public void test_id_query() {
        // 根据id查询文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-docs-get.html
        Long id = 1L;
//        GetResponse getResponse = client.prepareGet("demo_index", "_doc", "1")
//                // NOTE： 显示指定返回的field，否则下面getResponse.getField会返回null，指定storedFields后不返回_source，默认情况下只返回_source
//                .setStoredFields("id", "title", "content")
//                .get();
//        Assert.assertEquals(String.valueOf(id), getResponse.getId());
//        Assert.assertEquals("demo_index", getResponse.getIndex());
//        Assert.assertTrue(getResponse.isExists());
//        Assert.assertEquals(id, getResponse.getField("id").getValue());
//        Assert.assertEquals(idToTitleAndContentMapper.get(id)[0], getResponse.getField("title").getValue());
//        Assert.assertEquals(idToTitleAndContentMapper.get(id)[1], getResponse.getField("content").getValue());

        GetResponse getResponse = client.prepareGet("demo_index", "_doc", "1").get();
        Assert.assertEquals(String.valueOf(id), getResponse.getId());
        Assert.assertEquals("demo_index", getResponse.getIndex());
        Assert.assertTrue(getResponse.isExists());
        Assert.assertEquals(id, new Long((Integer)getResponse.getSource().get("id")));
        Assert.assertEquals(idToTitleAndContentMapper.get(id)[0], getResponse.getSource().get("title"));
        Assert.assertEquals(idToTitleAndContentMapper.get(id)[1], getResponse.getSource().get("content"));

        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(String.valueOf(id));
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(queryBuilder).get();
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits());
    }

    @Test
    public void test_term_query() {
        // 根据term关键词查询文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-search.html
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.termQuery("title", "什么")).get();
        Assert.assertEquals(RestStatus.OK, searchResponse.status());
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits());

        // 相当于MySQL in查询
        searchResponse = client.prepareSearch("demo_index")
                .setQuery(QueryBuilders.termsQuery("title", "版本", "发展")).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits());
        List<String> idList = Arrays.asList("1", "2");
        searchResponse.getHits().forEach(searchHit -> Assert.assertTrue(idList.contains(searchHit.getId())));
    }

    @Test
    public void test_query_string() {
        // 根据queryString查询文档
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.queryStringQuery("什么运动").defaultField("title")).get();
        Assert.assertEquals(RestStatus.OK, searchResponse.status());
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits());
    }

    @Test
    public void test_match_query() {
        // match查询会根据你查询的字段类型不一样，采用不同的查询方式
        // 查询的是日期或者数值，他会将你基于字符串的查询内容转换为日期或者数值对待
        // 查询的内容是一个不能被分词的内容(keyword)，match查询不会对你指定的查询关键字进行分词
        // 如果查询的内容是一个可以被分词的内容(text)，match会将你指定的查询内容根据一定的方式分词
        // match查询的底层就是多个term查询

        // 自动分词为： 版本，发展
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.matchQuery("title", "版本发展")).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits());
        List<String> idList = Arrays.asList("1", "2");
        searchResponse.getHits().forEach(searchHit -> Assert.assertTrue(idList.contains(searchHit.getId())));

        // province 是keyword类型，不自动分词，所以查不出结果
        searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.matchQuery("province", "广东北京")).get();
        Assert.assertEquals(0, searchResponse.getHits().getTotalHits());

        // 布尔match查询
        searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.matchQuery("title", "版本发展").operator(Operator.AND)).get();
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits());

        searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.matchQuery("title", "版本发展").operator(Operator.OR)).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits());
        searchResponse.getHits().forEach(searchHit -> Assert.assertTrue(idList.contains(searchHit.getId())));

        // multi_match一个查询关键字对应多个field
        searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.multiMatchQuery("广东", "province", "title")).get();
        Assert.assertEquals(4, searchResponse.getHits().getTotalHits());
        searchResponse.getHits().forEach(searchHit -> Assert.assertTrue(searchHit.getSourceAsString().contains("广东")));
    }

    @Test
    public void test_match_all() {
        SearchResponse searchResponse = client.prepareSearch("demo_index").setTypes("_doc").setQuery(QueryBuilders.matchAllQuery()).get();
        Assert.assertEquals(idToTitleAndContentMapper.size(), searchResponse.getHits().getTotalHits());
    }

    @Test
    public void test_prefix_query() {
        // NOTE: 关键字不会被分词 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/query-dsl-prefix-query.html

        // 不分词字段prefix查询
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.prefixQuery("province", "广")).get();
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits());
        searchResponse.getHits().forEach(searchHit -> Assert.assertEquals("广东", searchHit.getSourceAsMap().get("province")));

        // 分词字段prefix查询
        searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.prefixQuery("content", "推")).get();
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits());
        Assert.assertEquals("4", searchResponse.getHits().getHits()[0].getId());
    }

    @Test
    public void test_wildcard_query() {
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.wildcardQuery("title","发?")).get();
        Assert.assertEquals(2, searchResponse.getHits().getTotalHits());

        searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.wildcardQuery("title","发*")).get();
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits());
    }

    @Test
    public void test_range_query() {
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.rangeQuery("id").gte(2).lte(3)).get();
        Assert.assertEquals(2, searchResponse.getHits().getHits()[0].getSourceAsMap().get("id"));
        Assert.assertEquals(3, searchResponse.getHits().getHits()[1].getSourceAsMap().get("id"));
    }

    @Test
    public void test_regexp_query() {
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.regexpQuery("title", "发[展|文]")).get();
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
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(queryBuilder).get();
        searchResponse.getHits().forEach(hit -> Assert.assertTrue(hit.getSourceAsMap().get("province").equals("广东") || hit.getSourceAsMap().get("province").equals("北京")));

        // 省份是广东时title必须包含“干什么”
        queryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery("province", "广东"))
                            .must(QueryBuilders.termQuery("title", "干什么")))
                .should(QueryBuilders.termQuery("province", "北京"));
        searchResponse = client.prepareSearch("demo_index").setQuery(queryBuilder).get();
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
        searchResponse = client.prepareSearch("demo_index").setQuery(queryBuilder).get();
        Assert.assertEquals("3", searchResponse.getHits().getHits()[0].getId());

        // TODO boosting查询
    }

    @Test
    public void test_filter_query() {
        // 根据你的查询条件去查询文档，不去计算得分和相关度，filter会对经常被过滤额数据进行缓存
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("province", "广东"))
                .filter(QueryBuilders.termQuery("title", "35"));
        SearchResponse searchResponse = client.prepareSearch("demo_index")
                .setQuery(queryBuilder).get();
        Assert.assertEquals("2", searchResponse.getHits().getHits()[0].getId());
    }

    @Test
    public void test_highlight_query() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("content", "中国");

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("content", 5).preTags("<font color='red'>").postTags("</font>");

        SearchResponse searchResponse = client.prepareSearch("demo_index")
                .setQuery(queryBuilder).highlighter(highlightBuilder).get();
//        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
//        searchResponse.getHits().forEach(hit -> {
//            Arrays.asList(hit.getHighlightFields().get("content").getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + fragment.toString()));
//        });
        Assert.assertEquals("2", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("5", searchResponse.getHits().getHits()[1].getId());
        Assert.assertEquals("3", searchResponse.getHits().getHits()[2].getId());
        Assert.assertEquals(3, searchResponse.getHits().getHits()[0].getHighlightFields().get("content").getFragments().length);
        Assert.assertEquals(1, searchResponse.getHits().getHits()[1].getHighlightFields().get("content").getFragments().length);
        Assert.assertEquals(1, searchResponse.getHits().getHits()[2].getHighlightFields().get("content").getFragments().length);
        Assert.assertEquals("7日，当<font color='red'>中国</font>高山滑雪选手徐铭甫在北京冬奥会男子滑降比赛中冲过终点时", searchResponse.getHits().getHits()[0].getHighlightFields().get("content").getFragments()[0].toString());
        Assert.assertEquals("，<font color='red'>中国</font>高山滑雪运动也在这一刻取得了历史性的突破", searchResponse.getHits().getHits()[0].getHighlightFields().get("content").getFragments()[1].toString());
        Assert.assertEquals("这是历史上<font color='red'>中国</font>选手首次参加并完成奥运会高山滑雪男子滑降的比赛", searchResponse.getHits().getHits()[0].getHighlightFields().get("content").getFragments()[2].toString());
        Assert.assertEquals("春节期间，全国<font color='red'>中国</font>邮政快递业运行情况总体安全稳定", searchResponse.getHits().getHits()[1].getHighlightFields().get("content").getFragments()[0].toString());
        Assert.assertEquals("他们不畏<font color='red'>中国</font>强手敢打敢拼", searchResponse.getHits().getHits()[2].getHighlightFields().get("content").getFragments()[0].toString());
    }

    @Test
    public void test_sort_query() {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch("demo_index")
                .setQuery(queryBuilder)
                .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .get();

        List<String> idList = Arrays.asList("5", "4", "3", "2", "1");
        List<String> idList1 = Arrays.asList(searchResponse.getHits().getHits()).stream().map(hit -> hit.getId()).collect(Collectors.toList());
        Assert.assertArrayEquals(idList.toArray(), idList1.toArray());

        queryBuilder = QueryBuilders.matchAllQuery();
        searchResponse = client.prepareSearch("demo_index")
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
        SearchResponse searchResponse = client.prepareSearch("demo_index").addAggregation(cardinalityAggregationBuilder).get();
        Cardinality cardinality = searchResponse.getAggregations().get("cardin");
        Assert.assertEquals(3, cardinality.getValue());

        // TODO range聚合范围统计

        // 统计聚合查询，查询field的最大值、最小值、平均值等
        ExtendedStatsAggregationBuilder extendedStatsAggregationBuilder = AggregationBuilders.extendedStats("extStats").field("views");
        searchResponse = client.prepareSearch("demo_index").addAggregation(extendedStatsAggregationBuilder).get();
        ExtendedStats extendedStats = searchResponse.getAggregations().get("extStats");
        Assert.assertEquals(5, extendedStats.getCount());
        Assert.assertEquals(5.0, extendedStats.getMin(), 0);
        Assert.assertEquals(100.0, extendedStats.getMax(), 0);

        // 根据province分组统计views
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("groupProvince").field("province");
        termsAggregationBuilder.subAggregation(AggregationBuilders.sum("groupViewsSum").field("views"));
        termsAggregationBuilder.subAggregation(AggregationBuilders.max("groupViewsMax").field("views"));
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("title", "elasticsearch"));
        searchResponse = client.prepareSearch("demo_index")
                .setQuery(queryBuilder)
                .addAggregation(termsAggregationBuilder).get();

//        searchResponse.getAggregations().forEach(System.out::println);

        // 判断是否有三个省份
        Assert.assertEquals(3, ((StringTerms)searchResponse.getAggregations().get("groupProvince")).getBuckets().size());
        Set<String> provinceSet = idToTitleAndContentMapper.values().stream().map(objects -> (String)objects[2]).collect(Collectors.toSet());
        ((StringTerms)searchResponse.getAggregations().get("groupProvince")).getBuckets().forEach(bucket -> Assert.assertTrue(provinceSet.contains(bucket.getKeyAsString())));

        // 广东doc=2
        Assert.assertEquals(2, ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("广东").getDocCount());
        // 上海doc=1
        Assert.assertEquals(1, ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("上海").getDocCount());
        // 北京doc=1
        Assert.assertEquals(1, ((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("北京").getDocCount());

        // 广东=25
        Assert.assertEquals(25.0, ((InternalSum)((StringTerms)searchResponse.getAggregations().get("groupProvince")).getBucketByKey("广东").getAggregations().get("groupViewsSum")).getValue(), 0);
        // 上海=50
        Assert.assertEquals(50.0, ((InternalSum)((StringTerms)searchResponse.getAggregations().get("groupProvince")).getBucketByKey("上海").getAggregations().get("groupViewsSum")).getValue(), 0);
        // 北京=100
        Assert.assertEquals(100.0, ((InternalSum)((StringTerms)searchResponse.getAggregations().get("groupProvince")).getBucketByKey("北京").getAggregations().get("groupViewsSum")).getValue(), 0);

        Assert.assertEquals(20.0, ((InternalMax)((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("广东").getAggregations().get("groupViewsMax")).getValue(), 0);
        Assert.assertEquals(50.0, ((InternalMax)((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("上海").getAggregations().get("groupViewsMax")).getValue(), 0);
        Assert.assertEquals(100.0, ((InternalMax)((StringTerms) searchResponse.getAggregations().get("groupProvince")).getBucketByKey("北京").getAggregations().get("groupViewsMax")).getValue(), 0);
    }
}
