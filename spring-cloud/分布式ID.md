# 分布式 ID



## 什么是分布式 ID？

分布式ID是指在分布式系统中生成的全局唯一的标识符，用于标识不同的实体或数据对象。在分布式系统中，由于数据存储、计算和处理都分散在不同的节点上，因此需要一种可靠的方式来跟踪和标识这些数据对象，分布式ID就是为此而设计的。

### 一、分布式ID的特点

1. **全局唯一性**：每个ID在整个分布式系统中必须是唯一的，不能出现重复。这是分布式ID最基本也是最重要的特性。
2. **高性能与高可用**：在高并发的情况下，分布式ID生成系统需要具备高可用性和低延迟，能够快速生成ID，以满足系统的性能需求。
3. **有序性（可选）**：在某些场景下，生成的ID可能需要具有有序性，方便按照时间排序和索引。但并非所有分布式ID生成方案都保证有序性，这取决于具体的应用需求。

### 二、分布式ID的生成方案

1. **UUID**：UUID（Universally Unique Identifier，通用唯一识别码）是一种由算法生成的唯一的ID。UUID由一组32个十六进制数字组成（通常表示为8-4-4-4-12的格式，如：863e254b-ae34-4371-87da-204b71d46a7b），通常用于保证分布式系统中数据的唯一性。然而，UUID的长度较长，且无序性可能导致数据库索引效率低下。
2. **数据库自增ID**：在单机系统中，可以使用数据库的自增ID作为唯一标识符。但在分布式系统中，由于多个数据库节点可能同时生成ID，因此需要使用特定的策略（如设置不同的起始值和步长）来避免ID冲突。然而，这种方法在扩展性和性能上可能受到限制。
3. **雪花算法（Snowflake Algorithm）**：雪花算法是一种由Twitter开发的分布式ID生成算法，它生成的ID是一个64位的长整型数值。雪花算法通过结合时间戳、机器ID和序列号等元素来确保ID的全局唯一性。此外，雪花算法还具有有序性和高性能的特点，适用于大规模分布式系统中的标识符需求。但需要注意的是，雪花算法依赖于系统时间，因此系统时间的回拨或不同节点之间的时间差异可能会导致生成的ID不唯一或不按照预期顺序递增。
4. **号段模式**：号段模式是一种高效生成分布式唯一ID的方案，它依赖于数据库，通过将分配到的号段缓存到内存中，直接从号段中取值生成ID，从而避免频繁访问数据库。这种方法适用于需要高性能和全局唯一ID的分布式系统场景。但需要注意的是，号段模式需要合理设置号段的大小和更新策略，以避免ID的浪费和冲突。

### 三、分布式ID的应用场景

分布式ID广泛应用于各种分布式系统中，如数据库主键、订单号、用户ID等场景。在这些场景中，需要确保生成的ID具有全局唯一性、高性能和可扩展性等特点，以满足系统的稳定性和性能需求。

综上所述，分布式ID是分布式系统中不可或缺的一环，其设计直接影响系统的性能和稳定性。在实际应用中，需要根据具体的应用需求和场景选择合适的生成方案。



## 分布式 ID 生成方式

分布式ID的生成方式有多种，每种方式都有其独特的优点和适用场景。以下是一些常见的分布式ID生成方式：

### 1. UUID（通用唯一识别码）

- **原理**：UUID是通过一定的算法生成的128位数字，通常包括网卡MAC地址、时间戳、名字空间（Namespace）、随机或伪随机数、时序等元素。
- **优点**：
  - 本地生成ID，不需要进行远程调用，时延低，性能高。
  - 全球唯一，数据迁移容易。
- **缺点**：
  - UUID过长，生成的是字符串，存储空间高。
  - MySQL 数据量大时，因为 MySQL 使用 B+ 树索引结构会导致数据插入变慢。
  - 不是有序的，无法保证趋势递增。
  - 可读性差，不体现业务信息。

### 2. 数据库自增ID

- **原理**：利用数据库的自增策略（如MySQL的auto_increment）来实现分布式ID。
- **优点**：
  - 主键自动增长，不用手工设值。
  - 数字型，占用空间小，检索有利。
  - 绝对有序。
- **缺点**：
  - 并发性能不高，受限于数据库性能。
  - 不太适合重构的系统，因为涉及旧数据迁移容易ID冲突。
  - 暴露商业信息，例如可以推断出订单量。

### 3. 数据库集群模式

- **原理**：通过多个数据库实例设置不同的起始值和步长来生成全局唯一的ID。
- **优点**：
  - 可以有效生成集群中的唯一ID。
  - 解决了单点的问题。
  - 降低ID生成数据库操作的负载。
- **缺点**：
  - 需要独立部署多个数据库实例，成本高。
  - 后期不方便扩展。

### 4. 数据库号段模式

- **原理**：直接从数据库生成批量的ID到本地，直到本地ID号段用完再申请。
- **优点**：
  - 速度快。
  - 如果生成ID的服务器宕机，本地ID可以支撑一段时间。
