package com.future.demo.test;

import io.restassured.RestAssured;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class ApiTests {

    @Test
    public void testPredicate() {
        // 演示路由基本配置
        String param1 = "Dexterleslie";
        RestAssured.given()
                .queryParam("param1", param1).get("http://localhost:8080/api/v1/test1")
                .then().assertThat().body(is("成功调用/api/v1/test1接口，[param1=" + param1 + "]"));

        // 演示需要提供param1参数才路由
        RestAssured.given()
                .queryParam("param1", param1)
                .post("http://localhost:8080/api/v1/test2")
                .then().assertThat().body(is("你的请求参数param1=" + param1));

        // 无法匹配 spring.cloud.gateway.routes[1].predicates[1]=Query=param1
        RestAssured.given()
                .post("http://localhost:8080/api/v1/test2")
                .then().assertThat().body(is("{\"errorMessage\":\"访问资源 /api/v1/test2 不存在\",\"errorCode\":600}"));

        // 演示参数等于指定值才路由
        param1 = "h2";
        // 无法匹配 spring.cloud.gateway.routes[2].predicates[1]=Query=param1,h1
        RestAssured.given()
                .queryParam("param1", param1)
                .post("http://localhost:8080/api/v1/test3")
                .then().assertThat().body(is("{\"errorMessage\":\"访问资源 /api/v1/test3 不存在\",\"errorCode\":600}"));
        param1 = "h1";
        RestAssured.given()
                .queryParam("param1", param1)
                .post("http://localhost:8080/api/v1/test3")
                .then().assertThat().body(is("成功调用/api/v1/test3接口，param1=" + param1));

        // 演示指定header1=h2和header2=h3才路由
        String header1 = "1";
        String header2 = "2";
        RestAssured.given()
                .header("header1", header1)
                .header("header2", header2)
                .post("http://localhost:8080/api/v1/test5")
                .then().assertThat().body(is("{\"errorMessage\":\"访问资源 /api/v1/test5 不存在\",\"errorCode\":600}"));
        header1 = "h2";
        RestAssured.given()
                .header("header1", header1)
                .header("header2", header2)
                .post("http://localhost:8080/api/v1/test5")
                .then().assertThat().body(is("{\"errorMessage\":\"访问资源 /api/v1/test5 不存在\",\"errorCode\":600}"));
        header2 = "h3";
        RestAssured.given()
                .header("header1", header1)
                .header("header2", header2)
                .post("http://localhost:8080/api/v1/test5")
                .then().assertThat().body(is("成功调用/api/v1/test5接口，header1=h2,header2=h3"));
    }

    @Test
    public void testFilter() throws Exception {
        // 演示AddRequestHeader、AddRequestParameter filter
        RestAssured.given()
                .queryParam("userId", 1)
                .post("http://localhost:8080/api/v1/test6")
                .then().assertThat().body(is("成功调用/api/v1/test6接口，header1=h1,userId=2"));
        RestAssured.given()
                .param("userId", 2)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post("http://localhost:8080/api/v1/test6")
                .then().assertThat().body(is("成功调用/api/v1/test6接口，header1=h1,userId=2"));

        // 测试局部filter
        RestAssured.given()
                .get("http://localhost:8080/obs-default-epu-555-33/2021-08-20/uuu-01.jpg")
                .then().assertThat().body(is("param1=pvalue1,header1=hvalue1,bucketName=default-epu-555-33,objectName=2021-08-20/uuu-01.jpg"));
    }
}
