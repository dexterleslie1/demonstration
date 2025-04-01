package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class EmailValidatorTests {
    /**
     * https://stackoverflow.com/questions/7169991/emailaddress-validation-in-java
     */
    @Test
    public void test() {
        EmailValidator emailValidator = EmailValidator.getInstance();
        String email = "11";
        Assert.assertFalse(emailValidator.isValid(email));

        email = "xxx@gmail.com";
        Assert.assertTrue(emailValidator.isValid(email));
        email = "xxx@qq.com";
        Assert.assertTrue(emailValidator.isValid(email));
        email = "xxx@163.com";
        Assert.assertTrue(emailValidator.isValid(email));
        email = "xxx@github.com";
        Assert.assertTrue(emailValidator.isValid(email));
        email = "xxx@yahoo.com";
        Assert.assertTrue(emailValidator.isValid(email));
    }
}
