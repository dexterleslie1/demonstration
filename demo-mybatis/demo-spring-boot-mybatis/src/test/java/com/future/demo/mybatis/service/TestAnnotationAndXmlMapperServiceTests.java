package com.future.demo.mybatis.service;

import com.future.demo.mybatis.Application;
import com.future.demo.mybatis.entity.TestAnnotationAndXmlMapperModel;
import com.future.demo.mybatis.mapper.TestAnnotationAndXmlMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TestAnnotationAndXmlMapperServiceTests {
    @Resource
    TestAnnotationAndXmlMapperService testAnnotationAndXmlMapperService;
    @Resource
    TestAnnotationAndXmlMapper testAnnotationAndXmlMapper;

    @Before
    public void setup() {
        this.testAnnotationAndXmlMapper.truncate();
    }

    @Test
    public void testAdd() {
        TestAnnotationAndXmlMapperModel model = new TestAnnotationAndXmlMapperModel();
        model.setFlag("annotation");
        this.testAnnotationAndXmlMapperService.add(model);
        model = new TestAnnotationAndXmlMapperModel();
        model.setFlag("xml");
        this.testAnnotationAndXmlMapperService.add1(model);
        Assert.assertEquals(2, this.testAnnotationAndXmlMapper.findAll().size());
        Assert.assertEquals("annotation", this.testAnnotationAndXmlMapper.findAll().get(0).getFlag());
        Assert.assertEquals("xml", this.testAnnotationAndXmlMapper.findAll().get(1).getFlag());
    }
}
