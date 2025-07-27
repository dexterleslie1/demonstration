package com.future.demo;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.client.config.impl.LocalConfigInfoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class SentinelRuleInitializer implements CommandLineRunner {

    @Value("${spring.cloud.sentinel.datasource.ds1.nacos.server-addr:localhost:8848}")
    String sentinelNacosServerAddr;

    @Override
    public void run(String... args) throws Exception {
        // region 初始化阿里 Sentinel 规则

        // 清除本地缓存（路径默认 ~/.nacos/config）
        LocalConfigInfoProcessor.cleanAllSnapshot();

        ConfigService configService = NacosFactory.createConfigService(sentinelNacosServerAddr);

        String dataId = "demo-spring-boot-sentinel";
        String group = "DEFAULT_GROUP";
        int timeoutMilliseconds = 3000;

        String config = configService.getConfig(dataId, group, timeoutMilliseconds);
        if (StringUtils.isBlank(config)) {
            String content = "[{\n" +
                    "    \"resource\": \"myTest1\",\n" +
                    "    \"limitApp\": \"default\",\n" +
                    "    \"grade\": 1,\n" +
                    "    \"count\": 1,\n" +
                    "    \"strategy\": 0,\n" +
                    "    \"controlBehavior\": 0,\n" +
                    "    \"clusterMode\": false\n" +
                    "}]";
            boolean result = configService.publishConfig(dataId, group, content, ConfigType.JSON.getType());
            Assert.isTrue(result, "设置 Nacos 中默认的 Sentinel 规则失败");
            if (log.isDebugEnabled()) {
                log.debug("成功设置 Nacos 中默认的 Sentinel 规则");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Nacos 中已经存在 Sentinel 规则");
            }
        }

        // endregion
    }

}
