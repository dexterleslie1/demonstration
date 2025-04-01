package com.future.demo.pkg3;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class MyBeanDefinitionRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 @EnableMyBean name 注解值
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableMyBean.class.getName());
        String name = (String) annotationAttributes.get("name");

        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(MyBean.class);
        // 把 @EnableMyBean name 注解值注入到 MyBean 实例的 name 字段中
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("name", name);
        // 添加属性
        rootBeanDefinition.setPropertyValues(propertyValues);
        // 注入到spring容器
        registry.registerBeanDefinition(MyBean.class.getName(), rootBeanDefinition);
    }
}


