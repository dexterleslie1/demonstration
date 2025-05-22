# RabbitMQ



## 部署

### 基于 Docker

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples)

通过 `https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases` 下载 rabbitmq-delayed-message-exchanges 插件 ` rabbitmq-delayed-message-exchange v3.8.9`

Dockerfile 内容如下：

```dockerfile
FROM library/rabbitmq:3.7.16-rc.3-management

RUN apt-get update
RUN apt-get install curl -y
RUN curl --silent --output /tmp/rabbitmq_delayed_message_exchange-3.8.9-0199d11c.ez https://fut001.oss-cn-hangzhou.aliyuncs.com/rabbitmq/rabbitmq_delayed_message_exchange-3.8.9-0199d11c.ez

#将队列延迟插件拷贝到rabbitmq插件目录
RUN mv /tmp/rabbitmq_delayed_message_exchange-3.8.9-0199d11c.ez /opt/rabbitmq/plugins/
#安装常用插件
RUN /opt/rabbitmq/sbin/rabbitmq-plugins enable --offline rabbitmq_management rabbitmq_delayed_message_exchange
```

docker-compose.yaml 内容如下：

```yaml
version: "3.0"

services:
  # 启动rabbitmq
  rabbitmq:
    build:
      context: ./
    image: demo-rabbitmq-dev
    environment:
      - RABBITMQ_DEFAULT_USER=root
      - RABBITMQ_DEFAULT_PASS=123456
      # TODO: 时区设置不起作用
      - TZ=Asia/Shanghai
    network_mode: host
```

启动 rabbitmq 服务器

```sh
docker-compose up -d
```

检查服务器是否正常运行，打开浏览器访问 `http://localhost:15672/`，使用帐号root，密码123456登录

关闭服务器

```sh
docker-compose down
```



## 队列

匿名队列和命名队列，注意：多个消费队列同名时，一个消息只能够被一个消息队列订阅并处理，多个消费队列不同名时，一个消息可以被多个消息队列订阅并处理。使用匿名队列能够对一个发布的消息被多个匿名队列订阅和处理。



## 队列和消息持久化

往非持久化队列发送持久化消息，消息会自动转换为非持久化类型。



## 交换机

为何有交换机：一个队列中的一个消息只能够被消费一次，如果要实现一个消息被多次消费，需要引入交换机+多个队列实现一个消息分发到多个队列中被多个消费程序消费。

默认交换机：默认交换机使用队列名称作为 routingKey 被隐式地绑定到每个队列中，往默认交换机（名称为空）+队列名称作为 routingKey 发送消息最终会被和该队列连接的消费者接收到，注意：开发者是不可能解除绑定或者显式绑定队列到默认交换机中。

交换机类型：

- fanout：routingKey 作用被忽略，指定了也不起作用。
- direct：根据 routingKey 分发消息到指定的队列。
- topic：根据 routingKey 模糊匹配，routingKey 使用使用点号分割开，“#”表示所有、全部的意思；“*”只匹配到一个词。`https://www.cnblogs.com/SR-Program/p/12623560.html`



## 死信

死信，顾名思义就是无法被消费的消息，一般来说 Producer 将消息投递到 broker 或者直接丢到 queue 中，Consumer 从 Queue 中取出消息进行消费，但是某些时候由于特定的原因导致 Queue 中的某些消息无法被消费，这样的消息如果没有后续的处理就变成了死信。

rabbitmq 中的消息当遇到以下几种情况，会变成死信：消息被拒绝(basic.reject / basic.nack)，并且requeue = false、消息TTL过期、队列达到最大长度。

死信队列有其特殊的应用场景，例如用户在商城下单成功并点击去支付的时候，如果在指定的时间内未支付，那么就可以将该下单消息投递到死信队列中，至于后续怎么处理死信队列需要结合具体的应用场景。

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/spring-amqp-demo-dead-letter)



## 自动应答和手动应答

> [参考链接](https://github.com/LeanKit-Labs/wascally/issues/84)

### 自动应答

默认配置情况下，自动应答抛出异常，消息不会从消息队列中删除并且不会被 requeue。

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/rabbitmq-ack-automatically)



