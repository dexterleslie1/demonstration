package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 性能接口压测 Simulation：测试 GET http://localhost:8080/性能
 */
public class PerformanceSimulation extends Simulation {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceSimulation.class);

    private static final HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .userAgentHeader(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");

    // GET /性能，路径使用 URL 编码
    private static final HttpRequestActionBuilder performance = http("性能")
            .get("/")
            // 参考 Gatling 文档：单次 check(...) 支持定义多个 checks
            // https://docs.gatling.io/concepts/checks/#check-type
            .check(
                    status().is(200),
                    responseTimeInMillis().lte(2000),
                    bodyString().exists(),
                    // substring 演示：校验响应体包含指定子串（等同于 find().exists()）
                    substring("data").exists(),
                    // 响应中包含“UUID:”关键词
                    substring("UUID:").exists(),
                    // regex 演示：匹配响应 JSON 结构与 UUID
                    //{
                    //    "errorCode": 0,
                    //    "errorMessage": null,
                    //    "data": "UUID:1a05ac9b-e609-438b-913d-4b859470dc1b"
                    //}
                    /*regex("\"errorCode\"\\s*:\\s*0").exists(),
                    regex("\"data\"\\s*:\\s*\"UUID:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\"").exists(),*/
                    // substring 演示：校验响应体不包含 "error"
                    /*substring("error").notExists(),*/
                    // 保存bodyString到session中，在exec时能够通过session.getString("bodyString")访问
                    /*bodyString().saveAs("bodyString"),*/
                    // check bodyString内容为{...}
                    /*bodyString().is("{\"foo\": \"bar\"}"),*/
                    ResponseUtil.parseResponse(new TypeReference<ObjectResponse<MyBean>>() {
                    }, true).saveAs("myResponse")
            );

    private static final ScenarioBuilder scenario = scenario("性能接口压测")
            /*.exec(repeat(2).on(performance))*/
            .exec(performance)
            // 打印bodyString
            /*.exec(session -> {
                logger.info("bodyString: {}", session.getString("bodyString"));
                return session;
            })*/;

    {
        logger.info("Starting Performance simulation...");

        setUp(scenario.injectOpen(
                /*atOnceUsers(32)*/
                atOnceUsers(1)
        ))
                .protocols(httpProtocol)
                .maxDuration(Duration.ofSeconds(30));

        logger.info("Performance simulation setup completed");
    }
}
