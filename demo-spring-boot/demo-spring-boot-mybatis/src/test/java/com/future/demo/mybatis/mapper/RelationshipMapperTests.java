package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.Application;
import com.future.demo.mybatis.entity.DeptModel;
import com.future.demo.mybatis.entity.EmpModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class RelationshipMapperTests {
    @Resource
    RelationshipMapper relationshipMapper;

    @Test
    public void testManyToOne() {
        String name = "李四";
        EmpModel empModel = this.relationshipMapper.getEmpAndDeptByEmpName1(name);
        Assert.assertEquals(2, empModel.getId().longValue());
        Assert.assertEquals(name, empModel.getName());
        Assert.assertEquals(22, empModel.getAge().intValue());
        Assert.assertEquals("研发部", empModel.getDept().getName());
        Assert.assertEquals(2, empModel.getDept().getId().longValue());

        empModel = this.relationshipMapper.getEmpAndDeptByEmpName2(name);
        Assert.assertEquals(2, empModel.getId().longValue());
        Assert.assertEquals(name, empModel.getName());
        Assert.assertEquals(22, empModel.getAge().intValue());
        Assert.assertEquals("研发部", empModel.getDept().getName());
        Assert.assertEquals(2, empModel.getDept().getId().longValue());
        Assert.assertEquals(2, empModel.getAddress().getId().longValue());
        Assert.assertEquals("北京", empModel.getAddress().getAddress());

        empModel = this.relationshipMapper.getEmpAndDeptByEmpName3(name);
        Assert.assertEquals(2, empModel.getId().longValue());
        Assert.assertEquals(name, empModel.getName());
        Assert.assertEquals(22, empModel.getAge().intValue());
        Assert.assertEquals("研发部", empModel.getDept().getName());
        Assert.assertEquals(2, empModel.getDept().getId().longValue());
    }

    @Test
    public void testOneToMany() {
        String name = "研发部";
        DeptModel deptModel = this.relationshipMapper.getDeptAndEmpByDeptName1(name);
        Assert.assertEquals(2, deptModel.getId().longValue());
        Assert.assertEquals(name, deptModel.getName());
        Assert.assertEquals(2, deptModel.getEmpList().size());
        Assert.assertEquals(1, deptModel.getEmpList().get(0).getId().longValue());
        Assert.assertEquals("张三", deptModel.getEmpList().get(0).getName());
        Assert.assertEquals(21, deptModel.getEmpList().get(0).getAge().intValue());
        Assert.assertEquals(2, deptModel.getEmpList().get(1).getId().longValue());
        Assert.assertEquals("李四", deptModel.getEmpList().get(1).getName());
        Assert.assertEquals(22, deptModel.getEmpList().get(1).getAge().intValue());

        deptModel = this.relationshipMapper.getDeptAndEmpByDeptName2(name);
        Assert.assertEquals(2, deptModel.getId().longValue());
        Assert.assertEquals(name, deptModel.getName());
        Assert.assertEquals(2, deptModel.getEmpList().size());
        Assert.assertEquals(1, deptModel.getEmpList().get(0).getId().longValue());
        Assert.assertEquals("张三", deptModel.getEmpList().get(0).getName());
        Assert.assertEquals(21, deptModel.getEmpList().get(0).getAge().intValue());
        Assert.assertEquals(2, deptModel.getEmpList().get(1).getId().longValue());
        Assert.assertEquals("李四", deptModel.getEmpList().get(1).getName());
        Assert.assertEquals(22, deptModel.getEmpList().get(1).getAge().intValue());
    }
}
