# SpringCloud



## SpringCloud 和 SpringCloud Alibaba

SpringCloud和SpringCloud Alibaba都是微服务架构中的重要工具，它们各自具有独特的特点和优势。以下是对这两者的详细比较：

**一、定义与背景**

1. **SpringCloud**
   - SpringCloud是基于SpringBoot的一整套实现微服务的框架。
   - 它提供了微服务开发所需的配置管理、服务发现、断路器、智能路由、微代理、控制总线、全局锁、决策竞选、分布式会话和集群状态管理等组件。
   - SpringCloud是Spring社区开发的一套微服务架构框架，为开发者构建分布式系统提供了多种解决方案。
2. **SpringCloud Alibaba**
   - SpringCloud Alibaba是阿里巴巴提供的微服务开发一站式解决方案，是阿里巴巴开源中间件与SpringCloud体系的融合。
   - 它包含了开发分布式应用微服务的必需组件，方便开发者轻松使用这些组件来开发分布式应用服务。
   - 作为SpringCloud体系下的新实现，SpringCloud Alibaba具备了更多的功能，旨在为企业级应用提供全面的支持。

**二、核心组件与功能**

1. **SpringCloud**
   - 核心组件：Eureka（服务发现）、Ribbon（负载均衡）、Feign（声明式的Web服务客户端）、Hystrix（断路器）、Zuul（网关）等。
   - 功能：服务发现与注册、配置中心、消息总线、负载均衡、断路器、数据监控等。
2. **SpringCloud Alibaba**
   - 核心组件：Nacos（服务发现与配置管理）、Sentinel（流量控制、熔断降级）、RocketMQ（分布式消息队列）、Dubbo（高性能RPC框架）、Seata（分布式事务解决方案）等。
   - 功能：除了包含SpringCloud的核心功能外，还提供了更多的企业级支持，如分布式事务、数据库连接池优化等。

**三、主要区别**

1. **组件与工具**
   - SpringCloud提供了一系列的组件和工具，这些组件和工具都是Spring官方提供的标准解决方案。
   - SpringCloud Alibaba则更侧重于为企业级应用提供全面的支持，包括对微服务架构、分布式事务等的深度集成，并提供了丰富的生态系统。
2. **云服务支持**
   - SpringCloud本身并不直接支持阿里云服务。
   - SpringCloud Alibaba则内置了对阿里云一系列服务（如RDS、OSS、Sentinel等）的无缝对接，使得开发者能够更容易地利用这些云服务。
3. **本地化调整**
   - SpringCloud是一个国际化的框架，适用于全球范围内的开发者。
   - SpringCloud Alibaba则针对中国市场做了很多本地化的调整，比如对中文文档的支持、适配国内法规等。

**四、使用场景与选择建议**

1. **使用场景**
   - SpringCloud适用于需要快速构建微服务架构、对云服务没有特定要求、或者希望使用Spring官方提供的标准组件和工具的开发者。
   - SpringCloud Alibaba适用于在中国市场部署应用、需要充分利用阿里云的服务、或者希望使用阿里巴巴提供的微服务解决方案和最佳实践的开发者。
2. **选择建议**
   - 在选择SpringCloud或SpringCloud Alibaba时，需要根据具体的应用场景和需求进行权衡。
   - 如果应用需要在中国市场部署，并且希望充分利用阿里云的服务，那么SpringCloud Alibaba可能是更好的选择。
   - 如果应用对云服务没有特定要求，或者希望使用Spring官方提供的标准组件和工具，那么SpringCloud也是一个不错的选择。

综上所述，SpringCloud和SpringCloud Alibaba都是优秀的微服务框架，但它们各有特点。在选择时，需要根据具体的应用场景和需求进行权衡，以选择最适合自己的框架。



## SpringCloud、SpringCloud Alibaba 和 SpringBoot 兼容性

SpringCloud Alibaba、SpringCloud 和 SpringBoot 兼容性列表`https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E`

SpringCloud 和 SpringBoot 兼容性列表`https://stackoverflow.com/questions/42659920/is-there-a-compatibility-matrix-of-spring-boot-and-spring-cloud`

