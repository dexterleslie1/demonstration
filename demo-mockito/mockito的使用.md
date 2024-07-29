# `mockito`的使用

## `@RunWith(MockitoJUnitRunner.class)`使用

> 详细使用请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-mockito/demo-mockito-basic/src/test/java/com/future/demo/AnnotationMock1Test.java)
>
> `@RunWith(MockitoJUnitRunner.class)` 是 Mockito 和 JUnit 结合使用的一个注解，它主要用于初始化 Mockito 的环境，以便在 JUnit 测试中更方便地使用 Mockito。
>
> 具体来说，`MockitoJUnitRunner` 的作用有以下几点：
>
> 1. **自动初始化 Mock 对象**：当你使用 `@Mock` 注解一个接口或类时，`MockitoJUnitRunner` 会在测试运行前自动创建该接口的 Mock 对象，并将其注入到测试类中。这样你就不需要手动调用 `Mockito.mock()` 方法来创建 Mock 对象了。
> 2. **自动注入 Mock 对象**：如果你的测试类中有一些字段被标记为 `@Mock` 或 `@InjectMocks`，`MockitoJUnitRunner` 会自动将这些字段注入到测试类的实例中。例如，使用 `@InjectMocks` 注解的字段会自动将所有标记为 `@Mock` 的字段注入进去。

使用例子：

```java

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

// 自动扫描@Mock注解的字段并创建其对象注入到测试实例中
// 不需要手动调用Mockito.mock()方法来创建 Mock 对象
@RunWith(MockitoJUnitRunner.class)
public class AnnotationMock1Test {
	@Mock
	private List mockListObject;
	
	@Test
	public void verify_if_function_called(){
		mockListObject.add("val1");
		Mockito.verify(mockListObject).add("val1");
	}
}

```

## `@Mock + MockitoAnnotations.initMocks()`使用

>详细使用请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-mockito/demo-mockito-basic/src/test/java/com/future/demo/AnnotationMock2Test.java)
>
>`MockitoAnnotations.initMocks`用于手动创建`@Mock`注解的字段对象并注入到测试实例中

使用例子：

```java
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AnnotationMock2Test {
	@Mock
	private List mockListObject;
	
	@Before
	public void setup(){
		// 解析@Mock注解，否则mockListObject对象为null
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void verify_if_function_called(){
		mockListObject.add("val1");
		Mockito.verify(mockListObject).add("val1");
	}
}

```

## 匹配指定的参数值

> 匹配指定的方法调用参数

使用例子：

```java
@Test
public void test_specific_parameters_match(){
    List mockListObject=Mockito.mock(List.class);
    Mockito.when(mockListObject.add("val1")).thenReturn(false);
    Mockito.when(mockListObject.add("val2")).thenReturn(true);
    Assert.assertFalse(mockListObject.add("val1"));
    Assert.assertTrue(mockListObject.add("val2"));
}
```

## 匹配指定参数类型的任意值

> 匹配指定参数类型的任意值方法调用

使用例子：

```java
@Test
public void test_any_parameters_value_match(){
    List mockListObject=Mockito.mock(List.class);
    Mockito.when(mockListObject.add(Mockito.anyInt())).thenReturn(true);
    for(int i=0;i<1000;i++){
        Assert.assertTrue(mockListObject.add(i));
    }
}
```

## 使用`ArgumentMatcher`匹配指定参数

> todo ...

## 调用`verify`验证时匹配调用参数

> todo ...

## `ArgumentCaptor`使用

> todo ...

## 测试`mock`对象是否已调用指定函数

> todo ...

## 模拟抛出异常

> todo ...

## `Answer`使用

> todo ...

## 验证调用次数

> todo ...

## 执行顺序

> todo ...

## 真实对象`spy`

> todo ...

## 真实对象部分`mock`

> todo ...

## 静态`log`字段注入替换为`mock logger`

> 详细使用请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-mockito/demo-mockito-springboot/src/test/java/com/future/demo/StaticLoggerFieldInjectionTests.java)
>
> 外部参考链接：
>
> - [mock-private-static-final-field-using-mockito-or-jmockit](https://stackoverflow.com/questions/30703149/mock-private-static-final-field-using-mockito-or-jmockit)
> - [what-is-the-best-way-to-unit-test-slf4j-log-messages](https://stackoverflow.com/questions/4650222/what-is-the-best-way-to-unit-test-slf4j-log-messages/60988775#60988775)
>
> 替换通过`lombok @Slf4j`注解注入的`log`字段为`mock logger`

例子：

```java
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes={Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class StaticLoggerFieldInjectionTests {
    @LocalServerPort
    int port;

    // 用于注入mock的logger
    @Mock
    Logger log;

    // 用于替换通过lombok @Slf4j注入的静态log字段
    @Autowired
    ApiController apiController;

    @Autowired
    private RestTemplate restTemplate = null;

    @Before
    public void setup() throws Exception {
        // 初始化@Mock注解的字段
        MockitoAnnotations.initMocks(this);
        // 替换ApiController对象中的静态log为mock logger
        setFinalStatic(ApiController.class.getDeclaredField("log"), log);
    }

    // 替换静态字段
    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @Test
    public void test1() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:"+ port + "/api/test1",
                String.class);
        Assert.assertEquals("Hello ....", response.getBody());

        // 用于验证是否使用指定的参数调用log.info(...)方法
        Mockito.verify(log).info("Api for testing is called.");
    }

}

```

## @MockBean

> 自动创建 mock bean 并注入到 spring 容器中，自动替换所有同类型的 bean
>
> 详细使用请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-mockito/demo-mockito-springboot/src/test/java/com/future/demo/MockBeanTests.java)

## @InjectMocks + @Mock

> 使用以上组合注入 @Mock 注解生成的 bean 到 @InjectMocks bean中
>
> 如果允许情况下建议使用 @MockBean 替换这个使用组合
>
> 详细使用请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-mockito/demo-mockito-springboot/src/test/java/com/future/demo/InjectMocksNMockTests.java)

## @InjectMocks + @Spy

> 和 @InjectMocks + @Mock 区别是，如果没有定义 mock 规则(Mockito.doReturn("param2=p2").when(this.myServiceInner).test2(Mockito.anyString());) 则  @Spy 注入的 bean 会执行原来没有被 mock 的代码逻辑(实现一个实例部分接口被 mock的目的)，而 @Mock 注入的 bean 没有定义 mock  规则只会返回默认值(String类型返回返回值为null，int类型返回返回值为0)。
>
> 如果允许情况下建议使用 @SpyBean 替换这个使用组合
>
> 详细使用请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-mockito/demo-mockito-springboot/src/test/java/com/future/demo/InjectMocksNSpyTests.java)

## @SpyBean

> 自动创建 mock bean 并注入到 spring 容器中，自动替换所有同类型的 bean，没有被定义 mock 规则的方法默认执行原始逻辑。
>
> 详细使用请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-mockito/demo-mockito-springboot/src/test/java/com/future/demo/SpyBeanTests.java)