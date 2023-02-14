package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.Application;
import com.future.demo.mybatis.entity.EmpModel;
import com.future.demo.mybatis.entity.UserModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class DynamicSqlMapperTests {
    @Resource
    DynamicSqlMapper dynamicSqlMapper;

    @Test
    public void testWhereIf() {
        Long id = 1L;
        String name = "张三";
        Integer age = 21;
        List<EmpModel> empModelList = this.dynamicSqlMapper.testWhereIf(id, name, age);
        Assert.assertEquals(1, empModelList.size());
        Assert.assertEquals(id, empModelList.get(0).getId());

        id = 0L;
        empModelList = this.dynamicSqlMapper.testWhereIf(id, name, age);
        Assert.assertEquals(1, empModelList.size());
        Assert.assertEquals(1, empModelList.get(0).getId().longValue());
    }

    @Test
    public void testChooseWhenOtherwise() {
        Long id = 1L;
        String name = "张三";
        Integer age = 21;
        List<EmpModel> empModelList = this.dynamicSqlMapper.testChooseWhenOtherwise(id, name, age);
        Assert.assertEquals(1, empModelList.size());
        Assert.assertEquals(id, empModelList.get(0).getId());

        age = null;
        empModelList = this.dynamicSqlMapper.testChooseWhenOtherwise(id, name, age);
        Assert.assertEquals(1, empModelList.size());
        Assert.assertEquals(1, empModelList.get(0).getId().longValue());
    }

    @Test
    public void testForeach() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        List<EmpModel> empModelList = this.dynamicSqlMapper.testForeach(idList);
        Assert.assertEquals(2, empModelList.size());
        Assert.assertEquals(1, empModelList.get(0).getId().longValue());
        Assert.assertEquals(2, empModelList.get(1).getId().longValue());
    }

    @Test
    public void testSqlTag() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        List<EmpModel> empModelList = this.dynamicSqlMapper.testSqlTag(idList);
        Assert.assertEquals(2, empModelList.size());
        Assert.assertEquals(1, empModelList.get(0).getId().longValue());
        Assert.assertEquals(2, empModelList.get(1).getId().longValue());
    }
}
