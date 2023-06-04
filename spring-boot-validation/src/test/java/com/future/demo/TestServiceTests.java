package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class TestServiceTests {
    @Autowired
    TestService testService;

    @Test
    public void testSingleParam() {
        try {
            this.testService.testSingleParam(null);
            Assert.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals("没有提供p1参数", ex.getConstraintViolations().iterator().next().getMessage());
        }

        try {
            this.testService.testSingleParam("");
            Assert.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals("没有提供p1参数", ex.getConstraintViolations().iterator().next().getMessage());
        }

        try {
            this.testService.testSingleParam("   ");
            Assert.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals("没有提供p1参数", ex.getConstraintViolations().iterator().next().getMessage());
        }

        this.testService.testSingleParam("p1");
    }
}
