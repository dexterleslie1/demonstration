package com.future.demo.test;

import feign.*;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ApiTests {

    @Test
    public void testPredicate() throws Exception {
        String host = "localhost";
        int port = 8080;

        Api api = Feign.builder()
                // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
                .retryer(Retryer.NEVER_RETRY)
                // https://qsli.github.io/2020/04/28/feign-method-timeout/
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
//                .decoder(new JacksonDecoder())
                // feign logger
                // https://cloud.tencent.com/developer/article/1588501
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
                // ErrorDecoder
                // https://cloud.tencent.com/developer/article/1588501
//                .errorDecoder(new ErrorDecoder() {
//                    @Override
//                    public Exception decode(String methodKey, Response response) {
//                        try {
//                            String json = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
//                            ObjectResponse<String> responseError = JSONUtil.ObjectMapperInstance.readValue(json, new TypeReference<ObjectResponse<String>>(){});
//                            return new BusinessException(responseError.getErrorCode(), responseError.getErrorMessage());
//                        } catch (IOException e) {
//                            return e;
//                        }
//                    }
//                })
//                .requestInterceptor(new RequestInterceptor() {
//                    @Override
//                    public void apply(RequestTemplate template) {
//                        template.header("token", token);
//                    }
//                })
                .target(Api.class, "http://" + host + ":" + port);

        // 演示路由基本配置
        String param1 = "Dexterleslie";
        String str = api.test1(param1);
        Assert.assertEquals("成功调用/api/v1/test1接口，[param1=" + param1 + "]", str);

        // 演示需要提供param1参数才路由
        str = api.test21(param1);
        Assert.assertEquals("你的请求参数param1=" + param1, str);
        try {
            // 无法匹配 spring.cloud.gateway.routes[1].predicates[1]=Query=param1
            api.test22();
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertTrue(ex.getMessage().contains("[404 Not Found]"));
        }

        // 演示参数等于指定值才路由
        param1 = "h2";
        try {
            // 无法匹配 spring.cloud.gateway.routes[2].predicates[1]=Query=param1,h1
            api.test3(param1);
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertTrue(ex.getMessage().contains("[404 Not Found]"));
        }
        param1 = "h1";
        str = api.test3(param1);
        Assert.assertEquals("成功调用/api/v1/test3接口，param1=" + param1, str);

        // 演示指定header1=h2和header2=h3才路由
        String header1 = "1";
        String header2 = "2";
        try {
            api.test5(header1, header2);
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertTrue(ex.getMessage().contains("[404 Not Found]"));
        }
        header1 = "h2";
        try {
            api.test5(header1, header2);
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertTrue(ex.getMessage().contains("[404 Not Found]"));
        }
        header2 = "h3";
        str = api.test5(header1, header2);
        Assert.assertEquals("成功调用/api/v1/test5接口，header1=h2,header2=h3", str);
    }

    @Test
    public void testFilter() throws Exception {
        String host = "localhost";
        int port = 8080;

        Api api = Feign.builder()
                // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
                .retryer(Retryer.NEVER_RETRY)
                // https://qsli.github.io/2020/04/28/feign-method-timeout/
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
//                .decoder(new JacksonDecoder())
                // feign logger
                // https://cloud.tencent.com/developer/article/1588501
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
                // ErrorDecoder
                // https://cloud.tencent.com/developer/article/1588501
//                .errorDecoder(new ErrorDecoder() {
//                    @Override
//                    public Exception decode(String methodKey, Response response) {
//                        try {
//                            String json = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
//                            ObjectResponse<String> responseError = JSONUtil.ObjectMapperInstance.readValue(json, new TypeReference<ObjectResponse<String>>(){});
//                            return new BusinessException(responseError.getErrorCode(), responseError.getErrorMessage());
//                        } catch (IOException e) {
//                            return e;
//                        }
//                    }
//                })
//                .requestInterceptor(new RequestInterceptor() {
//                    @Override
//                    public void apply(RequestTemplate template) {
//                        template.header("token", token);
//                    }
//                })
                .target(Api.class, "http://" + host + ":" + port);

        // 演示AddRequestHeader、AddRequestParameter filter
        String str = api.test61(1L);
        Assert.assertEquals("成功调用/api/v1/test6接口，header1=h1,userId=2", str);
        str = api.test62(1L);
        Assert.assertEquals("成功调用/api/v1/test6接口，header1=h1,userId=2", str);

        // 测试局部filter
        str = api.getObject();
        Assert.assertEquals("param1=pvalue1,header1=hvalue1,bucketName=default-epu-555-33,objectName=2021-08-20/uuu-01.jpg", str);
    }
}
