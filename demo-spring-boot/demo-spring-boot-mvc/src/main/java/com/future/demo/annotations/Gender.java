package com.future.demo.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {GenderValidator.class})
@Target({FIELD})
@Retention(RUNTIME)
public @interface Gender {
    // 国际化对应的key
    String message() default "{person.gender.error}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
