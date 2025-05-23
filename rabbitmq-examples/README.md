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

消息的“容器”，存储交换机分发过来的消息，等待消费者（Consumer）消费。

匿名队列和命名队列，注意：多个消费者注册到同一个队列时，一个消息只能够被一个消息者订阅并处理，多个消费者注册到多个不同名队列时，一个消息可以被多个消息者订阅并处理。使用匿名队列能够对一个发布的消息被多个匿名队列订阅和处理。

特性：

- 持久化（队列在RabbitMQ重启后仍存在）。
- 排他性（仅限一个连接使用）。
- 自动删除（当最后一个消费者取消订阅时删除队列）。



### 独占队列（exclusive）

在RabbitMQ中，`exclusive`（独占）属性的作用主要体现在队列的可见性和生命周期管理上，以下是其具体作用和相关要点：

### 独占队列的可见性

- **仅对首次声明它的连接可见**：独占队列仅对于在首次声明队列为独占的连接中建立的所有通道可见。在一个连接中声明了一个队列为独占之后，不能在其他连接中声明具有相同名称的独占队列，其他连接中的通道也无法访问独占队列。
- **独占队列的加锁特性**：如果队列是排外的（`exclusive=true`），会对当前队列加锁，其他通道不能访问。如果强制访问，会报异常，如`com.rabbitmq.client.ShutdownSignalException: channel error; protocol method:#method<channel.close>(reply-code=405, reply-text=RESOURCE_LOCKED - cannot obtain exclusive access to locked queue 'queue_name' in vhost '/', class-id=50, method-id=20)`。

### 独占队列的生命周期管理

- **连接断开时自动删除**：独占队列只对首次声明它的连接可见，并且在连接断开时自动删除。
- **与持久性和自动删除属性的关系**：如果队列是独占的，即使设置了持久性参数为`true`，当连接断开时，队列仍会被删除，队列中的数据也将被清除。此外，`auto-delete`属性在独占队列的上下文中，其效果与独占属性相似，即队列会在特定条件下（如连接断开、所有消费者断开连接等）自动删除。

### 独占队列的适用场景和风险

- **适用场景**：独占队列适用于生产者和消费者在同一个进程中且对数据丢失不敏感的情况。例如，在某些临时性的、内部使用的消息传递场景中，可以使用独占队列来简化队列管理。
- **风险**：如果在以下情景中之一发生，独占队列将被删除，队列中的数据将被清除：客户端程序调用`close()`方法、经纪人更新或异常重启、连接心跳超时、其他错误（如节流）。因此，在使用独占队列时，需要谨慎行事，确保不会因为意外情况导致数据丢失。

### 独占队列与持久化、自动删除属性的关系

- **与持久化的关系**：如果队列是独占的（`exclusive=true`），设置持久性参数（`durable=true`）没有意义，因为不管服务器挂了还是客户端主动/被动断开了，队列都会自动删除。
- **与自动删除的关系**：独占队列和自动删除队列（`auto-delete=true`）在队列生命周期管理上有相似之处，但独占队列更侧重于队列的可见性控制。自动删除队列是在最后一个消费者断开连接之后队列自动被删除，而独占队列是在连接断开时自动删除。



## 队列和消息持久化

往非持久化队列发送持久化消息，消息会自动转换为非持久化类型。



## 消费者

#### **一、消费者（Consumer）的核心角色**

1. 定义
   - 消费者是RabbitMQ中**接收并处理消息**的应用程序或服务，负责从队列中拉取消息并执行业务逻辑（如订单处理、日志记录等）。
2. 与生产者的关系
   - 生产者（Producer）发送消息到交换机，交换机路由到队列，消费者从队列中消费消息。
   - **关键点**：消费者不直接与生产者交互，而是通过队列解耦。

------

#### **二、消费者的工作流程**

