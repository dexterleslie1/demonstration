# `kafka`



## 概念

### `Topic` 和 `Partition`

**Kafka Topic 是什么？**

Kafka Topic 是 Kafka 消息系统的核心概念之一，用于对消息进行分类和存储。以下是 Kafka Topic 的详细解释：

------

**1. Topic 的基本定义**

- **Topic 是消息的逻辑分类**：
  Kafka 中的消息（数据）按照 Topic 进行分类。例如，电商系统可以有 `orders`（订单）、`payments`（支付）、`shipments`（发货）等 Topic。
- **Topic 是消息的存储单元**：
  每个 Topic 是一个逻辑上的容器，消息会被持久化存储在 Kafka 集群中，直到被消费者消费或达到配置的保留时间（Retention Time）。

------

**2. Topic 的核心特性**

- 多生产者、多消费者：
  - 生产者（Producer）可以向同一个 Topic 发送消息。
  - 消费者（Consumer）可以从同一个 Topic 订阅消息。
- 分区（Partition）机制：
  - 一个 Topic 可以划分为多个分区（Partition），分区是消息的实际存储单元。
  - 分区可以分布在不同的 Broker 上，实现消息的并行处理和扩展性。
- 消息有序性：
  - 在单个分区内，消息是有序的（按写入顺序存储）。
  - 不同分区之间的消息是无序的。
- 消息持久化：
  - 消息会被持久化存储在磁盘上，直到达到配置的保留时间或磁盘空间不足。

------

**3. Topic 的结构**

- 分区（Partition）：
  - 一个 Topic 由一个或多个分区组成。
  - 每个分区是一个有序的、不可变的消息序列。
  - 分区可以分布在不同的 Broker 上，实现负载均衡和容错。
- 副本（Replica）：
  - 每个分区可以有多个副本（Replica），其中一个副本是 Leader，其他副本是 Follower。
  - Leader 负责处理读写请求，Follower 同步 Leader 的数据。
  - 副本机制提高了 Kafka 的容错性。
- 偏移量（Offset）：
  - 每个消息在分区中有一个唯一的偏移量（Offset），用于标识消息的位置。
  - 消费者通过偏移量来跟踪已消费的消息。

------

**4. Topic 的使用场景**

- **日志收集**：
  将应用程序的日志发送到 Kafka Topic，供日志分析系统消费。
- **消息队列**：
  作为消息队列，解耦生产者和消费者。
- **流处理**：
  Kafka Streams 或 Flink 等流处理框架可以从 Topic 读取数据，进行处理后再写入其他 Topic。
- **事件溯源**：
  将系统中的事件（如订单创建、支付完成）发送到 Kafka Topic，供其他系统消费。

------

**5. Topic 的创建与管理**

- 创建 Topic：

  使用 Kafka 命令行工具或 API 创建 Topic，可以指定分区数、副本数等参数。

  ```bash
  /usr/bin/kafka-topics --bootstrap-server localhost:9092 --create --topic my-topic --partitions 3 --replication-factor 1
  ```

- 查看 Topic：

  ```bash
  /usr/bin/kafka-topics --bootstrap-server localhost:9092 --list
  /usr/bin/kafka-topics --bootstrap-server localhost:9092 --describe --topic my-topic
  ```

- 删除 Topic：

  ```bash
  /usr/bin/kafka-topics --bootstrap-server localhost:9092 --delete --topic my-topic
  ```

------

**6. Topic 的配置参数**

- 分区数（Partitions）：
  - 决定 Topic 的并行处理能力。
  - 分区数越多，吞吐量越高，但也会增加系统复杂度。
- 副本数（Replication Factor）：
  - 决定 Topic 的容错性。
  - 副本数越多，容错性越高，但也会增加存储开销。
- 保留时间（Retention Time）：
  - 消息在 Kafka 中保留的时间（默认 7 天）。
- 保留大小（Retention Size）：
  - 消息在 Kafka 中保留的总大小（默认无限制）。

------

**7. Topic 的示例**

假设有一个电商系统，可以创建以下 Topic：

- orders：存储订单数据。
  - 分区数：3
  - 副本数：2
