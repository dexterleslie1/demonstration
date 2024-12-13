# `spring-boot`项目的测试

>`https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/integration-testing.html`



## `web`环境测试

### 使用`openfeign`进行`web`环境测试

> 案例的详细请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-openfeign-client`

注意：

- 目前项目中使用这种方法+`mock`技术实现各个微服务独立的集成测试。
- 建议使用 mockmvc 替代此测试方案。

`pom.xml`配置引用`openfeign`依赖如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.7.RELEASE</version>
        <relativePath/>
    </parent>

    <dependencies>
        <!-- 从jitpack中引用自定义工具依赖构件 -->
        <dependency>
            <groupId>com.github.dexterleslie1</groupId>
            <artifactId>future-common</artifactId>
            <version>1.0.1</version>
        </dependency>
        
        <!-- 用于测试的openfeign依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <!-- 引用spring-cloud-starter-openfeign必须的依赖 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <!--
                    spring-cloud-dependencies的版本需要和spring-boot-starter-parent的版本兼容，
                    否则启动测试时候会报错
                 -->
                <version>Hoxton.SR10</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <!-- jitpack公共仓库，用于引用自定义构件 -->
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
</project>

```

在测试目录中创建`TestSupportConfiguration.java`配置`openfeign`

```java
import com.future.common.feign.CustomizeErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        clients = {
                TestSupportApiFeign.class
        }
)
public class TestSupportConfiguration {
        /**
         * openfeign支持自动检查并抛出业务异常不需要编写代码判断errorCode是否不等于0
         *
         * @return
         */
        @Bean
        ErrorDecoder errorDecoder() {
                return new CustomizeErrorDecoder();
        }
}
```

在测试`resources`目录中创建`application-test.properties`配置用于配置`openfeign`

```properties
# 配置openfeign中服务的ip地址
app-test-service.ribbon.NIWSServerListClassName=com.netflix.loadbalancer.ConfigurationBasedServerList
app-test-service.ribbon.listOfServers=127.0.0.1:18080

# 配置feign超时时间为15秒
ribbon.ConnectTimeout=15000
ribbon.ReadTimeout=15000

```

创建用于测试业务的`feign`客户端`TestSupportApiFeign.java`

```java
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        contextId = "testSupportApiFeign",
        value = "app-test-service",
        path = "/api/v1")
public interface TestSupportApiFeign {

    @GetMapping("test401Error")
    public ObjectResponse<String> test401Error() throws BusinessException;

}
```

创建测试用例`ApiTests.java`

```java
import com.future.common.exception.BusinessException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ApiTests {

    @Resource
    TestSupportApiFeign testSupportApiFeign;

    @Test
    public void test401Error() {
        try {
            this.testSupportApiFeign.test401Error();
            Assert.fail("没有抛出预期异常");
        } catch (BusinessException ex) {
            Assert.assertEquals(90000, ex.getErrorCode());
            Assert.assertEquals("调用 /api/v1/test401Error 失败", ex.getErrorMessage());
        }
    }

}

```



## 集成测试或单元测试



### `mockmvc`测试

> 案例的详细请参考`https://gitee.com/dexterleslie/demonstration/blob/master/demo-spring-boot/demo-spring-boot-test/src/test/java/com/future/demo/test/MockMvcTests.java`

`MockMvc` 是 Spring Framework 提供的一个用于测试 Spring MVC 控制器（Controller）的类。它允许你以编程的方式执行 HTTP 请求，并验证返回的结果。这对于在开发过程中编写单元测试或集成测试非常有用，因为它不需要启动一个完整的 HTTP 服务器。

在`pom`中引入测试依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

在测试类`MockMvcTests`中添加`@AutoConfigureMockMvc`启用`mockmvc`测试。

在测试类`MockMvcTests`中添加如下代码注入`MockMvc`实例

```java
@Resource
private MockMvc mockMvc;
```

使用`MockMvc`实例调用`/api/v1/addUser`接口

```java
ResultActions response = mockMvc.perform(get("/api/v1/add")
        .queryParam("a", "1")
        .queryParam("b", "2")
        // 注入一个随机token就模拟已经登录
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED));
response.andExpect(status().isOk())
        .andExpect(jsonPath("$.data", is(3)));
```

完整的测试用例`MockMvcTests`如下：

```java
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.Application;
import com.future.demo.TestService;
import com.future.demo.UserModel;
import com.future.demo.mapper.UserMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// https://stackoverflow.com/questions/42249791/resolving-port-already-in-use-in-a-spring-boot-test-defined-port
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
// 启用mockmvc测试
@AutoConfigureMockMvc
public class ControllerTests {

    @Resource
    UserMapper userMapper;
    @SpyBean
    TestService testService;
    @Resource
    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {
        // 场景: 测试spybean使用原来的逻辑
        ResultActions response = mockMvc.perform(get("/api/v1/add")
                .queryParam("a", "1")
                .queryParam("b", "2")
                // 注入一个随机token就模拟已经登录
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(3)));

        // 场景: 测试spybean使用被mock后指定的规则
        Mockito.doReturn(5).when(this.testService).add(Mockito.anyInt(), Mockito.anyInt());
        response = mockMvc.perform(get("/api/v1/add")
                .queryParam("a", "1")
                .queryParam("b", "2")
                // 注入一个随机token就模拟已经登录
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(5)));

        // 场景: 测试没有被mock
        response = mockMvc.perform(get("/api/v1/minus")
                .queryParam("a", "1")
                .queryParam("b", "2")
                // 注入一个随机token就模拟已经登录
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(-1)));

        // 场景: 测试spring-security在mockmvc测试中是否生效，不提供token预期报错
        response = mockMvc.perform(get("/api/v1/minus")
                .queryParam("a", "1")
                .queryParam("b", "2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isForbidden());

        // 场景: 测试集成mybatis-plus测试，查看是否正确加载mybatis-plus
        this.userMapper.delete(Wrappers.query());
        response = mockMvc.perform(post("/api/v1/addUser")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString()));
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", CoreMatchers.is("成功创建用户")));
        UserModel userModel = this.userMapper.selectList(Wrappers.query()).get(0);
        Assert.assertEquals("中文测试", userModel.getName());
        Assert.assertEquals("dexterleslie@gmail.com", userModel.getEmail());
    }

}

```



### `service`单元测试

>案例的详细请参考`https://gitee.com/dexterleslie/demonstration/blob/master/demo-spring-boot/demo-spring-boot-test/src/test/java/com/future/demo/test/ServiceTests.java`

在Spring Boot中，对服务层（Service）进行单元测试是一个常见的做法，以确保业务逻辑的正确性。这通常涉及到模拟（Mock）依赖项，如数据访问对象（DAO）或外部服务，以隔离正在测试的服务层逻辑。

`ServiceTests`测试用例内容如下：

```java
import com.future.demo.Application;
import com.future.demo.TestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

// https://stackoverflow.com/questions/42249791/resolving-port-already-in-use-in-a-spring-boot-test-defined-port
@DirtiesContext
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ServiceTests {

    @SpyBean
    TestService testService;

    @Test
    public void test() {
        int c = this.testService.add(1, 2);
        Assert.assertEquals(3, c);

        Mockito.when(this.testService.add(1, 2)).thenReturn(5);
        c = this.testService.add(1, 2);
        Assert.assertEquals(5, c);
    }
}
```

