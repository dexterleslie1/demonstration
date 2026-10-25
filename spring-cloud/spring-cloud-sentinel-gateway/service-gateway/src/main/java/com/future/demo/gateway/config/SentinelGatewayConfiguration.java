package com.future.demo.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * API 分组限流：group-a 并发=2（精确+前缀并存），group-b 并发=3
 */
@Configuration
@Slf4j
public class SentinelGatewayConfiguration implements CommandLineRunner {

    private static final MediaType JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);

    @Override
    public void run(String... args) {
        BlockRequestHandler blockRequestHandler = (exchange, ex) -> {
            Map<String, Object> body = new HashMap<>();
            body.put("errorCode", 429);
            body.put("errorMessage", "被限流了");
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(JSON_UTF8)
                    .bodyValue(body);
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);

        Set<ApiDefinition> definitions = new HashSet<>();
        // group-a：精确 /api/v1/a/exact + 前缀 /api/v1/a/pattern/**，共享同一并发配额
        Set<ApiPredicateItem> groupAItems = new HashSet<>();
        groupAItems.add(pathItem("/api/v1/a/exact", SentinelGatewayConstants.URL_MATCH_STRATEGY_EXACT));
        groupAItems.add(pathItem("/api/v1/a/pattern/**", SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
        definitions.add(new ApiDefinition("group-a").setPredicateItems(groupAItems));
        // group-b：仅前缀
        definitions.add(new ApiDefinition("group-b").setPredicateItems(
                Collections.singleton(pathItem("/api/v1/b/**", SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX))));
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);

        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(threadRule("group-a", 2));
        rules.add(threadRule("group-b", 3));
        GatewayRuleManager.loadRules(rules);

        if (log.isDebugEnabled()) {
            log.debug("已加载 API 分组流控：group-a 并发=2（exact+pattern），group-b 并发=3");
        }
    }

    private static ApiPathPredicateItem pathItem(String pattern, int matchStrategy) {
        return new ApiPathPredicateItem().setPattern(pattern).setMatchStrategy(matchStrategy);
    }

    private static GatewayFlowRule threadRule(String apiGroupName, int concurrency) {
        return new GatewayFlowRule(apiGroupName)
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setGrade(RuleConstant.FLOW_GRADE_THREAD)
                .setCount(concurrency);
    }
}