### 手动应答

> 参考rabbitmq-examples/rabbitmq-ack-manually

应用抛出异常后，没有 basicNack 或者 basicReject，消息不会从消息队列中删除并且不会被 requeue，注意：如果存在多个同名的队列，没有被 ack、nack、reject 的消息，会被自动 requeue 并且被分发到同名的其他队列中处理。


ack：acknowledge the message, which tells RabbitMQ that this message has been handled. RabbitMQ will mark the message as acknowledged, and remove it from the queue permanently.

nack："negative acknowledge" or "not acknowledged" - this tells RabbitMQ that the message was not handled properly. By default, 'nack' will put the message back in the queue for later handling. You can also force the message to not requeue with 'nack'

reject：explicit "not acknowledged" and do not requeue (by default). RabbitMQ will drop the message from the queue entirely, as the message will not be processable in that queue. you can specify a 'requeue' parameter for reject, like nack. 



## 六种模式

> [参考链接](https://www.cnblogs.com/Runawayprogrammer/p/15980060.html)



### 简单模式 simple

> https://www.cnblogs.com/zousc/p/12725453.html
>

一个消费者、一个队列、默认交换机。一个消费者绑定到队列，队列自动绑定到默认交换机，注意：使用默认交换机（名称为空） 通过 queueName 作为routingKey 发送消息到唯一一个队列中。

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/rabbitmq-helloworld)



### 工作模式 worker

多个消费者、一个队列、默认交换机。多个消费者绑定到队列，队列自动绑定到默认交换机，每个消费者获取到的消息唯一（因为消费队列同名）。

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/rabbitmq-worker-mode)



### 订阅模式 publish/subscribe

多个消费者、多个队列、一个fanout类型交换机。一对一绑定消费者和队列，多个队列绑定到 fanout 类型交换机，注意：交换机类型为 fanout，绑定 routingKey 和发布消息 routingKey 会被忽略。

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/message-pubsub)



### 路由模式 routing

生产者发送的消息主要根据定义的路由规则决定往哪个队列发送，注意：交换机类型为 direct，发布消息 routingKey 需要和接收消息队列绑定的 routingKey 对应才能够接收消息。

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/rabbitmq-direct)、[示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/message-routing)



### 主题模式 topic

生产者，一个交换机（topicExchange），模糊匹配路由规则，多个队列，多个消费者 ，注意：交换机类型为 topic，发布消息 routingKey 和接收消息队列绑定的 routingKey 通过模糊匹配模式匹配并接收消息。

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/rabbitmq-topic)



### RPC 模式

> 注意：暂时未用到不研究。

客户端 Client 先发送消息到消息队列，远程服务端 Server 获取消息，然后再写入另一个消息队列，向原始客户端 Client 响应消息处理结果



## QOS

> prefetch_count



## SpringBoot AMQP

### 配置和基本使用

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/spring-amqp-configuration-demo)

POM 配置

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

application.properties 配置

```properties
spring.rabbitmq.host=${rabbitmq.ip:localhost}
spring.rabbitmq.port=${rabbitmq.port:5672}
spring.rabbitmq.username=${rabbitmq.username:root}
spring.rabbitmq.password=${rabbitmq.password:123456}
```

Java 配置

```java
@Configuration
public class Config {
    // 交换机名称
    public static final String topicExchangeName = "spring-boot-exchange";
    // 队列名称
    static final String queueName = "spring-boot";

    // 创建队列
    @Bean
    Queue queue() {
        return new Queue(queueName, false, false, true);
    }

    // 创建交换机
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName, false, true);
    }

    // 绑定队列到交换机
    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.*");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
```

Receiver 消息监听

```java
@Component
public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message) {
        logger.info("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}
```

测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class ApplicationTests {
    @Autowired
    private AmqpTemplate amqpTemplate = null;
    @Autowired
    private Receiver receiver = null;

    @Test
    public void test1() throws InterruptedException, TimeoutException {
        amqpTemplate.convertAndSend(Config.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!");
        if(!receiver.getLatch().await(2000, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException();
        }
    }
}
```