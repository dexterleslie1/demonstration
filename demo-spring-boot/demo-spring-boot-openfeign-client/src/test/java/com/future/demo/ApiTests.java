package com.future.demo;

import com.future.common.exception.BusinessException;
import org.junit.Assert;
import org.junit.Test;
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
        } catch (BusinessException ex) {
            Assert.assertEquals(90000, ex.getErrorCode());
            Assert.assertEquals("调用 /api/v1/test401Error 失败", ex.getErrorMessage());
        }
    }

}
