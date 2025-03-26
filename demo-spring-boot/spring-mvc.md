# `mvc`

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mvc`



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



## mvc 项目的创建和配置



### 基于 xml 配置

>`https://springjava.com/spring-mvc/spring-mvc-with-xml-configuration-example/`

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-mvc-xml-based-config`

启用 tomcat

```bash
mvn tomcat7:run
```

访问`http://localhost:8080/`测试示例



### 基于 java 配置

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-mvc-java-based-config`

启用 tomcat

```bash
mvn tomcat7:run
```

访问`http://localhost:8080/`测试示例



## `@RequestMapping`路径配置中使用通配符`?`、`*`、`**`

```java
package com.future.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// ?：代表匹配任意一个字符
// *：代表匹配任意多个字符
// **：代表匹配任意多层路径
@Controller
public class DemoController {
    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    @ResponseBody
    @RequestMapping("/hell?")
    public String hello2() {
        return "Hell?!";
    }

    @ResponseBody
    @RequestMapping("/hell*")
    public String hello3() {
        return "Hell*!";
    }

    @ResponseBody
    @RequestMapping("/hello/**")
    public String hello4() {
        return "Hello/**!";
    }
}
```

```java
// region 测试路径中使用通配符

this.mockMvc.perform(get("/hello"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello!"));

// 测试路径中使用?符号
this.mockMvc.perform(get("/hella"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hell?!"));
this.mockMvc.perform(get("/hell1"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hell?!"));

// 测试路径中使用*符号
this.mockMvc.perform(get("/hell"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hell*!"));
this.mockMvc.perform(get("/hellaaaaa"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hell*!"));
this.mockMvc.perform(get("/hell11111"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hell*!"));

// 测试路径中使用**符号
this.mockMvc.perform(get("/hello/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello/**!"));
this.mockMvc.perform(get("/hello/1/2"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello/**!"));
this.mockMvc.perform(get("/hello/12/3/4/5"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello/**!"));

// endregion
```



## `@RequestMapping`的`method`、`params`、`headers`、`consumers`、`produces`请求限制

```java
package com.future.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// 测试@RequestMapping注解的限制
@Controller
public class RequestMappingLimitationController {
    // 限制请求方法，只允许POST请求
    @ResponseBody
    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    public String test1() {
        return "test1";
    }

    // 限制请求参数
    @ResponseBody
    @RequestMapping(value = "/test2", params = {"age=18", "username", "gender!=1"})
    public String test2() {
        return "test2";
    }

    // 限制请求头
    @ResponseBody
    @RequestMapping(value = "/test3", headers = {"age=18", "username"})
    public String test3() {
        return "test3";
    }

    // 限制请求体类型
    @ResponseBody
    @RequestMapping(value = "/test4", consumes = {"application/json"})
    public String test4() {
        return "test4";
    }

    // 限制响应体类型
    @ResponseBody
    @RequestMapping(value = "/test5", produces = {"text/html"})
    public String test5() {
        return "<h1>test5</h1>";
    }
}
```

```java
// region 测试@RequestMapping注解的限制

// 测试限制请求方法
this.mockMvc.perform(post("/test1"))
        .andExpect(status().isOk())
        .andExpect(content().string("test1"));
this.mockMvc.perform(get("/test1"))
        .andExpect(status().isMethodNotAllowed());

// 测试限制请求参数
this.mockMvc.perform(post("/test2")
                .queryParam("username", "").queryParam("age", "18"))
        .andExpect(status().isOk())
        .andExpect(content().string("test2"));
this.mockMvc.perform(post("/test2")
                .queryParam("username", "").queryParam("age", "19"))
        .andExpect(status().isBadRequest());
this.mockMvc.perform(post("/test2")
                .queryParam("age", "18"))
        .andExpect(status().isBadRequest());
this.mockMvc.perform(post("/test2")
                .queryParam("username", "").queryParam("age", "18").queryParam("gender", "1"))
        .andExpect(status().isBadRequest());

// 测试限制请求头
this.mockMvc.perform(post("/test3")
                .header("username", "").header("age", "18"))
        .andExpect(status().isOk())
        .andExpect(content().string("test3"));
this.mockMvc.perform(post("/test3")
                .header("username", "").header("age", "19"))
        .andExpect(status().isNotFound());
this.mockMvc.perform(post("/test3")
                .header("age", "18"))
        .andExpect(status().isNotFound());

// 测试请求体类型
this.mockMvc.perform(post("/test4")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("test4"));
this.mockMvc.perform(post("/test4"))
        .andExpect(status().isUnsupportedMediaType());

// 测试请求体类型
this.mockMvc.perform(post("/test5"))
        .andExpect(status().isOk())
        .andExpect(content().string("<h1>test5</h1>"));

// endregion
```



