package com.future.demo.mybatis.service;

import com.future.demo.mybatis.entity.TestAnnotationAndXmlMapperModel;
import com.future.demo.mybatis.mapper.TestAnnotationAndXmlMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TestAnnotationAndXmlMapperService {
    @Resource
    TestAnnotationAndXmlMapper testAnnotationAndXmlMapper;

    public void add(TestAnnotationAndXmlMapperModel model) {
        this.testAnnotationAndXmlMapper.add(model);
    }

    public void add1(TestAnnotationAndXmlMapperModel model) {
        this.testAnnotationAndXmlMapper.add1(model);
    }
}
