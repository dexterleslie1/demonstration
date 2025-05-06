package com.future.demo;

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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.InternalMax;
import org.elasticsearch.search.aggregations.metrics.InternalValueCount;
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
import java.util.stream.Collectors;

// TODO 数据压力测试
public class DemoTests {
    final static String IndexChatFriendRelation = "practise_demo_chat_friend_relation";
    final static String IndexChatGroupNGroupAssist = "practise_demo_chat_group_n_groupassist";
    final static String IndexChatGroupNGroupAssistMember = "practise_demo_chat_group_n_groupassist_member";
    final static String IndexChatMessage = "practise_demo_chat_message";

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
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(IndexChatFriendRelation).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());

            acknowledgedResponse = client.admin().indices().prepareDelete(IndexChatGroupNGroupAssist).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());

            acknowledgedResponse = client.admin().indices().prepareDelete(IndexChatGroupNGroupAssistMember).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());

            acknowledgedResponse = client.admin().indices().prepareDelete(IndexChatMessage).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        } catch (Exception ex) {
            // 忽略索引不存在情况
        }

        // 准备测试好友测试数据
        List<FriendRelation> friendRelationList = Arrays.asList(
                // https://www.cnblogs.com/whl-jx911/p/14611942.html
                new FriendRelation(1, 1, 2, "friend21419059人寿保险黄经理", "YydTesting001"),
                new FriendRelation(2, 1, 3, "平安银行陈经理", "Python"),
                new FriendRelation(3, 1, 4, "同事啊强在Yyd公司", "Go"),
                new FriendRelation(4, 1, 5, "aDexterleslie", "Ruby"),

                new FriendRelation(5, 6, 2, "人寿保险黄经理", "YydTesting001"),
                new FriendRelation(6, 6, 3, "平安银行陈经理", "Python"),
                new FriendRelation(7, 6, 4, "同事啊强在Yyd公司", "Go"),
                new FriendRelation(8, 6, 5, "aDexterleslie", "Ruby")
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
                        .startObject("userId")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("friendId")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("remark")
                            .field("type", "text")
                            .field("store", false)
                            .field("analyzer", "ik_smart_pinyin")
                        .endObject()
                        .startObject("uniqueIdentifier")
                            .field("type", "keyword")
                            .field("store", false)
                            .field("normalizer", "keyword_lowercase")
                        .endObject()
                    .endObject()
                .endObject();
        CreateIndexResponse createIndexResponse = client.admin().indices()
                .prepareCreate(IndexChatFriendRelation)
                .addMapping("_doc", xContentBuilder)
                .setSettings(xContentBuilderSettings)
                .get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        friendRelationList.forEach(friendRelation -> {
            try {
                XContentBuilder xContentBuilderTemporary = XContentFactory.jsonBuilder()
                        .startObject()
                            .field("id", friendRelation.getId())
                            .field("userId", friendRelation.getUserId())
                            .field("friendId", friendRelation.getFriendId())
                            .field("remark", friendRelation.getRemark())
                            .field("uniqueIdentifier", friendRelation.getUniqueIdentifier())
                        .endObject();
                IndexResponse indexResponse = client.prepareIndex(IndexChatFriendRelation, "_doc", String.valueOf(friendRelation.getId()))
                        .setSource(xContentBuilderTemporary)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                        .get();
                Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        });

        // 准备群组和群发助手的测试数据
        List<ChatGroupNGroupAssist> chatGroupNGroupAssistList = Arrays.asList(
//                new ChatGroupNGroupAssist(1, "大众点评", 0, Arrays.asList(
//                        new ChatGroupNGroupAssistMember(1, 1, 1, "的答案是发展", "YydTesting001"),
//                        new ChatGroupNGroupAssistMember(2, 1, 2, "世界各国要", "Java002")
//                )),
//                new ChatGroupNGroupAssist(2, "广州地铁乘车码", 0, Arrays.asList(
//                        new ChatGroupNGroupAssistMember(4, 2, 3, "世界经济", "WhenDoYou"),
//                        new ChatGroupNGroupAssistMember(5, 2, 4, "复苏和发展", "Howoldareyou")
//                )),
//                new ChatGroupNGroupAssist(3, "广州市黄埔区图书馆", 1, Arrays.asList(
//                        new ChatGroupNGroupAssistMember(7, 3, 5, "需要早日走出疫情", "Howdoyoudo"),
//                        new ChatGroupNGroupAssistMember(8, 3, 6, "冲击阴影", "Idunno")
//                )),
//                new ChatGroupNGroupAssist(4, "恒心客家宴地铁", 1, Arrays.asList(
//                        new ChatGroupNGroupAssistMember(10, 4, 7, "更需要尽快修炼好", "Herecomes"),
//                        new ChatGroupNGroupAssistMember(11, 4, 8, "实现发展动能转换", "Thereisnothing")
//                ))

                new ChatGroupNGroupAssist(1, "大众点评", 0, Arrays.asList(
                        new ChatGroupNGroupAssistMember(1, 3),
                        new ChatGroupNGroupAssistMember(1, 1),
                        new ChatGroupNGroupAssistMember(1, 2),
                        new ChatGroupNGroupAssistMember(1, 8)
                )),
                new ChatGroupNGroupAssist(2, "广州地铁乘车码", 0, Arrays.asList(
                        new ChatGroupNGroupAssistMember(2, 3),
                        new ChatGroupNGroupAssistMember(2, 4),
                        new ChatGroupNGroupAssistMember(2, 8)
                )),
                new ChatGroupNGroupAssist(3, "广州市黄埔区图书馆", 1, Arrays.asList(
                        new ChatGroupNGroupAssistMember(3, 5),
                        new ChatGroupNGroupAssistMember(3, 6)
                )),
                new ChatGroupNGroupAssist(4, "恒心客家宴地铁", 1, Arrays.asList(
                        new ChatGroupNGroupAssistMember(4, 7),
                        new ChatGroupNGroupAssistMember(4, 8)
                ))
        );

        // 用户昵称和uniqueIdentifier数据
        Map<Long, String[]> userIdToUserInfo = new HashMap<>();
        userIdToUserInfo.put(1L, new String[] {"的答案是发展", "YydTesting001"});
        userIdToUserInfo.put(2L, new String[] {"世界各国要", "Java002"});
        userIdToUserInfo.put(3L, new String[] {"世界经济", "WhenDoYou"});
        userIdToUserInfo.put(4L, new String[] {"复苏和发展", "Howoldareyou"});
        userIdToUserInfo.put(5L, new String[] {"需要早日走出疫情", "Howdoyoudo"});
        userIdToUserInfo.put(6L, new String[] {"冲击阴影", "Idunno"});
        userIdToUserInfo.put(7L, new String[] {"更需要尽快修炼好", "Herecomes"});
        userIdToUserInfo.put(8L, new String[] {"实现发展动能转换", "Thereisnothing"});

        // TODO
        // 用户好友备注

        xContentBuilderSettings = XContentFactory.jsonBuilder()
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

        xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("id")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("type")
                            .field("type", "integer")
                            .field("store", false)
                        .endObject()
                        .startObject("name")
                            .field("type", "text")
                            .field("store", false)
                            .field("analyzer", "ik_smart_pinyin")
                        .endObject()
                    .endObject()
                .endObject();

        createIndexResponse = client.admin().indices()
                .prepareCreate(IndexChatGroupNGroupAssist)
                .addMapping("_doc", xContentBuilder)
                .setSettings(xContentBuilderSettings)
                .get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        chatGroupNGroupAssistList.forEach(chatGroupNGroupAssist -> {
            try {
                XContentBuilder xContentBuilderTemporary = XContentFactory.jsonBuilder()
                        .startObject()
                            .field("id", chatGroupNGroupAssist.getId())
                            .field("name", chatGroupNGroupAssist.getName())
                            .field("type", chatGroupNGroupAssist.getType())
                        .endObject();
                IndexResponse indexResponse = client.prepareIndex(IndexChatGroupNGroupAssist, "_doc", String.valueOf(chatGroupNGroupAssist.getId()))
                        .setSource(xContentBuilderTemporary)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                        .get();
                Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        });

        xContentBuilderSettings = XContentFactory.jsonBuilder()
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

        xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
//                        .startObject("id")
//                            .field("type", "long")
//                            .field("store", false)
//                        .endObject()
                        .startObject("groupId")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("userId")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("memberId")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("remark")
                            .field("type", "text")
                            .field("store", false)
                            .field("analyzer", "ik_smart_pinyin")
                        .endObject()
                        .startObject("uniqueIdentifier")
                            .field("type", "keyword")
                            .field("store", false)
                            .field("normalizer", "keyword_lowercase")
                        .endObject()
                    .endObject()
                .endObject();

        createIndexResponse = client.admin().indices()
                .prepareCreate(IndexChatGroupNGroupAssistMember)
                .addMapping("_doc", xContentBuilder)
                .setSettings(xContentBuilderSettings)
                .get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        chatGroupNGroupAssistList.forEach(chatGroupNGroupAssist -> {
            chatGroupNGroupAssist.getMemberList().forEach(member -> {
                chatGroupNGroupAssist.getMemberList().forEach(memberC -> {
                    try {
                        XContentBuilder xContentBuilderTemporary = XContentFactory.jsonBuilder()
                                .startObject()
                                    .field("groupId", member.getGroupId())
                                    .field("userId", member.getMemberId())
                                    .field("memberId", memberC.getMemberId())
                                    .field("remark", userIdToUserInfo.get(memberC.getMemberId())[0])
                                    .field("uniqueIdentifier", userIdToUserInfo.get(memberC.getMemberId())[1])
                                .endObject();
                        IndexResponse indexResponse = client.prepareIndex(IndexChatGroupNGroupAssistMember, "_doc")
                                .setSource(xContentBuilderTemporary)
                                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                                .get();
                        Assert.assertEquals(RestStatus.CREATED, indexResponse.status());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Assert.fail(e.getMessage());
                    }
                });
            });
        });

        // 准备消息的测试数据
        List<ChatMessage> chatMessageList = Arrays.asList(
                new ChatMessage(1, 1,2, 0, "习近平总书记一语道破奥林匹克冰雪运动的魅力所在", true),
                new ChatMessage(1, 2,1, 0, "习近平总书记一语道破奥林匹克冰雪运动的魅力所在", false),
                new ChatMessage(2, 1,2, 0, "冬残奥会就像是一个冰雪弹射器", false),
                new ChatMessage(2, 2,1, 0, "冬残奥会就像是一个冰雪弹射器", false),
                new ChatMessage(3, 1,2, 0, "上世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(3, 2,1, 0, "上世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(4, 1,4,0, "中世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(4, 4,1,0, "中世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(5, 5,6, 0, "下世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(5, 6,5, 0, "下世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),

                new ChatMessage(6, 1,0, 1, "习近平总书记一语道破奥林匹克冰雪运动的魅力所在", true),
                new ChatMessage(6, 2,0, 1, "习近平总书记一语道破奥林匹克冰雪运动的魅力所在", false),
                new ChatMessage(6, 3,0,1, "习近平总书记一语道破奥林匹克冰雪运动的魅力所在", false),
                new ChatMessage(7, 1,0, 2, "上世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(7, 2,0,2, "上世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(7, 3,0, 2, "上世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(9, 1,0, 2, "中世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(9, 2,0,2, "中世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(9, 3,0, 2, "中世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(10, 1,0, 3, "下世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(10, 2,0,3, "下世纪推动我国冰雪运动和冰雪产业飞跃式发展", false),
                new ChatMessage(10, 3,0, 3, "下世纪推动我国冰雪运动和冰雪产业飞跃式发展", false)
        );

        xContentBuilderSettings = XContentFactory.jsonBuilder()
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

        xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("id")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("userId")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
//                        .startObject("userIdFrom")
//                            .field("type", "long")
//                            .field("store", false)
//                        .endObject()
//                        .startObject("userIdTo")
//                            .field("type", "long")
//                            .field("store", false)
//                        .endObject()
                        .startObject("friendId")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("groupId")
                            .field("type", "long")
                            .field("store", false)
                        .endObject()
                        .startObject("content")
                            .field("type", "text")
                            .field("store", false)
                            .field("analyzer", "ik_max_word")
                        .endObject()
                        .startObject("deleted")
                            .field("type", "boolean")
                            .field("store", false)
                        .endObject()
                    .endObject()
                .endObject();

        createIndexResponse = client.admin().indices()
                .prepareCreate(IndexChatMessage)
                .addMapping("_doc", xContentBuilder)
                .setSettings(xContentBuilderSettings)
                .get();
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        Assert.assertTrue(createIndexResponse.isShardsAcknowledged());

        chatMessageList.forEach(chatMessage -> {
            try {
                XContentBuilder xContentBuilderTemporary = XContentFactory.jsonBuilder()
                        .startObject()
                            .field("id", chatMessage.getId())
                            .field("userId", chatMessage.getUserId())
//                            .field("userIdFrom", chatMessage.getUserIdFrom())
//                            .field("userIdTo", chatMessage.getUserIdTo())
                            .field("friendId", chatMessage.getFriendId())
                            .field("groupId", chatMessage.getGroupId())
                            .field("content", chatMessage.getContent())
                            .field("deleted", chatMessage.isDeleted())
                        .endObject();
                IndexResponse indexResponse = client.prepareIndex(IndexChatMessage, "_doc")
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
    public void test_search_friend() {
        // 需求： 根据好友备注、azp号搜索
        // 中文分词、英文分词、拼音分词、拼音首字母分词都使用prefix查询

        String keyword = "yY".toLowerCase();
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 1L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword))
                );

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        SearchResponse searchResponse = client.prepareSearch(IndexChatFriendRelation)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals("1", searchResponse.getHits().getAt(0).getId());
        Assert.assertEquals("<font color='red'>YydTesting001</font>", searchResponse.getHits().getHits()[0].getHighlightFields().get("uniqueIdentifier").getFragments()[0].string());
        Assert.assertEquals("3", searchResponse.getHits().getAt(1).getId());
        Assert.assertEquals("同事啊强在<font color='red'>Yyd</font>", searchResponse.getHits().getHits()[1].getHighlightFields().get("remark").getFragments()[0].string());

        keyword = "yYd".toLowerCase();
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 1L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword))
                );

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatFriendRelation)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals("1", searchResponse.getHits().getAt(0).getId());
        Assert.assertEquals("<font color='red'>YydTesting001</font>", searchResponse.getHits().getHits()[0].getHighlightFields().get("uniqueIdentifier").getFragments()[0].string());
        Assert.assertEquals("3", searchResponse.getHits().getAt(1).getId());
        Assert.assertEquals("同事啊强在<font color='red'>Yyd</font>", searchResponse.getHits().getHits()[1].getHighlightFields().get("remark").getFragments()[0].string());

        keyword = "公";
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 1L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword))
                );

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatFriendRelation)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals("3", searchResponse.getHits().getAt(0).getId());
        Assert.assertEquals("同事啊强在Yyd<font color='red'>公司</font>", searchResponse.getHits().getHits()[0].getHighlightFields().get("remark").getFragments()[0].string());

        keyword = "公司";
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 1L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword))
                );

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatFriendRelation)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals(1, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals("3", searchResponse.getHits().getAt(0).getId());
        Assert.assertEquals("同事啊强在Yyd<font color='red'>公司</font>", searchResponse.getHits().getHits()[0].getHighlightFields().get("remark").getFragments()[0].string());

        // 场景： 拼音查询
        keyword = "gOn".toLowerCase();
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 1L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword))
                );

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatFriendRelation)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

