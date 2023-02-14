package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.Application;
import com.future.demo.mybatis.entity.UserModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class SelectMapperTests {
    @Resource
    ParametersMapper parametersMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    SelectMapper selectMapper;

    @Before
    public void setup() {
        this.userMapper.truncate();
    }

    @Test
    public void testCount() {
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
        int count = this.selectMapper.countUser();
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetUserByNameToMap() {
        String name1 = UUID.randomUUID().toString();
        UserModel model = new UserModel();
        model.setName(name1);
        model.setPassword("123456");
        model.setAge(21);
        this.parametersMapper.testParameterObjectType(model);
        Map<String, Object> mapResult = this.selectMapper.getUserByNameToMap(name1);
        Assert.assertEquals(name1, mapResult.get("name"));
        Assert.assertEquals("123456", mapResult.get("password"));
        Assert.assertEquals(21, mapResult.get("age"));
    }

    @Test
    public void testGetUserAllToMap() {
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();
        UserModel model = new UserModel();
        model.setName(name1);
        model.setPassword("123456");
        model.setAge(21);
        this.parametersMapper.testParameterObjectType(model);
        Long id1 = model.getId();
        model = new UserModel();
        model.setName(name2);
        model.setPassword("123456");
        model.setAge(22);
        this.parametersMapper.testParameterObjectType(model);
        Long id2 = model.getId();
        Map<Long, Map<String, Object>> mapResult = this.selectMapper.getUserAllToMap();
        Assert.assertTrue(mapResult.containsKey(id1));
        Assert.assertTrue(mapResult.containsKey(id2));
        Assert.assertEquals(name1, mapResult.get(id1).get("name"));
        Assert.assertEquals(name2, mapResult.get(id2).get("name"));
    }
}
