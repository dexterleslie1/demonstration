package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class Tests {
    @LocalServerPort
    private Integer port;

    /**
     *
     */
    @Test
    public void test_upload_and_download() throws IOException {
        //region 测试不提供token会预期报错
        try {
            RestTemplate restTemplate = new RestTemplate();
            String content = UUID.randomUUID().toString();
            File file = createFileIfNotExists(content);
            FileSystemResource fileSystemResource = new FileSystemResource(file);

            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
            parameters.add("file", fileSystemResource);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);
            String url = "http://localhost:" + port + "/api/v1/upload";
            restTemplate.exchange(
                    url, HttpMethod.POST, entity, byte[].class);
            Assert.fail("预期异常没有抛出");
        } catch (HttpClientErrorException ex) {
            String errorMessage = ex.getResponseBodyAsString();
            Assert.assertEquals("{\"errorCode\":90000,\"errorMessage\":\"请提供token参数\",\"data\":null}", errorMessage);
        }
        //endregion

        RestTemplate restTemplate = new RestTemplate();
        String content = UUID.randomUUID().toString();
        File file = createFileIfNotExists(content);
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", fileSystemResource);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("token", UUID.randomUUID().toString());
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);
        String url = "http://localhost:" + port + "/api/v1/upload";
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, byte[].class);
        Assert.assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        String responseString = new String(response.getBody(), "utf-8");
        Assert.assertEquals("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"成功上传1个文件\"}", responseString);

        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        entity = new HttpEntity<>(null, headers);
        url = "http://localhost:" + port + "/api/v1/download/" + file.getName();
        response = restTemplate.exchange(
                url, HttpMethod.GET, entity, byte[].class);
        Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        responseString = new String(response.getBody(), "utf-8");
        Assert.assertEquals(content, responseString);
    }

    final static String TemporaryDirectory = System.getProperty("java.io.tmpdir");

    File createFileIfNotExists(String content) throws IOException {
        String filename = TemporaryDirectory + "/" + content + ".tmp";
        File file = new File(filename);
        file.createNewFile();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes("utf-8"));
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        }
        return file;
    }
}
