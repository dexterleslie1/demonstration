package com.future.demo;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

/**
 * param1参数是否为空校验
 */
public class Param1JobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        String param1 = jobParameters.getString("param1");
        if(!StringUtils.hasText(param1)) {
            throw new JobParametersInvalidException("没有提供param1参数");
        }
    }
}
