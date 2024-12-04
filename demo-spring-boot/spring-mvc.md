# `mvc`



## `mvc`是什么？

Spring MVC是一个基于Java的开源Web应用程序框架，它是Spring框架的一个重要组成部分，提供了一种模型-视图-控制器（MVC）架构模式来构建灵活、可扩展的Web应用程序。以下是对Spring MVC的详细解释：

**一、MVC架构模式**

MVC是一种软件设计模式，它将应用程序分为三个主要部分：模型（Model）、视图（View）和控制器（Controller）。

1. **模型（Model）**：表示应用程序的数据和业务逻辑。在Spring MVC中，模型可以是一个简单的Java对象，也可以是一个复杂的数据结构。它负责与数据库交互，处理数据的增删改查操作，并将结果传递给视图。
2. **视图（View）**：用于呈现数据给用户。它是用户界面的呈现层，负责将模型中的数据渲染成HTML、JSON、XML等格式，并向用户展示。Spring MVC支持多种视图技术，包括JSP、Thymeleaf、Freemarker等。
3. **控制器（Controller）**：处理用户的请求并相应地更新模型和视图。在Spring MVC中，控制器是应用程序的中心处理器，负责接收用户的请求，调用模型来处理业务逻辑，并选择适当的视图来展示结果。

**二、Spring MVC的核心组件**

1. **DispatcherServlet**：作为前端控制器，是Spring MVC的核心组件。它负责接收所有进入的HTTP请求，并根据请求的信息（如URL）来查找对应的处理器（Controller）。DispatcherServlet不直接处理请求，而是将请求分发给适当的控制器进行处理。
2. **HandlerMapping**：根据请求的URL或其他信息，查找并确定处理该请求的Controller。HandlerMapping可以配置多种映射策略，如基于注解的映射、基于XML配置的映射等。
3. **HandlerAdapter**：由于Spring MVC支持多种类型的处理器（Controller），因此需要一个适配器来调用这些处理器。HandlerAdapter根据处理器的类型（如基于接口的Controller、基于注解的Controller等），调用相应的处理器来处理请求。
4. **ViewResolver**：根据视图名称解析出具体的视图对象（View）。这个视图对象可以是JSP、HTML、PDF等任何类型。视图对象使用Model中的数据进行渲染，生成最终的HTML或其他格式的响应内容。

**三、Spring MVC的工作原理**

Spring MVC的工作原理可以概括为以下几个核心步骤：

1. 用户通过浏览器或其他客户端发送HTTP请求到Web服务器。
2. DispatcherServlet接收所有进入的HTTP请求，并根据请求的信息（如URL）来查找对应的处理器（Controller）。
3. HandlerMapping查找并确定处理该请求的Controller，并返回一个包含Controller和相关拦截器（如果有的话）的HandlerExecutionChain对象给DispatcherServlet。
4. HandlerAdapter根据处理器的类型调用相应的处理器来处理请求。
5. 处理器执行具体的业务逻辑，处理用户请求，并返回一个ModelAndView对象。ModelAndView包含了模型数据（Model）和视图名称（View Name），用于后续的视图渲染。
6. DispatcherServlet将ModelAndView对象传递给ViewResolver。
7. ViewResolver根据视图名称解析出具体的视图对象（View）。
8. 视图对象使用Model中的数据进行渲染，生成最终的HTML或其他格式的响应内容。
9. DispatcherServlet将渲染后的内容返回给客户端（如浏览器），作为HTTP响应的body部分。

**四、Spring MVC的优点**

1. **模块化设计和松耦合的架构**：使得开发人员能够轻松地扩展和定制应用程序，提高开发效率和代码的可维护性。
2. **支持多种视图技术**：如JSP、Thymeleaf、Freemarker等，为开发人员提供了更多的选择。
3. **国际化和本地化支持**：使开发人员能够根据用户的语言和地区提供不同的视图和消息。
4. **RESTful Web服务支持**：使开发人员能够轻松地构建和管理RESTful API。
5. **丰富的测试支持**：包括单元测试、集成测试和端到端测试，确保应用程序的质量和稳定性。

综上所述，Spring MVC是一个功能强大、灵活可扩展的Web应用程序框架，它充分利用了Java的优点和MVC架构模式的优势，为开发人员提供了丰富的功能和特性。



## 知识点

