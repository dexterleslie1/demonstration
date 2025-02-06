# Lombok



## maven 项目中引用 Lombok 依赖

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.8</version>
    <scope>provided</scope>
</dependency>
```



## @SuperBuilder

### 介绍

Lombok 的 `@SuperBuilder` 注解用于在继承关系中构建对象，它扩展了 `@Builder` 注解的功能，允许你方便地构建继承类的对象，同时继承父类的构建器方法。


**基本用法:**

假设我们有两个类：`Animal` 和 `Cat`，`Cat` 继承自 `Animal`：

```java
import lombok.*;

@Data
@Builder
public class Animal {
    private String name;
    private String type;
}

@Data
@SuperBuilder
public class Cat extends Animal {
    private String color;
}

public class Main {
    public static void main(String[] args) {
        Cat cat = Cat.builder()
                .name("Whiskers")
                .type("Cat")
                .color("Gray")
                .build();
        System.out.println(cat);
    }
}
```

在这个例子中：

* `Animal` 类使用 `@Builder` 注解生成其 builder。
* `Cat` 类使用 `@SuperBuilder` 注解，它自动继承了 `Animal` 的 builder 方法（`name` 和 `type`），并添加了 `Cat` 自己的属性 `color` 的 builder 方法。  因此，我们在构建 `Cat` 对象时，可以同时设置 `Animal` 和 `Cat` 的属性。


**关键点:**

* **继承父类的 Builder:** `@SuperBuilder` 最重要的功能是继承父类的 `Builder` 方法。  你不需要在 `Cat` 的 builder 中重新定义 `name` 和 `type` 的设置方法。
* **避免重复:**  这避免了在子类中重复定义父类属性的 builder 方法，减少了代码冗余。
* **保持一致性:**  使用 `@SuperBuilder` 保持了构建父类和子类对象的风格一致。
* **需要 `@Builder` 注解在父类:**  父类必须使用 `@Builder` 注解才能被 `@SuperBuilder` 正确继承。


**与 `@Builder` 的区别:**

`@SuperBuilder` 和 `@Builder` 的主要区别在于：

* `@Builder`:  用于生成单个类的 builder。
* `@SuperBuilder`:  用于生成继承类中 builder，自动继承父类的 builder 方法。


**高级用法 (与其他 Lombok 注解结合):**

`@SuperBuilder` 可以与其他 Lombok 注解（例如 `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` 等）一起使用，以进一步简化代码。


**潜在问题:**

* **冲突:** 如果父类和子类有同名属性，可能会导致 builder 方法冲突。  你需要仔细检查并解决潜在的命名冲突。
* **复杂继承:**  在复杂的继承结构中，使用 `@SuperBuilder` 需要谨慎处理，以确保 builder 的正确构建。

总而言之，`@SuperBuilder` 简化了继承类对象的构建过程，提高了代码可读性和可维护性。  它尤其在构建继承层次结构中的对象时非常有用，可以有效避免代码重复，并保持构建风格的一致性。  但是，需要仔细处理潜在的命名冲突问题。



### 用法示例

```java
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AsyncInvocationModel {
    protected final static ObjectMapper OMInstance = new ObjectMapper();

    private AsyncInvocationType type;
    private Map<String, Object> parameters;

    /**
     * @return
     */
    public String toJson() {
        String json = null;
        try {
            json = OMInstance.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    /**
     * @param json
     * @return
     */
    public static AsyncInvocationModel fromJson(String json, Class<? extends AsyncInvocationModel> cls) throws IOException {
        AsyncInvocationModel asyncInvocationModel = null;
        try {
            asyncInvocationModel = OMInstance.readValue(json, cls);
        } catch (IOException e) {
            throw e;
        }
        return asyncInvocationModel;
    }
}
```

```java
@Getter
@SuperBuilder
@NoArgsConstructor
public class AsyncInvocationRestModel extends AsyncInvocationModel {
    private String authenticationBasicUser;
    private String authenticationBasicPassword;
    private String host;
    private int port;
    private String uri;
}
```

```java
public enum AsyncInvocationType {
    /**
     * RESTful api类型调用
     */
    REST
}
```

```java
@Test
public void test() throws IOException {
    // region @SuperBuilder 注解测试

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("param1", "p1");
    String authenticationBasicUser = "user1";
    String host = "192.168.1.1";
    int port = 8091;
    String uri = "/api/v1/user/login";

    // 测试创建RESTful类型AsyncInvocation
    AsyncInvocationRestModel asyncInvocationRestModel =
            AsyncInvocationRestModel.builder()
                    .parameters(parameters)
                    .authenticationBasicUser(authenticationBasicUser)
                    .host(host)
                    .port(port)
                    .uri(uri)
                    .type(AsyncInvocationType.REST)
                    .build();

    Assert.assertEquals(parameters.get("param1"), asyncInvocationRestModel.getParameters().get("param1"));
    Assert.assertEquals(authenticationBasicUser, asyncInvocationRestModel.getAuthenticationBasicUser());
    Assert.assertEquals(host, asyncInvocationRestModel.getHost());
    Assert.assertEquals(port, asyncInvocationRestModel.getPort());
    Assert.assertEquals(uri, asyncInvocationRestModel.getUri());
    Assert.assertEquals(AsyncInvocationType.REST, asyncInvocationRestModel.getType());

    // 测试lombok json
    String json = asyncInvocationRestModel.toJson();
    AsyncInvocationRestModel asyncInvocationRestModelFromJson =
            (AsyncInvocationRestModel) AsyncInvocationModel.fromJson(json, AsyncInvocationRestModel.class);
    Assert.assertEquals(parameters.get("param1"), asyncInvocationRestModelFromJson.getParameters().get("param1"));
    Assert.assertEquals(authenticationBasicUser, asyncInvocationRestModelFromJson.getAuthenticationBasicUser());
    Assert.assertEquals(host, asyncInvocationRestModelFromJson.getHost());
    Assert.assertEquals(port, asyncInvocationRestModelFromJson.getPort());
    Assert.assertEquals(uri, asyncInvocationRestModelFromJson.getUri());
    Assert.assertEquals(AsyncInvocationType.REST, asyncInvocationRestModel.getType());

    // endregion
}
```