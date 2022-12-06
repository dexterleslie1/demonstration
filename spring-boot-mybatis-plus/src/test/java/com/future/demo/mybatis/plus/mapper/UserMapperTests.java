package com.future.demo.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.mybatis.plus.Application;
import com.future.demo.mybatis.plus.entity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class UserMapperTests {

    @Autowired
    private UserMapper userMapper;

    @Before
    public void setup() {
        this.userMapper.delete(null);
    }

    @Test
    public void testSelect() {
        String name = "Jone";

        User user = new User();
        user.setId(1L);
        user.setAge(18);
        user.setName(name);
        user.setEmail("test1@baomidou.com");
        userMapper.insert(user);

        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(1, userList.size());
    }

    @Test
    public void testInsert() {
        // insert前总记录数
        int countInsertBefore = userMapper.selectCount(null);
        User user = new User();
        user.setId(10001l);
        user.setAge(30);
        user.setName("Dexterleslie");
        user.setEmail("dexterleslie@gmail.com");
        userMapper.insert(user);

        int countInsertAfter = userMapper.selectCount(null);

        // 删除上面插入数据
        userMapper.deleteById(10001l);

        Assert.assertEquals(countInsertBefore+1, countInsertAfter);
    }

    @Test
    public void testSelectQueryWrapper() {
        String name = "Dexterleslie";
        User user = new User();
        user.setId(10001l);
        user.setAge(30);
        user.setName(name);
        user.setEmail("dexterleslie@gmail.com");
        userMapper.insert(user);

        // selectOne
        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", name);
        User userResult = userMapper.selectOne(queryWrapper);
        Assert.assertEquals(userResult.getId(), user.getId());

        userMapper.deleteById(user.getId());
    }

    @Test
    public void testCommaSeperatedStringFieldToList() {
        String name = "Jone";

        User user = new User();
        user.setId(1L);
        user.setAge(18);
        user.setName(name);
        user.setEmail("test1@baomidou.com");
        user.setAuthorities(Arrays.asList("p1", "p2"));
        userMapper.insert(user);

        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", name);
        User userResult = userMapper.selectOne(queryWrapper);
        Assert.assertEquals(2, userResult.getAuthorities().size());
        Assert.assertEquals("p1", userResult.getAuthorities().get(0));
        Assert.assertEquals("p2", userResult.getAuthorities().get(1));
    }

    @Test
    public void testQueryWithIn() {
        String name = "Jone";

        User user = new User();
        user.setId(1L);
        user.setAge(18);
        user.setName(name);
        user.setEmail("test1@baomidou.com");
        userMapper.insert(user);

        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.in("name", Arrays.asList(name));

        List<User> users = userMapper.selectList(queryWrapper);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals(name, users.get(0).getName());
    }

    @Test
    public void testLambdaQuery() {
        String name = "Jone";

        User user = new User();
        user.setId(1L);
        user.setAge(18);
        user.setName(name);
        user.setEmail("test1@baomidou.com");
        userMapper.insert(user);

        List<User> users = userMapper.selectList(Wrappers.<User>lambdaQuery().and(wrapper -> wrapper.eq(User::getName, name)));
        Assert.assertEquals(1, users.size());
        Assert.assertEquals(name, users.get(0).getName());
    }
}