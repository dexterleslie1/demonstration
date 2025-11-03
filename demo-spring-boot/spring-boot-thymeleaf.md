## 概念

### 一句话概括

**Thymeleaf** 是一个用于 Web 和独立环境的现代服务器端 Java 模板引擎。它的主要目标是为你的工作流程带来优雅的**自然模板**——可以在浏览器中直接显示的原型模板，也能作为正式的工作模板。

---

### 核心概念：什么是模板引擎？

在深入 Thymeleaf 之前，先理解模板引擎的作用。

*   **问题**：在传统的 Java Web 开发（如 JSP）中，我们经常在 HTML 中混杂大量的 Java 代码（脚本片段），导致前端页面变得混乱，难以设计和维护。
*   **解决方案**：模板引擎将**数据（来自后端 Java 代码）** 和**表现层（前端 HTML 页面）** 分离开。你编写一个包含特殊占位符或逻辑表达式的模板文件，模板引擎会接收这个模板和后台传过来的数据，然后将数据填充到占位符中，最终生成一个纯的、包含动态数据的 HTML 文件，并发送给浏览器。

---

### Thymeleaf 的主要特点

1.  **自然模板（Natural Templates） - 最大的亮点**
    Thymeleaf 的核心优势是它的语法是标准的 HTML 属性。这意味着：
    *   **对于后端**：它使用以 `th:` 开头的属性（如 `th:text`, `th:if`）来动态处理内容。
    *   **对于前端设计师/浏览器**：如果你直接在一个浏览器中打开 Thymeleaf 模板文件（`.html`），所有 `th:*` 属性会被浏览器忽略，并显示其内嵌的静态默认内容。这极大地便利了前后端协作。

    **示例对比：**
    ```html
    <!-- 这是一个 Thymeleaf 模板 -->
    <p th:text="${message}">这是默认的静态消息，如果后端没有数据，就显示这个。</p>
    ```
    *   **在浏览器中直接打开**：你会看到段落显示“这是默认的静态消息...”。
    *   **通过服务器渲染后**：如果后端控制器传递了一个 `message="Hello, World!"`，那么最终生成的 HTML 将是 `<p>Hello, World!</p>`。

2.  **与 Spring 生态无缝集成**
    Thymeleaf 是 Spring 官方推荐的可与 Spring MVC、Spring Boot 完美结合的视图技术，尤其在替代传统的 JSP 方面表现优异。

3.  **功能强大**
    它支持各种表达式（变量、消息国际化、链接等）和高级功能（条件判断 `th:if`、循环 `th:each`、模板布局 `th:insert` 等）。

4.  **高度可扩展**
    允许你定义自己的方言（Dialect），定制模板行为。

---

### 一个完整的 Thymeleaf 工作示例

让我们看一个从后端到前端的完整流程。

**1. 后端控制器 (Spring MVC Controller)**
```java
@Controller
public class HomeController {

    @GetMapping("/hello")
    public String sayHello(Model model) {
        // 向模板传递数据
        model.addAttribute("message", "欢迎学习 Thymeleaf！");
        model.addAttribute("users", Arrays.asList("Alice", "Bob", "Charlie"));
        // 返回模板名称（不需要写.html后缀，Spring Boot 会自动查找 src/main/resources/templates/hello.html）
        return "hello"; 
    }
}
```

**2. 前端模板 (src/main/resources/templates/hello.html)**
```html
<!DOCTYPE html>
<!-- 引入 Thymeleaf 的命名空间，这样才能使用 th:* 属性 -->
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Thymeleaf 示例</title>
</head>
<body>
    <h1 th:text="${message}">默认的标题</h1>

    <!-- 条件判断 -->
    <p th:if="${not #lists.isEmpty(users)}">用户列表如下：</p>
    <p th:unless="${not #lists.isEmpty(users)}">暂无用户。</p>

    <!-- 循环遍历 -->
    <ul>
        <!-- 遍历 users 列表，每个元素临时命名为 user -->
        <li th:each="user : ${users}" th:text="${user}">默认用户 (张三)</li>
    </ul>

    <!-- 链接表达式 (URL) -->
    <a th:href="@{/about}">关于我们</a>

    <!-- 直接使用当前时间 -->
    <p>当前时间：<span th:text="${#temporals.format(#temporals.createNow(), 'yyyy-MM-dd HH:mm')}">2023-01-01</span></p>
</body>
</html>
```

**3. 最终在浏览器中生成的 HTML**
当用户访问 `/hello` 时，Thymeleaf 引擎会处理模板，生成如下纯 HTML：

```html
<!DOCTYPE html>
<html>
<head>
    <title>Thymeleaf 示例</title>
</head>
<body>
    <h1>欢迎学习 Thymeleaf！</h1>
    <p>用户列表如下：</p>
    <ul>
        <li>Alice</li>
        <li>Bob</li>
        <li>Charlie</li>
    </ul>
    <a href="/about">关于我们</a>
    <p>当前时间：<span>2023-10-27 14:30</span></p>
</body>
</html>
```