## 请求参数处理`@RequestParam`、`@RequestBody`、`@PathVariable`等

```java
package com.future.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// 测试请求参数
@RestController
@RequestMapping("/api/v1")
public class RequestController {
    // 测试@RequestParam
    @RequestMapping("test1")
    public String test1(@RequestParam(value = "name", required = false) String name,
                        @RequestParam(value = "age", defaultValue = "15") Integer age) {
        return "name=" + name + ",age=" + age;
    }

    // 测试pojo
    @RequestMapping("test2")
    public String test2(Person person) {
        return "name=" + person.getName() + ",age=" + person.getAge() + ",hobby=" + Arrays.toString(person.getHobby()) + ",address=" + person.getAddress().toString();
    }

    // 测试@RequestHeader
    @RequestMapping("test3")
    public String test3(@RequestHeader(value = "name") String name,
                        @RequestHeader(value = "age") Integer age) {
        return "name=" + name + ",age=" + age;
    }

    // 测试@CookieValue
    @RequestMapping("test4")
    public String test4(@CookieValue(value = "name") String name,
                        @CookieValue(value = "age", defaultValue = "15") Integer age) {
        return "name=" + name + ",age=" + age;
    }

    // 测试@RequestBody获取JSON请求体
    @RequestMapping("test5")
    public String test5(@RequestBody Person person) {
        return "name=" + person.getName() + ",age=" + person.getAge() + ",hobby=" + Arrays.toString(person.getHobby()) + ",address=" + person.getAddress().toString();
    }

    // 测试请求路径参数
    @RequestMapping("test7/{name}/{age}")
    public String test(@PathVariable(value = "name") String name, @PathVariable("age") int age) {
        return "name=" + name + ",age=" + age;
    }

    // 测试文件上传
    @RequestMapping("test6")
    public String test6(Person person
            , @RequestParam("fileList") MultipartFile[] fileList) {
        return "name=" + person.getName() + ",age=" + person.getAge() + ",hobby="
                + Arrays.toString(person.getHobby())
                + ",address=" + person.getAddress().toString()
                + ",fileList=" + Arrays.stream(fileList).map(f -> {
            try {
                return new String(f.getBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
```

