package com.future.demo.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.mybatis.plus.Application;
import com.future.demo.mybatis.plus.entity.FootballMatch;
import com.future.demo.mybatis.plus.entity.User;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class UserMapperTests {

    @Resource
    private UserMapper userMapper;
    @Resource
    private FootballMatchMapper footballMatchMapper;

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

        // 测试select指定列
        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.select("id", "name", "age");
        queryWrapper.eq("name", name);
        queryWrapper.orderByAsc("id");
        List<Map<String, Object>> mapList = userMapper.selectMaps(queryWrapper);
        Assert.assertEquals(1, mapList.size());
        Assert.assertEquals(name, mapList.get(0).get("name"));
        Assert.assertEquals(18, mapList.get(0).get("age"));
    }

    @Test
    public void testInsert() {
        // insert前总记录数
        int countInsertBefore = userMapper.selectCount(null).intValue();
        User user = new User();
        user.setId(10001l);
        user.setAge(30);
        user.setName("Dexterleslie");
        user.setEmail("dexterleslie@gmail.com");
        userMapper.insert(user);

        int countInsertAfter = userMapper.selectCount(null).intValue();

        // 删除上面插入数据
        userMapper.deleteById(10001l);

        Assert.assertEquals(countInsertBefore + 1, countInsertAfter);
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

    /**
     * 测试自定义sql，只查询指定列name并且去重
     * https://blog.csdn.net/qq_44695727/article/details/123434199
     * https://blog.csdn.net/tcctcszhanghao/article/details/106576886
     */
    @Test
    public void testCustomizeQuery() {
        String name = "Jone";

        User user = new User();
        user.setId(1L);
        user.setAge(18);
        user.setName(name);
        user.setEmail("test1@baomidou.com");
        userMapper.insert(user);

        user = new User();
        user.setId(2L);
        user.setAge(18);
        user.setName(name);
        user.setEmail("test2@baomidou.com");
        userMapper.insert(user);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct name,age");
        queryWrapper.eq("age", 18);
        List<Map<String, Object>> nameMapList = userMapper.selectMaps(queryWrapper);
        Assert.assertEquals(1, nameMapList.size());
        Assert.assertEquals(2, nameMapList.get(0).size());
        Assert.assertEquals(name, nameMapList.get(0).get("name"));
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

    /**
     * 测试 where (teamIdA=1 and teamIdB=2) or (teamIdA=2 and teamIdB=1)
     */
    @Test
    public void testOrCombinationCondition() {
        Long teamIdA = 2L;
        Long teamIdB = 3L;
        FootballMatch match = this.footballMatchMapper.selectOne(Wrappers.<FootballMatch>lambdaQuery()
                .and(wrapper -> wrapper.eq(FootballMatch::getTeamIdA, teamIdA).eq(FootballMatch::getTeamIdB, teamIdB))
                .or(wrapper -> wrapper.eq(FootballMatch::getTeamIdB, teamIdA).eq(FootballMatch::getTeamIdA, teamIdB)));
        Assert.assertNotNull(match);
        Assert.assertEquals(match.getTeamIdA(), teamIdA);
        Assert.assertEquals(match.getTeamIdB(), teamIdB);

        QueryWrapper<FootballMatch> queryWrapper = Wrappers.query();
        queryWrapper.and(wrapper -> {
            wrapper.eq("teamIdA", teamIdA).eq("teamIdB", teamIdB);
        }).or(wrapper -> {
            wrapper.eq("teamIdB", teamIdA).eq("teamIdA", teamIdB);
        });
        match = this.footballMatchMapper.selectOne(queryWrapper);
        Assert.assertNotNull(match);
        Assert.assertEquals(match.getTeamIdA(), teamIdA);
        Assert.assertEquals(match.getTeamIdB(), teamIdB);
    }

    /**
     * 使用last指定limit语句
     */
    @Test
    public void testLambdaUpdate() {
        String name = "Jone";

        User user = new User();
        user.setId(1L);
        user.setAge(18);
        user.setName(name);
        user.setEmail("test1@baomidou.com");
        userMapper.insert(user);
        user = new User();
        user.setId(2L);
        user.setAge(18);
        user.setName(name);
        user.setEmail("test1@baomidou.com");
        userMapper.insert(user);

        // 只更新第一条数据
        // https://blog.csdn.net/sj1231984/article/details/118159040
        LambdaUpdateWrapper<User> updateWrapper = Wrappers.<User>lambdaUpdate()
                .set(User::getAge, 19)
                .orderByAsc(User::getId)
                .last("limit 1");
        userMapper.update(null, updateWrapper);

        List<User> users = userMapper.selectList(Wrappers.<User>lambdaQuery().orderByAsc(User::getId));
        Assert.assertEquals(2, users.size());
        Assert.assertEquals(new Integer(19), users.get(0).getAge());
        Assert.assertEquals(new Integer(18), users.get(1).getAge());
    }
}