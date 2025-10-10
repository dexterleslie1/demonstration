package com.future.demo;

import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.feign.FeignUtil;
import com.future.common.http.ObjectResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ApiTests {

    @Resource
    TestSupportApiFeign testSupportApiFeign;

    @Test
    public void test401Error() {
        try {
            this.testSupportApiFeign.test401Error();
            Assert.fail("没有抛出预期异常");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getClass().toString(), ex instanceof BusinessException);
            Assert.assertEquals(90000, ((BusinessException) ex).getErrorCode());
            Assert.assertEquals("调用 /api/v1/test401Error 失败", ((BusinessException) ex).getErrorMessage());
        }
    }

    @Test
    public void testHttp200() throws BusinessException {
        ObjectResponse<String> response = this.testSupportApiFeign.testHttp200();
        Assertions.assertEquals(ErrorCodeConstant.ErrorCodeCommon, response.getErrorCode());
        Assertions.assertEquals("测试异常", response.getErrorMessage());
        Assertions.assertNull(response.getData());
        try {
            FeignUtil.throwBizExceptionIfResponseFailed(response);
            Assertions.fail();
        } catch (BusinessException ex) {
            Assertions.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assertions.assertEquals("测试异常", ex.getErrorMessage());
            Assertions.assertNull(ex.getData());
        }
    }
}
