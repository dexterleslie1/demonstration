package example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import example.util.ErrorCodeBodyValidator;
import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class BasicSimulation extends Simulation {

    // Initialize logback logger
    private static final Logger logger = LoggerFactory.getLogger(BasicSimulation.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Logback logging demonstration
    static {
        logger.debug("Debug level log: This is a debug message");
        logger.warn("Warn level log: This is a warning message");
    }

    // HTTP 协议配置：对所有请求生效，请求级未设置的会继承此处配置
    private static final HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")           // 请求头 Accept: application/json
            .contentTypeHeader("application/json")      // 请求头 Content-Type: application/json
            .userAgentHeader(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
            // 统一注入 Authorization：使用 Session 函数按当前用户动态设置
            // 有 token 时（登录后）带 Authorization: Bearer <token>，无 token 时（登录前）返回空字符串
            .header("Authorization", session -> {
                String token = session.getString("token");
                return token != null ? "Bearer " + token : "";
            });

    // 自定义复杂校验：使用 .validate("失败描述", (actual, session) -> {...})
    // - actual: 当前 check 提取的值（如 jsonPath 取到的值）
    // - session: 当前虚拟用户的 Session，可读 username 等
    // - 校验通过：return actual（或任意值）
    // - 校验失败：throw new RuntimeException("错误信息")，Gatling 会将该请求记为失败
    private static final HttpRequestActionBuilder login = http("Login")
            .post("/api/v1/login")
            .body(StringBody(session -> "{\"username\":\"" + session.getString("username") + "\",\"password\":\"" + session.getString("password") + "\"}"))
            .check(status().is(200))
            // 若要对整段响应做复杂校验，可用 bodyString().validate(...)
            // 使用公共工具校验 JSON 响应中的 errorCode（0 表示成功，>0 表示失败）
            .check(bodyString().validate("是否成功登录", ErrorCodeBodyValidator::validateErrorCode))
            .check(jsonPath("$.data.token").saveAs("token"))
            .check(jsonPath("$.data.userId").saveAs("userId"))
            .check(jsonPath("$.data.username").validate("username 与登录用户一致",
                    (actual, session) -> {
                        String expected = session.getString("username");
                        if (expected == null || !expected.equals(actual)) {
                            throw new RuntimeException("username 不一致，期望 " + expected + "，实际 " + actual);
                        }
                        return actual;
                    }));

    // 使用 transform 解析 body 并提取 $.data 存入 friendsList
    private final static HttpRequestActionBuilder getFriends = http("Get Friends")
            .get("/api/v1/friends")
            .check(status().is(200))
            .check(bodyString().validate("是否成功获取好友列表", ErrorCodeBodyValidator::validateErrorCode))
            .check(jsonPath("$.data").exists())
            .check(jsonPath("$.data[*].friendId").exists())
            .check(bodyString().transform(body -> {
                try {
                    JsonNode root = objectMapper.readTree(body);
                    return root.has("data") ? root.get("data") : null;
                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new RuntimeException(e);
                }
            }).saveAs("friendsList"));

    private final static HttpRequestActionBuilder getProfile = http("Get Profile")
            .get("/api/v1/profile")
            .check(status().is(200))
            .check(bodyString().validate("是否成功获取个人信息", ErrorCodeBodyValidator::validateErrorCode))
            .check(jsonPath("$.data.userId").validate("userId 与登录用户一致",
                    (actual, session) -> {
                        String expected = session.getString("userId");
                        if (expected == null || !expected.equals(actual)) {
                            throw new RuntimeException("userId 不一致，期望 " + expected + "，实际 " + actual);
                        }
                        return actual;
                    }))
            .check(jsonPath("$.data.username").validate("username 与登录用户一致",
                    (actual, session) -> {
                        String expected = session.getString("username");
                        if (expected == null || !expected.equals(actual)) {
                            throw new RuntimeException("username 不一致，期望 " + expected + "，实际 " + actual);
                        }
                        return actual;
                    }))
            .check(jsonPath("$.data.nickname").exists())
            .check(jsonPath("$.data.email").exists());

    // Form 参数提交演示：application/x-www-form-urlencoded
    private final static HttpRequestActionBuilder formDemo = http("Form Demo")
            .post("/api/v1/form-demo")
            // asFormUrlEncoded()：将请求体按表单编码发送
            // - 设置 Content-Type: application/x-www-form-urlencoded
            // - 将下方 formParam 拼成 key1=value1&key2=value2 格式；若未调用，formParam 可能不生效或编码方式不符合服务端预期
            .asFormUrlEncoded()
            .formParam("name", session -> session.getString("username"))
            .formParam("message", "你好form")
            .check(status().is(200))
            .check(bodyString().validate("Form 响应", ErrorCodeBodyValidator::validateErrorCode))
            .check(jsonPath("$.data.name").exists())
            .check(jsonPath("$.data.message").is("你好form"));

    // 场景定义：用户 API 测试流程
    private static final ScenarioBuilder scenario = scenario("User API Test")
            .exec(session -> {
                // 随机选择一个测试用户（user1 到 user10）
                int userIndex = (int) (Math.random() * 10) + 1;
                String username = "user" + userIndex;
                String password = "password" + userIndex;
                logger.info("Executing scenario for user: {}", username);
                return session.set("username", username).set("password", password);
            })
            // Step 1: 用户登录
            .exec(login)
            // 登录失败则停止当前用户，不执行后续 Get Friends、Get Profile
            .exitHereIfFailed()
            .exec(session -> {
                logger.info("Login successful, token: {}, userId: {}",
                        session.getString("token"), session.getString("userId"));
                return session;
            })
            .pause(1) // 模拟用户思考时间
            // Step 2: 获取好友列表
            .exec(getFriends)
            .exec(session -> {
                logger.debug("Friends list retrieved successfully, friendsList: {}", (ArrayNode) session.get("friendsList"));
                return session;
            })
            .pause(1) // 模拟用户思考时间
            // Step 3: 获取个人信息
            .exec(getProfile)
            .exec(session -> {
                logger.info("Profile retrieved successfully for user: {}", session.getString("username"));
                return session;
            })
            .pause(1)
            // 无限请求
            .exec(repeat(Integer.MAX_VALUE).on(formDemo));

    // 断言（Assertion）：在压测结束后对全局指标做校验，不通过则整个 Simulation 标记为失败
    // global().failedRequests().count().lt(1L) 表示：全局失败请求数必须 < 1，即不允许有任何请求失败
    // 若有任意请求失败（如 HTTP 非 2xx、check 未通过等），断言失败，可用于 CI 中判定测试不通过
    private static final Assertion assertion = global().failedRequests().count().lt(1L);

    {
        logger.info("Starting Gatling simulation...");

        // setUp：构建并启动负载测试，绑定场景、注入策略、断言与协议；
        // scenario.injectOpen(...): 使用 Open 模型向该场景注入虚拟用户（用户到达率由注入策略决定）
        // atOnceUsers(1): 立即同时启动 1 个用户（无 ramp，测试开始即全部就绪）
        // assertions(assertion): 使上述断言在结束后执行
        // protocols(httpProtocol): 为该场景绑定上述 HTTP 协议配置（baseUrl、请求头等）
        // maxDuration(Duration.ofSeconds(10)): 整场测试最多持续 10 秒，到时强制结束（防止无限 repeat 导致测试永不结束）
        setUp(scenario.injectOpen(atOnceUsers(1))).assertions(assertion).protocols(httpProtocol)
                .maxDuration(Duration.ofSeconds(60));

        logger.info("Simulation setup completed");
    }
}