```java
// region 测试请求参数

// 测试@RequestParam注解
String name = "Dexter";
Integer age = 18;
this.mockMvc.perform(get("/api/v1/test1").queryParam("name", name).queryParam("age", age + ""))
        .andExpect(status().isOk())
        .andExpect(content().string("name=" + name + ",age=" + age));
this.mockMvc.perform(get("/api/v1/test1").queryParam("name", name))
        .andExpect(status().isOk())
        .andExpect(content().string("name=" + name + ",age=15"));
this.mockMvc.perform(get("/api/v1/test1"))
        .andExpect(status().isOk())
        .andExpect(content().string("name=null" + ",age=15"));

// 测试pojo获取参数
this.mockMvc.perform(get("/api/v1/test2").queryParam("name", name).queryParam("age", age + "")
                .queryParam("hobby", "coding").queryParam("hobby", "读书")
                .queryParam("address.country", "中国").queryParam("address.city", "广州市"))
        .andExpect(status().isOk())
        .andExpect(content().string("name=" + name + ",age=" + age + ",hobby=[coding, 读书],address=Person.Address(city=广州市, country=中国)"));

// 测试@RequestHeader注解
this.mockMvc.perform(get("/api/v1/test3").header("name", name).header("age", age + ""))
        .andExpect(status().isOk())
        .andExpect(content().string("name=" + name + ",age=" + age));

// 测试@CookieValue注解
this.mockMvc.perform(get("/api/v1/test4").cookie(new Cookie("name", name)).cookie(new Cookie("age", age + "")))
        .andExpect(status().isOk())
        .andExpect(content().string("name=" + name + ",age=" + age));

// 测试@RequestBody注解
Person person = new Person();
person.setName("Dexter");
person.setAge(18);
person.setHobby(new String[]{"coding", "读书"});
person.setAddress(new Person.Address("广州市", "中国"));
this.mockMvc.perform(post("/api/v1/test5").contentType(MediaType.APPLICATION_JSON).content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(person)))
        .andExpect(status().isOk())
        .andExpect(content().string("name=" + name + ",age=" + age + ",hobby=[coding, 读书],address=Person.Address(city=广州市, country=中国)"));

// 测试请求路径参数
this.mockMvc.perform(get("/api/v1/test7/" + name + "/" + age))
        .andExpect(status().isOk())
        .andExpect(content().string("name=Dexter,age=18"));
this.mockMvc.perform(get("/api/v1/test7/ /" + age))
        .andExpect(status().isOk())
        .andExpect(content().string("name= ,age=18"));
this.mockMvc.perform(get("/api/v1/test7/" + name + "/0"))
        .andExpect(status().isOk())
        .andExpect(content().string("name=Dexter,age=0"));

// 测试文件上传
this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/test6")
                .file(new MockMultipartFile("fileList", "hello".getBytes()))
                .file(new MockMultipartFile("fileList", "hello1".getBytes()))
                .queryParam("name", name).queryParam("age", age + "")
                .queryParam("hobby", "coding").queryParam("hobby", "读书")
                .queryParam("address.country", "中国").queryParam("address.city", "广州市"))
        .andExpect(status().isOk())
        .andExpect(content().string("name=" + name + ",age=" + age + ",hobby=[coding, 读书],address=Person.Address(city=广州市, country=中国),fileList=[hello, hello1]"));

// endregion
```



## 响应处理`JSON`和文件下载响应

```java
package com.future.demo.controller;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// 测试响应体
@RestController
@RequestMapping("/api/v1/response")
public class ResponseController {

    // 测试JSON响应体
    @RequestMapping("/test1")
    public Person test1() {
        Person person = new Person();
        person.setName("张三");
        person.setAge(20);
        person.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        person.setAddress(new Person.Address("北京市", "海淀区"));
        return person;
    }

    // 测试文件下载响应体
    // HttpEntity代表整个请求体，其中包含了请求头和请求体
    // ResponseEntity代表整个响应体，其中包含了响应头和响应体
    @RequestMapping("/test2")
    public ResponseEntity<InputStreamResource> test2() throws IOException {
        ClassPathResource resource = new ClassPathResource("test.txt");
        InputStream inputStream = resource.getInputStream();

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        String filename = URLEncoder.encode("测试文件.txt", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(inputStream.available())
                .header("Content-Disposition", "attachment;filename=" + filename)
                .body(inputStreamResource);
    }
}
```

```java
// region 测试响应体

// 测试JSON响应体
this.mockMvc.perform(get("/api/v1/response/test1"))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"id\":null,\"name\":\"张三\",\"age\":20,\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}"));

// 测试下载文件
this.mockMvc.perform(get("/api/v1/response/test2"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello!"));

// endregion
```



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



### 示例

```java
package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.vo.request.PersonAddVo;
import com.future.demo.vo.request.PersonUpdateVo;
import com.future.demo.vo.response.PersonVo;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// 演示restful风格的控制器
// 演示spring框架的数据校验功能
@RestController
@RequestMapping("/api/v1/restful")
public class RestfulController {

    // 新增person
    @RequestMapping(value = "/person", method = RequestMethod.POST)
    public ObjectResponse<Person> add(@RequestBody @Validated PersonAddVo vo/* 使用vo接收前端提交的数据 */) {
        Person person = new Person();
        BeanUtils.copyProperties(vo, person);
        person.setId(10L);
        ObjectResponse<Person> response = new ObjectResponse<>();
        response.setData(person);
        return response;
    }

    // 根据id获取person
    @RequestMapping(value = "/person/{id}", method = RequestMethod.GET)
    public ObjectResponse<PersonVo> get(@PathVariable("id") Long id) {
        Person person = new Person();
        person.setId(id);
        person.setName("张三");
        person.setAge(18);
        person.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        person.setAddress(new Person.Address("北京市", "海淀区"));

        PersonVo vo = new PersonVo();
        BeanUtils.copyProperties(person, vo);

        ObjectResponse<PersonVo> response = new ObjectResponse<>();
        response.setData(vo);
        return response;
    }

    // 根据id更新person
    @RequestMapping(value = "/person", method = RequestMethod.PUT)
    public ObjectResponse<PersonVo> update(@RequestBody PersonUpdateVo vo) {
        PersonVo personVo = new PersonVo();
        BeanUtils.copyProperties(vo, personVo);
        ObjectResponse<PersonVo> response = new ObjectResponse<>();
        response.setData(personVo);
        return response;
    }

    // 根据id删除person
    @RequestMapping(value = "/person/{id}", method = RequestMethod.DELETE)
    public ObjectResponse<String> delete(@PathVariable("id") Long id) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功删除id=" + id);
        return response;
    }
}
```