1. 建立连接与声明资源

   - 消费者需先与RabbitMQ服务器建立连接（`Connection`），然后创建通道（`Channel`）。

   - 声明队列（可选，若队列已存在可省略）：

     ```python
     channel.queue_declare(queue='task_queue', durable=True)
     ```

2. 订阅队列

   - 通过 basic_consume 方法订阅队列，并指定回调函数处理消息：

     ```python
     def callback(ch, method, properties, body):
         print(f"Received {body}")
         ch.basic_ack(delivery_tag=method.delivery_tag)  # 手动确认消息
      
     channel.basic_consume(queue='task_queue', on_message_callback=callback)
     ```

3. 消息处理

   - **自动确认模式（Auto-Ack）**：消息处理后自动确认（不推荐，可能导致消息丢失）。

   - 手动确认模式（Manual-Ack）：消费者处理完成后显式确认（推荐，确保消息不丢失）：

     ```python
     ch.basic_ack(delivery_tag=method.delivery_tag)  # 成功处理后确认
     # 或 ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)  # 处理失败拒绝
     ```

4. 启动消费

   - 调用 start_consuming 进入阻塞状态，持续监听队列：

     ```python
     channel.start_consuming()
     ```

------

#### **三、消费者的关键机制**

1. 消息确认（Acknowledgement）

   - **作用**：确保消息被可靠处理。
   - 模式：
     - **自动确认（Auto-Ack）**：消息一投递即确认，若消费者崩溃会导致消息丢失。
     - **手动确认（Manual-Ack）**：消费者处理完成后显式确认，RabbitMQ才会删除消息。
   - **推荐**：生产环境使用手动确认，避免消息丢失。

2. 公平分发（Fair Dispatch）

   - **问题**：默认情况下，RabbitMQ会轮询分发消息，可能导致某些消费者负载过高。

   - 解决：通过 basic_qos 设置预取计数（Prefetch Count），限制消费者未确认消息的数量：

     ```python
     channel.basic_qos(prefetch_count=1)  # 每个消费者最多同时处理1条消息
     ```

3. 拒绝与重试（Reject & Requeue）

   - 拒绝消息：消费者可通过 basic_nack 拒绝消息，并选择是否重新入队：

     ```python
     ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)  # 不重新入队，进入死信队列
     ```

   - **死信队列（DLX）**：未被确认或被拒绝的消息可路由到死信队列，供后续分析或重试。

------

#### **四、消费者的常见问题与解决方案**

1. 消息丢失
   - **原因**：消费者崩溃且未确认消息。
   - 解决：
     - 启用手动确认。
     - 配置持久化队列和消息。
     - 使用事务或发布者确认（Publisher Confirms）确保消息到达交换机。
2. 消息重复消费
   - **原因**：消费者处理超时或崩溃后重新消费。
   - 解决：
     - 消息设计为幂等（如订单ID唯一，避免重复处理）。
     - 使用唯一消息ID记录处理状态。
3. 消费者阻塞
   - **原因**：消费者处理消息耗时过长，导致队列积压。
   - 解决：
     - 优化消费者性能（如批量处理、异步处理）。
     - 使用公平分发（`prefetch_count`）限制并发消息数。

------

#### **五、消费者的设计模式**

1. 简单消费者

   - **场景**：单线程处理简单任务。

   - 代码示例：

     ```python
     def simple_consumer():
         connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
         channel = connection.channel()
         channel.queue_declare(queue='simple_queue')
      
         def callback(ch, method, properties, body):
             print(f" [x] Received {body}")
             time.sleep(body.count(b'.'))  # 模拟耗时任务
             print(" [x] Done")
             ch.basic_ack(delivery_tag=method.delivery_tag)
      
         channel.basic_qos(prefetch_count=1)
         channel.basic_consume(queue='simple_queue', on_message_callback=callback)
         channel.start_consuming()
     ```

