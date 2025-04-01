package com.future.demo.spring.boot.test;

import com.yyd.common.http.response.ObjectResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Tests {

    @LocalServerPort
    int localServerPort;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void test() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + localServerPort + "/api/v1/test1");
        ResponseEntity<ObjectResponse<String>> responseEntity =
                restTemplate.exchange(builder.toUriString(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {});
        ObjectResponse<String> response = responseEntity.getBody();
        Assert.assertEquals("common.property1=v1", response.getData());
    }

}
