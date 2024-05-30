# `spring-boot`项目的测试

## `web`环境测试

### 使用`openfeign`进行`web`环境测试

> 案例的详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-openfeign-client)

`pom.xml`配置引用`openfeign`依赖如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    ...

    <dependencies>
        <!-- 从jitpack中引用自定义工具依赖构件 -->
        <dependency>
            <groupId>com.github.dexterleslie1</groupId>
            <artifactId>future-common</artifactId>
            <version>1.0.1</version>
        </dependency>
        
        <!-- openfeign 依赖 -->
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



## 集成测试（Integration Testing）