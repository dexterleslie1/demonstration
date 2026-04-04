package com.future.demo;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@Slf4j
@SpringBootTest(classes = DemoRestAssuredApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoRestAssuredApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // 转换 JSON 响应为 Java 对象
        Response response = RestAssured
                // HTTP Basic 认证
                .given().auth().basic("client1", "123")
                .get(this.getBasePath() + "/test1");

        // 判断是否HTTP 200
        if (response.getStatusCode() != 200) {
            String responseStr = response.asString();
            throw new AssertionError("非预期响应: 状态码=" + response.getStatusCode()
                    + ", 响应=" + responseStr);
        }

        // 判断是否业务错误
        int errorCode = response.path("errorCode");
        if (errorCode > 0) {
            String errorMessage = response.path("errorMessage");
            throw new AssertionError(errorMessage);
        }
        log.info("{}", response.asString());
    }

    public String getBasePath() {
        return "http://localhost:" + this.port;
    }

}
