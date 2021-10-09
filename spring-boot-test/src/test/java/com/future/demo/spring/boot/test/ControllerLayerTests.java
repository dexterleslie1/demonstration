package com.future.demo.spring.boot.test;

import com.yyd.common.http.response.ObjectResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ControllerLayerTests {
    @Autowired
    ApiController apiController;

    @Test
    public void test() {
//        Assert.assertNotNull(apiController);
//        int a = 3;
//        int b = 5;
//        ObjectResponse<Integer> response = this.apiController.add(3, 5);
//        Assert.assertEquals(a+b, response.getData().intValue());
    }
}
