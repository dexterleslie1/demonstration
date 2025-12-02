package com.future.demo;

import com.future.demo.service.ValidationService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = {DemoSpringBootMvcApplication.class}
)
public class ValidationServiceTests {
    @Autowired
    ValidationService validationService;

    @Test
    public void testSingleParam() {
        try {
            this.validationService.testSingleParam(null);
            Assertions.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            String message = ex.getConstraintViolations().iterator().next().getMessage();
            Assertions.assertTrue("Parameter p1 not provide!".equals(message) ||
                    "没有提供p1参数！".equals(message));
        }

        try {
            this.validationService.testSingleParam("");
            Assertions.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            String message = ex.getConstraintViolations().iterator().next().getMessage();
            Assertions.assertTrue("Parameter p1 not provide!".equals(message) ||
                    "没有提供p1参数！".equals(message));
        }

        try {
            this.validationService.testSingleParam("   ");
            Assertions.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            String message = ex.getConstraintViolations().iterator().next().getMessage();
            Assertions.assertTrue("Parameter p1 not provide!".equals(message) ||
                    "没有提供p1参数！".equals(message));
        }

        this.validationService.testSingleParam("p1");

        // region 测试校验两个参数情况

        // 同时触发两个校验规则
        try {
            this.validationService.testDoubleParam(null, null);
            Assertions.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            Assertions.assertEquals(2, ex.getConstraintViolations().size());
            String message = ex.getConstraintViolations().iterator().next().getMessage();
            Assertions.assertTrue("没有提供参数2".equals(message) ||
                    "没有提供参数1".equals(message));
        }

        // 只触发一个校验规则
        try {
            this.validationService.testDoubleParam(null, "x");
            Assertions.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            String message = ex.getConstraintViolations().iterator().next().getMessage();
            Assertions.assertEquals("没有提供参数1", message);
        }

        // endregion
    }
}
