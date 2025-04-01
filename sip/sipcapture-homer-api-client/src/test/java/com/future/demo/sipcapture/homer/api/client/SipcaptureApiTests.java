package com.future.demo.sipcapture.homer.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes={Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class SipcaptureApiTests {
    @Autowired
    private RestTemplate restTemplate = null;

    /**
     *
     */
    @Test
    public void test() throws IOException {
        ObjectMapper OMInstance = new ObjectMapper();
        Map<String, String> mapParameters = new HashMap<>();
        mapParameters.put("username", "admin");
        mapParameters.put("password", "sipcapture");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(OMInstance.writeValueAsString(mapParameters), headers);

        try {
            // 登录homer服务器
            String responseString = this.restTemplate.postForObject(
                    "http://152.32.226.177:9080/api/v3/auth",
                    request,
                    String.class);
            JsonNode nodeResponse = OMInstance.readTree(responseString);
            String token = nodeResponse.get("token").asText();

            // 下载pcap文件
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.APPLICATION_OCTET_STREAM));
            headers.set("Authorization", "Bearer "+ token);
            String requestJson = "{\"timestamp\":{\"from\":1610940000000,\"to\":1610940600000},\"param\":{\"search\":{\"1_call\":{\"callid\":[\"68e3608f-7ba8-4344-8535-3435aaf6d886\"],\"uuid\":[]}},\"location\":{},\"transaction\":{\"call\":true,\"registration\":false,\"rest\":false},\"id\":{},\"timezone\":{\"value\":-480,\"name\":\"Local\"}}}";

            request = new HttpEntity<>(requestJson,headers);
            String url = "http://152.32.226.177:9080/api/v3/export/call/messages/pcap";
            ResponseEntity<byte[]> response = restTemplate.exchange( url, HttpMethod.POST, request, byte[].class);
            HttpHeaders responseHeaders = response.getHeaders();
            if(responseHeaders.getContentType().equals(MediaType.APPLICATION_OCTET_STREAM)) {
                byte [] bytes = response.getBody();

                FileOutputStream outputStream = null;
                try {
                    File file = File.createTempFile("pppp", ".pcap");
                    outputStream = new FileOutputStream(file);
                    IOUtils.write(bytes, outputStream);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if(outputStream!=null) {
                        outputStream.close();
                    }
                }
            }
        } catch (RestClientException exception) {
            // 密码错误或者网络失败出现http错误
            exception.printStackTrace();
        }
    }
}