访问`https://spring.io/projects/spring-cloud`，通过`ctrl+f`搜索`Table 1. Release train Spring Boot compatibility`定位到`spring-cloud`和`spring-boot`兼容表格

| Release Train                                                | Boot Version                          |
| ------------------------------------------------------------ | ------------------------------------- |
| [2023.0.x](https://github.com/spring-cloud/spring-cloud-release/wiki/Spring-Cloud-2023.0-Release-Notes) aka Leyton | 3.3.x, 3.2.x                          |
| [2022.0.x](https://github.com/spring-cloud/spring-cloud-release/wiki/Spring-Cloud-2022.0-Release-Notes) aka Kilburn | 3.0.x, 3.1.x (Starting with 2022.0.3) |
| [2021.0.x](https://github.com/spring-cloud/spring-cloud-release/wiki/Spring-Cloud-2021.0-Release-Notes) aka Jubilee | 2.6.x, 2.7.x (Starting with 2021.0.3) |
| [2020.0.x](https://github.com/spring-cloud/spring-cloud-release/wiki/Spring-Cloud-2020.0-Release-Notes) aka Ilford | 2.4.x, 2.5.x (Starting with 2020.0.3) |
| Hoxton                                                       | 2.2.x, 2.3.x (Starting with SR5)      |
| Greenwich                                                    | 2.1.x                                 |
| Finchley                                                     | 2.0.x                                 |
| Edgware                                                      | 1.5.x                                 |
| Dalston                                                      | 1.5.x                                 |
| Camden                                                       | 1.4.x                                 |
| Brixton                                                      | 1.3.x, 1.4.x                          |
| Angel                                                        | 1.2.x                                 |



## SpringCloud 作用

SpringCloud是一个微服务框架，是能快速构建分布式系统的工具，其作用主要体现在以下几个方面：

**一、提供微服务架构解决方案**

SpringCloud利用Spring Boot的开发便利性，简化了分布式系统基础设施的开发，如服务发现注册、配置中心、消息总线、负载均衡、断路器、数据监控等，都可以用Spring Boot的开发风格做到一键启动和部署。它并没有重复制造轮子，而是将各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，通过Spring Boot风格进行再封装，屏蔽掉了复杂的配置和实现原理，最终给开发者留出了一套简单易懂、易部署和易维护的分布式系统开发工具包。

**二、核心组件及其作用**

1. **服务发现和注册**：如Eureka，它允许微服务实例动态地注册到服务注册中心，并且允许客户端查询可用服务实例的详细信息，从而实现服务的自动发现。这有助于微服务架构中的服务自动化部署和故障转移。
2. **负载均衡**：如Ribbon，它提供客户端的负载均衡功能，可以在多个微服务实例之间分配请求，以提高系统的吞吐量和可用性。
3. **配置管理**：如Spring Cloud Config，它允许将配置信息外部化，并集中管理。通过配置服务器，可以轻松地管理不同环境（如开发、测试、生产）的配置信息，支持配置的动态刷新。
4. **API网关**：如Spring Cloud Gateway，它作为微服务架构中的单一入口点，负责路由转发、身份验证、监控等功能。它可以帮助开发者简化客户端与微服务之间的通信，并提供额外的安全层。
5. **事件总线**：Spring Cloud Bus，它允许微服务之间通过消息传递机制进行通信，可以基于消息中间件（如RabbitMQ、Kafka）实现，支持事件的发布/订阅模式。

**三、增强微服务架构的可靠性**

SpringCloud提供了多种工具和组件来增强微服务架构的可靠性，如断路器（如Hystrix）、智能路由、集群状态监控等。这些功能可以帮助开发者及时发现和处理微服务架构中的故障，提高系统的稳定性和可用性。

**四、支持多种部署环境和平台**

SpringCloud支持多种部署环境和平台，包括云平台、容器化平台等。它提供了与这些平台的集成支持，使得微服务应用可以更加灵活地部署和扩展。

**五、持续更新和优化**

SpringCloud作为一个开源项目，一直在不断地更新和优化。随着微服务架构的不断发展，SpringCloud也在不断地引入新的功能和改进，以满足开发者的需求。例如，在最近的更新中，Spring Cloud Gateway引入了gRPC路由的原生支持，提高了微服务之间的通信效率；Spring Cloud Config增强了与多云环境的兼容性，提高了配置管理的灵活性和安全性。

综上所述，SpringCloud在微服务架构中发挥着至关重要的作用，它提供了一套完整的工具和组件来帮助开发者构建、部署和管理微服务应用。









## 服务注册与发现



### 定义

微服务服务注册与发现是微服务架构中的核心组件，它解决了微服务实例动态变化所带来的挑战，使得服务间能够高效地相互找到并进行通信。以下是对微服务服务注册与发现的详细解析：

**一、基本概念**

1. 服务注册（Service Registration）：
   - 定义：服务提供者将自己的元数据信息（如主机和端口号、身份验证信息、协议、版本号以及运行环境的信息等）注册到服务注册中心的过程。
   - 目的：使服务注册中心能够记录和追踪所有可用的服务实例。
2. 服务发现（Service Discovery）：
   - 定义：服务消费者（客户端）在需要调用服务时，通过查询服务注册中心获取服务提供者的服务实例信息的过程。
   - 目的：使服务消费者能够动态地获取服务实例信息，无需预先知道服务提供者的具体地址。

**二、工作模式**

微服务服务注册与发现主要有两种工作模式：

1. 自注册模式（客户端/直连模式）：
   - 服务消费者直接与注册中心交互，获取服务提供者的地址信息。
   - 优点：直接控制服务发现过程。
   - 缺点：需要修改代码，对跨平台支持不够友好。
2. 代理模式（服务端模式）：
   - 服务消费者通过一个代理（如API网关或服务发现代理）来获取服务提供者的地址信息。
   - 优点：跨平台友好，易于维护。
   - 缺点：增加了代理服务的复杂性和可能的性能开销。

**三、关键技术**

1. 服务注册中心的设计与实现：
   - **数据存储**：服务注册中心需要能够存储大量的服务实例信息，因此需要一个高效、可扩展的数据存储系统。
   - **网络通信**：服务注册中心需要能够处理大量的网络请求，包括服务注册请求、服务发现请求以及健康检查请求等。
   - **服务健康检查**：服务注册中心需要定期对注册的服务进行健康检查，以确认服务是否还在运行。
   - **高可用/分布式**：在分布式架构中，服务注册中心的高可用性至关重要。通过合理选择一致性、可用性和分区容错性的平衡点，可以优化服务注册与发现的性能和可靠性。
2. 服务发现机制的设计与实现：
   - **服务查询**：服务发现机制需要能够根据服务名称查询出对应的服务实例信息。
   - **负载均衡**：在多个相同的服务实例中，服务发现机制需要能够选择一个合适的实例进行调用。
   - **服务降级与熔断**：当被调用的服务出现故障时，服务发现机制需要能够进行服务降级或者熔断，以保证系统的稳定性。

**四、常用工具与框架**

1. Eureka：
   - 简介：Eureka是Netflix开源的一款提供服务注册和发现的产品，也是Spring Cloud体系中的重要组件之一。
   - 优点：与Spring Cloud集成良好，使用和配置简单，具有自我保护机制。
   - 缺点：自我保护模式可能会让客户端获取到不可用的服务实例信息，不支持跨语言，只能在Java环境中使用。
2. Zookeeper：
   - 简介：Zookeeper是Apache的一个开源项目，提供配置维护、域名服务、分布式同步、组服务等功能。
   - 优点：提供了一种中心化的服务，用于维护配置信息、命名、提供分布式同步和提供组服务。
   - 缺点：CP特性使其在出现网络分区时可能无法提供服务，API使用起来相对复杂，学习成本较高。
3. Consul：
   - 简介：Consul是HashiCorp公司推出的开源工具，用于实现分布式系统的服务发现与配置。
   - 优点：内置了服务注册与发现框架，支持健康检查，并且支持多数据中心。
   - 缺点：一些高级功能的使用和配置相对复杂。
4. Etcd：
   - 简介：Etcd是一个开源的、基于Raft协议的分布式键值存储系统，由CoreOS开发，主要用于共享配置和服务发现。
   - 优点：使用Raft协议保证了数据的一致性，支持SSL保证了数据的安全性。
   - 缺点：性能相比Zookeeper和Consul要差一些。
5. Nacos：
   - 简介：Nacos是阿里巴巴开源的一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。
   - 优点：支持几乎所有主流类型的服务的发现、配置和管理，更加适合云原生应用场景；提供了简单易用的UI界面；支持数据持久化，避免了数据的丢失。
   - 缺点：是阿里巴巴内部使用的产品，对于一些特定的业务场景可能需要进行定制化开发。

**五、实现流程**

1. 服务注册流程：
   - 服务启动时，向注册中心发送注册请求。
   - 注册中心接收到注册请求后，存储服务的元数据（如服务名、地址、端口、版本等）。
   - 注册中心定期更新服务的状态，确保服务的可用性。
2. 服务发现流程：
   - 客户端在需要调用某个服务时，首先向注册中心请求该服务的实例列表。
   - 注册中心将活跃的服务实例列表返回给客户端。
   - 客户端选择一个可用的服务实例进行调用。

**六、总结**

微服务服务注册与发现是微服务架构中不可或缺的重要组成部分。通过服务注册与发现，服务实例能够动态地注册到注册中心，并在需要时被服务消费者发现。这大大提高了微服务架构的灵活性和可扩展性。在选择服务注册与发现的工具或框架时，需要根据具体业务需求和技术架构进行综合考虑。



### Eureka

注意：停止更新，将会抛弃



#### 单机版

>`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-eureka`

启动示例中相关应用

访问`http://localhost:9999/`查看 Eureka 面板



#### 集群版

>`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-eureka-cluster`
>
>参考资料`https://blog.csdn.net/weixin_43907332/article/details/94473626`

编辑 /etc/hosts 添加以下内容

```
127.0.0.1 abc1.com
127.0.0.1 abc2.com
127.0.0.1 abc3.com
```

启动示例中相关应用

分别访问 Eureka 面板，面板中 DS Replicas 会显示其他 Eureka 节点，`http://localhost:9999/`、`http://localhost:10000/`、`http://localhost:10001/`

关闭其中两台 Eureka 后重启 zuul 和 helloworld 服务，访问如下 url 服务依旧正常，`http://localhost:8080/api/v1/sayHello`



#### 基本配置

Eureka 服务端配置

```properties
eureka.instance.hostname=${spring.application.name}
# 由于该应用为注册中心,所以设置为false,代表不向注册中心注册自己
eureka.client.register-with-eureka=false
# 用于指定Eureka客户端不需要从Eureka服务器获取服务注册信息，这通常适用于Eureka服务器节点本身或在某些特殊场景下不需要知道其他服务位置的客户端。
eureka.client.fetch-registry=false
# 作用是让Eureka客户端知道它应该连接到哪个Eureka服务器来注册自己或发现其他服务
eureka.client.serviceUrl.defaultZone=http://localhost:${server.port}/eureka/

# 服务端是否开启自我保护机制 （默认true）
eureka.server.enable-self-preservation=false
# 将 eureka.server.eviction-interval-timer-in-ms 设置为2000毫秒意味着Eureka服务器将每2秒检查一次已注册的服务实例，
# 如果发现某个实例在一定时间内（这个时间由 eureka.instance.lease-expiration-duration-in-seconds 配置项设置）没有发送心跳信号（表示该实例仍然活跃），
# 则Eureka服务器将认为这个实例已经失效，并将其从服务注册表中移除。
eureka.server.eviction-interval-timer-in-ms=30000
```

Eureka 客户端配置

```properties
# 作用是让Eureka客户端知道它应该连接到哪个Eureka服务器来注册自己或发现其他服务
eureka.client.serviceUrl.defaultZone=http://localhost:9999/eureka/
# eureka实例面板显示的实例标识
eureka.instance.instance-id=zuul1
# eureka实例面板显示实例的主机ip
eureka.instance.prefer-ip-address=true

# 客户端向注册中心发送心跳的时间间隔，（默认30秒）
eureka.instance.lease-renewal-interval-in-seconds=10
# eureka注册中心（服务端）在收到客户端心跳之后，等待下一次心跳的超时时间，如果在这个时间内没有收到下次心跳，则移除该客户端。（默认90秒）
eureka.instance.lease-expiration-duration-in-seconds=30
```



#### 禁用自我保护机制

Eureka的自我保护机制是Eureka注册中心的一个重要特性，旨在提高分布式系统的可用性和容错性，特别是在网络分区或网络不稳定的情况下。以下是关于Eureka自我保护机制的详细解释：

**一、机制概述**

Eureka的自我保护机制是为了防止在网络问题导致服务注册中心错误地剔除健康的服务实例，从而造成更大的系统故障。在网络异常情况下，Eureka会进入自我保护模式，暂停从注册表中移除因心跳丢失而被认为不可用的服务实例，尽量保证服务发现的可用性。

**二、工作原理**

1. **心跳机制**：Eureka客户端会定期（默认每30秒）向Eureka服务器发送心跳信号，以证明该实例仍然处于健康状态。
2. **心跳检测**：Eureka服务器会定期检测各个服务实例的心跳信号。如果某个服务实例在一定时间内（默认90秒）没有发送心跳，Eureka服务器通常会将该实例从注册表中移除。
3. **自我保护模式触发条件**：Eureka服务器会计算最近15分钟内收到的心跳的比例（实际收到的心跳与理论上应该收到的心跳数量的比值）。如果Eureka发现接收到的心跳数量突然下降到低于一个阈值（通常是85%），它将进入自我保护模式。
4. **自我保护模式下的行为**：在自我保护模式下，Eureka服务器将不会从注册表中移除因心跳丢失而被认为不可用的服务实例，即使这些实例实际上可能已经停止发送心跳。这样做是为了防止由于网络抖动等原因导致的误删健康的服务实例，从而保证了服务的高可用性和稳定性。

**三、自我保护模式的解除**

当网络状况恢复正常，Eureka服务器收到的心跳数量恢复到正常水平并持续一段时间后，Eureka会自动退出自我保护模式，并恢复正常的服务实例剔除机制。这一过程是自动的，不需要人工干预。

**四、自我保护机制的可配置性**

Eureka的自我保护机制是可配置的。通过修改Eureka服务器配置文件中的相关参数，可以调整何时触发自我保护模式，或者完全关闭此功能。例如，可以通过修改`eureka.server.enable-self-preservation`的配置值来开启或关闭自我保护模式。然而，在生产环境中通常建议开启自我保护模式，以确保在网络不稳定的情况下Eureka注册中心的稳定性不被破坏。

**五、注意事项**

1. 自我保护机制虽然提高了系统的容错性，但也可能导致注册表中存在一些已经失效的服务实例。因此，在生产环境中，如果发现自我保护模式频繁启动，就需要进一步排查网络或其他问题，并及时修复。
2. 在自我保护模式下，Eureka的注册表中可能会包含一些已经不可用的服务实例信息，这可能导致服务发现的结果不准确。因此，在使用Eureka时需要注意这一点。

综上所述，Eureka的自我保护机制是一种重要的安全保护措施，它通过在网络异常情况下暂停剔除失效的服务实例来保护注册表的稳定性。这一机制提高了分布式系统的可用性和容错性，使得服务消费者在网络故障恢复后仍然能够发现和调用服务实例。



**一旦 Eureka 客户端没有心跳约在一分钟内从 Eureka 实例列表剔除**

Eureka 服务端配置

```properties
# 服务端是否开启自我保护机制 （默认true）
eureka.server.enable-self-preservation=false
# 将 eureka.server.eviction-interval-timer-in-ms 设置为2000毫秒意味着Eureka服务器将每2秒检查一次已注册的服务实例，
# 如果发现某个实例在一定时间内（这个时间由 eureka.instance.lease-expiration-duration-in-seconds 配置项设置）没有发送心跳信号（表示该实例仍然活跃），
# 则Eureka服务器将认为这个实例已经失效，并将其从服务注册表中移除。
eureka.server.eviction-interval-timer-in-ms=30000
```

Eureka 客户端配置

```properties
# 客户端向注册中心发送心跳的时间间隔，（默认30秒）
eureka.instance.lease-renewal-interval-in-seconds=10
# eureka注册中心（服务端）在收到客户端心跳之后，等待下一次心跳的超时时间，如果在这个时间内没有收到下次心跳，则移除该客户端。（默认90秒）
eureka.instance.lease-expiration-duration-in-seconds=30
```



### Zookeeper

注意：用得少，所以不做实验



### Consul

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-consul-parent`

启动 Consul 服务

```bash
docker-compose up -d
```

访问`http://localhost:8500/`，检查 Consul 服务器是否正常

启动所有应用

访问`http://localhost:8081/api/v1/a/sayHello?name=Dexter`以测试应用服务是否正常



### Nacos

注意：阿里巴巴主流注册中心



## 服务调用和负载均衡

> 服务调用实现如下：
>
> Ribbon（停止更新，许多遗留项目还在使用，所以需要学习）
>
> LoadBalancer
>
> Feign（停止更新，不需要学习，已经被openfeign取代）
>
> OpenFeign

### Ribbon

> 进程内负载均衡（负载均衡+RestTemplate）。
>
> 参考 spring-cloud/spring-cloud-ribbon-parent demo

#### 使用IRule替换负载均衡算法

> 默认负载均衡算法是RoundRobinRule

```java
// 定义IRule配置类
@Configuration
public class MyRuleRandom {
    @Bean
    public IRule rule() {
        // 随机选择服务负载均衡算法
        return new RandomRule();
    }
}

// 配置Application启动类加载MyRuleRandom配置@RibbonClient
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "spring-cloud-helloworld", configuration = MyRuleRandom.class)
public class ApplicationRibbon {
```

### OpenFeign

> https://www.jianshu.com/p/c0cb63e7640c
>
> OpenFeign是一个声明式web服务客户端，让编写Web服务客户端变得更加容易，只需要创建一个接口并在接口上添加注解即可。Feign也可以支持拔插式的编码器和解码器。SpringCloud对Feign进行了封装，使其支持了Spring MVC标准注解和HttpMessageConverters。Feign可以与Eureka和Ribbon组合使用以支持负载均衡。
>
> 前面在使用Ribbon时候，利用RestTemplete对http请求封装处理形成一套模板化的调用方法。但是在实际开发过程中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用，所以Feign在此基础上做了进一步的封装，我们只需要在一个微服务接口上标注一个Feign注解即可完成对服务提供方的接口绑定，简化了Spring Cloud Ribbon时，自动封装服务调用客户端的开发量。
>
> 和Ribbon对比，Feign只需要在服务调用接口上加一个注解就可以了，优雅而简单的实现了服务调用。
> 
>
>
> 知识点：
>
> - openfeign超时配置
>
> - openfeign日志配置
> - feign添加请求头信息，使用@RequestHeader注解 https://www.cnblogs.com/laeni/p/12733920.html
>
> 
>
> 参考spring-cloud/spring-cloud-feign-demo

### LoadBalancer



## 分布式事务

### Seata

### LCN



## 服务熔断和服务降级

### Hystrix

### Resilience4J（Circuit Breaker 标准）

todo Circuit Breaker 和 Resilience4J 关系

### Sentinel



> 服务降级(fallback)、服务熔断(circuitbreaker)、服务限流。
>
> 参考spring-cloud/spring-cloud-hystrix

### 服务降级

> NOTE: feign客户端调用服务时达到ribbon.ReadTimeout超时，即使execution.isolation.thread.timeoutInMilliseconds未达到超时时间也会fallback
>
> 配置方式分为2种：服务提供者配置服务降级、服务调用者feign配置服务降级

### 服务熔断

> 参考spring-cloud/spring-cloud-hystrix/README.md

### 服务限流



## 服务链路追踪

### Sleuth + Zipkin

注意：逐渐地被 Micrometer Tracing 替代

### Micrometer Tracing

### Skywalking



## 服务网关

>`netflix zuul`（进入维护状态，被`springcloud`抛弃）、`netflix zuul2`（推迟上线计划，被`springcloud`抛弃）、`gateway`（`springcloud`自研新一代网关）

### Zuul



#### 代理请求到外部服务

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-zuul`



运行示例步骤如下：

1. 先运行`demo-assistant-external-service`服务，此服务模拟提供上传和下载的外部服务。
2. 再运行`demo-zuul-service`服务后，执行其中`Tests.java`测试。

示例核心配置如下：

- `zuul`网关配置代理外部服务的上传和下载接口，`application.yaml`配置如下：

  ```yaml
  zuul:
    routes:
      # 代理上传和下载接口到外部服务
      demo-external-service-api-upload:
        path: /api/v1/upload
        url: http://localhost:18090
        strip-prefix: false
      demo-external-service-api-download:
        path: /api/v1/download/**
        url: http://localhost:18090
        strip-prefix: false
  ```

- `zuul`网关自定义`filter`在请求中注入外部服务的开发者`token`，代码如下：

  ```java
  package com.future.demo.spring.cloud.zuul;
  
  import com.netflix.zuul.ZuulFilter;
  import com.netflix.zuul.context.RequestContext;
  
  import javax.servlet.http.HttpServletRequest;
  import java.util.UUID;
  
  public class UploadAndDownloadFilter extends ZuulFilter {
  
      @Override
      public String filterType() {
          return "pre"; // 在请求被路由之前执行
      }
  
      @Override
      public int filterOrder() {
          return 1; // Filter的执行顺序
      }
  
      @Override
      public boolean shouldFilter() {
          RequestContext ctx = RequestContext.getCurrentContext();
          HttpServletRequest request = ctx.getRequest();
          // 基于请求URL或其他信息决定是否应用此Filter
          String requestURI = request.getRequestURI();
          // 仅对上传和下载接口应用此filter
          return requestURI.startsWith("/api/v1/upload") || requestURI.startsWith("/api/v1/download");
      }
  
      @Override
      public Object run() {
          RequestContext ctx = RequestContext.getCurrentContext();
          HttpServletRequest request = ctx.getRequest();
  
          // 添加一个名为"token"
          ctx.addZuulRequestHeader("token", UUID.randomUUID().toString());
  
          return null;
      }
  }
  ```

  ```java
  @Bean
  UploadAndDownloadFilter uploadAndDownloadFilter() {
      return new UploadAndDownloadFilter();
  }
  ```




### Gateway

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-gateway`



## 服务配置和管理



### 定义

微服务服务配置和管理是微服务架构中的关键环节，它涉及到服务的启动、运行、性能、安全性和可扩展性等多个方面。以下是对微服务服务配置和管理的详细阐述：

**一、微服务配置管理的概念**

微服务配置管理是指在微服务架构中对各个服务的配置数据进行统一管理和控制的过程。这些配置数据包括但不限于数据库连接信息、API密钥、环境变量、服务端口、日志级别等。有效的配置管理能够确保服务在不同环境中的一致性，并快速响应业务需求变化。

**二、微服务配置管理的策略**

1. **外部化配置**：将应用程序的配置文件从代码中分离，使其能独立于应用程序进行管理。这种策略允许开发人员在不修改代码的情况下更改配置，提升了配置的灵活性和可管理性。
2. **动态配置**：支持在运行时修改配置，而无需重启服务。这能够根据业务需求实时调整配置，提高系统响应速度和可用性，同时降低运维成本。
3. **版本控制与审计**：对配置文件进行版本控制，可以追踪任何更改并回滚到以前的版本。同时，确保所有配置变更都有记录，便于审计和排查问题。

**三、微服务配置管理的最佳实践**

1. **使用环境变量**：对于敏感信息（如数据库密码），建议使用环境变量存储，避免将其硬编码在配置文件中。这不仅提高了安全性，也使得配置更加灵活。
2. **采用加密机制**：确保敏感配置项（如API密钥）采用加密存储，防止配置被未授权访问。可以使用专门的加密工具或服务进行管理。
3. **定期审计配置**：定期检查和审计配置变更，确保没有未授权的访问或更改。可以利用工具对配置历史进行保存和审核，增强系统的安全性。
4. **选择合适的配置管理工具**：常用的配置管理工具有Spring Cloud Config、Apollo、Disconf等，它们提供了集中化的配置管理服务，支持多种配置源和动态更新。选择适合团队和项目的工具，可以提高配置管理的效率和效果。
5. **结合CI/CD工具实现自动化部署**：结合持续集成和持续部署（CI/CD）工具，实现配置的自动化部署和更新。这可以显著降低配置管理的复杂性，提高部署的效率和可靠性。

**四、微服务配置管理的挑战与解决方案**

1. **挑战**：随着微服务数量的增加和复杂度的提升，配置管理的难度也会增加。如何确保所有服务的配置数据一致、准确和安全成为了一个重要的问题。
2. 解决方案：
   - 建立统一的配置管理平台和规范，确保所有服务都遵循相同的配置管理策略。
   - 引入自动化工具和流程，减少人为错误和重复劳动。
   - 定期对配置数据进行审计和检查，及时发现和解决问题。

综上所述，微服务服务配置和管理是微服务架构中不可或缺的一部分。通过外部化配置、动态更新、版本控制与审计等策略以及最佳实践的应用，可以确保服务在不同环境中的一致性、安全性和可扩展性。同时，选择合适的配置管理工具和结合CI/CD工具实现自动化部署也是提高配置管理效率和效果的关键。



### Config + Bus

> 注意：Nacos 逐渐替代 Config + Bus，所以不做实验。



### SpringCloud Config Server

>使用 Consul 或者 Nacos 替代，很久之前已经做过实验`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-config-center`。



### Consul

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-consul-parent`



#### 基本配置

访问 Consul `http://localhost:8500/` 按照约定添加 yaml 配置

- profile=default 时 yaml 配置 config/spring-cloud-service-c/data（config 是约定的目录前缀，spring-cloud-service-c 是微服务名称，data 是约定的配置文件名称）

  ```yaml
  my:
   k1: default,version=1
  ```

- profile=dev 时 yaml 配置 config/spring-cloud-service-c-dev/data（config 是约定的目录前缀，spring-cloud-service-c 是微服务名称，-dev 是 dev profile，data 是约定的配置文件名称）

  ```yaml
  my:
   k1: dev,version=1
  ```

- profile=prod 时 yaml 配置 config/spring-cloud-service-c-prod/data（config 是约定的目录前缀，spring-cloud-service-c 是微服务名称，-prod 是 prod profile，data 是约定的配置文件名称）

  ```yaml
  my:
   k1: prod,version=1
  ```

pom 添加 Consul 配置客户端依赖

```xml
<!-- Consul 作为服务配置和管理服务器的依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-config</artifactId>
</dependency>
```

bootstrap.yml 内容如下：

```yaml
spring:
  application:
    name: spring-cloud-service-c
  cloud:
    consul:
      host: 127.0.0.1
      port: 8500
      discovery:
        service-name: ${spring.application.name}
      # 配置 Consul 作为服务配置和管理服务器
      config:
        # Consul 不同 profile 配置目录路径使用 '-' 分隔，
        # 例如： config/spring-cloud-service-c-dev/data、config/spring-cloud-service-c-prod/data
        profile-separator: '-'
        format: YAML
        # 5 从 Consul 刷新一次配置
        watch:
          wait-time: 5
  # 加载 Consul 配置中心的 config/spring-cloud-service-c-prod/data 目录下的配置文件
  profiles:
    active: prod

```

application.yml 内容如下：

```yaml
server:
  port: 8083
```

使用 @Value 注入配置 my.k1

```java
@RestController
// 自定从 Consul 配置中心刷新到最新配置
@RefreshScope
public class ApiController {
    @Value("${my.k1:}")
    private String k1;

    /**
     * @param name
     * @return
     */
    @RequestMapping(value = "/api/v1/c/sayHello", method = RequestMethod.POST)
    public String sayHello(@RequestParam(value = "name", defaultValue = "") String name) {
        return "Hello " + name + "!!!，配置k1=" + k1;
    }
}
```

访问`http://localhost:8081/api/v1/a/sayHello?name=Dexter`查看是否成功加载 my.k1 配置。



#### @RefreshScope 自动刷新配置

在 @Value 所在类添加 @RefreshScope 自动刷新配置 

```java
// 自定从 Consul 配置中心刷新到最新配置
@RefreshScope
public class ApiController {
    @Value("${my.k1:}")
    private String k1;
```



### Nacos



## springcloudalibaba nacos

> 知识点：
>
> - springcloudalibaba和nacos集成配置
> - nacos服务注册基本配置
> - nacos服务注册Group、Namespace配置
>
> 参考spring-cloud/spring-cloud-nacos

## springcloudalibaba sentinel

> 知识点：
>
> - springcloudalibaba和sentinel集成配置

