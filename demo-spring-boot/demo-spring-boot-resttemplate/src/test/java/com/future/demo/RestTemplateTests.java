package com.future.demo;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yyd.common.http.response.ObjectResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class RestTemplateTests {
    @LocalServerPort
    int port;

    @Autowired
    private RestTemplate restTemplate = null;

    @Test
    public void test() {
        // 测试 JSON 响应转换为 Java 对象
        ResponseEntity<ObjectResponse<Map<String, Object>>> response = this.restTemplate.exchange(this.getBasePath() + "/api/v1/test1", HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<Map<String, Object>>>() {
        });
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        ObjectResponse<Map<String, Object>> objectResponse = response.getBody();
        Assert.assertNotNull(objectResponse);
        Assert.assertTrue(objectResponse.getData().containsKey("k1"));
        Assert.assertEquals("v1", objectResponse.getData().get("k1"));
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
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithBodyMap", HttpMethod.POST, httpEntity, String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String response = responseEntity.getBody();
        String message = "你提交的body参数：" + json;
        Assert.assertEquals(message, response);
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
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithHeaders", HttpMethod.POST, httpEntity, String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String response = responseEntity.getBody();
        String message = "你提交的参数 name=" + name + ",token=" + token;
        Assert.assertEquals(message, response);
    }

    /**
     *
     */
    @Test
    public void testPostWithoutParameters() {
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithoutParameters", HttpMethod.POST, null, String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String response = responseEntity.getBody();
        Assert.assertEquals("没有任何参数提交", response);
    }

    @Test
    public void test1() {
        try {
            // 返回数据类型为ObjectResponse<Map<String, Object>>和接收数据类型ObjectResponse<String>不匹配
            // 预期抛出RestClientException
            this.restTemplate.exchange(this.getBasePath() + "/api/v1/test1", HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
            });
            Assert.fail("预期异常没有抛出");
        } catch (RestClientException ex) {
            Assert.assertTrue(ex.getMessage().contains("Error while extracting response for type"));
        }

        ObjectResponse<Object> response = this.restTemplate.exchange(this.getBasePath() + "/api/v1/test1", HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<Object>>() {
        }).getBody();
        Assert.assertNotNull(response.getData());
        Assert.assertTrue(((Map<String, Object>) response.getData()).containsKey("k1"));
    }

    String getBasePath() {
        return "http://localhost:" + this.port;
    }
}
