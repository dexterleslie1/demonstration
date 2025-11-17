## 返回给前端的 `JSON` 数据有枚举类型的最佳实践

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



## `Jackson`

>`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-json-lib`

### `POM` 配置

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



### 测试 `Bean` 转换为 `json` 字符串

>使用 `ObjectMapper#writeValueAsString()` 函数。

```java
/**
 * 测试 bean 转换为 json 字符串
 */
@Test
public void testBean2json() throws IOException {
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



### 测试 `json` 字符串转换为 `Bean`

>使用 `ObjectMapper#readValue()` 函数。

```java
/**
 * 测试 json 字符串转换为 bean
 */
@Test
public void testJson2Bean() throws IOException {
    String json = "{\"userId\":12345,\"loginname\":\"dexter\",\"createTime\":\"2025-02-11 16:46:04\",\"enable\":true}";

    long userId = 12345l;
    String loginname = "dexter";
    LocalDateTime createTime = LocalDateTime.parse("2025-02-11 16:46:04", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    ObjectMapper OMInstance = new ObjectMapper();
    BeanClass beanClass = OMInstance.readValue(json, BeanClass.class);
    Assert.assertEquals(userId, beanClass.getUserId());
    Assert.assertEquals(loginname, beanClass.getLoginname());
    Assert.assertEquals(false, beanClass.isEnable());
    Assert.assertEquals(createTime, beanClass.getCreateTime());
}
```



### 测试 `json` 字符串转换为 `JsonNode`

>使用 `ObjectMapper#readTree()` 函数。

```java
/**
 * 测试 json 字符串转换为 JsonNode
 *
 * @throws IOException
 */
@Test
public void testJson2JsonNode() throws IOException {
    String json = "{\"userId\":12345,\"loginname\":\"dexter\",\"createTime\":1577874822420,\"enable\":true}";
    ObjectMapper OMInstance = new ObjectMapper();
    JsonNode jsonNode = OMInstance.readTree(json);
    Assert.assertTrue(jsonNode.get("enable").asBoolean());
}
```



### 测试 `ObjectNode` 用于面向对象创建 `json` 结构对象

>使用 `ObjectMapper#createObjectNode()` 函数。

```java
/**
 * 测试 ObjectNode 用于面向对象创建 json 结构对象
 *
 * @throws IOException
 */
@Test
public void testObjectNode() throws IOException {
    ObjectMapper OMInstance = new ObjectMapper();
    ObjectNode objectNode = OMInstance.createObjectNode();
    objectNode.put("enable", true);
    objectNode.put("loginname", "dexter");

    String json = OMInstance.writeValueAsString(objectNode);
    JsonNode jsonNode = OMInstance.readTree(json);
    Assert.assertTrue(jsonNode.get("enable").asBoolean());
    Assert.assertEquals("dexter", jsonNode.get("loginname").asText());

    objectNode = JsonNodeFactory.instance.objectNode();
    objectNode.put("enable", true);
    objectNode.put("loginname", "dexter");
    Assert.assertTrue(jsonNode.get("enable").asBoolean());
    Assert.assertEquals("dexter", jsonNode.get("loginname").asText());
}
```

### 转换JsonNode为ObjectNode

>How to convert JsonNode to ObjectNode：https://stackoverflow.com/questions/32713109/how-to-convert-jsonnode-to-objectnode
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-java/demo-library/demo-json-lib

```java
/**
 * 转换JsonNode为ObjectNode
 *
 * @throws IOException
 */
@Test
public void jsonNodeToObjectNode() throws IOException {
    String json = "{\"userId\":12345,\"loginname\":\"dexter\",\"createTime\":1577874822420,\"enable\":true}";
    ObjectMapper OMInstance = new ObjectMapper();
    JsonNode jsonNode = OMInstance.readTree(json);
    ObjectNode objectNode = (ObjectNode) jsonNode;
    log.info(jsonNode.toString());
    log.info(objectNode.toString());
}
```

### JsonNode和ObjectNode的区别

`ObjectNode` 和 `JsonNode` 的区别在于它们的 **可变性** 和 **功能定位**。

简单来说：
- **`JsonNode`** 是只读的视图，用于**查看和遍历** JSON 数据。
- **`ObjectNode`** 是可写的实现，用于**构建和修改** JSON 数据。

---

#### 核心区别对比表

