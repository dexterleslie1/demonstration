package com.future.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.future.common.json.JSONUtil;
import org.awaitility.Awaitility;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 2, time = 30, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(128)
public class PerfJmhTests {

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(PerfJmhTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx4G",
                        "-server"/*,
                        "-XX:+UseG1GC",
                        "-XX:InitialHeapSize=8g",
                        "-XX:MaxHeapSize=8g",
                        "-XX:MaxGCPauseMillis=500",
                        "-XX:+DisableExplicitGC",
                        "-XX:+UseStringDeduplication",
                        "-XX:+ParallelRefProcEnabled",
                        "-XX:MaxMetaspaceSize=512m",
                        "-XX:MaxTenuringThreshold=1"*/)
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化
     */
    @Setup(Level.Trial)
    public void setup() {
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
    }

    @Benchmark
    public void test() throws ExecutionException, InterruptedException, IOException {
        String host = "localhost";
        int port = 8085;

        // 注意：下面代码不要删除，用于测试WebsocketClientSupport
        /*String clientId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        WebsocketClientSupport websocketClientSupport = new YydWebsocketClient(host, port, clientId, token, ClientDeviceType.AndroidHuawei);

        websocketClientSupport.connect();

        Assert.isTrue(websocketClientSupport.isConnected(), "没有connected状态");
        Assert.isTrue(websocketClientSupport.getData().size() == 1, "接收到的消息不只一条");
        Assert.isTrue(websocketClientSupport.getData().get(0).get("data").asText().contains("wsNotifyClientConnected"), "没有接受到nsNotifyClientConnected信号");

        websocketClientSupport.close();*/

        Map<String, Object> storeMapper = new HashMap<>();

        String clientId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();

        WebSocketClient client = new StandardWebSocketClient();
        String url = "ws://" + host + ":" + port + "/connector?clientId=" + clientId + "&token=" + token;
        ListenableFuture<WebSocketSession> future = client.doHandshake(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                try {
                    String payload = message.getPayload();
                    ArrayNode arrayNode = (ArrayNode) JSONUtil.ObjectMapperInstance.readTree(payload);
                    if (arrayNode != null && arrayNode.size() > 0) {
                        boolean receivedWSNotifyClientConnectedEvent = false;
                        List<JsonNode> data = new ArrayList<>();
                        for (JsonNode node : arrayNode) {
                            // 处理websocket服务器发出的 wsClientConnectedEvent
                            if (node.has("data")) {
                                String JSON = node.get("data").asText();
                                JsonNode nodeData = null;
                                try {
                                    nodeData = JSONUtil.ObjectMapperInstance.readTree(JSON);
                                } catch (JsonProcessingException ex) {
                                    // 消息可能不为JSON格式
                                }
                                if (nodeData != null && nodeData.has("type") &&
                                        "wsNotifyClientConnected".equals(nodeData.get("type").asText())) {
                                    receivedWSNotifyClientConnectedEvent = true;
                                }
                            }
                            data.add(node);
                        }

                        storeMapper.put("data", data);
                        storeMapper.put("receivedWSNotifyClientConnectedEvent", receivedWSNotifyClientConnectedEvent);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, url);
        WebSocketSession session = future.get();

        Assert.isTrue(session.isOpen());

        Awaitility.await()
                .pollInterval(1, TimeUnit.SECONDS)
                .atMost(30, TimeUnit.SECONDS)
                .until(() -> storeMapper.containsKey("data") && storeMapper.containsKey("receivedWSNotifyClientConnectedEvent") &&
                        ((List<JsonNode>) storeMapper.get("data")).size() == 1 && (Boolean) storeMapper.get("receivedWSNotifyClientConnectedEvent"));

        session.close();
    }
}