```java
// region 测试restful api

// 新增person
PersonAddVo personAddVo = new PersonAddVo();
personAddVo.setName("张三");
personAddVo.setSex("男");
personAddVo.setSex1("男");
personAddVo.setAge(18);
personAddVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
this.mockMvc.perform(post("/api/v1/restful/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":{\"id\":10,\"name\":\"张三\",\"age\":18,\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}}"));

// 根据id获取person
this.mockMvc.perform(get("/api/v1/restful/person/2"))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":{\"id\":2,\"name\":\"张三\",\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}}"));

// 根据id更新person
PersonUpdateVo personUpdateVo = new PersonUpdateVo();
personUpdateVo.setId(10L);
personUpdateVo.setName("张三");
personUpdateVo.setAge(18);
personUpdateVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
personUpdateVo.setAddress(new Person.Address("北京市", "海淀区"));
this.mockMvc.perform(put("/api/v1/restful/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personUpdateVo)))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":{\"id\":10,\"name\":\"张三\",\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}}"));

// 根据id删除person
this.mockMvc.perform(delete("/api/v1/restful/person/11"))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"成功删除id=11\"}"));

// endregion
```



## 拦截器`HandlerInterceptor`用法

MyHandlerInterceptor 拦截器

```java
package com.future.demo.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Data
@Component
public class MyHandlerInterceptor implements HandlerInterceptor {

    boolean isPreHandle = false;
    boolean isPostHandle = false;
    boolean isAfterCompletion = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        isPreHandle = true;

        String preHandleReturnFalse = request.getParameter("preHandleReturnFalse");
        if (preHandleReturnFalse != null)
            return false;
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        isPostHandle = true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        isAfterCompletion = true;
    }

    public void reset() {
        isPreHandle = false;
        isPostHandle = false;
        isAfterCompletion = false;
    }

}
```

配置 MyHandlerInterceptor 拦截器拦截的 URL 路径部分

```java
package com.future.demo.config;

import com.future.demo.interceptor.MyHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer /* WebMvcConfigurer对spring mvc进行配置 */ {

    @Autowired
    MyHandlerInterceptor myHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myHandlerInterceptor)
                // 只对/hello路径进行拦截
                .addPathPatterns("/hello");
    }
}
```

测试拦截器

```java
// region 测试拦截器

// 拦截/hello请求
this.mockMvc.perform(get("/hello"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello!"));
Assertions.assertTrue(this.myHandlerInterceptor.isPreHandle());
Assertions.assertTrue(this.myHandlerInterceptor.isPostHandle());
Assertions.assertTrue(this.myHandlerInterceptor.isAfterCompletion());
this.myHandlerInterceptor.reset();
// 拦截/hello请求但preHandle返回false
this.mockMvc.perform(get("/hello").param("preHandleReturnFalse", "false"))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
Assertions.assertTrue(this.myHandlerInterceptor.isPreHandle());
Assertions.assertFalse(this.myHandlerInterceptor.isPostHandle());
Assertions.assertFalse(this.myHandlerInterceptor.isAfterCompletion());
this.myHandlerInterceptor.reset();
// 不拦截/hella请求
this.mockMvc.perform(get("/hella"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hell?!"));
Assertions.assertFalse(this.myHandlerInterceptor.isPreHandle());
Assertions.assertFalse(this.myHandlerInterceptor.isPostHandle());
Assertions.assertFalse(this.myHandlerInterceptor.isAfterCompletion());
this.myHandlerInterceptor.reset();

// endregion
```



