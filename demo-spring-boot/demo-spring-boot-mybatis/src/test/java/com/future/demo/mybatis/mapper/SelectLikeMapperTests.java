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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class SelectLikeMapperTests {
    @Resource
    ParametersMapper parametersMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    SelectLikeMapper selectLikeMapper;

    @Before
    public void setup() {
        this.userMapper.truncate();
    }

    @Test
    public void test() {
        String name1 = "生物工程abc";
        String name2 = "软件工程888";
        String name3 = UUID.randomUUID().toString();
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
        model = new UserModel();
        model.setName(name3);
        model.setPassword("123456");
        model.setAge(22);
        this.parametersMapper.testParameterObjectType(model);

        List<UserModel> userModelList = this.selectLikeMapper.selectLike1("%工程%");
        Assert.assertEquals(2, userModelList.size());

        userModelList = this.selectLikeMapper.selectLike2("工程");
        Assert.assertEquals(2, userModelList.size());
    }
}