---

### Thymeleaf 与其他模板引擎的对比

| 特性               | Thymeleaf                        | JSP               | FreeMarker |
| :----------------- | :------------------------------- | :---------------- | :--------- |
| **自然模板**       | **支持**（最大优势）             | 不支持            | 不支持     |
| **语法**           | HTML 属性式                      | 类 XML/自定义标签 | 自定义指令 |
| **与 Spring 集成** | **优秀**（官方推荐）             | 良好              | 良好       |
| **学习曲线**       | 平缓                             | 中等              | 中等       |
| **性能**           | 良好（Spring Boot 下优化得很好） | 良好              | 非常快     |

### 总结：Thymeleaf 是什么？

| 方面         | 描述                                                         |
| :----------- | :----------------------------------------------------------- |
| **本质**     | 一个**服务器端的 Java 模板引擎**。                           |
| **核心思想** | **自然模板**：模板本身是有效的 HTML，可直接在浏览器中预览。  |
| **主要用途** | 在 Spring Boot/MVC 应用中动态生成 HTML 页面。                |
| **核心语法** | 通过 HTML 标签的自定义属性（如 `th:text`, `th:each`）来操作 DOM。 |
| **最大优势** | **完美分离了前端原型设计和后端逻辑集成**，便于团队协作。     |

**总而言之，如果你在使用 Spring Boot 开发现代化的 Web 应用，并希望有一个既能方便前端设计师制作静态原型，又能让后端工程师轻松集成动态数据的视图技术，Thymeleaf 是一个非常理想和流行的选择。**

## SpringBoot集成

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-boot-thymeleaf)

POM配置Thymeleaf依赖和HMR支持

```xml
<!-- Thymeleaf依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<!-- HMR支持依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

开发环境参考本站[链接](/springboot/spring-boot-thymeleaf.html#热模块替换hmr)以启用HMR

创建Controller跳转页面

```java
package com.future.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Dexterleslie.Chan
 */
@Controller
public class DemoController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        // 为 include hello-suffix页面变量设置值
        model.addAttribute("value1", "value11");
        return "index";
    }

    /**
     * 演示redirect到另外一个thymeleaf url
     *
     * @return
     */
    @GetMapping(value = "/redirect")
    public String redirect() {
        return "redirect:/hello-suffix";
    }

    /**
     *
     * @return
     */
    @GetMapping(value = "/accessHttpServletRequest")
    public String accessHttpServletRequest() {
        return "accessHttpServletRequest";
    }
}

```

resources/templates目录下创建index.html页面

```html
<!DOCTYPE html>
<html lang="en">
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style type="text/css">
        .backgroundColorYellowGreen {
            background-color: yellowgreen;
        }
    </style>
</head>
<body>
Hello World!
</body>
</html>
```



## 热模块替换HMR

> 详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-thymeleaf)
>
> SpringBoot集成Thymeleaf(不重启刷新html)：https://blog.csdn.net/KevinDai007/article/details/79479847

- 步骤1、application.properties添加

```properties
# 开发环境不启用thymeleaf缓存以便配置HMR，生产环境需要启用thymeleaf缓存以提高性能
spring.thymeleaf.cache=false
```

- 步骤2、pom.xml添加spring-boot-devtools依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

- 步骤3、启用 Settings->Build, Execution, Deployment->Compiler->Build project automatically

- 步骤4、选择ctrl+option(Alt)+shift+/ > Registry后，启用compiler.automake.allow.when.app.running

## 语法或表达式

### 字面量替换分隔符

> 说明：字面量替换分隔符。管道符号 |之间的所有内容都会被当作一个字符串来处理，你可以在其中直接插入变量。

Controller定义

```java
@Controller
public class DemoController {

    @Value("${oauth2.client_id}")
    String clientId;
    @Value("${oauth2.redirect_uri}")
    String redirectUri;

    @GetMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("client_id", clientId);
        model.addAttribute("redirect_uri", redirectUri);
        return "index";
    }
}
```

index.html字面量使用

```html
<!DOCTYPE html>
<html lang="en">
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style type="text/css">
        .backgroundColorYellowGreen {
            background-color: yellowgreen;
        }
    </style>
</head>
<body>
<a th:href="|https://github.com/login/oauth/authorize?client_id=${client_id}&redirect_uri=${redirect_uri}|">GitHub登录</a>
</body>
</html>
```

### th:if

如果有errorDescription则显示，否则不显示。

```java
String errorDescription = ex.getMessage();
model.addAttribute("errorDescription", errorDescription);
```

```html
<div th:if="${errorDescription!=null}" th:text="${errorDescription}" style="color:red;"></div>
```

### 只显示变量值，不生成HTML标签

只显示username变量值，不生成HTML标签

```html
<body>
    Hello <th:block th:text="${username}"></th:block>
</body>
```