- **缺点**：
  - 如果生成ID的服务器宕机且本地ID用尽，会出现ID大量空的情况。
  - 存储空间高。
  - 在服务器重启或故障转移等情况下，可能会导致ID的生成出现不连续的情况。

### 5. Redis实现的分布式ID

- **原理**：利用Redis的自增命令incr生成全局唯一ID。具体实现方式是，在Redis中维护一个自增的计数器，每次生成ID时，从Redis中获取计数器的值，然后将其加一并更新回Redis。
- **优点**：
  - 高性能、可扩展性强。
  - 唯一有序的。
- **缺点**：
  - 需要依赖Redis集群，否则存在单点问题。
  - 可能存在ID冲突的风险（如果Redis集群设计不当）。

### 6. 雪花算法（Snowflake Algorithm）

- **原理**：雪花算法是一种生成分布式全局唯一ID的算法，生成的ID称为Snowflake IDs。这种算法由Twitter创建，并用于推文的ID。一个Snowflake ID有64位，其中前41位是时间戳，表示了自选定的时期以来的毫秒数；接下来的10位代表计算机ID，防止冲突；其余12位代表每台机器上生成ID的序列号，这允许在同一毫秒内创建多个Snowflake ID。
- **优点**：
  - 生成的ID全局唯一、趋势递增。
  - 性能高，可扩展性强。
- **缺点**：
  - 需要时钟回拨处理机制。
  - 依赖机器ID和数据中心ID的分配。

### 7. 基于Zookeeper的顺序节点

- **原理**：利用Zookeeper的顺序节点特性来生成全局唯一ID。
- **优点**：
  - 利用Zookeeper的集群特性保证高可用。
  - ID全局唯一。
- **缺点**：
  - 需要依赖Zookeeper集群。
  - 可能会受到Zookeeper性能的限制。
  - 并发竞争较大不适合用Zookeeper。

### 8. 百度uid-generator

- **原理**：基于Twitter的Snowflake算法进行改进，增加了更多的配置和灵活性。uid-generator支持自定义时间戳、工作机器ID和序列号等各部分的位数，而且uid-generator中采用用户自定义workId的生成策略。
- **优缺点**：类似Snowflake算法，但配置更灵活。

### 9. 美团Leaf

- **原理**：Leaf是美团点评开源的分布式ID生成系统，包含基于数据库和基于Zookeeper的两种实现方式。
- **特点**：根据具体业务需求，可以选择基于数据库或Zookeeper的实现方式。

综上所述，分布式ID的生成方式多种多样，每种方式都有其独特的优点和适用场景。在选择具体的生成方式时，需要根据系统的具体需求和环境来决定。



## 采用基于雪花算法的分布式 ID

### 基于雪花算法 ID 生成实现

在Java中，基于雪花算法（Snowflake）的ID生成实现主要有几种不同的方式，这些实现通常都遵循雪花算法的核心思想，即将一个64位的ID分为多个部分，分别表示时间戳、机器ID和序列号等信息，以确保生成的ID在分布式系统中具有全局唯一性、时间有序性和高性能。以下是一些常见的Java实现方式：

一、自定义实现

开发者可以根据自己的需求，自定义实现雪花算法。这种实现方式通常涉及以下几个步骤：

1. **定义ID结构**：确定64位ID中各部分所占的位数，如41位时间戳、10位机器ID和12位序列号等。
2. **生成时间戳**：获取当前时间的毫秒值，并减去一个固定的起始时间戳，以缩短时间戳的长度并满足存储需求。
3. **生成机器ID**：根据机器的IP地址、MAC地址或其他唯一标识生成机器ID，确保每个节点生成的ID都是唯一的。
4. **生成序列号**：在同一毫秒内生成多个ID时，使用序列号进行区分。序列号通常是一个自增的计数器，当达到最大值时，需要等待下一毫秒再继续生成。
5. **组装ID**：将时间戳、机器ID和序列号按位拼接成一个64位的整数ID。

二、使用开源库

除了自定义实现外，开发者还可以使用一些开源的Java库来实现雪花算法。这些库通常提供了更为完善的功能和更好的性能表现。例如：

1. **Hutool**：Hutool是一个Java工具包，其中包含了雪花算法的实现。使用Hutool可以很方便地生成全局唯一的ID。
2. **Baidu-UidGenerator**：这是百度开源的一个全局唯一ID生成器，基于雪花算法进行了优化和扩展，支持多数据中心和高并发场景。
3. **Twitter的Snowflake**：虽然Twitter原版的Snowflake算法是用Scala实现的，但Java社区也提供了很多基于该算法的开源实现，可以直接集成到Java项目中使用。

三、注意事项

在使用雪花算法生成ID时，需要注意以下几点：

