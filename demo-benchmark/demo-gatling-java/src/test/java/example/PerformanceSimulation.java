package example;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * 性能接口压测 Simulation：测试 GET http://localhost:8080/性能
 */
public class PerformanceSimulation extends Simulation {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceSimulation.class);

    // 加载配置
    private static final Config conf = ConfigFactory.load();

    // 读取自定义参数
    private static final String baseUrl = conf.getString("baseUrl");

    private static final HttpProtocolBuilder httpProtocol =
            http.baseUrl(baseUrl)
                    .acceptHeader("application/json")
                    .userAgentHeader(
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
    private static final HttpProtocolBuilder httpProtocol2 =
            http.baseUrl("http://localhost:8080")
                    .acceptHeader("application/json")
                    .userAgentHeader(
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");

    // GET /性能，路径使用 URL 编码
    private static final HttpRequestActionBuilder performance = http("性能")
            .get("/")
            .queryParam("param1", session -> {
                String param1 = session.getString("param1");
                return param1 == null ? "" : param1;
            })
            // 参考 Gatling 文档：单次 check(...) 支持定义多个 checks
            // https://docs.gatling.io/concepts/checks/#check-type
            .check(
                    status().is(200),
                    // status().in(200, 400),
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
                    bodyString().saveAs("bodyString")
                    // check bodyString内容为{...}
                    /*bodyString().is("{\"foo\": \"bar\"}"),*/
                    // 用于校验给请求传递param1参数
                    /*, jsonPath("$.data.param1").is("p1")*/
            );

    private static final ScenarioBuilder scenario = scenario("性能接口压测")
            .exec(pause(Duration.ofSeconds(5)))
            /*.exec(session -> {
                logger.info("性能接口压测");
                return session;
            })*/
            /*.exec(repeat(2).on(performance))*/
            .exec(performance)
            // 给请求传递param1参数
            /*.exec(session -> session.set("param1", "p1")).exec(performance)*/
            // 打印bodyString
            /*.exec(session -> {
                logger.info("bodyString: {}", session.getString("bodyString"));
                return session;
            })*/;
    private static final ScenarioBuilder scenario2 = scenario("另外一个测试场景")
            .exec(pause(Duration.ofSeconds(5)))
            /*.exec(session -> {
                logger.info("另外一个测试场景");
                return session;
            })*/
            .exec(performance);

    // 场景A生成数据，场景B消费数据（同一次运行内跨场景传递）
    private static final BlockingQueue<String> sharedParam1Queue = new LinkedBlockingQueue<>();
    private static final ScenarioBuilder scenarioProducerA = scenario("场景A-生产数据")
            .exec(session -> {
                String param1 = "p" + System.nanoTime();
                sharedParam1Queue.offer(param1);
                logger.info("场景A produce param1={}", param1);
                return session;
            });
    private static final ScenarioBuilder scenarioConsumerB = scenario("场景B-消费数据")
            /*.asLongAs(session -> sharedParam1Queue.isEmpty()).on(
                    pause(Duration.ofMillis(50))
            )*/
            .exec(session -> {
                String param1 = sharedParam1Queue.poll();
                if (param1 == null) {
                    // 理论上不会走到这里：上面已等待队列非空；这里兜底避免 NPE
                    param1 = "";
                }
                logger.info("场景B consume param1={}", param1);
                return session.set("param1", param1);
            })
            // param1不为空才执行performance请求，否则退出
            .doIfOrElse(session -> !session.getString("param1").isEmpty()).then(performance).orElse(exitHere());

    private static final ScenarioBuilder scenario3 = scenario("从session获取repeat次数")
            .exec(session -> {
                session = session.set("staffCount", 3);
                return session;
            })
            .exec(repeat(session -> session.getInt("staffCount")).on(performance));

    private static final ScenarioBuilder scenario4 = scenario("编写复杂的逻辑过滤数据")
            .exec(session -> {
                List<String> field1List = List.of("b1-f1");
                session = session.set("field1List", field1List);
                return session;
            })
            .exec(performance.check(jsonPath("$.data.dataList").ofList().saveAs("dataList")))
            .exec(session -> {
                List<String> field1List = session.getList("field1List");
                List<Map> dataList = session.getList("dataList");
                List<Map> dataListFiltered = dataList.stream().filter(o -> {
                    String field1 = (String) o.get("field1");
                    return field1List.contains(field1);
                }).collect(Collectors.toList());
                session = session.set("dataList", dataListFiltered);
                return session;
            })
            .exec(session -> {
                logger.info("bodyString {}", session.getString("bodyString"));
                logger.info("dataList {}", (List<Map>) session.get("dataList"));
                return session;
            });

    private static final ScenarioBuilder scenario5 = scenario("测试exitHereIfFailed")
            .exec(repeat(3).on(
                    exec(performance.check(jsonPath("$.errorCode").ofInt().is(200)))
                            // 表示测试失败就退出，导致不会repeat执行3次，只会执行1次
                            .exitHereIfFailed()))
            .exec(session -> {
                logger.info("测试是否执行到这里1");
                return session;
            });

    // 提示：param1参数如果在repeat循环中动态创建，这种情况不好传递
    /*private static HttpRequestActionBuilder createRequest(String param1) {
        logger.info("使用参数param1={}调用createRequest函数", param1);
        return http("性能")
                .get("/")
                .queryParam("param1", session -> param1 == null ? "" : param1)
                // 参考 Gatling 文档：单次 check(...) 支持定义多个 checks
                // https://docs.gatling.io/concepts/checks/#check-type
                .check(
                        status().is(200)
                        // 用于校验给请求传递param1参数
                        , jsonPath("$.data.param1").is(param1)
                        , bodyString().saveAs("bodyString")
                );
    }

    private static final ScenarioBuilder scenario6 = scenario("测试给请求动态提供参数")
            .exec(repeat(3, "index").on(
                    exec(session -> {
                        int index = session.getInt("index");
                        session = session.set("param1", "param1-" + index);
                        return session;
                    }).exec(createRequest("Hello world!"*//* 注意：这里找不到方案从session中获取param1并作为参数 *//*))))
            .exec(session -> {
                logger.info("bodyString {}", session.getString("bodyString"));
                return session;
            });*/

    {
        logger.info("Starting Performance simulation...");

        // 两个场景同时进行，不会等一个跑完再跑另一个。
        /*setUp(scenario.injectOpen(
         *//*atOnceUsers(32)*//*
                                atOnceUsers(1))
                        // 多个 protocol 用来表示场景里会用到多种协议，例如同时发 HTTP 和 WebSocket
                        // .protocols(httpProtocol, wsProtocol);
                        .protocols(httpProtocol),
                scenario2.injectOpen(atOnceUsers(2))
                        .protocols(httpProtocol2))
                .maxDuration(Duration.ofSeconds(30));*/

        // 先跑完一个再跑另一个，需要用 andThen() 把两次注入串起来
        /*setUp(scenario.injectOpen(
                        atOnceUsers(1))
                // 多个 protocol 用来表示场景里会用到多种协议，例如同时发 HTTP 和 WebSocket
                // .protocols(httpProtocol, wsProtocol);
                .protocols(httpProtocol)
                .andThen(scenario2.injectOpen(atOnceUsers(2))
                        .protocols(httpProtocol2)))
                .maxDuration(Duration.ofSeconds(30));*/

        // 从session获取repeat次数
        /*setUp(scenario3.injectOpen(
                        atOnceUsers(1))
                .protocols(httpProtocol));*/

        // 编写复杂的逻辑过滤数据
        /*setUp(scenario4.injectOpen(
                        atOnceUsers(1))
                .protocols(httpProtocol));*/

        // 测试exitHereIfFailed
        /*setUp(scenario5.injectOpen(
                        atOnceUsers(1))
                .protocols(httpProtocol));*/

        // 测试给请求动态提供参数
        /*setUp(scenario6.injectOpen(
                        atOnceUsers(2))
                .protocols(httpProtocol));*/

        // 示例：场景A生产数据，场景B消费数据（andThen 串行执行）
        /*setUp(scenarioProducerA.injectOpen(atOnceUsers(1))
                .protocols(httpProtocol)
                .andThen(scenarioConsumerB.injectOpen(atOnceUsers(2))
                        .protocols(httpProtocol2)))
                .maxDuration(Duration.ofSeconds(30));*/

        logger.info("Performance simulation setup completed");
    }
}
