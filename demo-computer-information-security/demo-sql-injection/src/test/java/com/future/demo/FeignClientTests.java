package com.future.demo;

import com.future.common.exception.BusinessException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSupportConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FeignClientTests {

    @Resource
    TestSupportDemoFeignClient testSupportDemoFeignClient;

    @Test
    public void test() throws Exception {
        try {
            // username参数sql
            this.testSupportDemoFeignClient.testErrorBasedSqlInjection("user'; select 1 from table_test; -- ");
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("Table 'demo.table_test' doesn't exist", ex.getErrorMessage());
        }

        Assert.assertEquals("从登录界面跳转到主界面",
                this.testSupportDemoFeignClient.testBooleanBasedBlindSqlInjection("' OR '1'='1").getData());

        Assert.assertTrue(
                this.testSupportDemoFeignClient.testTimeBasedBlindSqlInjection("' OR IF(1=1, SLEEP(1), 0) OR '1'='0").getData() >= 1000);

        List<String> userList = this.testSupportDemoFeignClient.testUnionBasedSqlInjection("' OR '1'='1' UNION SELECT null,col1, col2,null FROM secret_data -- ").getData();
        Assert.assertEquals(3, userList.size());
        Assert.assertEquals("secret-col1", userList.get(userList.size() - 1));

        List<String> usernameList =
                this.testSupportDemoFeignClient.testMybatisPlusSqlInjection("'' OR 1=1").getData();
        Assert.assertEquals(2, usernameList.size());
        Assert.assertEquals("user1", usernameList.get(0));
        Assert.assertEquals("user2", usernameList.get(1));

        usernameList = this.testSupportDemoFeignClient.testMybatisPlusWhereInSqlInjection(Collections.singletonList("1) OR 1=1 -- ")).getData();
        Assert.assertEquals(2, usernameList.size());
        Assert.assertEquals("user1", usernameList.get(0));
        Assert.assertEquals("user2", usernameList.get(1));
    }

}
