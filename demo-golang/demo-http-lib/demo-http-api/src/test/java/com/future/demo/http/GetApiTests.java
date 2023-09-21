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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetApiTests {
    @LocalServerPort
    int localServerPort;

    GetApiFeign getApiFeign;

    @Before
    public void before() {
        getApiFeign = Feign.builder()
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
                .target(GetApiFeign.class, "http://localhost:" + localServerPort);
    }

    @Test
    public void test() {
        String responseStr = this.getApiFeign.testGetSubmitParamByUrl1("v1");
        Assert.assertEquals("提交参数param1=v1", responseStr);

        responseStr = this.getApiFeign.testGetSubmitParamByUrl2("v1");
        Assert.assertEquals("提交参数param1=v1", responseStr);

        // NOTE: openfeign不能使用GET方法提交application/json格式数据，自动转换为POST方法
        // 所以下面测试使用openfeign无法调试通过，但是使用curl工具却可以使用GET方法提交application/json数据
//        FormDataDTO formDataDTO = new FormDataDTO();
//        formDataDTO.setParam1("v1");
//        responseStr = this.getApiFeign.testGetSubmitParamByJSON(formDataDTO);
//        Assert.assertEquals("提交参数param1=v1", responseStr);
    }
}
