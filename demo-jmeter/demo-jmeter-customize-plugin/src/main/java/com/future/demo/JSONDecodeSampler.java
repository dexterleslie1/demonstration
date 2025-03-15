package com.future.demo;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 测试 JSON 解码性能
 */
public class JSONDecodeSampler extends AbstractJavaSamplerClient {
    private static final Logger log = LoggerFactory.getLogger(JSONDecodeSampler.class);

    final static String JSON = "{\"errorCode\":0,\"errorMessage\":null,\"dataObject\":\"你好\"}";

    // 线程每个测试loop调用
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        try {
            // 样本测试开始，自动统计样本持续测试时间，不需要手动计算
            result.sampleStart();

            JSONObject jsonObject = JSONUtil.parseObj(JSON);
            int errorCode = jsonObject.getInt("errorCode");

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

            // 用于协助测试是否会打印错误日志
            /*boolean b = true;
            if(b) {
                throw new Exception("99999999");
            }*/

            // 标记样本成功
            result.setSuccessful(true);
            // 设置样本请求成功
            result.setResponseOK();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

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
            // 设置response body
            // 设置相应数据，会自动计算received数据长度
            result.setResponseData("123456".getBytes(StandardCharsets.UTF_8));
            result.setDataType(SampleResult.TEXT);

            result.sampleEnd();
        }
        return result;
    }

    // 测试线程启动时调用
    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        // 注意：log.debug不会打印日志，猜测可能需要调整jmeter日志等级配置
        // 在jmeter控制台或者jmeter.log日志文件中打印日志
        log.info("线程启动");
    }

    // 测试线程退出时调用
    @Override
    public void teardownTest(JavaSamplerContext context) {
        super.teardownTest(context);
        log.info("线程退出");
    }

    // 用于定义采样器在JMeter GUI中可用的参数。当你将自定义采样器添加到JMeter的测试计划中，并尝试配置它时，这些参数就会显示在“添加/编辑”对话框中。
    // getDefaultParameters() 方法应该返回一个 Arguments 对象，该对象包含了采样器需要的所有参数的定义。这些参数可以包括名称、默认值、描述等信息。
    @Override
    public Arguments getDefaultParameters() {
        return new Arguments() {{
            // 通过context.getParameter("k1")参数设置值
            addArgument("k1", "v1");
            addArgument("k2", "v2");
        }};
    }
}
