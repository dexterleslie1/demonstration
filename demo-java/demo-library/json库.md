# json 库



## 后端返回给前端的 JSON 数据有枚举类型的最佳实践

Java 后端返回给前端 JSON 数据中包含枚举类型的最佳实践，主要目标是确保前端能够轻松理解和使用这些数据，同时保持后端代码的清晰性和可维护性。  这里提供几种方法，并分析它们的优缺点：

**方法一：返回枚举的名称 (name())**

这是最简单的方法，直接返回枚举的名称字符串。

* **优点:**  简单直接，无需额外处理。
* **缺点:**  前端需要自行将名称映射回枚举值，增加了前端的工作量。如果后端枚举值修改，前端也需要同步修改。  可读性在枚举值含义不明显时较差。

* **示例:**

```java
public enum Status { ACTIVE, INACTIVE, PENDING }

// 后端返回: {"status": "ACTIVE"}
```

**方法二：返回枚举的名称和描述 (name() + description)**

扩展方法一，同时返回枚举的名称和描述，提高可读性。  需要在枚举中添加一个描述字段。

* **优点:**  比方法一更清晰，前端更容易理解。
* **缺点:**  仍然需要前端进行映射。  需要在枚举中维护额外的描述字段。

* **示例 (假设枚举添加了 `description` 字段):**

```java
public enum Status {
    ACTIVE("激活"), INACTIVE("禁用"), PENDING("待定");

    private final String description;
    Status(String description) { this.description = description; }

    public String getDescription() { return description; }
}

// 后端返回: {"status": {"name": "ACTIVE", "description": "激活"}}
```


**方法三：返回整数值 (ordinal() 或自定义值)**

使用枚举的 `ordinal()` 或自定义整数值。

* **优点:**  节省带宽，传输速度更快。
* **缺点:**  可读性差，前端需要维护一个映射表来将整数值转换为枚举值。  `ordinal()` 的值会随着枚举定义的变化而改变，因此不推荐使用。

* **示例 (自定义值):**

```java
public enum Status {
    ACTIVE(1), INACTIVE(0), PENDING(2);
    private final int value;
    Status(int value) { this.value = value; }
    public int getValue() { return value; }
}

// 后端返回: {"status": 1}
```

**方法四：自定义 DTO (Data Transfer Object)**

创建自定义 DTO 来封装枚举值及其描述。  这是推荐方法。

* **优点:**  最灵活，可扩展性强，易于维护。  前端能直接使用，解耦后端枚举的修改。
* **缺点:**  需要编写额外的 DTO 类。

* **示例:**

```java
public class StatusDto {
    private String name;
    private String description;
    // ... getter and setter ...
}

public enum Status {
    ACTIVE("激活"), INACTIVE("禁用"), PENDING("待定");

    private final String description;
    Status(String description) { this.description = description; }

    public String getDescription() { return description; }

    public StatusDto toDto() {
        StatusDto dto = new StatusDto();
        dto.setName(this.name());
        dto.setDescription(this.description);
        return dto;
    }
}


// 后端返回: {"status": {"name": "ACTIVE", "description": "激活"}}
```


**最佳实践建议:**

* **使用方法四 (自定义 DTO):**  这是最推荐的方法，它提供了最佳的可读性、可维护性和可扩展性。
* **避免使用 `ordinal()`:**  因为 `ordinal()` 值可能会发生变化。
* **提供清晰的文档:**  无论选择哪种方法，都应该提供清晰的 API 文档，说明枚举值的含义和返回格式。
* **考虑国际化:**  如果你的应用需要支持多种语言，则应该使用资源文件或其他国际化机制来管理枚举值的描述。
* **使用 Jackson 的 `@JsonFormat` 注解 (方法二和四):**  可以更精细地控制枚举的 JSON 序列化方式，例如指定格式化输出的字段。


总而言之，选择哪种方法取决于你的具体需求和项目规模。  对于小型项目，方法一或方法二可能就足够了；对于大型项目或需要更高可维护性的项目，建议使用方法四，创建自定义 DTO 来封装枚举数据。


记住，选择任何一种方法都应该优先考虑前端的易用性和后端代码的可维护性。  清晰的文档和一致的编码风格非常重要。

**方法四示例代码：**

>详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-json-lib`

```java
/**
 * 测试后端如何返回带有枚举类型数据给前端
 *
 * @throws JsonProcessingException
 */
@Test
public void testEnum() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    BeanClass beanClass = new BeanClass();
    beanClass.setStatus(Status.Paying.toDto());
    String JSON = objectMapper.writeValueAsString(beanClass);
    Assert.assertEquals("{\"userId\":0,\"loginname\":null,\"createTime\":null,\"status\":{\"name\":\"Paying\",\"description\":\"支付中\"}}", JSON);
}
```



## Jackson

>`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-json-lib`