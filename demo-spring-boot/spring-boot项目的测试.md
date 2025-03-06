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



### 使用 RestTemplate 测试

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-resttemplate`



#### JSON 响应转换为 Java 对象

```java
// 测试 JSON 响应转换为 Java 对象
ResponseEntity<ObjectResponse<Map<String, Object>>> response = this.restTemplate.exchange(this.getBasePath() + "/api/v1/test1", HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<Map<String, Object>>>() {
});
Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
ObjectResponse<Map<String, Object>> objectResponse = response.getBody();
Assert.assertNotNull(objectResponse);
Assert.assertTrue(objectResponse.getData().containsKey("k1"));
Assert.assertEquals("v1", objectResponse.getData().get("k1"));
```



### 使用 Rest Assured 测试

详细用法请参考 <a href="/java-library/rest-assured.html" target="_target">链接</a>



### Rest Assured 和 RestTemplate 比较

在选择 Rest Assured 和 RestTemplate 时，需要根据具体的使用场景和需求来决定。以下是两者的比较和一些建议：

**RestTemplate**

1. 用途：
   - RestTemplate 是 Spring 框架提供的一个用于访问 RESTful 服务的客户端。
   - 它主要用于服务之间的同步调用，可以发送 HTTP 请求并处理响应。
2. 优点：
   - 支持连接池、超时时间设置等高级配置。
   - 提供了丰富的请求和响应处理功能。
3. 缺点：
   - 编写请求方法相对繁琐，容易出错。
   - 不适合大规模服务调用的场景，可能导致服务调用链路阻塞。
4. 适用场景：
   - 适用于服务之间的简单同步调用。
   - 当需要精细控制 HTTP 请求和响应时，RestTemplate 是一个不错的选择。

**Rest Assured**

1. 用途：
   - Rest Assured 是一个 Java DSL（领域特定语言），用于简化基于 HTTP 的服务的测试。
   - 它主要用于自动化测试，特别是接口自动化测试。
2. 优点：
   - 简化了基于 HTTP 的服务的测试过程。
   - 支持多种 HTTP 请求方法（如 POST、GET、PUT、DELETE 等）。
   - 提供了丰富的断言和验证功能，便于测试响应数据。
3. 缺点：
   - 主要用于测试，而不是生产环境中的服务调用。
   - 相对于 RestTemplate，它在服务调用方面的功能较为有限。
4. 适用场景：
   - 适用于接口自动化测试。
   - 当需要验证 HTTP 服务的响应数据时，Rest Assured 是一个强大的工具。

**选择建议**

- 如果你需要进行服务之间的同步调用：
  - 首选 RestTemplate，因为它提供了丰富的请求和响应处理功能，并且与 Spring 框架紧密集成。
- 如果你需要进行接口自动化测试：
  - 首选 Rest Assured，因为它简化了测试过程，并提供了丰富的断言和验证功能。
- 综合考虑：
  - 如果你的项目既需要服务调用又需要接口测试，可以考虑同时使用 RestTemplate 和 Rest Assured。
  - RestTemplate 用于生产环境中的服务调用，而 Rest Assured 用于测试环境中的接口测试。

总之，RestTemplate 和 Rest Assured 各有优势和适用场景。在选择时，需要根据具体的使用需求、项目规模和测试要求来决定。



## 集成测试或单元测试



### `mockmvc`测试

> 案例的详细请参考`https://gitee.com/dexterleslie/demonstration/blob/master/demo-spring-boot/demo-spring-boot-test/src/test/java/com/future/demo/test/MockMvcTests.java`

注意：mockmvc 不使用正在运行的 Servlet 容器。

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



## MockMvc 测试从 JSON 返回中读取数据

>[stack overflow 从 JSON 返回中读取数据](https://stackoverflow.com/questions/47763332/how-to-extract-value-from-json-response-when-using-spring-mockmvc)

```java
// MockMvc 读取 JSON 字符串内容
response = mockMvc.perform(get("/api/v1/getUser")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString()))
        .andExpect(status().isOk());
// https://stackoverflow.com/questions/47763332/how-to-extract-value-from-json-response-when-using-spring-mockmvc
String email = JsonPath.read(response.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.email");
Assert.assertEquals("dexterleslie@gmail.com", email);
```