2. 竞争消费者（Work Queue）

   - **场景**：多消费者并行处理任务，负载均衡。
   - 关键点：
     - 每个消费者独立处理消息。
     - 消息默认公平分发（需配置`prefetch_count`）。

3. 发布/订阅消费者

   - **场景**：多个消费者监听同一队列（如日志系统）。
   - **实现**：通过`Fanout`交换机广播消息到多个队列，每个队列绑定一个消费者。

------

#### **六、最佳实践建议**

1. 资源管理
   - 合理设置`prefetch_count`，避免消费者过载或空闲。
   - 监控消费者吞吐量，动态调整并发数。
2. 错误处理
   - 捕获并记录消费者异常，避免崩溃导致消息丢失。
   - 对失败消息进行重试或死信队列（DLX）处理。
3. 日志与监控
   - 记录消费者处理日志，便于排查问题。
   - 监控队列积压（Queue Backlog）和消费者状态。
4. 性能优化
   - 批量处理消息（如每100条消息提交一次数据库）。
   - 使用异步处理（如Python的`asyncio`）提升吞吐量。

------

#### **七、总结**

- **核心职责**：消费者是RabbitMQ中处理消息的最终环节，需确保消息的可靠性和处理效率。
- **关键机制**：消息确认、公平分发、拒绝与重试是保障可靠性的核心。
- **设计原则**：根据业务需求选择消费者模式（简单、竞争或发布/订阅），避免过度设计或简化。

通过合理设计消费者，可以充分发挥RabbitMQ的解耦和异步处理能力，提升系统的稳定性和可扩展性。



## 交换机

交换机负责接收生产者（Producer）发送的消息，并根据规则将消息分发到对应的队列。

为何有交换机：一个队列中的一个消息只能够被消费一次，如果要实现一个消息被多次消费，需要引入交换机+多个队列实现一个消息分发到多个队列中被多个消费程序消费。

默认交换机：默认交换机使用队列名称作为 routingKey 被隐式地绑定到每个队列中，往默认交换机（名称为空）+队列名称作为 routingKey 发送消息最终会被和该队列连接的消费者接收到，注意：开发者是不可能解除绑定或者显式绑定队列到默认交换机中。

交换机类型：

- fanout：忽略路由键（routingKey 作用被忽略，指定了也不起作用），将消息广播到所有绑定的队列。

  fanout 类型的交换机使用请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/spring-amqp-fanout)

- direct：根据路由键精确匹配队列（一对一或一对多，根据 routingKey 分发消息到指定的队列）。

  direct 类型的交换机使用请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/spring-amqp-direct)

- topic：支持模糊匹配（如`*.error`），实现灵活路由。根据 routingKey 模糊匹配，routingKey 使用使用点号分割开，“#”表示所有、全部的意思；“*”只匹配到一个词。`https://www.cnblogs.com/SR-Program/p/12623560.html`



## 路由键（Routing Key）

生产者发送消息时指定的字符串，交换机根据它决定将消息路由到哪些队列。

用法：

- 在`Direct`交换机中，路由键必须与绑定键（Binding Key）完全匹配。
- 在`Topic`交换机中，支持通配符（如`error.#`匹配所有以`error.`开头的键）。



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



## 示例场景

假设一个电商系统：

- **需求**：将订单创建和支付消息分别路由到不同队列。
- 配置：
  1. 创建`Direct`交换机`X`。
  2. 创建队列 Q1（订单创建）、Q2（支付），并绑定到 X：
     - `Q1`绑定键：`order.create`
     - `Q2`绑定键：`order.pay`
- 消息路由：
  - 消息`{routing_key: "order.create", body: "创建订单"}` → 路由到`Q1`。
  - 消息`{routing_key: "order.pay", body: "支付订单"}` → 路由到`Q2`。



## 为何交换机和队列需要分开持久化？

交换机（Exchange）