//        searchResponse.getHits().forEach(hit -> {
//            hit.getHighlightFields().forEach((key, field) -> {
//                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + field.getName() + " - " + fragment.string()));
//            });
//        });

        Assert.assertEquals("3", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("同事啊强在Yyd<font color='red'>公司</font>", searchResponse.getHits().getHits()[0].getHighlightFields().get("remark").getFragments()[0].string());

        keyword = "Gong".toLowerCase();
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 1L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword))
                );

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatFriendRelation)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals("3", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("同事啊强在Yyd<font color='red'>公司</font>", searchResponse.getHits().getHits()[0].getHighlightFields().get("remark").getFragments()[0].string());

        keyword = "GS".toLowerCase();
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 1L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword))
                );

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatFriendRelation)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals("3", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("同事啊强在Yyd<font color='red'>公司</font>", searchResponse.getHits().getHits()[0].getHighlightFields().get("remark").getFragments()[0].string());
    }

    @Test
    public void test_search_chatGroup_and_chatGroupAssist() {
        // 需求： 根据群组和群发助手名称、群组和群发助手包含成员的备注、azp号搜索
        // 中文分词、英文分词、拼音分词、拼音首字母分词都使用prefix查询

        // 场景： 根据群组或者群发助手名称查询
        String keyword = "地铁";
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("id", 2L))
                        .should(QueryBuilders.termQuery("id", 3L))
                )
                .must(QueryBuilders.prefixQuery("name", keyword));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name", 5).preTags("<font color='red'>").postTags("</font>");

        SearchResponse searchResponse = client.prepareSearch(IndexChatGroupNGroupAssist)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

