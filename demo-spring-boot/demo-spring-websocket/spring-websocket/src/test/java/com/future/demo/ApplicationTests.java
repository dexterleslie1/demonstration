package com.future.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.future.common.json.JSONUtil;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest(classes = Application.class)
public class ApplicationTests {

    /**
     * 解析服务端下发的连接通知（JSON 数组）以及广播给其它客户端的 {@code {"content":"..."}} 消息。
     */
    private static TextWebSocketHandler newStoringHandler(Map<String, Object> storeMapper) {
        return new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                try {
                    String payload = message.getPayload();
                    JsonNode root = JSONUtil.ObjectMapperInstance.readTree(payload);
                    if (root.isArray()) {
                        ArrayNode arrayNode = (ArrayNode) root;
                        if (arrayNode.size() > 0) {
                            boolean receivedWSNotifyClientConnectedEvent = false;
                            List<JsonNode> data = new ArrayList<>();
                            for (JsonNode node : arrayNode) {
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
                    } else if (root.isObject() && root.has("content")) {
                        storeMapper.put("broadcastContent", root.get("content").asText());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    @Test
    public void contextLoads() throws IOException, ExecutionException, InterruptedException {
        WebSocketSession sessionA = null;
        WebSocketSession sessionB = null;
        try {
            String host = "localhost";
            int port = 8085;

            Map<String, Object> storeA = new HashMap<>();
            Map<String, Object> storeB = new HashMap<>();

            String clientIdA = UUID.randomUUID().toString();
            String tokenA = UUID.randomUUID().toString();
            String clientIdB = UUID.randomUUID().toString();
            String tokenB = UUID.randomUUID().toString();

            WebSocketClient client = new StandardWebSocketClient();
            String urlA = "ws://" + host + ":" + port + "/connector?clientId=" + clientIdA + "&token=" + tokenA;
            String urlB = "ws://" + host + ":" + port + "/connector?clientId=" + clientIdB + "&token=" + tokenB;

            ListenableFuture<WebSocketSession> futureA = client.doHandshake(newStoringHandler(storeA), urlA);
            sessionA = futureA.get();
            Assert.isTrue(sessionA.isOpen());
            Awaitility.await()
                    .pollInterval(1, TimeUnit.SECONDS)
                    .atMost(10, TimeUnit.SECONDS)
                    .until(() -> storeA.containsKey("data") && storeA.containsKey("receivedWSNotifyClientConnectedEvent") &&
                            ((List<JsonNode>) storeA.get("data")).size() == 1 && (Boolean) storeA.get("receivedWSNotifyClientConnectedEvent"));

            ListenableFuture<WebSocketSession> futureB = client.doHandshake(newStoringHandler(storeB), urlB);
            sessionB = futureB.get();
            Assert.isTrue(sessionB.isOpen());
            Awaitility.await()
                    .pollInterval(1, TimeUnit.SECONDS)
                    .atMost(10, TimeUnit.SECONDS)
                    .until(() -> storeB.containsKey("data") && storeB.containsKey("receivedWSNotifyClientConnectedEvent") &&
                            ((List<JsonNode>) storeB.get("data")).size() == 1 && (Boolean) storeB.get("receivedWSNotifyClientConnectedEvent"));

            // 广播消息
            String outbound = "hello-from-A-" + UUID.randomUUID();
            sessionA.sendMessage(new TextMessage(outbound));
            Awaitility.await()
                    .pollInterval(200, TimeUnit.MILLISECONDS)
                    .atMost(10, TimeUnit.SECONDS)
                    .until(() -> outbound.equals(storeB.get("broadcastContent")));
        } finally {
            if (sessionB != null) {
                sessionB.close();
                sessionB = null;
            }
            if (sessionA != null) {
                sessionA.close();
                sessionA = null;
            }
        }
    }
}
