# RocketMQ

>[参考官方文档](https://rocketmq.apache.org/zh/docs/4.x/)



## 部署

### Docker Compose 部署

>[参考官方文档](https://rocketmq.apache.org/zh/docs/4.x/quickstart/03quickstartWithDockercompose)

docker-compose.yaml 内容如下：

```yaml
version: '3.8'

services:
  namesrv:
    image: apache/rocketmq:4.9.6
    ports:
      - 9876:9876
    command: sh mqnamesrv
    # network_mode: host

  broker:
    image: apache/rocketmq:4.9.6
    ports:
      - 10909:10909
      - 10911:10911
      - 10912:10912
    environment:
      - NAMESRV_ADDR=namesrv:9876
    volumes:
      - ./broker.conf:/home/rocketmq/rocketmq-4.9.6/conf/broker.conf
    depends_on:
      - namesrv
    command: sh mqbroker -c /home/rocketmq/rocketmq-4.9.6/conf/broker.conf
    # network_mode: host

  dashboard:
    image: apacherocketmq/rocketmq-dashboard:latest
    ports:
      - 8080:8080
    environment:
      - JAVA_OPTS=-Drocketmq.namesrv.addr=namesrv:9876
    # network_mode: host

```

broker.conf 内容如下：

```
# 增大队列容量
maxMessageSize=16777216
```

启动服务

```sh
docker compose up -d
```

登录 broker 容器测试服务是否正常

```sh
docker compose exec -it broker bash

# 测试服务是否正常
sh tools.sh org.apache.rocketmq.example.quickstart.Producer
```

访问 dashboard，`http://localhost:8081/#/`



## Dashboard

>[参考官方文档](https://rocketmq.apache.org/zh/docs/4.x/deployment/03Dashboard)

Docker Compose 运行 dashboard

```yaml
version: '3.8'

services:
  dashboard:
    image: apacherocketmq/rocketmq-dashboard:latest
    ports:
      - 8080:8080
    environment:
      - JAVA_OPTS=-Drocketmq.namesrv.addr=namesrv:9876
    # network_mode: host

```

访问 dashboard，`http://localhost:8081/#/`



## SpringBoot

### 配置和使用

>[参考官方文档](https://rocketmq.apache.org/zh/docs/4.x/quickstart/03quickstartWithDockercompose#5sdk%E6%B5%8B%E8%AF%95%E6%B6%88%E6%81%AF%E6%94%B6%E5%8F%91)

POM 配置

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.9.6</version>
</dependency>
```

Java 配置

```java
@Configuration
public class Config {
    public final static String ProducerAndConsumerGroup = "demo-producer-and-consumer-group";
    public final static int TotalMessageCount = 100;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQProducer producer() {
        // 创建生产者实例，并设置生产者组名
        DefaultMQProducer producer = new DefaultMQProducer(ProducerAndConsumerGroup);
        // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
        producer.setNamesrvAddr("localhost:9876");
        return producer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQPushConsumer consumer(AtomicInteger counter) throws MQClientException {
        // 创建消费者实例，并设置消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(ProducerAndConsumerGroup);
        // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
        consumer.setNamesrvAddr("localhost:9876");
        // 订阅指定的主题和标签（* 表示所有标签）
        consumer.subscribe("TestTopic", "*");
        // 支持批量处理消息
        consumer.setConsumeMessageBatchMaxSize(256);
        // 设置并发线程数
        consumer.setConsumeThreadMin(16);
        consumer.setConsumeThreadMax(64);

        // 注册消息监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                /*System.out.println("Received message: " + new String(msg.getBody()));*/
                counter.incrementAndGet();
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        return consumer;
    }

    @Bean
    public AtomicInteger counter() {
        return new AtomicInteger();
    }
}
```

测试

```java
@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationTests {

    @Resource
    DefaultMQProducer producer;
    @Resource
    AtomicInteger counter;

    @Test
    public void test1() throws InterruptedException, MQBrokerException, RemotingException, MQClientException {
        // 并发发送消息以测试批量处理消息
        AtomicInteger iCounter = new AtomicInteger();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 8; i++) {
            threadPool.submit(() -> {
                try {
                    while (iCounter.getAndIncrement() < Config.TotalMessageCount) {
                        // 创建消息实例，指定 topic、Tag和消息体
                        Message msg = new Message("TestTopic", "TagA", ("Hello RocketMQ").getBytes());
                        // 发送消息并获取发送结果
                        SendResult sendResult = producer.send(msg);
                        Assertions.assertEquals(SendStatus.SEND_OK, sendResult.getSendStatus());
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            });
        }

        TimeUnit.SECONDS.sleep(3);

        Assertions.assertEquals(Config.TotalMessageCount, counter.get());
    }
}
```



## 常见错误处理



### 错误1

>注意：todo 没有根本解决。

错误信息：

```java
org.apache.rocketmq.client.exception.MQBrokerException: CODE: 2  DESC: [TIMEOUT_CLEAN_QUEUE]broker busy, start flow control for a while, period in queue: 201ms, size of queue: 288 BROKER: 192.168.1.190:10911
```

错误解决，在 broker.conf 配置中新增如下配置：

```
# 增加处理消息的线程数
sendMessageThreadPoolNums=32
```



## 基准测试

### 配置

如下：

- 测试平台 VMware ESXi, 7.0.3, 20328353、Intel(R) Xeon(R) Platinum 8269CY CPU @ 2.50GHz
- 4个8C6G实例，用于运行 demo-spring-rocketmq-benchmark 应用
- 1个8C16G实例，用于运行 RocketMQ 服务
- 1个8C4G实例，用于运行 OpenResty 服务
- 1个足够CPU和内存实例，用于运行 wrk 工具。



### 测试结果

消息发送速度

```sh
Running 1m test @ http://192.168.1.185/api/v1/send
  8 threads and 2048 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    25.57ms   44.04ms 462.09ms   91.24%
    Req/Sec    17.85k     1.98k   38.11k    70.19%
  Latency Distribution
     50%   11.93ms
     75%   15.81ms
     90%   58.35ms
     99%  247.21ms
  8525307 requests in 1.00m, 2.02GB read
Requests/sec: 141911.05
Transfer/sec:     34.38MB
```

消息消费速度为 92,650/s。

RocketMQ 实例 CPU 未全部使用

```
    0[|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||                                    61.5%]   4[|||||||||||||||||||||||||||||||||||||||||||||||||||||||||                                              52.4%]
    1[||||||||||||||||||||||||||||||||||||||||||||||||||||||                                                 48.6%]   5[|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||                                          56.0%]
    2[|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||                                  62.2%]   6[|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||                            68.5%]
    3[|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||                                      59.1%]   7[||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||                                           54.7%]
  Mem[|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||5.79G/15.6G] Tasks: 95, 546 thr, 251 kthr; 8 running
  Swp[|                                                                                                 256K/4.93G] Load average: 1.85 1.58 1.47 
                                                                                                                    Uptime: 00:12:45

  [Main] [I/O]
    PID USER       PRI  NI  VIRT   RES   SHR S  CPU%▽MEM%   TIME+  Command
   2330 3000        20   0  104G 13.6G 9291M S 429.1 85.2 22:53.67 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2831 3000        20   0  104G 13.6G 9291M R  57.4 85.2  2:36.90 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2834 3000        20   0  104G 13.6G 9291M S  54.1 85.2  2:34.42 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2786 3000        20   0  104G 13.6G 9291M R  41.9 85.2  1:50.13 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2756 3000        20   0  104G 13.6G 9291M R  31.8 85.2  1:41.18 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2815 3000        20   0  104G 13.6G 9291M R  28.4 85.2  1:39.90 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2787 3000        20   0  104G 13.6G 9291M R  27.0 85.2  1:40.09 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2714 3000        20   0  104G 13.6G 9291M R  19.6 85.2  1:07.48 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2814 3000        20   0  104G 13.6G 9291M R  13.5 85.2  0:45.40 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2757 3000        20   0  104G 13.6G 9291M S   9.5 85.2  0:42.56 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2859 3000        20   0  104G 13.6G 9291M R   6.8 85.2  0:18.68 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2860 3000        20   0  104G 13.6G 9291M S   6.8 85.2  0:18.64 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2737 3000        20   0  104G 13.6G 9291M S   6.1 85.2  0:16.71 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2861 3000        20   0  104G 13.6G 9291M S   6.1 85.2  0:18.55 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2862 3000        20   0  104G 13.6G 9291M S   6.1 85.2  0:18.50 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2840 3000        20   0  104G 13.6G 9291M S   4.1 85.2  0:09.03 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2846 3000        20   0  104G 13.6G 9291M S   4.1 85.2  0:09.06 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2851 3000        20   0  104G 13.6G 9291M S   4.1 85.2  0:09.07 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2857 3000        20   0  104G 13.6G 9291M S   4.1 85.2  0:09.20 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2858 3000        20   0  104G 13.6G 9291M S   4.1 85.2  0:09.11 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2864 3000        20   0  104G 13.6G 9291M S   4.1 85.2  0:09.19 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2828 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:09.06 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2832 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:09.13 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2835 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:09.13 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2837 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:09.01 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2838 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:09.07 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2839 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:08.98 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2853 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:09.06 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2854 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:09.21 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2856 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:09.09 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2865 3000        20   0  104G 13.6G 9291M S   3.4 85.2  0:08.98 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2826 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:09.20 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2829 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:09.11 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2830 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:09.20 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2833 3000        20   0  104G 13.6G 9291M R   2.7 85.2  0:08.98 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2836 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:09.27 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2842 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:09.06 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2843 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:09.14 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2844 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:09.07 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2848 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:08.91 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2852 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:08.98 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2855 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:09.20 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2863 3000        20   0  104G 13.6G 9291M S   2.7 85.2  0:08.99 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
   2827 3000        20   0  104G 13.6G 9291M S   2.0 85.2  0:09.06 /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/bin/java -server -Xms4004M -Xmx4004M -Xmn800M -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercen
```



## 配置项

### broker

#### `maxTransferCountOnMessageInMemory`

在RocketMQ中，`maxTransferCountOnMessageInMemory`是Broker配置文件中的一个重要参数，它用于控制Broker在内存中每次传输的最大消息数量。以下是关于该配置项的详细解释：

参数作用

- **控制消息拉取量**：`maxTransferCountOnMessageInMemory`参数决定了Broker在内存中每次传输给消费者的最大消息数量。如果消费者设置的`pullBatchSize`超过了这个值，Broker仍然只会返回最多`maxTransferCountOnMessageInMemory`条消息。
- **避免内存溢出**：通过限制每次传输的消息数量，可以防止Broker因一次性传输过多消息而导致内存溢出。

配置方法

- **修改配置文件**：要修改`maxTransferCountOnMessageInMemory`的值，需要编辑Broker的配置文件（通常是`broker.conf`）。在配置文件中找到或添加该参数，并设置为其期望的值。例如，将其设置为1024：

  ```plaintext
  maxTransferCountOnMessageInMemory=1024
  ```

- **重启Broker**：修改配置文件后，需要重启Broker服务以使更改生效。

注意事项

- **与客户端参数的关系**：虽然消费者可以通过`pullBatchSize`参数设置每次拉取的消息数量，但这个值不能超过Broker端`maxTransferCountOnMessageInMemory`的设置。因此，在调整客户端参数时，需要确保它们与Broker端的配置相兼容。
- **性能影响**：增加`maxTransferCountOnMessageInMemory`的值可能会提高消息传输的吞吐量，但同时也会增加Broker的内存压力。因此，在调整该参数时，需要根据实际场景和系统资源进行权衡。
- **监控与调优**：在生产环境中，建议对Broker的内存使用情况进行监控，并根据监控结果对`maxTransferCountOnMessageInMemory`参数进行调优。



## 批量消息消费配置调优

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-rocketmq/demo-spring-rocketmq-benchmark)

### 客户端调优

客户端如下：

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.9.6</version>
</dependency>
```

消费者批量消费关键调优配置如下：

```java
// 设置单个批次最大消息数量为1024，下面两个设置需要同时设置，否则设置无效
consumer.setConsumeMessageBatchMaxSize(1024);
consumer.setPullBatchSize(1024);

// 设置并发线程数
consumer.setConsumeThreadMin(16);
consumer.setConsumeThreadMax(128);
```



### `broker` 调优

关键调优配置如下：

```properties
# 控制Broker在内存中每次传输的最大消息数量
maxTransferCountOnMessageInMemory=1024
```



### 调优配置实验

调优配置是通过本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-rocketmq/demo-spring-rocketmq-benchmark) 协助测试出来的。实验步骤如下：

1. 启动应用

2. 使用 `ab` 工具生产100万个消息

   ```sh
   ab -n 1000000 -c 128 -k http://192.168.1.181:8080/api/v1/send
   ```

3. 等待消息消费完毕后访问统计接口 `http://192.168.1.181:8082/api/v1/statistics` 以查看批量处理情况。