//        searchResponse.getHits().forEach(hit -> {
//            hit.getHighlightFields().forEach((key, field) -> {
//                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + field.getName() + " - " + fragment.string()));
//            });
//        });

        Assert.assertEquals(1, searchResponse.getHits().getHits().length);
        Assert.assertEquals("2", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("广州<font color='red'>地铁</font>乘车码", searchResponse.getHits().getHits()[0].getHighlightFields().get("name").getFragments()[0].string());

        keyword = "d";
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("id", 2L))
                        .should(QueryBuilders.termQuery("id", 3L))
                )
                .must(QueryBuilders.prefixQuery("name", keyword));

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatGroupNGroupAssist)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals(1, searchResponse.getHits().getHits().length);
        Assert.assertEquals("2", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("广州<font color='red'>地铁</font>乘车码", searchResponse.getHits().getHits()[0].getHighlightFields().get("name").getFragments()[0].string());

        keyword = "di";
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("id", 2L))
                        .should(QueryBuilders.termQuery("id", 3L))
                )
                .must(QueryBuilders.prefixQuery("name", keyword));

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatGroupNGroupAssist)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals(1, searchResponse.getHits().getHits().length);
        Assert.assertEquals("2", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("广州<font color='red'>地铁</font>乘车码", searchResponse.getHits().getHits()[0].getHighlightFields().get("name").getFragments()[0].string());

        keyword = "dt";
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("id", 2L))
                        .should(QueryBuilders.termQuery("id", 3L))
                )
                .must(QueryBuilders.prefixQuery("name", keyword));

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatGroupNGroupAssist)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals(1, searchResponse.getHits().getHits().length);
        Assert.assertEquals("2", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("广州<font color='red'>地铁</font>乘车码", searchResponse.getHits().getHits()[0].getHighlightFields().get("name").getFragments()[0].string());

        keyword = "地";
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("id", 2L))
                        .should(QueryBuilders.termQuery("id", 3L))
                )
                .must(QueryBuilders.prefixQuery("name", keyword));

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatGroupNGroupAssist)
                .setTypes("_doc").setQuery(queryBuilder).highlighter(highlightBuilder).get();

        Assert.assertEquals(1, searchResponse.getHits().getHits().length);
        Assert.assertEquals("2", searchResponse.getHits().getHits()[0].getId());
        Assert.assertEquals("广州<font color='red'>地铁</font>乘车码", searchResponse.getHits().getHits()[0].getHighlightFields().get("name").getFragments()[0].string());

        // 场景： 根据群组或者群发助手包含成员
        keyword = "发展";
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 3L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword)));

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("groupByGroupId").field("groupId");
        termsAggregationBuilder.subAggregation(AggregationBuilders.max("groupMaxMemberId").field("memberId"));

        searchResponse = client.prepareSearch(IndexChatGroupNGroupAssistMember)
                .setTypes("_doc")
                .setQuery(queryBuilder)
                .addAggregation(termsAggregationBuilder)
                .get();