## 异常处理`@ExceptionHandler`、`@ControllerAdvice`、`@RestControllerAdvice`用法

### 局部异常处理

```java
package com.future.demo.controller;


import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exception")
public class ExceptionController {

    // 抛出算数术异常
    @RequestMapping("test1")
    public ObjectResponse<String> throwArithmeticException() {
        throw new ArithmeticException("算数异常");
    }

    // 抛出空指针异常
    @RequestMapping("test2")
    public ObjectResponse<String> throwNullPointerException() {
        throw new NullPointerException("空指针异常");
    }

    // 抛出自定义异常
    @RequestMapping("test3")
    public ObjectResponse<String> throwBusinessException() throws BusinessException {
        throw new BusinessException("自定义异常");
    }

    // 抛出其他异常
    /*@RequestMapping("test4")
    public ObjectResponse<String> throwException() throws Exception {
        throw new Exception("其他异常");
    }*/

    // 本controller处理算数术异常
    @ExceptionHandler(ArithmeticException.class)
    public ObjectResponse<String> handleArithmeticException(ArithmeticException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(e.getMessage());
        return response;
    }

    // 处理其他没有被处理的异常
    /*@ExceptionHandler(Throwable.class)
    public ObjectResponse<String> handleThrowable(Throwable e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage("其他没有被处理的异常");
        return response;
    }*/
}
```

### 全局异常处理

```java
package com.future.demo.exceptions;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    // SpringBoot3需要以下配置处理NoResourceFoundException异常，https://github.com/spring-projects/spring-boot/issues/38733
    // 处理404不存在资源异常
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleNotFound(NoResourceFoundException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        String message = "资源 " + e.getResourcePath() + " 不存在！";
        response.setErrorMessage(message);
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // 处理空指针异常
    @ExceptionHandler(NullPointerException.class)
    public ObjectResponse<String> handleNullPointerException(NullPointerException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage("空指针异常");
        return response;
    }

    // 处理BusinessException异常
    @ExceptionHandler(BusinessException.class)
    public ObjectResponse<String> handleBusinessException(BusinessException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(e.getMessage());
        return response;
    }

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

### 异常处理测试

```java
// region 测试异常处理

// 测试算数异常
this.mockMvc.perform(get("/api/v1/exception/test1"))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"算数异常\",\"data\":null}"));
// 测试空指针异常
this.mockMvc.perform(get("/api/v1/exception/test2"))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"空指针异常\",\"data\":null}"));
// 测试自定义异常
this.mockMvc.perform(get("/api/v1/exception/test3"))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"自定义异常\",\"data\":null}"));

