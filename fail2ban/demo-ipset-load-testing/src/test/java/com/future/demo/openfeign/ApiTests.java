package com.future.demo.openfeign;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.json.JSONUtil;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.codec.StringDecoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ApiTests {
    @Test
    public void test() throws Exception {
        String host = "192.168.1.182";
        int port = 80;

        Api api = Feign.builder()
                // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
                .retryer(Retryer.NEVER_RETRY)
                // https://qsli.github.io/2020/04/28/feign-method-timeout/
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new StringDecoder())
                // feign logger
                // https://cloud.tencent.com/developer/article/1588501
                .logger(new Logger.ErrorLogger())//.logLevel(Logger.Level.FULL)
                // ErrorDecoder
                // https://cloud.tencent.com/developer/article/1588501
                .errorDecoder(new ErrorDecoder() {
                    @Override
                    public Exception decode(String methodKey, Response response) {
                        try {
                            String json = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
                            ObjectResponse<String> responseError = JSONUtil.ObjectMapperInstance.readValue(json, new TypeReference<ObjectResponse<String>>(){});
                            return new BusinessException(responseError.getErrorCode(), responseError.getErrorMessage());
                        } catch (IOException e) {
                            return e;
                        }
                    }
                })
                .target(Api.class, "http://" + host + ":" + port);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<500; i++) {
            executorService.submit(() -> {
                for(int j=0; j<2000; j++) {
                    String xForwardedFor = ThreadLocalRandom.current().nextInt(256) + "." +
                            ThreadLocalRandom.current().nextInt(256) + "." +
                            ThreadLocalRandom.current().nextInt(256) + "." +
                            ThreadLocalRandom.current().nextInt(256);
                    for (int i1 = 0; i1 < 120; i1++) {
                        try {
                            api.index(xForwardedFor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

//        String xForwardedFor = "CDCD:910A:2222:5498:8475:1111:3900:2020";
//        String xForwardedFor = "192.168.1.53";
//        for (int i1 = 0; i1 < 120; i1++) {
//            try {
//                api.index(xForwardedFor);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}