| 特性         | JsonNode                                 | ObjectNode                                                   |
| :----------- | :--------------------------------------- | :----------------------------------------------------------- |
| **核心目的** | **读取、查询、遍历** JSON 数据           | **构建、修改、编辑** JSON 数据                               |
| **可变性**   | **不可变（只读）**                       | **可变（可写）**                                             |
| **继承关系** | 是 **父类/接口**                         | 是 **`JsonNode` 的子类**                                     |
| **创建方式** | 通常由 `ObjectMapper.readTree()` 返回    | `JsonNodeFactory.instance.objectNode()` 或 `ObjectMapper.createObjectNode()` |
| **修改方法** | 没有 `put()`, `set()`, `remove()` 等方法 | 有完整的 `put()`, `set()`, `remove()`, `with()` 等方法       |

---

#### 详细解释与代码示例

##### 1. JsonNode（只读视图）

`JsonNode` 是一个抽象类，它代表了 JSON 树模型中的一个节点。它的主要设计目标是提供一种统一的方式来**访问和检查** JSON 数据，而不关心数据是如何构建的。

**主要特点：**
- **只读**：你不能通过 `JsonNode` 修改底层的 JSON 结构。
- **通用接口**：从 `ObjectMapper.readTree()` 返回的具体节点类型（`ObjectNode`, `ArrayNode`, `TextNode` 等）都可以被向上转型为 `JsonNode` 来统一处理。

**常用方法（查询类）：**
- `get(String fieldName)`：获取子节点
- `path(String fieldName)`：安全获取子节点（不存在返回 `MissingNode`）
- `has(String fieldName)`：检查字段是否存在
- `asText()`, `asInt()`, `asBoolean()`：获取值
- `isObject()`, `isArray()`, `isTextual()`：判断节点类型

```java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

String json = "{\"name\":\"Alice\",\"age\":25,\"hobbies\":[\"reading\",\"coding\"]}";
ObjectMapper mapper = new ObjectMapper();

// 解析JSON，返回的是具体的节点类型，但通常用JsonNode接口接收
JsonNode rootNode = mapper.readTree(json);

// 1. 读取数据 - 完全可以
String name = rootNode.get("name").asText(); // "Alice"
int age = rootNode.get("age").asInt(); // 25

// 2. 尝试修改 - 编译错误！JsonNode没有修改方法
// rootNode.put("newField", "value"); // 错误！方法不存在
// rootNode.remove("name"); // 错误！方法不存在
```

##### 2. ObjectNode（可写实现）

`ObjectNode` 是 `JsonNode` 的具体子类，专门用于表示 JSON **对象**（即键值对集合）。它提供了完整的方法来修改 JSON 结构。

**主要特点：**
- **可写**：可以添加、修改、删除字段。
- **具体实现**：你需要明确使用 `ObjectNode` 类型来调用修改方法。

**常用方法（修改类）：**
- `put(String fieldName, Xxx value)`：添加/更新字段（基本类型）
- `putPOJO(String fieldName, Object value)`：添加复杂对象
- `set(String fieldName, JsonNode value)`：添加JsonNode
- `setAll(ObjectNode other)`：合并另一个ObjectNode
- `remove(String fieldName)`：删除字段

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

ObjectMapper mapper = new ObjectMapper();

// 创建空的JSON对象
ObjectNode objectNode = mapper.createObjectNode();

// 1. 构建/修改数据 - 核心功能
objectNode.put("name", "Bob");
objectNode.put("age", 30);
objectNode.put("active", true);

// 添加嵌套对象
ObjectNode addressNode = mapper.createObjectNode();
addressNode.put("city", "Beijing");
objectNode.set("address", addressNode);

// 2. 读取数据 - 也可以（因为继承自JsonNode）
String name = objectNode.get("name").asText(); // 完全合法

// 3. 删除数据
objectNode.remove("active");

System.out.println(objectNode.toString());
// 输出: {"name":"Bob","age":30,"address":{"city":"Beijing"}}
```

---

#### 关键关系：多态的使用

这是理解它们之间关系的关键点：

```java
ObjectMapper mapper = new ObjectMapper();
String json = "{\"key\":\"value\"}";

// 解析时，实际返回的是ObjectNode，但我们通常用JsonNode接收
JsonNode readOnlyView = mapper.readTree(json); // 实际是ObjectNode，但向上转型了

// 这很安全，因为我们只想读取
String value = readOnlyView.get("key").asText();

