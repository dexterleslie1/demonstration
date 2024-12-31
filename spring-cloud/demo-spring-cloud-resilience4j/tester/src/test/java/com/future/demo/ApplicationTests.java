package com.future.demo;

import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ApplicationTests {
    // 测试基于计数窗口的熔断
    // 注意：使用 COUNT_BASED 配置测试
    @Test
    public void testForCountBased() throws InterruptedException {
        // region 使用6个请求以触发熔断，resilience4j.circuitbreaker.configs.default.sliding-window-size=6 和 resilience4j.circuitbreaker.configs.default.minimum-number-of-calls 指定窗口大小

        // 前2个成功请求
        for (int i = 0; i < 2; i++) {
            String string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }

        // 连续3个失败请求
        for (int i = 0; i < 3; i++) {
            RestAssured.given()
                    .param("flag", "exception")
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
        }

        // 成功请求
        String string = RestAssured.given()
                .get("http://localhost:8080/api/v1/test1")
                .then().statusCode(200)
                .extract().asString();
        Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);

        // endregion

        // region 测试5秒内都是熔断状态，使用 resilience4j.circuitbreaker.configs.default.wait-duration-in-open-state=5s 配置

        TimeUnit.SECONDS.sleep(2);
        for (int i = 0; i < 10; i++) {
            string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":\"CircuitBreaker 'demo-service-provider' is OPEN and does not permit further calls\",\"data\":null}", string);
        }

        // endregion

        // region 测试5秒后，依旧调用异常触发从半开状态恢复到熔断状态

        // 加上之前2秒，共等待5秒
        TimeUnit.SECONDS.sleep(3);
        // 正常请求
        string = RestAssured.given()
                .param("flag", "exception")
                .get("http://localhost:8080/api/v1/test1")
                .then().statusCode(200)
                .extract().asString();
        Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":\"No fallback available.\",\"data\":null}", string);
        // 失败请求以触发从半开状态恢复到熔断状态
        string = RestAssured.given()
                .get("http://localhost:8080/api/v1/test1")
                .then().statusCode(200)
                .extract().asString();
        Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);

        TimeUnit.SECONDS.sleep(1);
        // 恢复到熔断状态后5秒内一直处于熔断状态
        for (int i = 0; i < 10; i++) {
            string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":\"CircuitBreaker 'demo-service-provider' is OPEN and does not permit further calls\",\"data\":null}", string);
        }

        // endregion

        // region 测试5秒后另一个半开状态，前2个半开状态的探测请求都成功后，会恢复到正常状态

        // 加上前面1秒，共等待5秒
        TimeUnit.SECONDS.sleep(4);
        // 前两个请求成功
        for (int i = 0; i < 2; i++) {
            string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }
        // 恢复到正常状态后，之后所有的请求都正常
        for (int i = 0; i < 10; i++) {
            string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }

        // endregion
    }

    // 测试慢调用的熔断
    @Test
    public void testForSlowCall() throws InterruptedException {
        // region 通过慢查询触发熔断

        // 2次正常查询
        for (int i = 0; i < 2; i++) {
            String string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }

        // 2次慢查询，超过 resilience4j.circuitbreaker.configs.default.slow-call-rate-threshold=30 设置的 30%
        for (int i = 0; i < 2; i++) {
            String string = RestAssured.given()
                    .param("flag", "sleep")
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=sleep\"}", string);
        }

        // 2次正常查询
        for (int i = 0; i < 2; i++) {
            String string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }

        // endregion

        // region 熔断状态中

        TimeUnit.SECONDS.sleep(2);
        // 5秒内熔断状态中所有请求失败
        for (int i = 0; i < 10; i++) {
            String string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":\"CircuitBreaker 'demo-service-provider' is OPEN and does not permit further calls\",\"data\":null}", string);
        }

        // endregion

        // region 5秒后半开状态测试

        // 加上之前的3秒，共等待5秒时间
        TimeUnit.SECONDS.sleep(3);
        for (int i = 0; i < 10; i++) {
            String string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }

        // endregion
    }

    // 测试基于时间窗口的熔断
    // 注意：使用 TIME_BASED 配置测试
    @Test
    public void testForTimeBased() throws InterruptedException {
        // region 触发熔断

        for (int i = 0; i < 3; i++) {
            String string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }

        // 50% 错误率触发熔断
        for (int i = 0; i < 3; i++) {
            RestAssured.given()
                    .param("flag", "exception")
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
        }

        // endregion

        // region 测试5秒内都是熔断状态，使用 resilience4j.circuitbreaker.configs.default.wait-duration-in-open-state=5s 配置

        TimeUnit.SECONDS.sleep(2);
        for (int i = 0; i < 10; i++) {
            String string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":\"CircuitBreaker 'demo-service-provider' is OPEN and does not permit further calls\",\"data\":null}", string);
        }

        // endregion

        // region 测试5秒后，依旧调用异常触发从半开状态恢复到熔断状态

        // 加上之前2秒，共等待5秒
        TimeUnit.SECONDS.sleep(3);
        // 正常请求
        String string = RestAssured.given()
                .param("flag", "exception")
                .get("http://localhost:8080/api/v1/test1")
                .then().statusCode(200)
                .extract().asString();
        Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":\"No fallback available.\",\"data\":null}", string);
        // 失败请求以触发从半开状态恢复到熔断状态
        string = RestAssured.given()
                .get("http://localhost:8080/api/v1/test1")
                .then().statusCode(200)
                .extract().asString();
        Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);

        TimeUnit.SECONDS.sleep(1);
        // 恢复到熔断状态后5秒内一直处于熔断状态
        for (int i = 0; i < 10; i++) {
            string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":\"CircuitBreaker 'demo-service-provider' is OPEN and does not permit further calls\",\"data\":null}", string);
        }

        // endregion

        // region 测试5秒后另一个半开状态，前2个半开状态的探测请求都成功后，会恢复到正常状态

        // 加上前面1秒，共等待5秒
        TimeUnit.SECONDS.sleep(4);
        // 前两个请求成功
        for (int i = 0; i < 2; i++) {
            string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }
        // 恢复到正常状态后，之后所有的请求都正常
        for (int i = 0; i < 10; i++) {
            string = RestAssured.given()
                    .get("http://localhost:8080/api/v1/test1")
                    .then().statusCode(200)
                    .extract().asString();
            Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=\"}", string);
        }

        // endregion
    }

    /*@Test
    public void testBulkhead() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                try {
                    String string = RestAssured.given()
                            .param("flag", "bulkheadsleep")
                            .get("http://localhost:8080/api/v1/test1")
                            .then().statusCode(200)
                            .extract().asString();
                    Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"flag=bulkheadsleep\"}", string);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }

//        TimeUnit.MILLISECONDS.sleep(500);
//        executorService.submit(() -> {
//            try {
//                String string = RestAssured.given()
//                        .param("flag", "bulkheadsleep")
//                        .get("http://localhost:8080/api/v1/test1")
//                        .then().statusCode(200)
//                        .extract().asString();
//                Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":\"Bulkhead 'demo-service-provider' is full and does not permit further calls\",\"data\":null}", string);
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        });

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
        }
    }*/
}