>详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mvc`

- `@RequestMapping`路径配置中使用通配符`?`、`*`、`**`
- `@RequestMapping`的`method`、`params`、`headers`、`consumers`、`produces`请求限制
- 请求参数处理`@RequestParam`、`@RequestBody`、`@PathVariable`等
- 响应处理`JSON`和文件下载响应
- 编写`restful`风格的`api`
- 拦截器`HandlerInterceptor`用法
- 异常处理`@ExceptionHandler`、`@ControllerAdvice`、`@RestControllerAdvice`用法
- 数据校验



## `restful`风格的`api`

`api http method`设计规则如下：

- 在新增数据时使用`POST`方法
- 在读取数据时使用`GET`方法
- 在更新数据时使用`PUT`方法
- 在删除数据时使用`DELETE`方法。

`api`的`URL`路径部分设计规则如下，本示例中：

- 新增`person`的`api`被设计为`POST /person`
- 更新`person`的`api`被设计为`PUT /person`
- 根据`id`查询`person`的`api`被设计为`GET /person/{id}`
- 根据`id`删除`person`的`api`被设计为`DELETE /person/{id}`
- 查询`person`列表的`api`被设计为`GET /persons`

`api`和前端数据交互设计规则如下：

- 新增`person`时前端提交的`vo`为`PersonAddVo`
- 修改`person`时前端提交的`vo`为`PersonUpdateVo`
- 后端返回`person`数据给前端时的`vo`为`PersonVo`



## 数据校验



### 基本用法

`maven`引入`org.springframework.boot:spring-boot-starter-validation`依赖

```xml
<dependencies>
	<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

`PersonAddVo`添加校验注解

```java
// 新增person vo
@Data
public class PersonAddVo {

    // 使用@NotBlank注解，表示name字段不能为null，且不能为空白字符串
    // {person.name.required}是为了国际化，具体的错误信息可以通过配置文件来配置
    @NotBlank(message = "{person.name.required}")
    private String name;

    @Max(value = 150, message = "年龄不能大于150")
    @Min(value = 0, message = "年龄不能小于0")
    private int age;
}
```

控制器添加`@Validated`注解启用数据校验

```java
@RequestMapping(value = "/person", method = RequestMethod.POST)
public ObjectResponse<Person> add(@RequestBody @Validated PersonAddVo vo/* 使用vo接收前端提交的数据 */) {
```

全局异常处理添加数据校验失败异常`MethodArgumentNotValidException`处理

```java
// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理spring数据校验失败异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ObjectResponse<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ObjectResponse<Map<String, String>> response = new ObjectResponse<>();
        response.setErrorMessage("参数校验失败");
        Map<String, String> map = e.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        response.setData(map);
        return response;
    }
}
```



### 校验失败国际化处理

`PersonAddVo`添加国际化消息`key`，如：`@NotBlank(message = "{person.name.required}")`

```java
// 新增person vo
@Data
public class PersonAddVo {
    // 使用@NotBlank注解，表示name字段不能为null，且不能为空白字符串
    // {person.name.required}是为了国际化，具体的错误信息可以通过配置文件来配置
    @NotBlank(message = "{person.name.required}")
    private String name;
}
```

新建`i18n`国际化文件：

`messages.properties`默认语言（当前没有配置的语言）文件内容如下：

```properties
person.name.required=name is required

```

`messages_en_US.properties`英语配置文件内容如下：

```properties
person.name.required=name is required

```

`messages_zh_CN.properties`中文配置文件内容如下：

```properties
person.name.required=名称必须

```

请求头加入`Accept-Language`表示客户端当前语言，例如：`Accept-Language: en-US`表示英语，`Accept-Language: zh-CN`表示中文

```java
// 中文语言
person = new Person();
person.setId(10L);
person.setName(" ");
person.setAge(-1);
person.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
person.setAddress(new Person.Address("北京市", "海淀区"));
this.mockMvc.perform(post("/api/v1/restful/person")
                .header("Accept-Language", "zh-CN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(person)))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"参数校验失败\",\"data\":{\"name\":\"名称必须\",\"age\":\"年龄不能小于0\"}}"));

```



### `Pattern`校验

使用正则表达式校验`person`性别`男`或者`女`

```java
@Pattern(regexp = "^[男|女]$", message = "性别只能是男或者女")
private String sex;
```



### 自定义校验注解

自定义注解类

```java
package com.future.demo.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {GenderValidator.class})
@Target({FIELD})
@Retention(RUNTIME)
public @interface Gender {
    // 国际化对应的key
    String message() default "{person.gender.error}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

```

自定义校验器

```java
package com.future.demo.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<Gender, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return "男".equals(value) || "女".equals(value);
    }
}

```

`i18n`配置如下：

```properties
# messages.properties配置如下：
person.gender.error=Gender is male or female

# messages_en_US.properties配置如下：
person.gender.error=Gender is male or female

# messages_zh_CN.properties配置如下：
person.gender.error=性别只能是男或者女
```

引用自定义注解

```java
@Gender
private String sex1;
```

