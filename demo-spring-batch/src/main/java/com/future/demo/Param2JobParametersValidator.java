package com.future.demo;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

/**
 * param2参数是否为空校验
 */
public class Param2JobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        String param1 = jobParameters.getString("param2");
        if(!StringUtils.hasText(param1)) {
            throw new JobParametersInvalidException("没有提供param2参数");
        }
    }
}
