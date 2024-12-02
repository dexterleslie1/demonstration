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
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class GetAutoIncrementPrimaryKeyMapperTests {
    @Resource
    ParametersMapper parametersMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    GetAutoIncrementPrimaryKeyMapper getAutoIncrementPrimaryKeyMapper;

    @Before
    public void setup() {
        this.userMapper.truncate();
    }

    @Test
    public void test() {
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();
        UserModel model = new UserModel();
        model.setName(name1);
        model.setPassword("123456");
        model.setAge(21);
        this.getAutoIncrementPrimaryKeyMapper.test1(model);
        Long id1 = model.getId();
        model = new UserModel();
        model.setName(name2);
        model.setPassword("123456");
        model.setAge(22);
        this.getAutoIncrementPrimaryKeyMapper.test2(model);
        Long id2 = model.getId();
        List<String> nameList = Arrays.asList(name1, name2);
        List<UserModel> userModelList = this.parametersMapper.testNamedParameterListType(nameList);
        Assert.assertEquals(2, userModelList.size());
        Assert.assertEquals(id1, userModelList.get(0).getId());
        Assert.assertEquals(id2, userModelList.get(1).getId());
    }
}
