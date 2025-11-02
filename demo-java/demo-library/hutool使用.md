## `HttpUtil`使用

> 详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-hutool/)

```java
// 测试get
String content = HttpUtil.get("http://httpbin.org/get", StandardCharsets.UTF_8);
Assert.assertTrue(content.contains("X-Amzn-Trace-Id"));

// 测试post
content = HttpUtil.post("http://httpbin.org/post", "name=张三&age=30");
Assert.assertTrue(content.contains("form"));

// 测试post提交JSON数据
// Map.of("name", "张三", "age", 30)
String json = JSONUtil.toJsonStr(new HashMap() {{
    this.put("name", "张三");
    this.put("age", 30);
}});
HttpResponse response = HttpRequest.post("http://httpbin.org/post")
        .header("Content-Type", "application/json")
        .body(json)
        .execute();
content = response.body();
Assert.assertTrue(content.contains("json"));

// 自定义请求
response = HttpRequest.get("http://httpbin.org/get")
        .form(new HashMap() {{
            this.put("key", "value");
        }})
        .header("Authorization", "Bearer your_token_here") // 添加请求头
        .timeout(5000) // 设置超时时间
        .execute(); // 发送请求
content = response.body();
Assert.assertTrue(content.contains("args"));
```



## ServletUtil

### 核心定义

**`ServletUtil`** 是 Hutool 工具库中 `hutool-extra` 模块提供的一个工具类，它的主要目的是**极大简化 Java Web 开发中对于 `HttpServletRequest` 和 `HttpServletResponse` 对象的操作**。

你可以把它理解为 **Servlet API 的“语法糖”或“增强包”**。它用一行代码替代了原来需要多行才能完成的、容易出错的模板化代码。

---

### 它解决了什么问题？

在传统的 Servlet 或 Spring MVC 开发中，我们经常需要写很多重复、繁琐的代码来处理请求和响应，例如：

1.  **从请求中获取参数**：需要处理编码、判断参数是否存在等。
2.  **向响应中写数据**：需要设置字符编码、Content-Type、获取输出流、写入数据、刷新并关闭流，步骤繁琐且容易忘记设置编码导致乱码。
3.  **文件下载**：需要设置一堆复杂的响应头。
4.  **获取客户端信息**：如 IP 地址，需要处理代理（X-Forwarded-For等头信息），逻辑复杂。

`ServletUtil` 将这些场景封装成一个个简单的方法，让开发者能专注于业务逻辑。

---

### 核心功能详解

`ServletUtil` 的功能主要分为两大类：**处理请求** 和 **处理响应**。

#### 一、 处理请求

这类方法通常接受 `HttpServletRequest` 对象作为参数。

1.  **便捷获取请求参数**
    ```java
    HttpServletRequest request = ...;
    
    // 1. 获取所有参数，并自动处理编码问题，返回 Map<String, String>
    Map<String, String> paramMap = ServletUtil.getParamMap(request);
    
    // 2. 获取单个参数值，带默认值
    String username = ServletUtil.getParam(request, "username", "默认用户");
    
    // 3. 获取参数值为指定类型（需转换）
    Integer age = ServletUtil.getParam(request, "age", 0); // 第三个参数是默认值
    ```

2.  **获取客户端IP**
    ```java
    // 自动分析 X-Forwarded-For, X-Real-IP, Proxy-Client-IP 等头信息，避免反向代理导致的IP获取错误
    String clientIP = ServletUtil.getClientIP(request);
    ```

3.  **获取请求体（Body）**
    ```java
    // 一键获取整个请求体的字符串，自动处理编码和流关闭问题
    String body = ServletUtil.getBody(request);
    ```

4.  **读取请求为指定类型**
    ```java
    // 如果是JSON请求，可以一步到位转换为Bean
    User user = ServletUtil.toBean(request, User.class);
    ```

#### 二、 处理响应

这类方法通常接受 `HttpServletResponse` 对象作为参数。**这是 `ServletUtil` 最强大、最常用的部分。**

