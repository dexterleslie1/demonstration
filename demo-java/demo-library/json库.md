# JSON 库



## 返回给前端的 JSON 数据有枚举类型的最佳实践

>提醒：推荐使用方法二（自定义 Serializer）比较简洁。

Java 后端返回给前端 JSON 数据中包含枚举类型的最佳实践，主要目标是确保前端能够轻松理解和使用这些数据，同时保持后端代码的清晰性和可维护性。  这里提供几种方法，并分析它们的优缺点：

### **方法一：返回枚举的名称 (name())**

这是最简单的方法，直接返回枚举的名称字符串。

* **优点:**  简单直接，无需额外处理。
* **缺点:**  前端需要自行将名称映射回枚举值，增加了前端的工作量。如果后端枚举值修改，前端也需要同步修改。  可读性在枚举值含义不明显时较差。

* **示例:**

```java
public enum Status { ACTIVE, INACTIVE, PENDING }

// 后端返回: {"status": "ACTIVE"}
```

### **方法二：返回枚举的名称和描述 (name() + description)**

>[参考链接](https://www.baeldung.com/jackson-serialize-enums)

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



**示例代码：**

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-json-lib)

BeanClass2

```java
@Data
public class BeanClass2 {
    private long userId;
    private String loginname;
    @JsonIgnore
    private boolean enable;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    // BeanClass2 用于演示基于 Jackson 库自定义枚举类型的 Serializer 返回预期格式的 JSON 数据给前端
    @JsonSerialize(using = Status.StatusSerializer.class)
    private Status status;
}
```

Status

```java
public enum Status {

    Create("创建"),
    Paying("支付中");

    private String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 基于 Jackson 库自定义枚举类型的 Serializer 返回预期格式的 JSON 数据给前端
    public static class StatusSerializer extends StdSerializer<Status> {
        public StatusSerializer() {
            this(Status.class);
        }

        public StatusSerializer(Class t) {
            super(t);
        }

        @Override
        public void serialize(Status status, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("name");
            jsonGenerator.writeString(status.name());
            jsonGenerator.writeFieldName("description");
            jsonGenerator.writeString(status.getDescription());
            jsonGenerator.writeEndObject();
        }

    }
}
```

测试代码

```java
/**
 * 测试后端如何返回带有枚举类型数据给前端
 *
 * @throws JsonProcessingException
 */
@Test
public void testEnum() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    BeanClass2 beanClass2 = new BeanClass2();
    beanClass2.setStatus(Status.Paying);
    String JSON = objectMapper.writeValueAsString(beanClass2);
    Assert.assertEquals("{\"userId\":0,\"loginname\":null,\"createTime\":null,\"status\":{\"name\":\"Paying\",\"description\":\"支付中\"}}", JSON);
}
```



### **方法三：返回整数值 (ordinal() 或自定义值)**

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

### **方法四：自定义 DTO (Data Transfer Object)**

>[参考链接](https://www.baeldung.com/jackson-serialize-enums)

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



**示例代码：**

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-json-lib)

StatusEnumDTO

```java
@Data
@Builder
public class StatusEnumDTO {
    private Status name;
    private String description;
}
```

BeanClass

```java
@Data
public class BeanClass {
    private long userId;
    private String loginname;
    @JsonIgnore
    private boolean enable;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    // BeanClass 用于演示基于 Jackson 库自定义枚举类型对应的 DTO 返回预期格式的 JSON 数据给前端
    private StatusEnumDTO status;
}
```

Status

```java
public enum Status {

    Create("创建"),
    Paying("支付中");

    private String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusEnumDTO toDto() {
        return StatusEnumDTO.builder().name(this).description(this.getDescription()).build();
    }
}

```

测试代码：

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



### **最佳实践建议**

建议如下：

* **使用方法四 (自定义 DTO):**  这是最推荐的方法，它提供了最佳的可读性、可维护性和可扩展性。
* **避免使用 `ordinal()`:**  因为 `ordinal()` 值可能会发生变化。
* **提供清晰的文档:**  无论选择哪种方法，都应该提供清晰的 API 文档，说明枚举值的含义和返回格式。
* **考虑国际化:**  如果你的应用需要支持多种语言，则应该使用资源文件或其他国际化机制来管理枚举值的描述。
* **使用 Jackson 的 `@JsonFormat` 注解 (方法二和四):**  可以更精细地控制枚举的 JSON 序列化方式，例如指定格式化输出的字段。


总而言之，选择哪种方法取决于你的具体需求和项目规模。  对于小型项目，方法一或方法二可能就足够了；对于大型项目或需要更高可维护性的项目，建议使用方法四，创建自定义 DTO 来封装枚举数据。

记住，选择任何一种方法都应该优先考虑前端的易用性和后端代码的可维护性。  清晰的文档和一致的编码风格非常重要。



## Jackson

>`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-json-lib`

### maven 项目 pom 配置

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.4</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.9.4</version>
</dependency>
```



### LocalDateTime、LocalDate、LocalTime 序列化配置

>`https://blog.csdn.net/REX1024/article/details/123657816`

pom 配置引入如下依赖

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.9.4</version>
</dependency>
```

配置 LocalDateTime 字段序列化和反序列化

```java
@Data
public class BeanClass {
    private long userId;
    private String loginname;
    @JsonIgnore
    private boolean enable;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    private StatusEnumDTO status;
}
```

```java
/**
 *
 */
@Test
public void bean2json() throws IOException {
    long userId = 12345l;
    String loginname = "dexter";
    boolean enable = true;
    LocalDateTime createTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    BeanClass beanClass = new BeanClass();
    beanClass.setUserId(userId);
    beanClass.setLoginname(loginname);
    beanClass.setEnable(enable);
    beanClass.setCreateTime(createTime);

    ObjectMapper OMInstance = new ObjectMapper();
    String json = OMInstance.writeValueAsString(beanClass);
    beanClass = OMInstance.readValue(json, BeanClass.class);
    Assert.assertEquals(userId, beanClass.getUserId());
    Assert.assertEquals(loginname, beanClass.getLoginname());
    Assert.assertEquals(false, beanClass.isEnable());
    Assert.assertEquals(createTime, beanClass.getCreateTime());
}
```