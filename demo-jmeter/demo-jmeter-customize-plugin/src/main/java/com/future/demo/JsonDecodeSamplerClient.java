package com.future.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.nio.charset.StandardCharsets;

public class JsonDecodeSamplerClient extends AbstractJavaSamplerClient {

    final static String JSON = "{\"errorCode\":0,\"errorMessage\":null,\"dataObject\":\"你好\"}";

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        result.sampleStart();

        try {
//            TimeUnit.SECONDS.sleep(1);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(JSON);
            int errorCode = node.get("errorCode").asInt();
            if (errorCode > 0) {
                // 注入变量registerSuccess，能够使用vars.get("registerSuccess")获取变量值
                javaSamplerContext.getJMeterVariables().put("registerSuccess", "false");
            } else {
                javaSamplerContext.getJMeterVariables().put("registerSuccess", "true");
            }

            // 设置发送数据长度
            result.setSentBytes(5);

            // 设置sample label
            result.setSampleLabel("我的样品");
            // 设置request headers
            result.setRequestHeaders("设置请求头值");
            // 设置request body
            // https://stackoverflow.com/questions/74187155/how-to-populate-request-request-body-tab-of-a-jmeter-sampler-result-displa
            result.setSamplerData("设置请求body内容");

//            boolean b = true;
//            if(b) {
//                throw new Exception("99999999");
//            }

            // 标记样本成功
            result.setSuccessful(true);
            // 设置样本请求成功
            result.setResponseOK();
        } catch (Exception e) {
            // 在启动jmeter console中打印异常堆栈
            e.printStackTrace();
            // 标记样本失败
            result.setSuccessful(false);
            result.setResponseMessage("发生错误，原因: " + e.getMessage());
            result.setResponseCode("50000");
        } finally {
            // 不使用responseData数据长度时手动设置received数据长度
            /*result.setBytes(5L);
            result.setBodySize(5L);*/

            // 设置response headers
            result.setResponseHeaders("设置response headers");
            // 设置相应数据，会自动计算received数据长度
            result.setResponseData("123456".getBytes(StandardCharsets.UTF_8));
            result.setDataType(SampleResult.TEXT);

            result.sampleEnd();
        }
        return result;
    }

//    @Override
//    public void setupTest(JavaSamplerContext context) {
//        super.setupTest(context);
//    }
//
//    @Override
//    public void teardownTest(JavaSamplerContext context) {
//        super.teardownTest(context);
//    }
//
//    @Override
//    public Arguments getDefaultParameters() {
//        return super.getDefaultParameters();
//    }
}
