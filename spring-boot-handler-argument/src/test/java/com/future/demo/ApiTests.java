package com.future.demo;


import com.future.demo.argument.Application;
import com.future.demo.argument.ParamMessageVO;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTests {
    @LocalServerPort
    int localServerPort;

    ApiFeign apiFeign;

    @Before
    public void before() {
        apiFeign = Feign.builder()
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
                .target(ApiFeign.class, "http://localhost:" + localServerPort);
    }

    @Test
    public void test() throws Exception {
        // 测试List<String>类型的参数以逗号分隔是否会被切割为多个List的元素
        ParamMessageVO vo = new ParamMessageVO();
        vo.setContentList(Arrays.asList("1,2,3", "a,b,c"));
        String responseStr = this.apiFeign.bodyWithObject(Collections.singletonList(vo));
        Assert.assertEquals("bodyJson=[ParamMessageVO{userIdTo:0,receiptId:null,contentList:[1,2,3, a,b,c]}]", responseStr);

        String resultStr = this.apiFeign.testRequestParam1(Arrays.asList("1,2,3", "a,b,c"));
        Assert.assertEquals("contentList=[1,2,3, a,b,c],size=2", resultStr);
    }
}