1. **时钟回拨问题**：由于系统时钟可能出现回拨现象，因此在生成ID时需要处理时钟回拨的情况，以避免生成重复的ID。
2. **机器ID的唯一性**：确保每个节点的机器ID都是唯一的，这是生成全局唯一ID的关键。
3. **序列号溢出问题**：在同一毫秒内生成的ID数量有限，当序列号达到最大值时，需要等待下一毫秒再继续生成。因此，在高并发场景下，需要合理设置序列号的长度和最大值。

综上所述，Java中基于雪花算法的ID生成实现方式多种多样，开发者可以根据自己的需求和项目特点选择适合的实现方式。无论是自定义实现还是使用开源库，都需要注意处理时钟回拨、机器ID唯一性和序列号溢出等问题，以确保生成的ID具有全局唯一性、时间有序性和高性能。



### 自己实现

>`https://www.cnblogs.com/relucent/p/4955340.html`

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/demo-distributed-id-twitter`

注意：似乎雪花算法有问题，生成的分布式ID总是偶数的，所以在 Sharding-JDBC 中无法使用此工具生成分布式ID并应用。



### Hutool 工具包

>详细用法请参考示例中的`https://gitee.com/dexterleslie/demonstration/blob/master/demo-java/demo-library/demo-hutool/src/test/java/com/future/demo/Tests.java`中的 testIdUtil 测试用例

注意：似乎雪花算法有问题，生成的分布式ID总是偶数的，所以在 Sharding-JDBC 中无法使用此工具生成分布式ID并应用。

```java
// 在分布式环境中，唯一ID生成应用十分广泛，生成方法也多种多样，Hutool针对一些常用生成策略做了简单封装。
// https://doc.hutool.cn/pages/IdUtil/
@Test
public void testIdUtil() {
    // 根据本地网卡 MAC 地址计算数据中心 ID
    // WORKER_ID 为 Snowflake 中的 MAX_WORKER_ID
    Snowflake snowflake = IdUtil.getSnowflake();
    long id = snowflake.nextId();
    long id2 = snowflake.nextId();
    long id3 = IdUtil.getSnowflakeNextId();

    Assert.assertEquals(snowflake.getDataCenterId(id), snowflake.getDataCenterId(id2));
    Assert.assertEquals(snowflake.getWorkerId(id), snowflake.getWorkerId(id2));

    Assert.assertEquals(snowflake.getDataCenterId(id), snowflake.getDataCenterId(id3));
    Assert.assertEquals(snowflake.getWorkerId(id), snowflake.getWorkerId(id3));
}
```



### 百度 uid-generator

>todo 配置麻烦没有做实验测试。
>
>`https://github.com/baidu/uid-generator/`



### 美团 Leaf

>`https://github.com/Meituan-Dianping/Leaf/blob/feature/spring-boot-starter/README_CN.md`

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/demo-distributed-id-leaf`



#### 配置和使用

美团 Leaf 雪花算法依赖于 zookeeper

zookeeper 服务 docker-compose.yaml 配置如下：

```yaml
version: "3.0"

services:
  demo-zookeeper:
    image: zookeeper:3.8.4
    environment:
      - TZ=Asia/Shanghai
      - JVMFLAGS=-Xmx512m -Xms512m -server
    network_mode: host
```

新增 pom 配置如下：

```xml
<!-- 分布式ID leaf 依赖 -->
<dependency>
    <!-- 官方依赖没有发布到 maven 仓库中 -->
    <groupId>com.tencent.devops.leaf</groupId>
    <artifactId>leaf-boot-starter</artifactId>
    <version>1.0.2-RELEASE</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>5.7.1</version>
</dependency>
```

应用 application.properties 配置如下：

```properties
# 启用雪花算法生成分布式ID
leaf.snowflake.enable=true
# zookeeper地址
leaf.snowflake.address=127.0.0.1:2181
```

Application 添加 @EnableLeafServer 注解

```java
@SpringBootApplication
@EnableLeafServer
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

应用中使用 Leaf 生成分布式ID

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ApplicationTests {

    @Autowired
    SnowflakeService snowflakeService;

    @Test
    public void contextLoads() throws InterruptedException {
        int evenCounter = 0;
        int oddsCounter = 0;
        for (int i = 0; i < 100; i++) {
            Result result = this.snowflakeService.getId("x");
            log.debug("status {}, id {}", result.getStatus(), result.getId());
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(1, 100));

            if (result.getId() % 2 == 0) {
                evenCounter++;
            } else {
                oddsCounter++;
            }
        }

        log.debug("even {}, odds {}", evenCounter, oddsCounter);
    }
}
```



#### 避免 snowflake workerId 重复

通过随机生成 leaf.snowflake.port 避免 workderId 重复

```properties
# Leaf 通过第一个网卡 ip地址+leaf.snowflake.port 生成 wokerId，
# 如果在同一台主机中运行两个 Leaf 服务，则 workerId 会重复，
# 通过手动配置随机生成 leaf.snowflake.port 避免重复的 workerId
leaf.snowflake.port=${random.int%1000000000}
```