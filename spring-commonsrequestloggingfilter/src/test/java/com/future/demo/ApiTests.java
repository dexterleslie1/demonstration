package com.future.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.json.JSONUtil;
import feign.*;
import feign.codec.ErrorDecoder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTests {
    @LocalServerPort
    int localServerPort;

    @Test
    public void test() throws Exception {
        String host = "localhost";
        int port = localServerPort;

        final String token = UUID.randomUUID().toString();
        Api api = Feign.builder()
                // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
                .retryer(Retryer.NEVER_RETRY)
                // https://qsli.github.io/2020/04/28/feign-method-timeout/
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder())
                // feign logger
                // https://cloud.tencent.com/developer/article/1588501
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.NONE)
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
                .requestInterceptor(new RequestInterceptor() {
                    @Override
                    public void apply(RequestTemplate template) {
                        template.header("token", token);
                    }
                })
                .target(Api.class, "http://" + host + ":" + port);
        String name = "Dexterleslie";
        ObjectResponse<MyVO> response = api.test1(name);
        Assert.assertEquals("你好，" + name, response.getData().getField1());
        Assert.assertTrue(response.getData().isField2());

        List<MyPostVO> myPostVOList = new ArrayList<>();
        try {
            api.testPost(myPostVOList);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertTrue(ex.getErrorCode()>0);
            Assert.assertEquals("没有指定myPostVOList", ex.getErrorMessage());
        }

        for(int i=0; i<2; i++) {
            MyPostVO myPostVO = new MyPostVO();
            myPostVO.setParameter1("parameter#" + (i+1));
            myPostVOList.add(myPostVO);
        }
        ObjectResponse<String> response1 = api.testPost(myPostVOList);
        Assert.assertFalse(response1.getErrorCode()>0);
        Assert.assertEquals("调用成功", response1.getData());

        for(int i=0; i<2; i++) {
            MyPostVO myPostVO = new MyPostVO();
            myPostVO.setParameter1("parameter#" + (i+1));
            myPostVOList.add(myPostVO);
        }
        response1 = api.testPut("/api/v1/testPut", myPostVOList);
        Assert.assertFalse(response1.getErrorCode()>0);
        Assert.assertEquals("调用成功", response1.getData());

        ObjectResponse<String> response2 = api.testHeaderWithToken();
        Assert.assertEquals("你提交的token=" + token, response2.getData());

        String dynamicToken = UUID.randomUUID().toString();
        response2 = api.testHeaderWithDynamicToken(dynamicToken);
        Assert.assertEquals("你提交的dynamicToken=" + dynamicToken, response2.getData());

        try {
            api.testResponseWithHttpStatus400();
            Assert.fail("意料中异常没有出现");
        } catch (BusinessException ex) {
            Assert.assertEquals(900, ex.getErrorCode());
            Assert.assertEquals("意料中异常,BAD_REQUEST: 400", ex.getErrorMessage());
        }

        byte []randomBytes = new byte[1024*1024];
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
            if(outputStream!=null) {
                outputStream.close();
                outputStream = null;
            }
        }

        ObjectResponse<String> response3 = api.upload(file);
        Assert.assertEquals("成功上传1个文件", response3.getData());

        Response response5 = api.download(filename);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(response5.body().asInputStream(), byteArrayOutputStream);
        Assert.assertArrayEquals(randomBytes, byteArrayOutputStream.toByteArray());

        // 测试delete
        try {
            api.delete("");
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("没有指定param1参数", ex.getErrorMessage());
        }
        response1 = api.delete("1111");
        Assert.assertEquals("删除成功", response1.getData());

        // 测试x-www-form-urlencoded
        String parameter1 = "测试";
        ObjectResponse<String> response6 = api.postWwwFormUrlencoded(parameter1);
        Assert.assertEquals("提交参数parameter1=" + parameter1, response6.getData());

        try {
            api.postWwwFormUrlencoded(null);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("没有指定param1参数", ex.getErrorMessage());
        }
    }
}
