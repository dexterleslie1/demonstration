package com.future.demo;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.future.common.http.ObjectResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class RestTemplateTests {
    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate restTemplate = null;

    @Test
    public void test() {
        // 测试 JSON 响应转换为 Java 对象
        ResponseEntity<ObjectResponse<Map<String, Object>>> response =
                this.restTemplate.exchange(this.getBasePath() + "/api/v1/test1",
                        HttpMethod.POST, null,
                        new ParameterizedTypeReference<ObjectResponse<Map<String, Object>>>() {
                        });
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ObjectResponse<Map<String, Object>> objectResponse = response.getBody();
        Assertions.assertNotNull(objectResponse);
        Assertions.assertTrue(objectResponse.getData().containsKey("k1"));
        Assertions.assertEquals("v1", objectResponse.getData().get("k1"));
    }

    /**
     * body 参数测试
     */
    @Test
    public void testPostWithBodyMap() {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("name", "Dexter1");
        objectNode.put("age", 11);
        String json = objectNode.toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity<>(json, headers);
        ResponseEntity<String> responseEntity =
                this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithBodyMap",
                        HttpMethod.POST, httpEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String response = responseEntity.getBody();
        String message = "你提交的body参数：" + json;
        Assertions.assertEquals(message, response);
    }

    /**
     * 头参数
     */
    @Test
    public void testPostWithHeaders() {
        String name = "Dexter1";
        String token = "f1447ac1-f007-49c1-a7c3-d9ad3637c60e";
        MultiValueMap<String, String> multiValueParams = new LinkedMultiValueMap<>();
        multiValueParams.add("name", name);
        MultiValueMap<String, String> multiValueHeaders = new LinkedMultiValueMap<>();
        multiValueHeaders.add("token", token);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueParams, multiValueHeaders);
        ResponseEntity<String> responseEntity =
                this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithHeaders",
                        HttpMethod.POST, httpEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String response = responseEntity.getBody();
        String message = "你提交的参数 name=" + name + ",token=" + token;
        Assertions.assertEquals(message, response);
    }

    /**
     * 测试提交 query 参数
     */
    @Test
    public void testPostWithQueryParameters() {
        String name = "Dexter1";
        // 方法1
        MultiValueMap<String, String> multiValueParams = new LinkedMultiValueMap<>();
        multiValueParams.add("name", name);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueParams, null);
        ResponseEntity<String> responseEntity =
                this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithQueryParams",
                        HttpMethod.POST, httpEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String response = responseEntity.getBody();
        String message = "你提交的参数 name=" + name;
        Assertions.assertEquals(message, response);

        // 方法2
        String url = UriComponentsBuilder.fromUriString(this.getBasePath() + "/api/v1/postWithQueryParams")
                .queryParam("name", name)
                .build()
                .toUriString();
        responseEntity =
                this.restTemplate.exchange(url,
                        HttpMethod.POST, null, String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        response = responseEntity.getBody();
        message = "你提交的参数 name=" + name;
        Assertions.assertEquals(message, response);

        // 方法3
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        responseEntity =
                this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithQueryParams?name={name}",
                        HttpMethod.POST, null, String.class, params);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        response = responseEntity.getBody();
        message = "你提交的参数 name=" + name;
        Assertions.assertEquals(message, response);
    }

    /**
     *
     */
    @Test
    public void testPostWithoutParameters() {
        ResponseEntity<String> responseEntity =
                this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithoutParameters",
                        HttpMethod.POST, null, String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String response = responseEntity.getBody();
        Assertions.assertEquals("没有任何参数提交", response);
    }

    @Test
    public void test1() {
        try {
            // 返回数据类型为ObjectResponse<Map<String, Object>>和接收数据类型ObjectResponse<String>不匹配
            // 预期抛出RestClientException
            this.restTemplate.exchange(this.getBasePath() + "/api/v1/test1",
                    HttpMethod.POST, null,
                    new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            Assertions.fail("预期异常没有抛出");
        } catch (RestClientException ex) {
            Assertions.assertTrue(ex.getMessage().contains("Error while extracting response for type"));
        }

        ObjectResponse<Object> response =
                this.restTemplate.exchange(this.getBasePath() + "/api/v1/test1",
                        HttpMethod.POST, null,
                        new ParameterizedTypeReference<ObjectResponse<Object>>() {
                        }).getBody();
        Assertions.assertNotNull(response.getData());
        Assertions.assertTrue(((Map<String, Object>) response.getData()).containsKey("k1"));
    }

    String getBasePath() {
        return "http://localhost:" + this.port;
    }
}
