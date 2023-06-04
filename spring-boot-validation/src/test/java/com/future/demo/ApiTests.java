package com.future.demo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yyd.common.constant.ErrorCodeConstant;
import com.yyd.common.exception.BusinessException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestPropertySource("classpath:application-test.properties")
public class ApiTests {
    @Autowired
    TestSupportApiFeignClient testSupportApiFeignClient;

    @Test
    public void testSingleParam() throws BusinessException {
        try {
            this.testSupportApiFeignClient.testSingleParam(null);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeConstraintViolation, ex.getErrorCode());
            Assert.assertEquals("Parameters validation failed!", ex.getErrorMessage());
            Assert.assertEquals(((ArrayNode) ex.getData()).get(0).asText(), "没有提供p1参数");
        }

        try {
            this.testSupportApiFeignClient.testSingleParam("");
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeConstraintViolation, ex.getErrorCode());
            Assert.assertEquals("Parameters validation failed!", ex.getErrorMessage());
            Assert.assertEquals(((ArrayNode) ex.getData()).get(0).asText(), "没有提供p1参数");
        }

        try {
            this.testSupportApiFeignClient.testSingleParam("    ");
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeConstraintViolation, ex.getErrorCode());
            Assert.assertEquals("Parameters validation failed!", ex.getErrorMessage());
            Assert.assertEquals(((ArrayNode) ex.getData()).get(0).asText(), "没有提供p1参数");
        }

        String resultStr = this.testSupportApiFeignClient.testSingleParam("p1").getData();
        Assert.assertEquals("成功调用", resultStr);
    }
}
