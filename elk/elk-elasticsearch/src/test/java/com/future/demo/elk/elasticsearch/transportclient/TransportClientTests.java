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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransportClientTests {
    TransportClient client;

    @Before
    public void before() throws UnknownHostException {
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
    }

    @After
    public void after() throws IOException {
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/transport-client.html
        if(client != null) {
            client.close();
        }
    }

    @Test
    public void test() throws IOException, InterruptedException {
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
                            .field("store", true)
                        .endObject()
                        .startObject("title")
                            .field("type", "text")
                            .field("store", true)
                            .field("index", true)
                            .field("analyzer", "ik_max_word")
                        .endObject()
                        .startObject("content")
                            .field("type", "text")
                            .field("store", true)
                            .field("index", true)
                            .field("analyzer", "ik_max_word")
                        .endObject()
                    .endObject()
                .endObject();
        AcknowledgedResponse acknowledgedResponse = client.admin().indices().preparePutMapping("demo_index")
                .setType("_doc")
                .setSource(builder).get();
        Assert.assertTrue(acknowledgedResponse.isAcknowledged());

        // 创建文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-docs-index.html
        Map<Long, String[]> idToTitleAndContentMapper = new HashMap<>();
        idToTitleAndContentMapper.put(1L, new String[] {"Elasticsearch 版本：6.8.8", "文章参考如下链接，但有些内容可能过时，以实践结果为主："});
        idToTitleAndContentMapper.put(2L, new String[] {"35个项目首次参赛的背后——北京冬奥会推动中国冬季运动跨越式发展", "7日，当中国高山滑雪选手徐铭甫在北京冬奥会男子滑降比赛中冲过终点时，中国高山滑雪运动也在这一刻取得了历史性的突破——这是历史上中国选手首次参加并完成奥运会高山滑雪男子滑降的比赛。"});
        idToTitleAndContentMapper.put(3L, new String[] {"北京冬奥会 | 燃！这个冬天，看中国的00后在干什么", "他们不畏强手敢打敢拼"});
        idToTitleAndContentMapper.put(4L, new String[] {"日本运动员发文点赞冬奥志愿者 日网友热议“好暖”", "太田雄贵在推文中说，“在前往开幕式会场的大巴上，冬奥志愿者们用英文提示大家当天的注意事项，可能因为反复练习，使用的文稿纸张有不少折痕。当志愿者略带紧张地宣读完毕后，车上的乘客纷纷用热烈的掌声赞美他们的努力，现场呈现出一幅很棒的画面”。"});
        idToTitleAndContentMapper.put(5L, new String[] {"春节假期全国揽收投递快递包裹7.49亿件", "春节期间，全国邮政快递业运行情况总体安全稳定，邮政快递服务业务量增幅较大。其中，揽收快递包裹4.2亿件，与2019年、2020年、2021年农历同期相比分别增长545%、338%、12.04%；投递快递包裹3.29亿件，与2019年、2020年、2021年农历同期相比分别增长645%、280%、21.6%。"});
        Set<Long> keySet = idToTitleAndContentMapper.keySet();
        for(Long idTemporary : keySet) {
            builder = XContentFactory.jsonBuilder()
                .startObject()
                    .field("id", idTemporary)
                    .field("title", idToTitleAndContentMapper.get(idTemporary)[0])
                    .field("content", idToTitleAndContentMapper.get(idTemporary)[1])
                .endObject();
            IndexResponse indexResponse = client.prepareIndex("demo_index", "_doc", String.valueOf(idTemporary))
                    .setSource(builder).get();
            Assert.assertEquals(DocWriteResponse.Result.CREATED, indexResponse.getResult());
            Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
            Assert.assertEquals(String.valueOf(idTemporary), indexResponse.getId());
            Assert.assertEquals("demo_index", indexResponse.getIndex());
        }

        // 根据id查询文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-docs-get.html
        Long id = 1L;
        GetResponse getResponse = client.prepareGet("demo_index", "_doc", "1")
                // NOTE： 显示指定返回的field，否则下面getResponse.getField会返回null，指定storedFields后不返回_source，默认情况下只返回_source
                .setStoredFields("id", "title", "content").get();
        Assert.assertEquals(String.valueOf(id), getResponse.getId());
        Assert.assertEquals("demo_index", getResponse.getIndex());
        Assert.assertTrue(getResponse.isExists());
        Assert.assertEquals(id, getResponse.getField("id").getValue());
        Assert.assertEquals(idToTitleAndContentMapper.get(id)[0], getResponse.getField("title").getValue());
        Assert.assertEquals(idToTitleAndContentMapper.get(id)[1], getResponse.getField("content").getValue());

        getResponse = client.prepareGet("demo_index", "_doc", "1").get();
        Assert.assertEquals(String.valueOf(id), getResponse.getId());
        Assert.assertEquals("demo_index", getResponse.getIndex());
        Assert.assertTrue(getResponse.isExists());
        Assert.assertEquals(id, new Long((Integer)getResponse.getSource().get("id")));
        Assert.assertEquals(idToTitleAndContentMapper.get(id)[0], getResponse.getSource().get("title"));
        Assert.assertEquals(idToTitleAndContentMapper.get(id)[1], getResponse.getSource().get("content"));

        Thread.sleep(1000);
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(String.valueOf(id));
        SearchResponse searchResponse = client.prepareSearch("demo_index").setQuery(queryBuilder).get();
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits());

        // 根据term关键词查询文档
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/java-search.html
        searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.termQuery("title", "什么")).get();
        Assert.assertEquals(RestStatus.OK, searchResponse.status());
        Assert.assertEquals(1, searchResponse.getHits().getTotalHits());

        // 根据queryString查询文档
        searchResponse = client.prepareSearch("demo_index").setQuery(QueryBuilders.queryStringQuery("什么运动").defaultField("title")).get();
        Assert.assertEquals(RestStatus.OK, searchResponse.status());
        Assert.assertEquals(3, searchResponse.getHits().getTotalHits());
    }
}