- **作用**：负责消息的路由逻辑（如根据路由键匹配队列）。
- 持久化场景：
  - 某些交换机（如`Fanout`）的绑定关系（队列与交换机的关联）需要持久化，确保重启后广播关系仍存在。
  - 用户自定义的交换机（如`Topic`）通常需要持久化，避免配置丢失。
- 非持久化场景：
  - 临时交换机（如测试环境）可能不需要持久化。

队列（Queue）

- **作用**：存储消息，等待消费者消费。
- 持久化场景：
  - 队列本身需持久化，否则重启后队列会消失，导致消息丢失。
  - 队列中的消息也需持久化（通过`delivery_mode=2`标记），确保消息在磁盘中。
- 非持久化场景：
  - 临时队列（如RPC请求的临时响应队列）可能不需要持久化。



## 交换机通过不同的路由键绑定到同一个队列中可以吗？这样的设计好吗？

### **一、可行性分析**

1. 技术实现

   - **完全支持**：在RabbitMQ中，一个队列可以通过不同的路由键（Routing Key）多次绑定到同一个交换机。

   - 示例代码（Python）：

     ```python
     # 声明队列
     channel.queue_declare(queue='task_queue')
      
     # 绑定队列到交换机，使用不同路由键
     channel.queue_bind(exchange='logs', queue='task_queue', routing_key='error')
     channel.queue_bind(exchange='logs', queue='task_queue', routing_key='warning')
     channel.queue_bind(exchange='logs', queue='task_queue', routing_key='info')
     ```

   - **效果**：当交换机收到路由键为`error`、`warning`或`info`的消息时，都会路由到`task_queue`。

2. 交换机类型支持

   - **Direct交换机**：路由键必须与绑定键完全匹配，支持多路由键绑定到同一队列。
   - **Topic交换机**：支持通配符（如`error.*`），也可绑定到同一队列。
   - **Fanout交换机**：无需路由键，所有绑定队列都会收到消息（与多路由键无关）。

------

### **二、设计优劣分析**

#### **1. 优点**

- 简化消费者逻辑
  - 消费者只需监听一个队列，即可处理多种类型的消息（如`error`和`warning`），减少队列数量。
- 灵活性
  - 可动态添加或删除绑定键，无需修改消费者代码（如新增`critical`路由键）。
- 资源优化
  - 减少队列数量，降低内存和磁盘占用（队列本身是资源消耗大户）。

#### **2. 缺点**

- 消息混杂
  - 同一队列中的消息类型可能多样，消费者需自行过滤（如通过消息头或内容判断），增加处理复杂度。
- 优先级问题
  - 无法通过队列区分消息优先级（如`error`消息需优先处理，但与`info`混在同一个队列中）。
- 扩展性限制
  - 若不同路由键的消息处理逻辑差异较大（如`error`需告警，`info`仅记录日志），混在一个队列中可能导致代码耦合。

------

### **三、适用场景**

1. 推荐使用多路由键绑定到同一队列的场景
   - **消息类型相似**：如日志系统（`error`、`warning`、`info`），处理逻辑类似（写入文件或数据库）。
   - **消费者单一**：只有一个消费者处理所有消息类型，且无需优先级区分。
   - **资源敏感**：希望减少队列数量以节省资源。
2. 不推荐使用的场景
   - **消息类型差异大**：如订单创建（`order.create`）和支付完成（`order.pay`），处理逻辑完全不同。
   - **需要优先级**：如`error`消息需立即处理，而`info`消息可延迟。
   - **高吞吐量**：不同路由键的消息处理速度差异大，混在一个队列中可能导致阻塞。

------

### **四、对比替代方案**



| **方案**                   | **优点**                         | **缺点**                   | **适用场景**           |
| -------------------------- | -------------------------------- | -------------------------- | ---------------------- |
| **多路由键绑定到同一队列** | 简化消费者逻辑，节省资源         | 消息混杂，优先级处理困难   | 日志系统、简单任务处理 |
| **不同队列绑定同一交换机** | 消息隔离，支持优先级和差异化处理 | 队列数量增加，资源占用更高 | 订单系统、支付系统     |
| **多交换机+多队列**        | 完全解耦，灵活性最高             | 配置复杂，管理成本高       | 复杂分布式系统         |



