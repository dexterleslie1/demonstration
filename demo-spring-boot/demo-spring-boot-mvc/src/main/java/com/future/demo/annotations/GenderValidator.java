package com.future.demo.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<Gender, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return "男".equals(value) || "女".equals(value);
    }
}
