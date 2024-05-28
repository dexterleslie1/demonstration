package com.future.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.future.demo.entity.User;
import com.future.demo.mapper.UserMapper;
import com.future.demo.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用于测试这个开发环境是否正常
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Before
    public void setup() {
        this.userMapper.delete(null);
    }

    @Test
    public void testSave() {
        // insert前总记录数
        int countInsertBefore = userService.count();
        User user = new User();
        user.setId(10001l);
        user.setAge(30);
        user.setName("中文测试");
        user.setEmail("dexterleslie@gmail.com");
        userService.save(user);

        int countInsertAfter = userService.count();

        Assert.assertEquals(countInsertBefore + 1, countInsertAfter);
    }

    // 测试分页查询
    @Test
    public void testPage() {
        // 新增数据
        int startId = 500000;
        int randomTotalCount = new Random().nextInt(1000);
        if (randomTotalCount <= 0) {
            randomTotalCount = 121;
        }

        for (int i = 0; i < randomTotalCount; i++) {
            long id = startId + i;
            this.userService.removeById(id);
        }

        int prevTotalCount = this.userService.count();

        if (randomTotalCount < 50) {
            randomTotalCount = 50;
        }

        for (int i = 0; i < randomTotalCount; i++) {
            long id = startId + i;

            User user = new User();
            user.setId(id);
            user.setAge(30 + i);
            user.setName("Dexterleslie" + i);
            user.setEmail("dexterleslie@gmail.com" + i);
            userService.save(user);
        }

        int currentTotalCount = this.userService.count();
        Assert.assertEquals(prevTotalCount + randomTotalCount, currentTotalCount);

        int currentPage = 1;
        int size = 50;
        Page<User> page = new Page<>(currentPage, size);
        page.orders().add(new OrderItem("id", false));
        this.userService.page(page);
        List<User> userList = page.getRecords();
        // 当前页码
        Assert.assertEquals(1, page.getCurrent());
        // 每页显示数量
        Assert.assertEquals(size, page.getSize());
        // 总页数
        int expectedPages;
        if (currentTotalCount % size != 0) {
            expectedPages = (currentTotalCount + size) / size;
        } else {
            expectedPages = currentTotalCount / size;
        }
        Assert.assertEquals(expectedPages, page.getPages());
        // 总记录数
        Assert.assertEquals(currentTotalCount, page.getTotal());
        Assert.assertEquals(size, userList.size());

        // 使用mapper分页
        page = new Page<>(currentPage, size);
        page.orders().add(new OrderItem("id", false));
        // 表示不需要select count(*) from ...
        page.setSearchCount(false);
        page = this.userMapper.selectPage(page, null);

        // 使用自定义count
        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.select("id");

        currentTotalCount = this.userMapper.selectCount(queryWrapper);
        userList = page.getRecords();
        // 当前页码
        Assert.assertEquals(1, page.getCurrent());
        // 每页显示数量
        Assert.assertEquals(size, page.getSize());
        // 总页数
        if (randomTotalCount % size != 0) {
            expectedPages = (randomTotalCount + size) / size;
        } else {
            expectedPages = randomTotalCount / size;
        }
        int actualPages;
        if (currentTotalCount % size != 0) {
            actualPages = (currentTotalCount + size) / size;
        } else {
            actualPages = currentTotalCount / size;
        }
        Assert.assertEquals(expectedPages, actualPages);
        // 总记录数
        Assert.assertEquals(randomTotalCount, currentTotalCount);
        Assert.assertEquals(size, userList.size());

        for (int i = 0; i < randomTotalCount; i++) {
            long id = startId + i;
            userService.removeById(id);
        }
    }

    /**
     * https://www.cnblogs.com/rwxwsblog/p/4512061.html
     */
    @Test
    public void batchUpdate() {
        User userTemporary = new User();
        userTemporary.setAge(156);
        userTemporary.setName("user1");
        userTemporary.setEmail("test1@baomidou.com");
        userMapper.insert(userTemporary);
        long userId1 = userTemporary.getId();

        userTemporary = new User();
        userTemporary.setAge(157);
        userTemporary.setName("user2");
        userTemporary.setEmail("test2@baomidou.com");
        userMapper.insert(userTemporary);
        long userId2 = userTemporary.getId();

        Map<Long, Integer> idToAgeMapper = new HashMap<>();
        idToAgeMapper.put(userId1, 156);
        idToAgeMapper.put(userId2, 157);

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Long id : idToAgeMapper.keySet()) {
            Map<String, Object> mapObject = new HashMap<>();
            mapObject.put("id", id);
            mapObject.put("age", idToAgeMapper.get(id));
            mapList.add(mapObject);
        }

        List<User> userListOriginal = this.userMapper.selectBatchIds(Arrays.asList(userId1, userId2));

        this.userMapper.updateAge(mapList);

        List<User> userList = this.userMapper.selectBatchIds(Arrays.asList(userId1, userId2));
        Map<Long, User> idToUserMapper = new HashMap<>();
        for (User user : userList) {
            idToUserMapper.put(user.getId(), user);
        }
        for (Long id : idToUserMapper.keySet()) {
            Assert.assertEquals(idToAgeMapper.get(id), idToUserMapper.get(id).getAge());
        }

        mapList = new ArrayList<>();
        for (User userOriginal : userListOriginal) {
            Map<String, Object> mapObject = new HashMap<>();
            mapObject.put("id", userOriginal.getId());
            mapObject.put("age", userOriginal.getAge());
            mapList.add(mapObject);
        }
        this.userMapper.updateAge(mapList);
    }

    @Test
    public void test_redis() {
        String randomKey = UUID.randomUUID().toString();
        this.redisTemplate.delete(randomKey);
        Assert.assertNull(this.redisTemplate.opsForValue().get(randomKey));
        this.redisTemplate.opsForValue().set(randomKey, randomKey, 30, TimeUnit.SECONDS);
        Assert.assertEquals(randomKey, this.redisTemplate.opsForValue().get(randomKey));
    }
}