------

### **五、最佳实践建议**

1. 明确消息分类

   - 如果消息类型差异大（如`order`、`payment`、`log`），建议使用不同队列。
   - 如果消息类型相似（如`log.error`、`log.info`），可绑定到同一队列。

2. 消费者设计

   - 若使用多路由键绑定到同一队列，消费者需通过消息内容或头属性区分类型：

     ```python
     def callback(ch, method, properties, body):
         if method.routing_key == 'error':
             handle_error(body)
         elif method.routing_key == 'info':
             log_info(body)
     ```

3. 性能监控

   - 监控队列积压（Queue Backlog），避免因消息混杂导致处理延迟。

------

### **六、总结**

- **技术可行性**：RabbitMQ支持多路由键绑定到同一队列。
- 设计合理性：
  - **推荐**：消息类型相似、处理逻辑单一、资源敏感的场景。
  - **不推荐**：消息类型差异大、需要优先级或差异化处理的场景。
- **关键点**：根据业务需求权衡**灵活性**与**复杂性**，避免过度设计或简化。

通过合理设计，可以在保证可靠性的同时，优化系统性能和可维护性。



## 多个消费者注册到同一个队列时，同一个消息会被多个消费者消费多次吗？

### **一、核心结论**

**不会**。在RabbitMQ中，当多个消费者注册到同一个队列时，**每个消息只会被一个消费者消费一次**（默认情况下）。这是RabbitMQ的**工作队列（Work Queue）模式**的核心特性，旨在实现任务分配和负载均衡。

------

### **二、工作原理详解**

1. 默认行为：轮询分发（Round-Robin）
   - RabbitMQ会将队列中的消息**轮询**分发给消费者。
   - 例如：队列中有消息 Msg1、Msg2、Msg3，消费者 C1 和 C2会分别收到：
     - `C1`：`Msg1` → `Msg3` → ...
     - `C2`：`Msg2` → ...
   - **关键点**：消息是**独占式消费**的，每个消息只会被一个消费者处理。
2. 消息确认机制（Acknowledgement）
   - 消费者处理完消息后，需向RabbitMQ发送确认（ACK）。
   - 若消费者未确认消息（如崩溃），RabbitMQ会重新将消息分发给其他消费者（需配置手动确认模式）。
   - 示例流程：
     1. 消息`Msg1`分发给`C1`。
     2. `C1`处理完成后发送ACK，RabbitMQ从队列中移除`Msg1`。
     3. 下一消息`Msg2`分发给`C2`，依此类推。

------

### **三、特殊场景分析**

1. 广播模式（Fanout Exchange）
   - 如果消息是通过`Fanout`交换机发送到多个队列（每个队列绑定一个消费者），则每个消费者会收到**相同消息的副本**。
   - 与工作队列的区别：
     - 工作队列：多个消费者共享**一个队列**，消息只被消费一次。
     - 广播模式：多个消费者绑定到**不同队列**，每个队列都有消息副本。
2. 消息拒绝与重新入队
   - 如果消费者拒绝消息（`basic_nack`）并设置`requeue=True`，消息会重新进入队列头部，可能被另一个消费者消费。
   - **但**：这属于异常情况，正常情况下消息只会被消费一次。

------

### **四、代码示例验证**

以下代码演示了多个消费者共享一个队列时，消息只被消费一次：

#### **生产者代码**

```python
import pika
 
connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue='task_queue', durable=True)
 
for i in range(5):
    message = f"Task {i}"
    channel.basic_publish(
        exchange='',
        routing_key='task_queue',
        body=message,
        properties=pika.BasicProperties(
            delivery_mode=2,  # 消息持久化
        ))
print(" [x] Sent 5 messages")
connection.close()
```

