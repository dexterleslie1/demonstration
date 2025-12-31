package com.example.activiti.config;

import org.activiti.spring.boot.ProcessEngineAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    ProcessEngineAutoConfiguration.class
})
public class ActivitiConfig {
}