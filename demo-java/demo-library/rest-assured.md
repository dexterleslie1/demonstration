# rest assured

>`https://github.com/rest-assured/rest-assured/wiki/GettingStarted`

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-rest-assured`



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