#### **消费者代码（启动两个消费者）**

```python
import pika
import time
 
def callback(ch, method, properties, body):
    print(f" [x] Received {body.decode()}")
    time.sleep(body.count(b'.'))  # 模拟耗时任务
    print(" [x] Done")
    ch.basic_ack(delivery_tag=method.delivery_tag)  # 手动确认
 
connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue='task_queue', durable=True)
channel.basic_qos(prefetch_count=1)  # 公平分发
channel.basic_consume(queue='task_queue', on_message_callback=callback)
 
print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()
```

#### **运行结果**

- 启动两个消费者后，消息会交替被两个消费者处理：
  - 消费者1：`Task 0` → `Task 2` → ...
  - 消费者2：`Task 1` → `Task 3` → ...
- **无消息重复消费**。

------

### **五、常见误区与纠正**

1. 误区1：多个消费者会收到同一消息的副本
   - **纠正**：只有在广播模式（Fanout）或每个消费者绑定到独立队列时才会发生。工作队列模式下，消息是独占的。
2. 误区2：消息会被随机分发给消费者
   - **纠正**：默认是轮询分发（Round-Robin），可通过`basic_qos`和公平分发策略优化。
3. 误区3：消费者崩溃会导致消息丢失
   - **纠正**：需启用手动确认模式（Manual-Ack），消费者崩溃后未确认的消息会重新入队。

------

### **六、如何实现消息被多个消费者消费？**

如果需要**同一消息被多个消费者消费**，可采用以下方案：

1. 广播模式（Fanout Exchange）
   - 消息通过`Fanout`交换机发送到多个队列，每个队列绑定一个消费者。
   - **适用场景**：日志系统、通知系统。
2. 发布/订阅模式（Topic/Direct Exchange）
   - 消费者通过不同的绑定键（Routing Key）订阅同一交换机，消息根据规则路由到多个队列。
   - 示例：
     - 交换机`logs`，队列`queue1`绑定`error`，队列`queue2`绑定`warning`。
     - 发送`error`消息时，`queue1`和`queue2`都会收到（如果同时绑定）。
3. 消息复制（非RabbitMQ原生支持）
   - 通过业务逻辑将消息发送到多个队列（如先发到`queue1`，再转发到`queue2`）。
   - **缺点**：增加系统复杂度。

------

### **七、总结**



| **场景**                  | **消息分发方式**        | **是否重复消费** | **适用模式**           |
| ------------------------- | ----------------------- | ---------------- | ---------------------- |
| 多个消费者共享一个队列    | 轮询分发（Round-Robin） | 否               | 工作队列（Work Queue） |
| 广播模式（Fanout）        | 每个队列一份消息副本    | 是               | 发布/订阅              |
| 发布/订阅（Topic/Direct） | 根据绑定键选择性分发    | 可能（需配置）   | 发布/订阅              |



- **默认行为**：工作队列模式下，多个消费者共享一个队列时，消息只被消费一次。
- **关键点**：根据业务需求选择模式，避免混淆工作队列与广播模式。

通过合理设计，可以灵活实现消息的**独占消费**或**广播消费**，满足不同场景的需求。



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



## QoS

RabbitMQ的QoS（Quality of Service，服务质量保证）是一种用于控制消费者从RabbitMQ中预取消息数量的机制，以下是对其详细介绍：

### 核心功能

在非自动确认消息的前提下，如果一定数目的消息（通过基于consume或者channel设置Qos的值）未被确认前，RabbitMQ不进行消费新的消息。这种机制一方面可以实现限速（将消息暂存到RabbitMQ内存中）的作用，一方面可以保证消息确认质量（比如确认了但是处理有异常的情况）。

### 关键参数

