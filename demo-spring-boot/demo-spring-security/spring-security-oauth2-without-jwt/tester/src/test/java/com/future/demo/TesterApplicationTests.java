package com.future.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class TesterApplicationTests {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void contextLoads() throws Exception {
        // region 使用 Selenium 获取 OAuth2.0 code

        String code = getCode("user1", "123456");
        Assertions.assertNotNull(code, "OAuth2.0 code 为空");

        // endregion

        // region 获取 access_token

        String string = RestAssured.given()
                .auth().basic("client1", "123")
                .param("grant_type", "authorization_code")
                .param("code", code)
                .post("http://localhost:9999/oauth/token")
                .then()
                .statusCode(200).extract().response().asString();
        JsonNode jsonNode = objectMapper.readTree(string);
        String accessToken = jsonNode.get("access_token").asText();
        String refreshToken = jsonNode.get("refresh_token").asText();
        String tokenType = jsonNode.get("token_type").asText();
        String scope = jsonNode.get("scope").asText();
        Assertions.assertNotNull(accessToken, "OAuth2.0 access_token 为空");
        Assertions.assertNotNull(refreshToken);
        Assertions.assertEquals("bearer", tokenType);
        Assertions.assertEquals("all", scope);
        Assertions.assertEquals(1, jsonNode.get("expires_in").asInt());

        // endregion

        // region 刷新 access_token

        // 测试无效的 refresh_token
        string = RestAssured.given()
                .auth().basic("client1", "123")
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken + "x")
                .post("http://localhost:9999/oauth/token")
                .then()
                .statusCode(400).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"invalid_grant\",\"error_description\":\"Invalid refresh token: " + (refreshToken + "x") + "\"}", string);

        // 测试不提供 refresh_token
        string = RestAssured.given()
                .auth().basic("client1", "123")
                .param("grant_type", "refresh_token")
                .post("http://localhost:9999/oauth/token")
                .then()
                .statusCode(400).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"invalid_grant\",\"error_description\":\"Invalid refresh token: null\"}", string);

        // 测试正常的 refresh_token
        string = RestAssured.given()
                .auth().basic("client1", "123")
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken)
                .post("http://localhost:9999/oauth/token")
                .then()
                .statusCode(200).extract().response().asString();
        jsonNode = objectMapper.readTree(string);
        accessToken = jsonNode.get("access_token").asText();
        Assertions.assertNotNull(accessToken, "OAuth2.0 access_token 为空");

        // endregion

        // region 调用资源接口

        // 测试没有提供 access_token
        string = RestAssured.given()
                .get("http://localhost:8081/order")
                .then().statusCode(401).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"unauthorized\",\"error_description\":\"Full authentication is required to access this resource\"}", string);

        // 测试提供 access_token
        string = RestAssured.given()
                .auth().oauth2(accessToken)
                .get("http://localhost:8081/order")
                .then().statusCode(200).extract().response().asString();
        Assertions.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"user1下单成功\"}", string);

        // 测试 refresh token 后，旧的 access_token 无效
        String oldAccessToken = accessToken;
        string = RestAssured.given()
                .auth().basic("client1", "123")
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken)
                .post("http://localhost:9999/oauth/token")
                .then()
                .statusCode(200).extract().response().asString();
        jsonNode = objectMapper.readTree(string);
        accessToken = jsonNode.get("access_token").asText();
        string = RestAssured.given()
                .auth().oauth2(oldAccessToken)
                .get("http://localhost:8081/order")
                .then().statusCode(401).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"invalid_token\",\"error_description\":\"" + oldAccessToken + "\"}", string);

        // 测试提供无效的 access_token
        string = RestAssured.given()
                .auth().oauth2(accessToken + "x")
                .get("http://localhost:8081/order")
                .then().statusCode(401).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"invalid_token\",\"error_description\":\"" + accessToken + "x\"}", string);

        // 测试提供 refresh_token
        string = RestAssured.given()
                .auth().oauth2(refreshToken)
                .get("http://localhost:8081/order")
                .then().statusCode(401).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"invalid_token\",\"error_description\":\"" + refreshToken + "\"}", string);

        // 测试过期 access_token
        TimeUnit.SECONDS.sleep(3);
        string = RestAssured.given()
                .auth().oauth2(accessToken)
                .get("http://localhost:8081/order")
                .then().statusCode(401).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"invalid_token\",\"error_description\":\"" + accessToken + "\"}", string);

        // endregion

        // region 测试 /oauth/check_token 端点

        // 测试正常的 access_token
        string = RestAssured.given()
                .auth().basic("client1", "123")
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken)
                .post("http://localhost:9999/oauth/token")
                .then()
                .statusCode(200).extract().response().asString();
        jsonNode = objectMapper.readTree(string);
        accessToken = jsonNode.get("access_token").asText();
        Assertions.assertNotNull(accessToken, "OAuth2.0 access_token 为空");
        string = RestAssured.given()
                .param("token", accessToken)
                .get("http://localhost:9999/oauth/check_token")
                .then()
                .statusCode(200).extract().response().asString();
        jsonNode = objectMapper.readTree(string);
        Assertions.assertTrue(jsonNode.get("active").asBoolean());
        Assertions.assertEquals("client1", jsonNode.get("client_id").asText());
        Assertions.assertEquals("user1", jsonNode.get("user_name").asText());
        Assertions.assertEquals("all", jsonNode.get("scope").get(0).asText());
        Assertions.assertEquals("sys:admin", jsonNode.get("authorities").get(0).asText());

        // 测试过期的 access_token
        TimeUnit.SECONDS.sleep(3);
        string = RestAssured.given()
                .param("token", accessToken)
                .get("http://localhost:9999/oauth/check_token")
                .then()
                .statusCode(400).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"invalid_token\",\"error_description\":\"Token was not recognised\"}", string);

        // endregion

        // region 刷新 access_token

        // 测试过期的 refresh_token
        TimeUnit.SECONDS.sleep(11);
        string = RestAssured.given()
                .auth().basic("client1", "123")
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken)
                .post("http://localhost:9999/oauth/token")
                .then()
                .statusCode(400).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"invalid_grant\",\"error_description\":\"Invalid refresh token: " + refreshToken + "\"}", string);

        // endregion

        // region 测试用户没有权限访问资源

        code = getCode("user2", "123456");
        Assertions.assertNotNull(code);

        string = RestAssured.given()
                .auth().basic("client1", "123")
                .param("grant_type", "authorization_code")
                .param("code", code)
                .post("http://localhost:9999/oauth/token")
                .then()
                .statusCode(200).extract().response().asString();
        jsonNode = objectMapper.readTree(string);
        accessToken = jsonNode.get("access_token").asText();

        string = RestAssured.given()
                .auth().oauth2(accessToken)
                .get("http://localhost:8081/order")
                .then().statusCode(403).extract().response().asString();
        Assertions.assertEquals("{\"error\":\"access_denied\",\"error_description\":\"Access is denied\"}", string);

        // endregion
    }

    // 使用 Selenium 获取 OAuth2.0 code
    String getCode(String username, String password) throws MalformedURLException {
        WebDriver driver = null;
        String code = null;
        try {
            FirefoxOptions options = new FirefoxOptions();
            /*Proxy proxy = new Proxy();
            proxy.setProxyType(Proxy.ProxyType.MANUAL);
            proxy.setHttpProxy("127.0.0.1:1080");
            proxy.setSocksProxy("127.0.0.1:1080");
            proxy.setSslProxy("127.0.0.1:1080");
            proxy.setFtpProxy("127.0.0.1:1080");
            proxy.setNoProxy("localhost,192.168.0.0/16,127.0.0.0/8,::1,www.example.com");
            options.setProxy(proxy);*/

            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            driver.get("http://localhost:9999/oauth/authorize?response_type=code&client_id=client1");

            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            try {
                driver.findElement(By.id("confirmationForm"));

                // 在授权确认页面
                driver.findElement(By.cssSelector("input[type='radio'][name='scope.all'][value='true']")).click();
                driver.findElement(By.cssSelector("input[name='authorize']")).click();
            } catch (NoSuchElementException ex) {
                // 不在授权确认页面
            }

            String currentUrl = driver.getCurrentUrl();
            int index = currentUrl.indexOf("code=");
            if (index >= 0) {
                code = currentUrl.substring(index + 5);
            }
        } finally {
            if (driver != null) {
                // 关闭浏览器
                driver.quit();
            }
        }
        return code;
    }

}
