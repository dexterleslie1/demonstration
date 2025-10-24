package com.future.demo.elasticsearch;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.future.common.json.JSONUtil;
import lombok.Data;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Assert;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 单条插入和批量插入性能比较
 */
// https://blog.csdn.net/tanhongwei1994/article/details/120419321
// 吞吐量模式：每秒多少次调用
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
// 断点调试时fork=0
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(2)
@State(Scope.Benchmark)
public class IndividualAndBulkAddPerfComparisonTests {
    private TransportClient client;

    // 整个测试初始化elasticsearch transport客户端
    @Setup(Level.Trial)
    public void setupTrial() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster")
                .build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
    }

    // 整个测试关闭elasticsearch transport客户端
    @TearDown(Level.Trial)
    public void teardownTrial() {
        if (client != null) {
            client.close();
        }
    }

    // 每个benchmark迭代创建索引
    @Setup(Level.Iteration)
    public void setupIteration() throws IOException {
        // 创建消息索引
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
    }

    // 每个benchmark迭代删除索引
    @TearDown(Level.Iteration)
    public void teardownIteration() {
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().prepareExists(indexChatMessage).get();

        if (indicesExistsResponse.isExists()) {
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().prepareDelete(indexChatMessage).get();
            Assert.assertTrue(acknowledgedResponse.isAcknowledged());
        }
    }

    private final static int MaximumMessageId = 10000000;
    private final static String content = "nTA132Xw5OlUzEMbwqEHtGPx2ktroW2ZFVUAM6i9bSMnbWuGZTCwYS7mZ3vVFbvdUpQo7sXN3LKsQEYNDL3lgzfO3W5ubIjf9uWMNz97hoT7600x5HTyRHDxT0mQalVvVDMZXaAFItzDAlwWnRW9XG2fGvjQOsVIkv3gWOsdc3sFC4H6bcFTFk3pptD13IqMxafNBSfS2D6Dkb4wj1ApSEVxOyO8Fp1zHhOzzZrSM31MYF3EWVfeGxQhptBRQKVKkU8jpzV07oH5rOk0pGQJQ6nBWXk0FNJjO1PFtcuS1Mmqft4iF2w3H1Jx1HgekowZE3iKSTuULs571bdeIvgxq1zkba5ulCxAdevGXK1WpA8BeVKuHnSyelZ2kGpcWLTfmvLdtWkV0dCNlVTkZnjZrP7SCZBYvlV9dYELRnDRRd9AVyaQUcQ9TwdHb6QoOaRPOub8UTjVBSqzGPq5gqaahOMijdoQz4VmSe1Ft8K0nimZeCff6Fni8VDwqB4xZp1tiHqlFK02QTJnm0SKUm32Tb8YssVOrBrGznBYPkRI1ptvDhcDpfq0MRTA4wFHV0mQoNlDwP8EHxORswtUXyozymXRTOZO4ra3BTJspXDT8WrtEI8SIgGmE1FpEkRztZsc9N4kAGTPJmjXFzIFhL1xm9tXd9Xu5UUGSBNVknRTbUZnIXohi0MUATuCduusFMzlaDz2TWxm660fc0jW3R7kWtPgJFEL78fu9NpQrPmVbInkcBoEDYR3LH8pWkZY6d430JSZkF42QHUqDbtVAwk6VwvFQIwWyGoMI0HBPaJ4p14vj1f0t0cBrX0P95IBfQxbkN4RjdUtHVqNREPMuLgS46VJJxoZzDlPhPw61YR8RczkrA9yBK0Ml2yTwivIpb4gzhoQH6zBwMWAwHsGovFFMAa8uPK81cXbmluCaGKvwnEBzQviuzcPPH7g9w7fzED1Q2luf8JTtak86MVUjhF96wYnXg4qp3gB9lroT4oF";
    private final static String indexChatMessage = "chat_message";
    private final static int userIdSize = 5;

    /**
     * 测试单条插入性能
     */
    @Benchmark
    public void testIndividualAdd() throws InterruptedException, JsonProcessingException {
        int messageId = 0;
        while (messageId <= 0) {
            messageId = RandomUtil.randomInt(1, MaximumMessageId);
        }

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

    /**
     * 测试批量插入性能
     */
    @Benchmark
    public void testBulkAdd() throws JsonProcessingException {
        int messageId = 0;
        while (messageId <= 0) {
            messageId = RandomUtil.randomInt(1, MaximumMessageId);
        }

        // 删除之前的messageId对应的索引
        DeleteByQueryRequestBuilder deleteByQueryRequestBuilder = new DeleteByQueryRequestBuilder(this.client, DeleteByQueryAction.INSTANCE);
        deleteByQueryRequestBuilder.filter(QueryBuilders.termQuery("id", messageId));
        deleteByQueryRequestBuilder.source(indexChatMessage);
        deleteByQueryRequestBuilder.get();

        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        for (int k = 1; k <= userIdSize; k++) {
            long userId = k;
            ESIndexChatMessageDTO esIndexChatMessageDTO = new ESIndexChatMessageDTO();
            esIndexChatMessageDTO.setId(messageId);
            esIndexChatMessageDTO.setUserId(userId);
            esIndexChatMessageDTO.setContent(content);
            esIndexChatMessageDTO.setContentJSON(content);
            String idStr = messageId + "#" + userId;
            bulkRequestBuilder.add(client.prepareIndex(indexChatMessage, "_doc", idStr)
                    .setSource(JSONUtil.ObjectMapperInstance.writeValueAsString(esIndexChatMessageDTO), XContentType.JSON));
        }

        BulkResponse bulkItemResponses = bulkRequestBuilder.get();
        Assert.assertFalse("id=" + messageId + "聊天消息索引数据同步失败，原因：" + bulkItemResponses.buildFailureMessage(), bulkItemResponses.hasFailures());
    }

    /**
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // 指定运行的基准测试类
                .include(IndividualAndBulkAddPerfComparisonTests.class.getSimpleName())
                // 指定不运行的基准测试类
                // .exclude(JMHSample_01_HelloWorld.class.getSimpleName())
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
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
