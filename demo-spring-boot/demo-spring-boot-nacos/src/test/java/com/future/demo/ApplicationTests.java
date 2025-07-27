package com.future.demo;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() throws NacosException {
        String serverAddr = "localhost:8848";
        ConfigService configService = NacosFactory.createConfigService(serverAddr);

        // dataId: 配置的唯一标识（如 "sentinel-flow-rules"）
        // group: 配置的分组（如 "DEFAULT_GROUP"）
        String dataId = "sentinel-flow-rules";
        String group = "DEFAULT_GROUP";
        boolean removed = configService.removeConfig(dataId, group);
        Assertions.assertTrue(removed);

        // 参数说明：
        // timeoutMs: 超时时间（毫秒，默认 3000ms）
        String content = configService.getConfig(dataId, group, 3000);
        Assertions.assertNull(content);

//        // 定义监听器（配置变更时触发）
//        Listener listener = new Listener() {
//            @Override
//            public Executor getExecutor() {
//                return null; // 使用默认线程池（或自定义线程池）
//            }
//
//            @Override
//            public void receiveConfigInfo(String configInfo) {
//                System.out.println("配置变更，新内容：" + configInfo);
//            }
//        };
//        // 添加监听器（需指定 dataId 和 group）
//        configService.addListener(dataId, group, listener);

        // 参数说明：
        // dataId: 配置的唯一标识
        // group: 配置的分组
        // content: 配置内容（如 JSON 格式的 Sentinel 规则）
        content = "[\n" +
                "  {\n" +
                "    \"resource\": \"myTest1\",\n" +
                "    \"grade\": 1,\n" +
                "    \"count\": 5,\n" +
                "    \"strategy\": 0,\n" +
                "    \"controlBehavior\": 0,\n" +
                "    \"limitApp\": \"default\"\n" +
                "  }\n" +
                "]";
        boolean success = configService.publishConfig(dataId, group, content, ConfigType.JSON.getType());
        Assertions.assertTrue(success);

        String contentActual = configService.getConfig(dataId, group, 3000);
        Assertions.assertEquals(content, contentActual);
    }

}