- payments：存储支付数据。
  - 分区数：2
  - 副本数：2
- shipments：存储发货数据。
  - 分区数：1
  - 副本数：1

生产者将订单、支付、发货数据分别发送到对应的 Topic，消费者（如订单处理系统、支付系统、物流系统）从对应的 Topic 订阅消息。

------

**8. Topic 的优势**

- **高吞吐量**：
  通过分区和副本机制，Kafka 可以实现高吞吐量的消息处理。
- **可扩展性**：
  可以通过增加分区或 Broker 来扩展 Topic 的处理能力。
- **容错性**：
  通过副本机制，Kafka 可以保证消息的可靠性。
- **持久化**：
  消息会被持久化存储，适合需要长期存储数据的场景。

------

**总结**

Kafka Topic 是 Kafka 消息系统的核心概念，用于对消息进行分类和存储。它具有多生产者、多消费者、分区、副本、持久化等特性，适用于日志收集、消息队列、流处理、事件溯源等多种场景。通过合理配置 Topic 的分区数、副本数等参数，可以实现高吞吐量、可扩展性和容错性。



## `Docker Compose` 运行

> [参考链接](https://blog.csdn.net/yudaonihaha/article/details/130768061)
>
> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

`.env`：

```properties
# 设置为 broker 所在实例的 ip 地址
kafka_advertised_listeners=192.168.1.x

```

`docker-compose.yml`：

```yaml
version: "3.8"

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.0
    environment:
      TZ: Asia/Shanghai
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      # todo 设置 ZooKeeper 的 JVM 堆内存
      # ZOOKEEPER_HEAP_OPTS: "-Xms1g -Xmx1g"
    ports:
      - "2181:2181"
  kafka:
    image: confluentinc/cp-kafka:7.3.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      # 映射 JMX 端口到主机
      - "9997:9997"
    environment:
      TZ: Asia/Shanghai
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://${kafka_advertised_listeners}:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      # 设置 Kafka 的 JVM 堆内存
      KAFKA_HEAP_OPTS: "-Xms1g -Xmx1g"
      # 配置 JMX 端口和认证，配置了 jmx 端口才能够被外部工具监控
      KAFKA_JMX_OPTS: "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.port=9997 -Dcom.sun.management.jmxremote.rmi.port=9997"
      # 禁用自动创建 Topic，否则 Spring Boot 会自动创建 partitions=0 的 topic
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "false"
  # 在 kafka 服务成功启动后自动配置 kafka 服务
  kafka-topic-config:
    image: confluentinc/cp-kafka:7.3.0
    depends_on:
      - kafka
    environment:
      TZ: Asia/Shanghai
    # 自动创建 topic
    entrypoint: /usr/bin/kafka-topics --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 128 --topic my-topic-1
  kafka-manager:
    image: sheepkiller/kafka-manager:latest
    ports:
      - "9000:9000"
    environment:
      TZ: Asia/Shanghai
      ZK_HOSTS: zookeeper:2181
      APPLICATION_SECRET: "123456"

```

启动 `kafka` 服务

```sh
docker compose up -d
```

进入 `kafka` 容器查看服务是否正常运行，下面命令如果没有报错则服务正常运行。

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --bootstrap-server localhost:9092 --list
```

如果启动失败查看 `kafka` 启动日志

```sh
docker compose logs -f kafka
```

或者使用生产和消费测试服务是否正常：

-  启动消费端，`--from-beiginning`表示所有消息重新接收一次

  ```sh
  KAFKA_JMX_OPTS="" /usr/bin/kafka-console-consumer --topic topic1 --bootstrap-server localhost:9092 --from-beginning
  ```

- 启动生产端

  ```sh
  KAFKA_JMX_OPTS="" /usr/bin/kafka-console-producer --topic topic1 --bootstrap-server localhost:9092
  ```

  



## 监控

### `kafka-manager`

>通过 `kafka-manager` 可以查看消息的生成速度，但是不能直接查看消息的速度。

参考下面的 `Docker Compose` 片段配置并运行 `kafka-manager`

```yaml
kafka-manager:
    image: sheepkiller/kafka-manager:latest
    ports:
      - "9000:9000"
    environment:
      TZ: Asia/Shanghai
      ZK_HOSTS: zookeeper:2181
      APPLICATION_SECRET: "123456"
```

访问 `http://192.168.1.x:9000/` 访问并配置 `kafka-manager`：

- 新增 `kafka` 集群

  点击功能 `Cluster` > `Add Cluster`，配置信息如下：

  - `Cluster Name`：随意填写
  - `Cluster Zookeeper Hosts`：填写 `zookeeper` 的 `ip` 地址，例如：`zookeeper:2181`
  - `Kafka Verson`：选择 `0.9.0.1`
  - `Enable JMX Polling`：勾选
  - 其他默认值。

  点击 `Save` 保存。



## 管理

### `Topic`

借助本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark) 做下面的测试。