//        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));

//        searchResponse.getHits().forEach(hit -> {
//            hit.getHighlightFields().forEach((key, field) -> {
//                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getSourceAsMap().get("groupId") + " - " + field.getName() + " - " + fragment.string()));
//            });
//        });

        Assert.assertEquals(2, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBuckets().size());
        Assert.assertEquals(2, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("1").getDocCount());
        Assert.assertEquals(2, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("2").getDocCount());
        Assert.assertEquals(8, (long) ((InternalMax)((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("1").getAggregations().get("groupMaxMemberId")).getValue());
        Assert.assertEquals(8, (long) ((InternalMax)((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("2").getAggregations().get("groupMaxMemberId")).getValue());

        // 获取高亮
        Map<String, Long> groupIdStrToMaxMemberIdMapper = ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBuckets().stream().collect(Collectors.toMap(bucket -> bucket.getKeyAsString(), bucket -> (long)((InternalMax)bucket.getAggregations().get("groupMaxMemberId")).getValue()));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        groupIdStrToMaxMemberIdMapper.forEach((key, val) -> boolQueryBuilder.should(
                QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("userId", 3L))
                    .must(QueryBuilders.termQuery("groupId", Long.parseLong(key)))
                    .must(QueryBuilders.termQuery("memberId", val))
        ));
        queryBuilder = QueryBuilders.boolQuery()
                .filter(boolQueryBuilder)
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword)));

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatGroupNGroupAssistMember)
                .setTypes("_doc")
                .setQuery(queryBuilder)
                .highlighter(highlightBuilder)
                .addSort(SortBuilders.fieldSort("groupId").order(SortOrder.DESC))
                .get();

