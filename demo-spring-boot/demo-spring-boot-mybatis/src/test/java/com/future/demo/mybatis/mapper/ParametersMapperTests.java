package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.Application;
import com.future.demo.mybatis.entity.TestAnnotationAndXmlMapperModel;
import com.future.demo.mybatis.entity.UserModel;
import com.future.demo.mybatis.service.TestAnnotationAndXmlMapperService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ParametersMapperTests {
    @Resource
    ParametersMapper parametersMapper;
    @Resource
    UserMapper userMapper;

    @Before
    public void setup() {
        this.userMapper.truncate();
    }

    @Test
    public void testParameterObjectType() {
        String name = UUID.randomUUID().toString();
        UserModel model = new UserModel();
        model.setName(name);
        model.setPassword("123456");
        model.setAge(21);
        this.parametersMapper.testParameterObjectType(model);
        UserModel model1 = this.userMapper.getByName(name);
        Assert.assertEquals(name, model1.getName());
    }

    @Test
    public void testNamedParameterObjectType() {
        String name = UUID.randomUUID().toString();
        UserModel model = new UserModel();
        model.setName(name);
        model.setPassword("123456");
        model.setAge(21);
        this.parametersMapper.testNamedParameterObjectType(model);
        UserModel model1 = this.userMapper.getByName(name);
        Assert.assertEquals(name, model1.getName());
    }

    @Test
    public void testNamedParameterPrimitieType() {
        String name = UUID.randomUUID().toString();
        String password = "123456";
        int age = 21;
        this.parametersMapper.testNamedParameterPrimitiveType(name, password, age);
        UserModel model1 = this.userMapper.getByName(name);
        Assert.assertEquals(name, model1.getName());
    }

    @Test
    public void testNamedParameterListType() {
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();
        UserModel model = new UserModel();
        model.setName(name1);
        model.setPassword("123456");
        model.setAge(21);
        this.parametersMapper.testParameterObjectType(model);
        model = new UserModel();
        model.setName(name2);
        model.setPassword("123456");
        model.setAge(22);
        this.parametersMapper.testParameterObjectType(model);
        List<String> nameList = Arrays.asList(name1, name2);
        List<UserModel> userModelList = this.parametersMapper.testNamedParameterListType(nameList);
        Assert.assertEquals(2, userModelList.size());
        Assert.assertEquals(name1, userModelList.get(0).getName());
        Assert.assertEquals(name2, userModelList.get(1).getName());
    }
}