// endregion
```



### SpringBoot2 特别提醒

在处理 SpringBoot2 404 异常时需要特殊配置，否则 Spring 框架不会抛出 NoHandlerFoundException

application.properties 加入如下配置：

```properties
# 设置下面两个选项才能启用在404情况下抛出NoHandlerFoundException
# https://stackoverflow.com/questions/54116245/404-exception-not-handled-in-spring-controlleradvice
# https://blog.csdn.net/INHERO/article/details/121531224
spring.mvc.throw-exception-if-no-handler-found=true
spring.resources.add-mappings=false
```

404 全局异常处理逻辑

```java
@ExceptionHandler(NoHandlerFoundException.class)
@ResponseBody
public ResponseEntity<ObjectResponse<String>> handleNotFound(NoHandlerFoundException e) {
    ObjectResponse<String> response = new ObjectResponse<>();
    String message = "资源 " + e.getRequestURL() + " 不存在！";
    response.setErrorMessage(message);
    response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
}
```



## 数据校验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/master/demo-spring-boot/demo-spring-boot-mvc/src/main/java/com/future/demo/controller/RestfulController.java)



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

数组校验

```java
@Data
public class PersonAddVo {
    @NotNull(message = "请指定爱好")
    @Size(min = 1, message = "请指定爱好")
    private String[] hobby;
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



### 方法参数校验

>提醒：方法参数校验需要在方法所属的类上添加 @Validated 注解，否则校验注解不起作用。

例子1：

```java
@RestController
@RequestMapping(value = "/api/v1")
// 注意：方法参数校验必须在类中添加 @Validated 注解，否则校验注解不生效
@Validated
public class ValidationController {
    @GetMapping(value = "testSingleParam")
    public ObjectResponse<String> testSingleParam(
            @NotNull(message = "{param.required.p1}")
            @NotBlank(message = "{param.required.p1}")
            @RequestParam(value = "p1", defaultValue = "") String p1) {
        return ResponseUtils.successObject("成功调用");
    }
}
```

例子2：

```java
@Service
// 注意：方法参数校验必须在类中添加 @Validated 注解，否则校验注解不生效
@Validated
public class ValidationService {
    public void testSingleParam(
            @NotNull(message = "{param.required.p1}")
            @NotBlank(message = "{param.required.p1}") String p1) {
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



### 校验失败异常处理



#### MethodArgumentNotValidException

```java
// 处理spring数据校验失败异常
@ExceptionHandler(MethodArgumentNotValidException.class)
public ObjectResponse<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    ObjectResponse<Map<String, String>> response = new ObjectResponse<>();
    response.setErrorMessage("参数校验失败");
    Map<String, String> map = e.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    response.setData(map);
    return response;
}
```



#### MissingServletRequestParameterException

```java
// 处理MissingServletRequestParameterException
@ExceptionHandler(MissingServletRequestParameterException.class)
public @ResponseBody
ResponseEntity<ObjectResponse<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
    String message = "缺失参数 \"" + e.getParameterName() + "\"！";
    response.setErrorMessage(message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
}
```



#### MethodArgumentTypeMismatchException

```java
// 处理MethodArgumentTypeMismatchException
@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public @ResponseBody
ResponseEntity<ObjectResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
    String message = "参数 " + e.getName() + " 值: " + String.valueOf(e.getValue()) + " 类型不匹配！";
    response.setErrorMessage(message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
}
```



#### ConstraintViolationException

```java
@ExceptionHandler(ConstraintViolationException.class)
public @ResponseBody
ResponseEntity<ObjectResponse<String>> handleConstraintViolationException(ConstraintViolationException e) {
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
    String message = e.getConstraintViolations().iterator().next().getMessage();

    if(log.isDebugEnabled())
        log.debug("参数校验失败，message={}", message);

    response.setErrorMessage(message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
}
```



## 跨域配置

### 局部配置

使用 @CrossOrigin 注解指定 controller 下所有接口支持跨域

```java
@Controller
// controller 下所有接口都支持跨域请求，并且只允许 abc.com 域名下的前端进行访问
@CrossOrigin(origins = "abc.com")
public class DemoController {
```

使用 curl 测试跨域

- 成功跨域访问

  ```bash
  curl -H "Origin: abc.com" --verbose  http://localhost:8080/hello
  ```

- 失败跨域访问，响应 403 错误和 Invalid CORS request 错误信息

  ```bash
  curl -H "Origin: abc1.com" --verbose  http://localhost:8080/hello
  ```



使用 @CrossOrigin 注解指定单个接口支持跨域

```java
// 当前接口支持跨域请求，并且只允许 abc.com 域名下的前端进行访问
@CrossOrigin(origins = "abc.com")
@ResponseBody
@RequestMapping("/hello")
public String hello() {
    return "Hello!";
}
```

使用 curl 测试跨域

- 成功跨域访问

  ```bash
  curl -H "Origin: abc.com" --verbose  http://localhost:8080/hello
  ```

- 失败跨域访问，响应 403 错误和 Invalid CORS request 错误信息

  ```bash
  curl -H "Origin: abc1.com" --verbose  http://localhost:8080/hello
  ```



### 全局配置

>`https://stackoverflow.com/questions/51720552/enabling-cors-globally-in-spring-boot`

java 配置

```java
// 全局跨域配置
@Bean
public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    // 允许跨域携带cookie
    config.setAllowCredentials(true);
    // 只允许 abc.com 跨域访问
    config.setAllowedOrigins(Collections.singletonList("abc.com"));
    config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
    // 所有路径都允许跨域访问
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
}
```

使用 curl 测试跨域

- 成功跨域访问

  ```bash
  curl -H "Origin: abc.com" --verbose  http://localhost:8080/hello
  ```

- 失败跨域访问，响应 403 错误和 Invalid CORS request 错误信息

  ```bash
  curl -H "Origin: abc1.com" --verbose  http://localhost:8080/hello
  ```

  