//        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
//
//        searchResponse.getHits().forEach(hit -> {
//            hit.getHighlightFields().forEach((key, field) -> {
//                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getSourceAsMap().get("groupId") + " - " + field.getName() + " - " + fragment.string()));
//            });
//        });

        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2L, Long.parseLong(searchResponse.getHits().getHits()[0].getSourceAsMap().get("groupId").toString()));
        Assert.assertEquals("实现<font color='red'>发展</font>动能转换", searchResponse.getHits().getHits()[0].getHighlightFields().get("remark").getFragments()[0].string());
        Assert.assertEquals(1L, Long.parseLong(searchResponse.getHits().getHits()[1].getSourceAsMap().get("groupId").toString()));
        Assert.assertEquals("实现<font color='red'>发展</font>动能转换", searchResponse.getHits().getHits()[1].getHighlightFields().get("remark").getFragments()[0].string());

        keyword = "fz";
        queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("userId", 3L))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword)));

        termsAggregationBuilder = AggregationBuilders.terms("groupByGroupId").field("groupId");
        termsAggregationBuilder.subAggregation(AggregationBuilders.max("groupMaxMemberId").field("memberId"));

        searchResponse = client.prepareSearch(IndexChatGroupNGroupAssistMember)
                .setTypes("_doc")
                .setQuery(queryBuilder)
                .addAggregation(termsAggregationBuilder)
                .get();

