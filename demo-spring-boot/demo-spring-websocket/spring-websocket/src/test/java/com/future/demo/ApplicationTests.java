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
    @Test
    public void contextLoads() throws IOException, ExecutionException, InterruptedException {
        WebSocketSession session = null;
        try {
            String host = "localhost";
            int port = 8085;

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
            session = future.get();

            Assert.isTrue(session.isOpen());

            Awaitility.await()
                    .pollInterval(1, TimeUnit.SECONDS)
                    .atMost(10, TimeUnit.SECONDS)
                    .until(() -> storeMapper.containsKey("data") && storeMapper.containsKey("receivedWSNotifyClientConnectedEvent") &&
                            ((List<JsonNode>) storeMapper.get("data")).size() == 1 && (Boolean) storeMapper.get("receivedWSNotifyClientConnectedEvent"));
        } finally {
            if (session != null) {
                session.close();
                session = null;
            }
        }
    }
}
