package com.future.demo;

import com.future.demo.app.Application;
import com.future.demo.pkg3.EnableMyBean;
import com.future.demo.pkg3.MyBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@EnableMyBean(name = "lisi")
public class Pkg3Tests {

    @Resource
    MyBean myBean;

    @Test
    public void test() {
        Assert.assertEquals("lisi", this.myBean.getName());
    }
}
