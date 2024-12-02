package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.Application;
import com.future.demo.mybatis.entity.TestResultMapModel;
import com.future.demo.mybatis.entity.UserModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ResultMapMapperTests {
    @Resource
    ResultMapMapper resultMapMapper;

    @Test
    public void test() {
        List<TestResultMapModel> testResultMapModelList = this.resultMapMapper.findAll1();
        Assert.assertEquals(2, testResultMapModelList.size());
        Assert.assertEquals(1, testResultMapModelList.get(0).getId().longValue());
        Assert.assertEquals("用户1", testResultMapModelList.get(0).getName());
        Assert.assertEquals("123456", testResultMapModelList.get(0).getPassword());
        Assert.assertEquals(2, testResultMapModelList.get(1).getId().longValue());
        Assert.assertEquals("用户2", testResultMapModelList.get(1).getName());
        Assert.assertEquals("123456", testResultMapModelList.get(1).getPassword());

        testResultMapModelList = this.resultMapMapper.findAll2();
        Assert.assertEquals(2, testResultMapModelList.size());
        Assert.assertEquals(1, testResultMapModelList.get(0).getId().longValue());
        Assert.assertEquals("用户1", testResultMapModelList.get(0).getName());
        Assert.assertEquals("123456", testResultMapModelList.get(0).getPassword());
        Assert.assertEquals(2, testResultMapModelList.get(1).getId().longValue());
        Assert.assertEquals("用户2", testResultMapModelList.get(1).getName());
        Assert.assertEquals("123456", testResultMapModelList.get(1).getPassword());
    }
}