1.  **向客户端写入数据（核心功能）**
    ```java
    HttpServletResponse response = ...;
    
    // 1. 写入字符串（自动设置 charset=UTF-8）
    ServletUtil.write(response, "Hello World", "text/plain");
    
    // 2. 写入JSON（最常用！！！）
    // 自动设置 Content-Type 为 application/json;charset=utf-8
    String jsonData = "{\"name\": \"张三\"}";
    ServletUtil.write(response, jsonData, ServletUtil.MEDIA_TYPE_JSON);
    
    // 3. 结合Hutool的JSONUtil，直接写入Java对象
    User user = new User("李四", 25);
    ServletUtil.write(response, JSONUtil.toJsonStr(user), ServletUtil.MEDIA_TYPE_JSON);
    ```

2.  **文件下载（一行代码搞定）**
    ```java
    File file = new File("/path/to/your/file.zip");
    // 自动设置以下头部，并写入文件流：
    // Content-Type: application/octet-stream
    // Content-Disposition: attachment; filename="file.zip"
    ServletUtil.write(response, file, "下载文件.zip");
    ```

3.  **设置响应头**
    ```java
    ServletUtil.setHeader(response, "Custom-Header", "MyValue");
    ServletUtil.setContentType(response, "application/json"); 
    // 或者使用 ServletUtil.MEDIA_TYPE_JSON
    ```

---

### 完整代码示例

下面是一个在 Spring MVC Controller 中使用 `ServletUtil` 的典型例子：

```java
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {

    @PostMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        // 1. 使用 ServletUtil 从请求中获取所有参数
        Map<String, String> paramMap = ServletUtil.getParamMap(request);
        String username = paramMap.get("username");
        String password = paramMap.get("password");

        // 2. 获取客户端IP（便于记录日志或风控）
        String clientIp = ServletUtil.getClientIP(request);

        // 3. 构建一个统一的响应结果
        Map<String, Object> result = new HashMap<>();
        if ("admin".equals(username) && "123456".equals(password)) {
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("ip", clientIp);
        } else {
            result.put("code", 401);
            result.put("msg", "用户名或密码错误");
        }

        // 4. 使用 ServletUtil 将 JSON 响应写入客户端（一行代码搞定，无需关心编码和流操作）
        String jsonStr = JSONUtil.toJsonStr(result);
        ServletUtil.write(response, jsonStr, ServletUtil.MEDIA_TYPE_JSON);
    }

    @GetMapping("/download")
    public void downloadFile(HttpServletResponse response) {
        // 一行代码实现文件下载
        ServletUtil.write(response, new File("data.xlsx"), "项目数据.xlsx");
    }
}
```

---

### 优势总结

使用 `ServletUtil` 的优势非常明显：

1.  **代码简洁**：将数行模板代码变为一行方法调用。
2.  **避免错误**：自动处理字符编码（UTF-8），根绝乱码可能性；自动管理流的关闭，避免资源泄漏。
3.  **功能强大**：覆盖了参数获取、IP识别、数据写入、文件下载等常见 Web 开发需求。
4.  **提升效率**：开发者无需记忆复杂的 Servlet API 调用细节，大幅提升开发速度。

### 依赖引入

记得在项目的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-extra</artifactId>
    <version>5.8.16</version> <!-- 请使用最新版本 -->
