package com.future.demo.http;


import feign.*;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostApiTests {
    @LocalServerPort
    int localServerPort;

    PostApiFeign postApiFeign;

    @Before
    public void before() {
        postApiFeign = Feign.builder()
                .retryer(Retryer.NEVER_RETRY)
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.NONE)
                .errorDecoder(new ErrorDecoder() {
                    @Override
                    public Exception decode(String methodKey, Response response) {
                        try {
                            String str = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
                            return new Exception(str);
                        } catch (IOException e) {
                            return e;
                        }
                    }
                })
                .target(PostApiFeign.class, "http://localhost:" + localServerPort);
    }

    @Test
    public void test() throws IOException {
        String responseStr = this.postApiFeign.testPostSubmitParamByUrl1("v1");
        Assert.assertEquals("提交参数param1=v1", responseStr);

        responseStr = this.postApiFeign.testPostSubmitParamByFormUrlencoded1("v1");
        Assert.assertEquals("提交参数param1=v1", responseStr);

        responseStr = this.postApiFeign.testPostSubmitParamByFormUrlencoded2("v1");
        Assert.assertEquals("提交参数param1=v1", responseStr);

        responseStr = this.postApiFeign.testPostSubmitParamByMultipartFormData("v1");
        Assert.assertEquals("提交参数param1=v1", responseStr);

        responseStr = this.postApiFeign.testPostSubmitParamByMultipartFormData2("v1");
        Assert.assertEquals("提交参数param1=v1", responseStr);

        FormDataDTO formDataDTO = new FormDataDTO();
        formDataDTO.setParam1("v1");
        responseStr = this.postApiFeign.testPostSubmitParamByJSON(formDataDTO);
        Assert.assertEquals("提交参数param1=v1", responseStr);

        responseStr = this.postApiFeign.testPostAndResponseWithString("dexter");
        Assert.assertEquals("{\"dataObject\":\"你好，dexter\"}", responseStr);

        responseStr = this.postApiFeign.testPostAndResponseWithJSONObject("dexter");
        Assert.assertEquals("{\"dataObject\":{\"greeting\":\"你好，dexter\"}}", responseStr);

        responseStr = this.postApiFeign.testPostAndResponseWithException("dexter");
        Assert.assertEquals("{\"errorMessage\":\"测试预期异常是否出现\",\"errorCode\":50000}", responseStr);

        responseStr = this.postApiFeign.testPostAndResponseWithJSONArray("dexter");
        Assert.assertEquals("{\"dataObject\":[\"你好，dexter#0\",\"你好，dexter#1\",\"你好，dexter#2\",\"你好，dexter#3\",\"你好，dexter#4\",\"你好，dexter#5\",\"你好，dexter#6\",\"你好，dexter#7\",\"你好，dexter#8\",\"你好，dexter#9\"]}", responseStr);

        byte[] randomBytes = new byte[1024 * 1024];
        Random random = new Random();
        random.nextBytes(randomBytes);
        InputStream inputStream = new ByteArrayInputStream(randomBytes);
        String filename = UUID.randomUUID().toString();
        String temporaryDirectory = System.getProperty("java.io.tmpdir");
        File file = new File(temporaryDirectory, filename);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        }

        responseStr = postApiFeign.upload("dexter", file);
        Assert.assertEquals("{\"file\":\"" + filename + "\",\"name\":\"你好，dexter\"}", responseStr);

        Response response5 = postApiFeign.download(filename);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(response5.body().asInputStream(), byteArrayOutputStream);
        Assert.assertArrayEquals(randomBytes, byteArrayOutputStream.toByteArray());
    }
}