// 但如果需要修改，就必须向下转型
if (readOnlyView instanceof ObjectNode) {
    ObjectNode writableNode = (ObjectNode) readOnlyView; // 向下转型
    writableNode.put("newKey", "newValue"); // 现在可以修改了
}
```

#### 实际应用场景

##### 场景1：只读取JSON数据（使用JsonNode）
```java
public void processUserData(String json) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(json);
    
    // 只需要读取，不需要修改
    if (root.has("email")) {
        String email = root.get("email").asText();
        sendNotification(email);
    }
}
```

##### 场景2：动态构建JSON（使用ObjectNode）
```java
public ObjectNode buildResponse(boolean success, String message, Object data) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode response = mapper.createObjectNode();
    
    response.put("success", success);
    response.put("message", message);
    response.putPOJO("data", data); // 添加复杂对象
    response.put("timestamp", System.currentTimeMillis());
    
    return response;
}
```

##### 场景3：修改现有的JSON
```java
public String updateUserStatus(String userJson, String newStatus) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    
    // 需要修改，所以明确用ObjectNode接收
    ObjectNode userNode = (ObjectNode) mapper.readTree(userJson);
    
    userNode.put("status", newStatus);
    userNode.put("lastUpdated", Instant.now().toString());
    userNode.remove("oldField"); // 删除旧字段
    
    return mapper.writeValueAsString(userNode);
}
```

#### 总结

| 选择依据                       | 使用                                             |
| :----------------------------- | :----------------------------------------------- |
| **只需要读取、查询、遍历JSON** | **`JsonNode`**（更通用、更安全）                 |
| **需要创建、修改、构建JSON**   | **`ObjectNode`**（必须使用这个具体类型）         |
| **从JSON字符串解析**           | 开始用 `JsonNode`，需要修改时转型为 `ObjectNode` |
| **从头创建JSON对象**           | 直接创建 `ObjectNode`                            |

**简单记忆：** 把 `JsonNode` 当作 `Map<String, Object>` 的只读视图，把 `ObjectNode` 当作可读写的 `HashMap<String, Object>`。

### 测试 `json array` 字符串转换为 `java List`

>使用 `ObjectMapper#readValue(json, new TypeReference<List<BeanClass>>() {})` 函数。
>
>Convert JSON Array to a Java Array or List with Jackson：https://stackabuse.com/converting-json-array-to-a-java-array-or-list

```java
/**
 * 测试 json array 字符串转换为 java List
 */
@Test
public void testJsonArrayToJavaList() throws IOException {
    long userId = 12345l;
    String loginname = "dexter";
    boolean enable = true;
    LocalDateTime createTime = LocalDateTime.now();

    List<BeanClass> beanClasseList = new ArrayList<BeanClass>();
    for (int i = 1; i <= 5; i++) {
        BeanClass beanClass = new BeanClass();
        beanClass.setUserId(userId);
        beanClass.setLoginname(loginname + "#" + i);
        beanClass.setEnable(enable);
        beanClass.setCreateTime(createTime);
    }

    ObjectMapper OMInstance = new ObjectMapper();
    String json = OMInstance.writeValueAsString(beanClasseList);

    List<BeanClass> beanClassListR = OMInstance.readValue(json, new TypeReference<List<BeanClass>>() {
    });

    Assert.assertEquals(beanClasseList.size(), beanClassListR.size());
    for (int i = 0; i < beanClasseList.size(); i++) {
        Assert.assertEquals(beanClasseList.get(i).getUserId(), beanClassListR.get(i).getUserId());
        Assert.assertEquals(beanClasseList.get(i).getLoginname(), beanClassListR.get(i).getLoginname());
        Assert.assertEquals(beanClasseList.get(i).isEnable(), beanClassListR.get(i).isEnable());
        Assert.assertEquals(beanClasseList.get(i).getCreateTime(), beanClassListR.get(i).getCreateTime());
    }
}
```



### 测试 `ArrayNode` 用法和转换为 `Stream`

>使用 `(ArrayNode) jsonNode.get("data")` 强制转换为 `ArrayNode`。

```java
/**
 * 测试 ArrayNode 用法和转换为 Stream
 *
 * @throws Exception
 */
@Test
public void testArrayNodeToStream() throws Exception {
    Map<String, List<String>> testMapper = new HashMap<>();
    testMapper.put("data", Arrays.asList("1", "2", "3"));
    ObjectMapper objectMapper = new ObjectMapper();
    String JSON = objectMapper.writeValueAsString(testMapper);
    JsonNode jsonNode = objectMapper.readTree(JSON);
    ArrayNode arrayNode = (ArrayNode) jsonNode.get("data");

    // https://stackoverflow.com/questions/32683785/create-java-8-stream-from-arraynode
    List<String> list = StreamSupport.stream(arrayNode.spliterator(), false).map(JsonNode::asText).collect(Collectors.toList());
    Assert.assertArrayEquals(new String[]{"1", "2", "3"}, list.toArray(new String[]{}));
}
```



### `LocalDateTime`、`LocalDate`、`LocalTime` 序列化配置

>`https://blog.csdn.net/REX1024/article/details/123657816`

`POM` 配置

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.9.4</version>
</dependency>
```

配置 `LocalDateTime` 字段序列化和反序列化

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

### 自定义序列化逻辑