</dependency>
```

**总而言之，`ServletUtil` 是一个面向 Servlet API 的实用工具类，它能让你用更优雅、更安全、更高效的方式来完成 Web 开发中的基础任务，是 Java Web 开发者必备的效率神器。**

### 示例

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-java/demo-library/demo-hutool)

```java
package com.future.demo;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    /**
     * 协助测试ServletUtil
     *
     * @param response
     */
    @PostMapping("test1")
    public void test1(HttpServletResponse response) {
        // 使用ServletUtil往HttpServletResponse写响应数据
        ObjectResponse<String> objectResponse = ResponseUtils.failObject(90001, "测试失败");
        String json = JSONUtil.toJsonStr(objectResponse);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ServletUtil.write(response, json, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    /**
     * 测试ServletUtil获取客户端IP
     *
     * @param request
     * @return
     */
    @GetMapping("testServletUtilGetClientIp")
    public ObjectResponse<String> testServletUtilGetClientIp(HttpServletRequest request) {
        String clientIp = ServletUtil.getClientIP(request);
        return ResponseUtils.successObject("客户端IP：" + clientIp);
    }
}
```

## JSONUtil

### 核心定义

**`JSONUtil`** 是 Hutool 工具库中提供的一个**关于 JSON 操作的综合性工具类**。它的核心目标是**用极其简单、直观的 API 来完成 Java 对象与 JSON 字符串之间的相互转换，以及 JSON 数据的创建、解析和操作**。

你可以把它理解为 **Jackson、Gson、Fastjson 等流行 JSON 库的一个“超集”或“替代品”**，但 API 设计得更加人性化和易用。

---

### 它解决了什么问题？

在传统开发中，使用 JSON 库通常会遇到以下痛点：

1.  **API 复杂**：不同的库（Jackson, Gson）有不同的 API，需要学习成本。
2.  **配置繁琐**：处理日期格式化、空值忽略等需要复杂的配置。
3.  **代码冗长**：实现一个简单的转换可能需要多行代码。
4.  **异常处理**：需要处理各种检查异常（如 `JsonProcessingException`）。

`JSONUtil` 的出现，就是为了**用一行代码解决上述所有问题**。

---

### 核心功能详解

`JSONUtil` 的功能可以概括为四大板块：

#### 一、 序列化：将 Java 对象转换为 JSON 字符串（最常用）

这是 `JSONUtil` 最核心、最常用的功能。

**1. 转换基本对象（Map, List, JavaBean）**
```java
// 转换 Map -> JSON
Map<String, Object> map = new HashMap<>();
map.put("name", "张三");
map.put("age", 25);
String jsonStr = JSONUtil.toJsonStr(map);
// 结果: {"age":25,"name":"张三"}

// 转换 JavaBean -> JSON
User user = new User("李四", 30);
String userJson = JSONUtil.toJsonStr(user);
// 结果: {"age":30,"name":"李四"}
```

**2. 高级序列化控制**
```java
// 创建配置对象，设置日期格式、忽略空值等
JSONConfig config = JSONConfig.create()
        .setDateFormat("yyyy-MM-dd HH:mm:ss") // 日期格式化
        .setIgnoreNullValue(true);            // 忽略null值

User user = new User("王五", null, new Date());
String jsonStr = JSONUtil.toJsonStr(user, config);
// 结果: {"createTime":"2024-05-30 10:30:00","name":"王五"} (age为null被忽略)
```

#### 二、 反序列化：将 JSON 字符串转换为 Java 对象

**1. 转换为简单类型**
```java
String jsonStr = "{\"name\":\"张三\",\"age\":25}";

// 转换为 Map
Map map = JSONUtil.toBean(jsonStr, Map.class);

// 转换为自定义 JavaBean
User user = JSONUtil.toBean(jsonStr, User.class);
System.out.println(user.getName()); // 输出: 张三
```

**2. 转换为复杂类型（如带泛型的 List）**
```java
String jsonArrayStr = "[{\"name\":\"张三\"}, {\"name\":\"李四\"}]";

// 传统方式处理泛型很麻烦，Hutool 非常简单
List<User> userList = JSONUtil.toList(JSONUtil.parseArray(jsonArrayStr), User.class);
// 或者使用 TypeReference
List<User> userList2 = JSONUtil.toBean(jsonArrayStr, new TypeReference<List<User>>() {}, false);
```

#### 三、 创建和解析 JSON：以编程方式构建 JSON

**1. 创建 JSON 对象**
```java
// 方法1：使用 createObj()
JSONObject jsonObj = JSONUtil.createObj()
        .set("name", "张三")
        .set("age", 25)
        .set("hobbies", JSONUtil.createArray().set("读书").set("游泳"));

// 方法2：使用 parseObj() 包装 Map
JSONObject jsonObj2 = JSONUtil.parseObj(map);

System.out.println(jsonObj.toString());
// 结果: {"age":25,"hobbies":["读书","游泳"],"name":"张三"}
```

**2. 创建 JSON 数组**
```java
JSONArray jsonArray = JSONUtil.createArray()
        .set("Java")
        .set("Python")
        .set(JSONUtil.createObj().set("name", "JavaScript"));

System.out.println(jsonArray.toString());
// 结果: ["Java","Python",{"name":"JavaScript"}]
```

#### 四、 XML 与 JSON 互转

这是一个非常实用的特色功能。

```java
// XML 转 JSON
String xml = "<user><name>张三</name><age>25</age></user>";
String json = JSONUtil.xmlToJson(xml).toString();
// 结果: {"name":"张三","age":25}

// JSON 转 XML
String jsonStr = "{\"name\":\"张三\",\"age\":25}";
String xmlResult = JSONUtil.toXmlStr(JSONUtil.parseObj(jsonStr));
// 结果: <name>张三</name><age>25</age>
```

---

### 完整实战示例

结合 Web 开发的常见场景：

```java
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONConfig;

public class JsonUtilExample {

    public static void main(String[] args) {
        // 场景1：构建统一的 API 响应
        JSONObject response = JSONUtil.createObj()
                .set("code", 200)
                .set("message", "操作成功")
                .set("data", JSONUtil.createObj()
                        .set("userId", 12345)
                        .set("token", "abc123xyz"));

        String responseJson = response.toStringPretty(); // 美化格式输出
        System.out.println(responseJson);
        // 输出:
        // {
        //     "code": 200,
        //     "message": "操作成功",
        //     "data": {
        //         "userId": 12345,
        //         "token": "abc123xyz"
        //     }
        // }

        // 场景2：解析外部 API 返回的 JSON
        String apiResponse = "{\"status\":\"OK\",\"data\":[{\"id\":1,\"name\":\"商品A\"},{\"id\":2,\"name\":\"商品B\"}]}";
        
        JSONObject json = JSONUtil.parseObj(apiResponse);
        String status = json.getStr("status"); // 获取 status 字段
        JSONArray dataArray = json.getJSONArray("data"); // 获取 data 数组
        
        // 遍历数组
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            System.out.println("商品ID: " + item.getInt("id") + ", 名称: " + item.getStr("name"));
        }
    }
}
```

---

### 与其他 JSON 库的对比

| 特性           | Hutool `JSONUtil`                           | Jackson/Gson                   | 优势              |
| -------------- | ------------------------------------------- | ------------------------------ | ----------------- |
| **学习成本**   | 极低，API 直观统一                          | 较高，需要学习特定注解和配置   | ✅ **Hutool 胜出** |
| **代码简洁性** | 一行代码完成多数操作                        | 需要多行代码和额外配置         | ✅ **Hutool 胜出** |
| **功能完整性** | 覆盖序列化、反序列化、创建、解析、XML转换等 | 功能全面但分散在不同类中       | ⚖️ **持平**        |
| **性能**       | 良好，满足大部分场景                        | 极致优化，特别适合高频大数据量 | ❌ **专业库胜出**  |
| **社区生态**   | 与 Hutool 生态完美集成                      | 行业标准，生态丰富             | ⚖️ **看场景**      |

---

### 什么时候选择 `JSONUtil`？

1.  **快速开发**：当你想要用最少的代码完成 JSON 操作时。
3.  **中小项目**：对性能要求不是极端苛刻的常规业务系统。
4.  **已有 Hutool 环境**：项目中已经使用了 Hutool，希望保持技术栈统一。
5.  **XML/JSON 互转**：需要处理 XML 和 JSON 相互转换的场景。

### 什么时候选择 Jackson/Gson？

1.  **高性能要求**：需要处理海量数据，对性能有极致要求。
2.  **复杂序列化**：需要高度定制化的序列化/反序列化逻辑。
3.  **微服务架构**：需要与 Spring Cloud 等框架深度集成。
4.  **已有技术栈**：项目中已经建立了 Jackson/Gson 的技术体系。

### 总结

**Hutool 的 `JSONUtil` 是一个设计精良、API 极其友好的 JSON 工具类，它让 Java 中的 JSON 操作变得简单而愉悦。对于绝大多数业务场景来说，它的简洁性和开发效率优势远远超过性能上的微小差距，是中小型项目和快速原型开发的绝佳选择。**

### 示例

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-java/demo-library/demo-hutool)

```java
/**
 * 测试JSONUtil
 */
@Test
public void testJSONUtil() {
    // 把对象转换成JSON字符串
    ObjectResponse<String> objectResponse = ResponseUtils.successObject("测试成功");
    String json = JSONUtil.toJsonStr(objectResponse);
    Assertions.assertEquals("{\"data\":\"测试成功\",\"errorCode\":0}" , json);

    // 不忽略null值
    objectResponse = ResponseUtils.successObject("测试成功");
    json = JSONUtil.toJsonStr(objectResponse, JSONConfig.create().setIgnoreNullValue(false));
    Assertions.assertEquals("{\"data\":\"测试成功\",\"errorCode\":0,\"errorMessage\":null}" , json);
}
```
