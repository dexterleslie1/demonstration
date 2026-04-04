## Rest Assured是什么呢？

>`https://github.com/rest-assured/rest-assured/wiki/GettingStarted`
>
>详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-rest-assured`

Rest Assured 是一个专为 Java 开发者设计的开源框架，用于简化和美化 RESTful API 的自动化测试。它通过一种名为 DSL（领域专用语言）的设计，让测试代码的编写变得像写自然语言句子一样流畅和直观，极大地提升了测试脚本的可读性和开发效率。

### 核心语法：Given-When-Then

Rest Assured 的核心是其三段式语法结构，这种结构深受 BDD（行为驱动开发）思想的启发，让测试逻辑一目了然：

1.  **`Given()` (假定)**
    这是测试的准备阶段。在这里，你可以设置发起请求所需的一切先决条件，例如：
    *   请求头 (`header`)
    *   请求参数 (`queryParam`, `pathParam`)
    *   请求体 (`body`)
    *   认证信息 (`auth`)
    *   内容类型 (`contentType`)

2.  **`When()` (当)**
    这是执行阶段。你在这里指定要执行的 HTTP 方法和目标路径，例如 `.get()`, `.post()`, `.put()`, `.delete()` 等。

3.  **`Then()` (那么)**
    这是验证阶段。你在这里对服务器的响应进行断言，以确保其行为符合预期，例如：
    *   断言状态码 (`statusCode`)
    *   断言响应体内容 (`body`)
    *   断言响应头 (`header`)
    *   断言响应时间 (`time`)

下面是一个完整的示例，展示了如何测试一个用户登录接口：

```java
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

// ...

given()
    .contentType("application/json")
    .body("{\"username\": \"test_user\", \"password\": \"123456\"}")
.when()
    .post("/api/login")
.then()
    .statusCode(200)
    .body("message", equalTo("Login successful"))
    .body("data.token", notNullValue());
```

这段代码清晰地表达了：“**假定**请求内容是JSON且包含用户名密码，**当**向登录接口发起POST请求时，**那么**期望返回状态码200，且响应体中包含成功消息和非空的token。”

### 主要特性

Rest Assured 提供了一系列强大的功能，使其成为 Java 生态中 API 测试的首选工具：

*   **无缝的测试框架集成**
    它可以与 JUnit、TestNG 等主流 Java 测试框架无缝集成，方便地组织和运行测试用例。

*   **全面的 HTTP 方法支持**
    支持 GET、POST、PUT、DELETE、PATCH 等所有常见的 HTTP 请求方法。

*   **强大的认证机制**
    内置了对多种认证方式的支持，如 Basic Auth、Form Auth、OAuth 2.0 等，让测试需要认证的接口变得非常简单。

*   **灵活的 JSON/XML 处理**
    内建了对 JSON 和 XML 的序列化与反序列化支持。你可以轻松地将 Java 对象作为请求体发送，也可以使用 JSON Path 或 XML Path 语法方便地提取和断言响应体中的特定字段。

*   **丰富的断言能力**
    深度集成了 Hamcrest 匹配器，提供了极其丰富和灵活的断言语法，可以验证响应内容、结构（甚至支持 JSON Schema 验证）、响应时间等各个方面。

*   **可复用的请求规范**
    通过 `RequestSpecBuilder` 等工具，可以将通用的请求配置（如基础 URL、公共请求头）定义为可复用的规范，避免代码重复，提升测试套件的可维护性。

### 如何开始

在 Maven 项目中，你只需在 `pom.xml` 文件中添加 Rest Assured 的依赖即可开始使用：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

## 依赖配置

```xml
<!-- rest assured 依赖 -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
```



## 转换 JSON 响应为Java 对象

```java
// 转换 JSON 响应为 Java 对象
Response response = RestAssured.get(this.getBasePath() + "/test1").then().statusCode(200)
        .extract().response();
ObjectResponse<MyBean> response1 = response.as(new TypeRef<ObjectResponse<MyBean>>() {
});
Assert.assertEquals("field1", response1.getData().getField1());
Assert.assertEquals("field2", response1.getData().getField2());
```



## HTTP Basic 验证

```java
Response response = RestAssured
        // HTTP Basic 认证
        .given().auth().basic("client1", "123")
        .get(this.getBasePath() + "/test1").then().statusCode(200)
        .extract().response();
```
