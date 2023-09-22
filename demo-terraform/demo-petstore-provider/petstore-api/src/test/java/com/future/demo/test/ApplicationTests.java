package com.future.demo.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.Application;
import com.future.demo.PetModel;
import com.future.demo.mapper.PetMapper;
import com.future.demo.test.config.TestSupportConfiguration;
import com.future.demo.test.feign.TestSupportDemoFeignClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSupportConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApplicationTests {

    @Resource
    PetMapper petMapper;
    @Resource
    TestSupportDemoFeignClient testSupportDemoFeignClient;

    @Test
    public void test() {
        String name = "pet1";
        Integer age = 23;
        Long petId = this.testSupportDemoFeignClient.add(name, age).getData();

        QueryWrapper<PetModel> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", name);
        PetModel petModel = this.petMapper.selectOne(queryWrapper);

        PetModel resultPetModel = this.testSupportDemoFeignClient.get(petModel.getId()).getData();
        Assert.assertEquals(petId, resultPetModel.getId());
        Assert.assertEquals(name, resultPetModel.getName());
        Assert.assertEquals(age, resultPetModel.getAge());
        Assert.assertNotNull(resultPetModel.getCreateTime());
        Assert.assertEquals(petModel.getCreateTime(), resultPetModel.getCreateTime());

        String newName = "pet1new";
        Integer newAge = 25;
        String resultStr = this.testSupportDemoFeignClient.update(petModel.getId(), newName, newAge).getData();
        Assert.assertEquals("成功修改pet", resultStr);
        resultPetModel = this.testSupportDemoFeignClient.get(petModel.getId()).getData();
        Assert.assertEquals(newName, resultPetModel.getName());
        Assert.assertEquals(newAge, resultPetModel.getAge());

        resultStr = this.testSupportDemoFeignClient.delete(petModel.getId()).getData();
        Assert.assertEquals("成功删除pet", resultStr);

        Assert.assertNull(this.testSupportDemoFeignClient.get(petModel.getId()).getData());
    }

}
