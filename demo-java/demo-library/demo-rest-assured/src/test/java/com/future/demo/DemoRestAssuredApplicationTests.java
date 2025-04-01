package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.demo.bean.MyBean;
import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

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
                .get(this.getBasePath() + "/test1").then().statusCode(200)
                .extract().response();
        ObjectResponse<MyBean> response1 = response.as(new TypeRef<ObjectResponse<MyBean>>() {
        });
        Assert.assertEquals("field1", response1.getData().getField1());
        Assert.assertEquals("field2", response1.getData().getField2());
    }

    public String getBasePath() {
        return "http://localhost:" + this.port;
    }

}