//        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));

        Assert.assertEquals(2, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBuckets().size());
        Assert.assertEquals(2, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("1").getDocCount());
        Assert.assertEquals(2, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("2").getDocCount());
        Assert.assertEquals(8, (long) ((InternalMax)((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("1").getAggregations().get("groupMaxMemberId")).getValue());
        Assert.assertEquals(8, (long) ((InternalMax)((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("2").getAggregations().get("groupMaxMemberId")).getValue());

        // 获取高亮
        groupIdStrToMaxMemberIdMapper = ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBuckets().stream().collect(Collectors.toMap(bucket -> bucket.getKeyAsString(), bucket -> (long)((InternalMax)bucket.getAggregations().get("groupMaxMemberId")).getValue()));
        BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
        groupIdStrToMaxMemberIdMapper.forEach((key, val) -> boolQueryBuilder1.should(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("userId", 3L))
                        .must(QueryBuilders.termQuery("groupId", Long.parseLong(key)))
                        .must(QueryBuilders.termQuery("memberId", val))
        ));
        queryBuilder = QueryBuilders.boolQuery()
                .filter(boolQueryBuilder1)
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.prefixQuery("remark", keyword))
                        .should(QueryBuilders.prefixQuery("uniqueIdentifier", keyword)));

        highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("remark", 5).preTags("<font color='red'>").postTags("</font>");
        highlightBuilder.field("uniqueIdentifier", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatGroupNGroupAssistMember)
                .setTypes("_doc")
                .setQuery(queryBuilder)
                .highlighter(highlightBuilder)
                .addSort(SortBuilders.fieldSort("groupId").order(SortOrder.DESC))
                .get();

//        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
//
//        searchResponse.getHits().forEach(hit -> {
//            hit.getHighlightFields().forEach((key, field) -> {
//                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getSourceAsMap().get("groupId") + " - " + field.getName() + " - " + fragment.string()));
//            });
//        });

        Assert.assertEquals(2, searchResponse.getHits().getTotalHits().value);
        Assert.assertEquals(2L, Long.parseLong(searchResponse.getHits().getHits()[0].getSourceAsMap().get("groupId").toString()));
        Assert.assertEquals("实现<font color='red'>发展</font>动能转换", searchResponse.getHits().getHits()[0].getHighlightFields().get("remark").getFragments()[0].string());
        Assert.assertEquals(1L, Long.parseLong(searchResponse.getHits().getHits()[1].getSourceAsMap().get("groupId").toString()));
        Assert.assertEquals("实现<font color='red'>发展</font>动能转换", searchResponse.getHits().getHits()[1].getHighlightFields().get("remark").getFragments()[0].string());
    }

    @Test
    public void test_search_message() {
        // 需求： 根据消息内容或者文件名称搜索
        // 中文分词、英文分词使用prefix查询

        // 场景： 聊天消息搜索汇总
        long currentUserId = 1L;
        Map<String, Long> groupIdAndFriendIdToCountMapper = new HashMap<>();
        Map<String, Long> groupIdAndFriendIdToMaxMessageIdMapper = new HashMap<>();
        Map<Long, String> idLongToHighlightMapper = new HashMap<>();
        idLongToHighlightMapper.put(3L, "上世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
        idLongToHighlightMapper.put(4L, "中世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
        idLongToHighlightMapper.put(9L, "中世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
        idLongToHighlightMapper.put(10L, "下世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");

        String keyword = "冰";

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("byGroupId").field("groupId")
                .subAggregation(AggregationBuilders.terms("byFriendId").field("friendId")
                                    .subAggregation(AggregationBuilders.count("count").field("id"))
                                    .subAggregation(AggregationBuilders.max("maxMessageId").field("id")));

        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("userId", currentUserId))
                        .must(QueryBuilders.termQuery("deleted", false))
                )
                .must(QueryBuilders.prefixQuery("content", keyword));

        SearchResponse searchResponse = client.prepareSearch(IndexChatMessage)
                .setTypes("_doc")
                .setQuery(queryBuilder)
                .addAggregation(termsAggregationBuilder)
                .get();

//        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));

//        searchResponse.getHits().forEach(hit -> {
//            hit.getHighlightFields().forEach((key, field) -> {
//                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + field.getName() + " - " + fragment.string()));
//            });
//        });

//        ((LongTerms)searchResponse.getAggregations().get("byGroupId")).getBuckets().forEach(bucketByGroupId -> {
//            ((LongTerms)bucketByGroupId.getAggregations().get("byFriendId")).getBuckets().forEach(bucketByFriendId -> {
//                System.out.println("groupId=" + bucketByGroupId.getKeyAsString() + ",friendId=" + bucketByFriendId.getKeyAsString() + ",count=" + ((InternalValueCount)bucketByFriendId.getAggregations().get("count")).getValue() + ",maxMessageId=" + (long)((InternalMax)bucketByFriendId.getAggregations().get("maxMessageId")).getValue());
//            });
//        });

        ((LongTerms)searchResponse.getAggregations().get("byGroupId")).getBuckets().forEach(bucketByGroupId -> {
            ((LongTerms)bucketByGroupId.getAggregations().get("byFriendId")).getBuckets().forEach(bucketByFriendId -> {
                String key = bucketByGroupId.getKeyAsString() + "#" + bucketByFriendId.getKeyAsString();
                groupIdAndFriendIdToCountMapper.put(key, ((InternalValueCount)bucketByFriendId.getAggregations().get("count")).getValue());
                groupIdAndFriendIdToMaxMessageIdMapper.put(key, (long)((InternalMax)bucketByFriendId.getAggregations().get("maxMessageId")).getValue());
            });
        });

//        groupId=0,friendId=2,count=2,maxMessageId=3
//        groupId=0,friendId=4,count=1,maxMessageId=4
//        groupId=2,friendId=0,count=2,maxMessageId=9
//        groupId=3,friendId=0,count=1,maxMessageId=10
        Assert.assertEquals(2L, ((InternalValueCount)((LongTerms)((LongTerms)searchResponse
                .getAggregations().get("byGroupId")).getBucketByKey("0")
                .getAggregations().get("byFriendId")).getBucketByKey("2").getAggregations().get("count")).getValue());
        Assert.assertEquals(1L, ((InternalValueCount)((LongTerms)((LongTerms)searchResponse
                .getAggregations().get("byGroupId")).getBucketByKey("0")
                .getAggregations().get("byFriendId")).getBucketByKey("4").getAggregations().get("count")).getValue());
        Assert.assertEquals(2L, ((InternalValueCount)((LongTerms)((LongTerms)searchResponse
                .getAggregations().get("byGroupId")).getBucketByKey("2")
                .getAggregations().get("byFriendId")).getBucketByKey("0").getAggregations().get("count")).getValue());
        Assert.assertEquals(1L, ((InternalValueCount)((LongTerms)((LongTerms)searchResponse
                .getAggregations().get("byGroupId")).getBucketByKey("3")
                .getAggregations().get("byFriendId")).getBucketByKey("0").getAggregations().get("count")).getValue());

        Assert.assertEquals(3L, (long)((InternalMax)((LongTerms)((LongTerms)searchResponse
                .getAggregations().get("byGroupId")).getBucketByKey("0")
                .getAggregations().get("byFriendId")).getBucketByKey("2").getAggregations().get("maxMessageId")).getValue());
        Assert.assertEquals(4L, (long)((InternalMax)((LongTerms)((LongTerms)searchResponse
                .getAggregations().get("byGroupId")).getBucketByKey("0")
                .getAggregations().get("byFriendId")).getBucketByKey("4").getAggregations().get("maxMessageId")).getValue());
        Assert.assertEquals(9L, (long)((InternalMax)((LongTerms)((LongTerms)searchResponse
                .getAggregations().get("byGroupId")).getBucketByKey("2")
                .getAggregations().get("byFriendId")).getBucketByKey("0").getAggregations().get("maxMessageId")).getValue());
        Assert.assertEquals(10L, (long)((InternalMax)((LongTerms)((LongTerms)searchResponse
                .getAggregations().get("byGroupId")).getBucketByKey("3")
                .getAggregations().get("byFriendId")).getBucketByKey("0").getAggregations().get("maxMessageId")).getValue());

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        groupIdAndFriendIdToMaxMessageIdMapper.values().forEach(value -> boolQueryBuilder.should(QueryBuilders.termQuery("id", value)));
        queryBuilder = QueryBuilders.boolQuery()
                .filter(boolQueryBuilder)
                .must(QueryBuilders.prefixQuery("content", keyword));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("content", 5).preTags("<font color='red'>").postTags("</font>");

        searchResponse = client.prepareSearch(IndexChatMessage)
                .setTypes("_doc")
                .setQuery(queryBuilder)
                .highlighter(highlightBuilder)
                .get();

//        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));

//        searchResponse.getHits().forEach(hit -> {
//            hit.getHighlightFields().forEach((key, field) -> {
//                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + field.getName() + " - " + fragment.string()));
//            });
//        });

        searchResponse.getHits().forEach(hit -> Assert.assertEquals(idLongToHighlightMapper.get(Long.parseLong(hit.getSourceAsMap().get("id").toString())), hit.getHighlightFields().get("content").getFragments()[0].string()));

        // 场景： 聊天消息搜索明细
        Map<String, String> idToHighlightMapper = new HashMap<>();
        idToHighlightMapper.put("2", "冬残奥会就像是一个<font color='red'>冰雪</font>弹射器");
        idToHighlightMapper.put("3", "上世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
        idToHighlightMapper.put("4", "中世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
        idToHighlightMapper.put("7", "上世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
        idToHighlightMapper.put("9", "中世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
        idToHighlightMapper.put("10", "下世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");

//        groupId=0,friendId=2,count=2,maxMessageId=3
//        groupId=0,friendId=4,count=1,maxMessageId=4
//        groupId=2,friendId=0,count=2,maxMessageId=9
//        groupId=3,friendId=0,count=1,maxMessageId=10
        Map<String, List<String>> groupIdAndFriendIdToMessageIdSortMapper = new HashMap<>();
        groupIdAndFriendIdToMessageIdSortMapper.put("0#2", Arrays.asList("3", "2"));
        groupIdAndFriendIdToMessageIdSortMapper.put("0#4", Collections.singletonList("4"));
        groupIdAndFriendIdToMessageIdSortMapper.put("2#0", Arrays.asList("9", "7"));
        groupIdAndFriendIdToMessageIdSortMapper.put("3#0", Collections.singletonList("10"));

        groupIdAndFriendIdToCountMapper.forEach((key, value) -> {
            long totalCount = groupIdAndFriendIdToCountMapper.get(key);
            int index = key.indexOf("#");
            long groupId = Long.parseLong(key.substring(0, index));
            long friendId = Long.parseLong(key.substring(index+1));
            int size = 1;
            long totalPage = totalCount/size;
            long totalCounter = 0;
            List<String> idSortList = new ArrayList<>();
            for(int currentPage=1; currentPage<=totalPage; currentPage++) {
                QueryBuilder queryBuilderTemporary = QueryBuilders.boolQuery()
                        .filter(QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery("userId", currentUserId))
                                .must(QueryBuilders.termQuery("friendId", friendId))
                                .must(QueryBuilders.termQuery("deleted", false))
                                .must(QueryBuilders.termQuery("groupId", groupId))
                        )
                        .must(QueryBuilders.prefixQuery("content", keyword));

                HighlightBuilder highlightBuilder1 = new HighlightBuilder();
                highlightBuilder1.field("content", 5).preTags("<font color='red'>").postTags("</font>");

                int start = (currentPage - 1) * size;
                SearchResponse searchResponseTemporary = client.prepareSearch(IndexChatMessage)
                        .setTypes("_doc")
                        .setQuery(queryBuilderTemporary)
                        .highlighter(highlightBuilder1)
                        .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                        .setFrom(start)
                        .setSize(size)
                        .get();

//                searchResponseTemporary.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
//
//                searchResponseTemporary.getHits().forEach(hit -> {
//                    hit.getHighlightFields().values().forEach(field -> {
//                        Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + field.getName() + " - " + fragment.string()));
//                    });
//                });

                Assert.assertEquals(totalCount, searchResponseTemporary.getHits().getTotalHits().value);

                totalCounter += searchResponseTemporary.getHits().getHits().length;

                searchResponseTemporary.getHits().forEach(hit -> {
                    String id = hit.getSourceAsMap().get("id").toString();
                    idSortList.add(id);
                    String highlightText = idToHighlightMapper.get(id);
                    Assert.assertEquals(highlightText, hit.getHighlightFields().get("content").getFragments()[0].toString());
                });
            }
            Assert.assertEquals(totalCount, totalCounter);
            Assert.assertArrayEquals(groupIdAndFriendIdToMessageIdSortMapper.get(key).toArray(), idSortList.toArray());
        });

//        // 场景： 群聊消息和群发助手搜索汇总
//        Map<String, Long> groupIdToCountMapper = new HashMap<>();
//        Map<Long, Long> groupIdToMessageIdNewestMapper = new HashMap<>();
//        Map<Long, String> idLongToHighlightMapper1 = new HashMap<>();
//        idLongToHighlightMapper1.put(9L, "中世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
//        idLongToHighlightMapper1.put(10L, "下世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
//
//        termsAggregationBuilder = AggregationBuilders.terms("groupByGroupId").field("groupId");
//        termsAggregationBuilder.subAggregation(AggregationBuilders.count("groupCount").field("id"));
//        termsAggregationBuilder.subAggregation(AggregationBuilders.max("groupIdMax").field("id"));
//
//        queryBuilder = QueryBuilders.boolQuery()
//                .filter(QueryBuilders.boolQuery()
//                        .must(QueryBuilders.termQuery("userId", currentUserId))
//                        .mustNot(QueryBuilders.termQuery("groupId", 0L))
//                        .must(QueryBuilders.termQuery("deleted", false))
//                )
//                .must(QueryBuilders.prefixQuery("content", keyword));
//
//        searchResponse = client.prepareSearch(IndexChatMessage)
//                .setTypes("_doc")
//                .setQuery(queryBuilder)
//                .addAggregation(termsAggregationBuilder)
//                .get();
//
////        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
//
////        searchResponse.getHits().forEach(hit -> {
////            hit.getHighlightFields().forEach((key, field) -> {
////                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + field.getName() + " - " + fragment.string()));
////            });
////        });
//
////        searchResponse.getAggregations().forEach(aggregation -> System.out.println(aggregation));
//
//        ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBuckets().forEach(bucket -> groupIdToCountMapper.put(bucket.getKeyAsString(), bucket.getDocCount()));
//        ((LongTerms) searchResponse.getAggregations().get("groupByGroupId")).getBuckets().forEach(bucket -> groupIdToMessageIdNewestMapper.put(Long.parseLong(bucket.getKeyAsString()), (long) ((InternalMax)bucket.getAggregations().get("groupIdMax")).getValue()));
//
//        Assert.assertEquals(2, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBuckets().size());
//        Assert.assertEquals(2, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("2").getDocCount());
//        Assert.assertEquals(1, ((LongTerms)searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("3").getDocCount());
//        Assert.assertEquals(9, (long) ((InternalMax)((LongTerms) searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("2").getAggregations().get("groupIdMax")).getValue());
//        Assert.assertEquals(10, (long) ((InternalMax)((LongTerms) searchResponse.getAggregations().get("groupByGroupId")).getBucketByKey("3").getAggregations().get("groupIdMax")).getValue());
//
//        BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
//        groupIdToMessageIdNewestMapper.values().forEach(value -> boolQueryBuilder1.should(QueryBuilders.termQuery("id", value)));
//        queryBuilder = QueryBuilders.boolQuery()
//                .filter(boolQueryBuilder1)
//                .must(QueryBuilders.prefixQuery("content", keyword));
//
//        highlightBuilder = new HighlightBuilder();
//        highlightBuilder.field("content", 5).preTags("<font color='red'>").postTags("</font>");
//
//        searchResponse = client.prepareSearch(IndexChatMessage)
//                .setTypes("_doc")
//                .setQuery(queryBuilder)
//                .highlighter(highlightBuilder)
//                .get();
//
////        searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
//
////        searchResponse.getHits().forEach(hit -> {
////            hit.getHighlightFields().forEach((key, field) -> {
////                Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + field.getName() + " - " + fragment.string()));
////            });
////        });
//
//        searchResponse.getHits().forEach(hit -> Assert.assertEquals(idLongToHighlightMapper1.get(Long.parseLong(hit.getSourceAsMap().get("id").toString())), hit.getHighlightFields().get("content").getFragments()[0].string()));
//
//        // 群聊消息和群发助手搜索明细
//        Map<String, String> idToHighlightMapper1 = new HashMap<>();
//        idToHighlightMapper1.put("7", "上世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
//        idToHighlightMapper1.put("9", "中世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
//        idToHighlightMapper1.put("10", "下世纪推动我国<font color='red'>冰雪</font>运动和<font color='red'>冰雪</font>产业飞跃式发展");
//
//        Map<String, List<String>> groupIdToMessageIdSortMapper = new HashMap<>();
//        groupIdToMessageIdSortMapper.put("2", Arrays.asList("9", "7"));
//        groupIdToMessageIdSortMapper.put("3", Collections.singletonList("10"));
//
//        groupIdToCountMapper.forEach((key, value) -> {
//            long totalCount = groupIdToCountMapper.get(key);
//            int size = 1;
//            long totalPage = totalCount/size;
//            long totalCounter = 0;
//            List<String> idSortList = new ArrayList<>();
//            for(int currentPage=1; currentPage<=totalPage; currentPage++) {
//                QueryBuilder queryBuilderTemporary = QueryBuilders.boolQuery()
//                        .filter(QueryBuilders.boolQuery()
//                                    .must(QueryBuilders.termQuery("userId", currentUserId))
//                                    .must(QueryBuilders.termQuery("groupId", Long.parseLong(key)))
//                        )
//                        .must(QueryBuilders.prefixQuery("content", keyword));
//
//                HighlightBuilder highlightBuilder1 = new HighlightBuilder();
//                highlightBuilder1.field("content", 5).preTags("<font color='red'>").postTags("</font>");
//
//                int start = (currentPage - 1) * size;
//                SearchResponse searchResponseTemporary = client.prepareSearch(IndexChatMessage)
//                        .setTypes("_doc")
//                        .setQuery(queryBuilderTemporary)
//                        .highlighter(highlightBuilder1)
//                        .addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
//                        .setFrom(start)
//                        .setSize(size)
//                        .get();
//
////                searchResponseTemporary.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
////
////                searchResponseTemporary.getHits().forEach(hit -> {
////                    hit.getHighlightFields().values().forEach(field -> {
////                        Arrays.asList(field.getFragments()).forEach(fragment -> System.out.println(hit.getId() + " - " + field.getName() + " - " + fragment.string()));
////                    });
////                });
//
//                Assert.assertEquals((long) groupIdToCountMapper.get(key), searchResponseTemporary.getHits().getTotalHits());
//
//                totalCounter += searchResponseTemporary.getHits().getHits().length;
//
//                searchResponseTemporary.getHits().forEach(hit -> {
//                    String id = hit.getSourceAsMap().get("id").toString();
//                    idSortList.add(id);
//                    String highlightText = idToHighlightMapper1.get(id);
//                    Assert.assertEquals(highlightText, hit.getHighlightFields().get("content").getFragments()[0].toString());
//                });
//            }
//            Assert.assertEquals(totalCount, totalCounter);
//            Assert.assertArrayEquals(groupIdToMessageIdSortMapper.get(key).toArray(), idSortList.toArray());
//        });
    }
}
