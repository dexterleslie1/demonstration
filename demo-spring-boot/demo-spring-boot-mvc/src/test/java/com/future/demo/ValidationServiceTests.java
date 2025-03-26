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
            Assertions.assertEquals("Parameter p1 not provide!", ex.getConstraintViolations().iterator().next().getMessage());
        }

        try {
            this.validationService.testSingleParam("");
            Assertions.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            Assertions.assertEquals("Parameter p1 not provide!", ex.getConstraintViolations().iterator().next().getMessage());
        }

        try {
            this.validationService.testSingleParam("   ");
            Assertions.fail("没有抛出预期异常");
        } catch (ConstraintViolationException ex) {
            Assertions.assertEquals("Parameter p1 not provide!", ex.getConstraintViolations().iterator().next().getMessage());
        }

        this.validationService.testSingleParam("p1");
    }
}
