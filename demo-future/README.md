# `future`



## `random-id-picker` 服务

>中文名称为随机 `id` 选择器服务。
>
>背景：在海量订单或者商品数据场景中，需要模拟根据订单 `ID` 或者商品 `ID` 列表查询订单或者商品信息。此时需要借助随机 `ID` 选择器按照一定的频率随机抽取 `ID` 列表。

### 服务组件

`future-random-id-picker` 组件：

>使用 `SpringBoot` 实现以 `restful` 方式提供接口的核心服务。

- `GitHub` 地址：`https://github.com/dexterleslie1/future-random-id-picker.git`



`future-random-id-picker-sdk` 组件：

>`SpringBoot` 应用集成 `random-id-picker` 服务使用的 `sdk`。

- `GitHub` 地址：`https://github.com/dexterleslie1/future-random-id-picker-sdk.git`



### `Docker Compose` 运行服务

`docker-compose.yaml` 如下：

```yaml
version: "3.1"

services:
  # 随机 id 选择器服务
  future-random-id-picker-api:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/random-id-picker-service
    environment:
      - JAVA_OPTS=-Xmx512m
      - TZ=Asia/Shanghai
      - db_host=future-random-id-picker-db
      - db_port=3306
    ports:
      - '50000:8080'
  future-random-id-picker-db:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/random-id-picker-db
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_general_ci
      - --skip-character-set-client-handshake
      - --innodb-buffer-pool-size=256m
    environment:
      - LANG=C.UTF-8
      - TZ=Asia/Shanghai
      - MYSQL_ROOT_PASSWORD=123456

```



### `SpringBoot` 应用集成

`POM` 配置片段：

```xml
<!-- 随机 id 选择器服务 -->
<dependency>
    <groupId>com.github.dexterleslie1</groupId>
    <artifactId>future-random-id-picker-sdk</artifactId>
    <version>1.0.2</version>
</dependency>

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

`application.properties` 中配置随机 `id` 选择器服务：

```properties
# 随机id选择器服务ip地址
spring.future.random.id.picker.host=${random_id_picker_host:localhost}
# 随机id选择器服务端口
spring.future.random.id.picker.port=${random_id_picker_port:50000}
# 随机id选择器服务本地缓存id最大总数
spring.future.random.id.picker.cache-size=102400
# 随机id选择器服务支持的flag列表
spring.future.random.id.picker.flag-list=order,product
```

在 `SpringBoot Application` 中启用随机 `id` 选择器服务：

```java
@SpringBootApplication
@EnableFutureRandomIdPicker
public class ApplicationService {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationService.class, args);
    }
}
```

使用随机 `id` 服务接口：

```java
@Resource
RandomIdPickerService randomIdPickerService;
```