创建名为 `topic1`，分区数为`1`，副本数为`1`的 `topic`：

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic topic1
```

显示 `topic1` 详情

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic topic1
```

修改 `topic1` 分区数

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --alter --bootstrap-server localhost:9092 --topic topic1 --partitions 128
```

查询所有 `topic` 列表

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --bootstrap-server localhost:9092 --list
```

删除 `topic`

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --delete --bootstrap-server localhost:9092 --topic topic1
```



## `Spring Boot` 操作 `kafka`

### 集成

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

`POM` 配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>2.7.18</version>
    </parent>

    <groupId>com.future.demo</groupId>
    <artifactId>demo-kafka-benchmark-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
       <module>service</module>
       <module>crond</module>
       <module>common</module>
    </modules>

    <properties>
       <maven.compiler.target>1.8</maven.compiler.target>
       <maven.compiler.source>1.8</maven.compiler.source>
    </properties>

    <dependencies>
       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-test</artifactId>
          <scope>test</scope>
       </dependency>
       <dependency>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
          <version>1.18.8</version>
       </dependency>

       <dependency>
          <groupId>org.springframework.kafka</groupId>
          <artifactId>spring-kafka</artifactId>
       </dependency>

       <dependency>
          <groupId>com.github.dexterleslie1</groupId>
          <artifactId>future-common</artifactId>
          <version>1.2.2</version>
       </dependency>
    </dependencies>

    <repositories>
       <repository>
          <id>aliyun</id>
          <url>https://maven.aliyun.com/repository/public</url>
       </repository>
       <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
       </repository>
    </repositories>
</project>
```

`application.properties`：

```properties
spring.kafka.bootstrap-servers=${kafka_bootstrap_servers:localhost}:9092
spring.kafka.consumer.group-id=my-group
# 设置 consumer 每次 poll 返回的最大记录数
spring.kafka.consumer.max-poll-records=1024
```

消费者配置：

```java
@Configuration
@Slf4j
public class Config {
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // 根据你的 Kafka 服务器地址设置
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "your-group-id"); // 设置消费者组 ID
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000); // 设置每次 poll 返回的最大记录数
//        return new DefaultKafkaConsumerFactory<>(props);
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数，需要设置 topic 分区数不为 0 才能并发消费消息。
        factory.setConcurrency(256);
        return factory;
    }

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @KafkaListener(topics = TopicName)
    public void receiveMessage(List<String> messages) throws InterruptedException {
        log.info("concurrent=" + this.concurrentCounter.incrementAndGet() + ",size=" + messages.size() + ",total=" + counter.addAndGet(messages.size()));

        TimeUnit.MILLISECONDS.sleep(500);

        this.concurrentCounter.decrementAndGet();
    }

}
```

生产者配置：

```java
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("send")
    public ObjectResponse<String> send() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplate.send(TopicName, message).get();
        return ResponseUtils.successObject("消息发送成功");
    }
}
```



### 使用 `KafKaAdmin` 修改 `Topic` 分区数

```java
@Component
public class TopicInitializer implements CommandLineRunner {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Override
    public void run(String... args) {
        NewTopic newTopic = new NewTopic(TopicName, 128, (short) 1);
        kafkaAdmin.createOrModifyTopics(newTopic);
    }
}
```

- 上面的代码修改 `Topic` 的分区数为 `128`，副本数为 `1`。