- **prefetchSize**：服务器传送最大内容量（以八位字节计算），如果没有限制，则为0。
- **prefetchCount**：服务器每次传递的最大消息数，如果没有限制，则为0。
- **global**：如果为true，则当前设置将会应用于整个Channel（频道）。

### 配置方式

消费确认模式必须是非自动ACK机制（这是使用basicQos的前提条件，否则Qos不生效），然后设置basicQos的值。另外，还可以基于consume和channel的粒度进行设置。

### 作用与影响

- **控制未确认消息缓冲区大小**：消息是异步发送的，在任何时候，channel上肯定不止只有一个消息，另外来自消费者的手动确认本质上也是异步的。因此存在一个未确认的消息缓冲区，通过使用basic.qos方法设置“预取计数”值，可以限制此缓冲区的大小，以避免缓冲区里面无限制的未确认消息问题。该值定义通道上允许的未确认消息的最大数量。一旦数量达到配置的数量，RabbitMQ将停止在通道上传递更多消息，除非至少有一个未处理的消息被确认。例如，假设在通道上有未确认的消息5、6、7、8，并且通道的预取计数设置为4，此时RabbitMQ将不会在该通道上再传递任何消息，除非至少有一个未应答的消息被ack。比如说tag=6这个消息刚被确认ACK，RabbitMQ将会感知这个情况并再发送一条消息。
- **影响吞吐量**：消息应答和QoS预取值对用户吞吐量有重大影响。通常，增加预取将提高向消费者传递消息的速度。虽然自动应答传输速率是最佳的，但是，在这种情况下已传递但尚未处理的消息的数量也会增加，从而增加了消费者的RAM消耗（随机存取存储器）。应该小心使用具有无限预处理的自动确认模式或手动确认模式，消费者消费了大量的消息如果没有确认的话，会导致消费者连接节点的内存消耗变大，所以找到合适的预取值是一个反复实现的过程，不同的负载该值取值也不同，100到300范围内的值通常可提供最佳的吞吐量，并且不会给消费者带来太大的风险。预取值为1是最保守的，当然这将使吞吐量变得很低，特别是消费者连接延迟很严重的情况下，特别是在消费者连接等待时间较长的环境中。对于大多数应用来说，稍微高一点的值将是最佳的。

### 配置示例

在Spring Boot中，可以通过在`application.properties`文件中添加RabbitMQ的配置来实现QoS设置，例如设置手动ACK和每次预取1条消息：

```properties
# RabbitMQ 配置
spring.rabbitmq.host=192.168.200.142
spring.rabbitmq.port=5672
spring.rabbitmq.virtual-host=/test
spring.rabbitmq.username=test
spring.rabbitmq.password=test
 
# 消费者配置
# 设置手动 ACK
spring.rabbitmq.listener.simple.acknowledge-mode=manual
# 设置 QoS，每次预取 1 条消息
spring.rabbitmq.listener.simple.prefetch=1
# 设置并发消费者数量
spring.rabbitmq.listener.simple.concurrency=3
# 设置最大并发消费者数量
spring.rabbitmq.listener.simple.max-concurrency=10
```



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



### 使用 @RabbitListener 注解方式配置

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/spring-amqp-annotation-rabbitlistener)

application.properties 配置

```properties
spring.rabbitmq.host=${rabbitmq.ip:localhost}
spring.rabbitmq.port=${rabbitmq.port:5672}
spring.rabbitmq.username=${rabbitmq.username:root}
spring.rabbitmq.password=${rabbitmq.password:123456}
# 消费者配置
# 设置手动 ACK
spring.rabbitmq.listener.simple.acknowledge-mode=manual
# 设置 QoS，每次预取 1 条消息
spring.rabbitmq.listener.simple.prefetch=1
# 设置并发消费者数量
spring.rabbitmq.listener.simple.concurrency=3
# 设置最大并发消费者数量
spring.rabbitmq.listener.simple.max-concurrency=10
```

Java 配置

