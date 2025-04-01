## 启动和关闭rabbitmq服务器

```shell
# 启动rabbitmq服务器
docker-compose up -d

# 检查rabbitmq服务器是否正常运行
# 打开浏览器访问，使用帐号root，密码123456登录
http://localhost:15672/

# 关闭rabbitmq服务器
docker-compose down
```

## 六种模式

> https://www.cnblogs.com/Runawayprogrammer/p/15980060.html

### 简单模式simple

> 一个消费者、一个队列、默认交换机。一个消费者绑定到队列，队列自动绑定到默认交换机，NOTE：使用默认交换机（名称为空） 通过queueName作为routingKey发送消息到唯一一个队列中。
>
> https://www.cnblogs.com/zousc/p/12725453.html
>
> 参考rabbitmq-examples/rabbitmq-helloworld

### 工作模式worker

> 多个消费者、一个队列、默认交换机。多个消费者绑定到队列，队列自动绑定到默认交换机，每个消费者获取到的消息唯一（因为消费队列同名）。
>
> 参考rabbitmq-examples/rabbitmq-worker-mode

### 订阅模式publish/subscribe

> 多个消费者、多个队列、一个fanout类型交换机。一对一绑定消费者和队列，多个队列绑定到fanout类型交换机，NOTE：交换机类型为fanout，绑定routingKey和发布消息routingKey会被忽略
>
> 参考rabbit-examples/message-pubsub

### 路由模式routing

> 生产者发送的消息主要根据定义的路由规则决定往哪个队列发送，NOTE：交换机类型为direct，发布消息routingKey需要和接收消息队列绑定的routingKey对应才能够接收消息
>
> 参考rabbitmq-examples/rabbitmq-direct和rabbitmq-examples/message-routing

### 主题模式topic

> 生产者，一个交换机(topicExchange)，模糊匹配路由规则，多个队列，多个消费者 ，NOTE：交换机类型为topic，发布消息routingKey和接收消息队列绑定的routingKey通过模糊匹配模式匹配并接收消息
>
> 参考rabbitmq-examples/rabbitmq-topic

### RPC模式

> 客户端 Client 先发送消息到消息队列，远程服务端 Server 获取消息，然后再写入另一个消息队列，向原始客户端 Client 响应消息处理结果
>
> NOTE：暂时未用到不研究

## 自动应答和手动应答

> https://github.com/LeanKit-Labs/wascally/issues/84

### 自动应答

> 默认配置情况下，自动应答抛出异常，消息不会从消息队列中删除并且不会被requeue
>
> 参考rabbitmq-examples/rabbitmq-ack-automatically

### 手动应答

> 应用抛出异常后，没有basicNack或者basicReject，消息不会从消息队列中删除并且不会被requeue，NOTE：如果存在多个同名的队列，没有被ack、nack、reject的消息，会被自动requeue并且被分发到同名的其他队列中处理。
>
>
> ack：acknowledge the message, which tells RabbitMQ that this message has been handled. RabbitMQ will mark the message as acknowledged, and remove it from the queue permanently.
>
> nack："negative acknowledge" or "not acknowledged" - this tells RabbitMQ that the message was not handled properly. By default, 'nack' will put the message back in the queue for later handling. You can also force the message to not requeue with 'nack'
>
> reject：explicit "not acknowledged" and do not requeue (by default). RabbitMQ will drop the message from the queue entirely, as the message will not be processable in that queue. you can specify a 'requeue' parameter for reject, like nack. 
>
> 参考rabbitmq-examples/rabbitmq-ack-manually

## 队列

> 匿名队列和命名队列，NOTE:多个消费队列同名时，一个消息只能够被一个消息队列订阅并处理，多个消费队列不同名时，一个消息可以被多个消息队列订阅并处理。使用匿名队列能够对一个发布的消息被多个匿名队列订阅和处理。

## 队列和消息持久化

> 往非持久化队列发送持久化消息，消息会自动转换为非持久化类型

## QOS

> prefetch_count

## 交换机

> 为何有交换机：一个队列中的一个消息只能够被消费一次，如果要实现一个消息被多次消费，需要引入交换机+多个队列实现一个消息分发到多个队列中被多个消费程序消费
>
>
> 默认交换机：默认交换机使用队列名称作为routingKey被隐式地绑定到每个队列中，往默认交换机(名称为空)+队列名称作为routingKey发送消息最终会被和该队列连接的消费者接收到，NOTE：开发者是不可能解除绑定或者显式绑定队列到默认交换机中。
>
>
> 交换机类型：
>
> - fanout：routingKey作用被忽略，指定了也不起作用
> - direct：根据routingKey分发消息到指定的队列
> - topic：根据routingKey模糊匹配，routingKey使用使用点号分割开，“#”表示所有、全部的意思；“*”只匹配到一个词。https://www.cnblogs.com/SR-Program/p/12623560.html

## 死信

> 死信,顾名思义就是无法被消费的消息,一般来说 Producer 将消息投递到 broker 或者直接丢到 queue 中,Consumer 从 Queue 中取出消息进行消费,但是某些时候由于特定的原因导致 Queue 中的某些消息无法被消费,这样的消息如果没有后续的处理就变成了死信。
>
> rabbitmq中的消息当遇到以下几种情况，会变成死信：消息被拒绝(basic.reject / basic.nack)，并且requeue = false、消息TTL过期、队列达到最大长度。
>
> 死信队列有其特殊的应用场景,例如用户在商城下单成功并点击去支付的时候,如果在指定的时间内未支付,那么就可以将该下单消息投递到死信队列中,至于后续怎么处理死信队列需要结合具体的应用场景。
>
> 参考rabbitmq-examples/spring-amqp-demo-dead-letter