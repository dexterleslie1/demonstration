package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

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
            .check(status().is(200));

    private static final ScenarioBuilder scenario = scenario("性能接口压测")
            .exec(repeat(Integer.MAX_VALUE).on(performance));

    {
        logger.info("Starting Performance simulation...");

        setUp(scenario.injectClosed(
                constantConcurrentUsers(32).during(Duration.ofSeconds(10))
        ))
                .protocols(httpProtocol)
                .maxDuration(Duration.ofSeconds(60));

        logger.info("Performance simulation setup completed");
    }
}