```java
@Configuration
public class Config {

    public static final String exchangeName = "spring-amqp-annotaion-rabbitlistener-exchange-demo";
    static final String queueName = "spring-amqp-annotaion-rabbitlistener-queue-demo";

    /*@Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }*/
}
```

Receiver 配置

```java
@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = Config.queueName, autoDelete = "true"),
        exchange = @Exchange(value= Config.exchangeName, type=ExchangeTypes.FANOUT, autoDelete = "true"),
))
public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch = new CountDownLatch(2);

    @RabbitHandler
    public void receiveMessage(String message,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        logger.info("Received <" + message + ">");
        latch.countDown();
        channel.basicAck(deliveryTag, false);
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
        for(int i=0; i<2; i++) {
            amqpTemplate.convertAndSend(Config.exchangeName, null, "Hello from RabbitMQ!" + i);
        }
        if(!receiver.getLatch().await(2000, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException();
        }
    }
}
```



### 编程式配置

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/rabbitmq-examples/spring-amqp-programatically-demo)

POM 配置

```xml
<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit</artifactId>
    <version>2.1.7.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-amqp</artifactId>
    <version>2.1.7.RELEASE</version>
</dependency>
```

测试

```java
public class ApplicationTests {
    String host;
    String username;
    String password;

    @Before
    public void setup() {
        String host = System.getenv("host");
        String username = System.getenv("username");
        String password = System.getenv("password");

        this.host = host;
        this.username = username;
        this.password = password;

        if(!StringUtils.hasText(this.host)) {
            this.host = "localhost";
        }

        if(!StringUtils.hasText(this.username)) {
            this.username = "root";
        }

        if(!StringUtils.hasText(this.password)) {
            this.password = "123456";
        }
    }

    /**
     *
     */
    @Test
    public void test1() throws InterruptedException, TimeoutException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        String exchangename = "chat.message.push.exchange";
        DirectExchange exchange = new DirectExchange(exchangename, false, true);
        rabbitAdmin.declareExchange(exchange);

        Queue queue1 = new AnonymousQueue();
        rabbitAdmin.declareQueue(queue1);
        String userId1 = "111";
        Binding binding = BindingBuilder.bind(queue1).to(exchange).with(userId1);
        rabbitAdmin.declareBinding(binding);

        Queue queue2 = new AnonymousQueue();
        rabbitAdmin.declareQueue(queue2);
        String userId2 = "112";
        binding = BindingBuilder.bind(queue2).to(exchange).with(userId2);
        rabbitAdmin.declareBinding(binding);

        int totalMessages = 10;
        CountDownLatch countDownLatch1 = new CountDownLatch(totalMessages);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.addQueues(queue1);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConcurrentConsumers(100);
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                countDownLatch1.countDown();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //
                }
            }
        };
        container.setMessageListener(messageListener);
        container.afterPropertiesSet();
        container.start();

        CountDownLatch countDownLatch2 = new CountDownLatch(totalMessages);
        container = new SimpleMessageListenerContainer(connectionFactory);
        container.addQueues(queue2);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConcurrentConsumers(100);
        messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                countDownLatch2.countDown();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //
                }
            }
        };
        container.setMessageListener(messageListener);
        container.afterPropertiesSet();
        container.start();

        RabbitTemplate amqpTemplate = new RabbitTemplate(connectionFactory);
        for(int i=0; i<totalMessages; i++) {
            amqpTemplate.convertAndSend(exchangename, userId1, "Hello world " + i);
        }
        for(int i=0; i<totalMessages; i++) {
            amqpTemplate.convertAndSend(exchangename, userId2, "Hello world " + i);
        }

        if(!countDownLatch1.await(2, TimeUnit.SECONDS)
            || !countDownLatch2.await(2, TimeUnit.SECONDS)){
            throw new TimeoutException();
        }

        connectionFactory.destroy();
    }
}
```