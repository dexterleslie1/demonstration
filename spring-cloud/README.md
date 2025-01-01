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
> Feign（停止更新，不需要学习，已经被openfeign取代）
>
> OpenFeign



### Ribbon

> 注意：停止更新，许多遗留项目还在使用，所以需要学习。
>
> Ribbon 实现客户端的负载均衡`http://www.cnblogs.com/chry/p/7263281.html`

进程内负载均衡（负载均衡 + RestTemplate ）。



#### 运行示例

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-ribbon-parent`

启动 Consul

```bash
docker compose up -d
```

启动 ApplicationRibbon、ApplicationHelloworld（修改端口后启动两个 ApplicationHelloworld 应用）

访问`http://localhost:8081/api/v1/external/sayHello?name=Dexter`测试 Ribbon + RestTemplate 负载均衡。



#### 基本配置

pom 引入 Ribbon 依赖

```xml
<!-- SpringCloud Ribbon 客户端依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

RestTemplate 使用 @LoadBalanced 注解

```java
/**
 * 无论是否何种Ribbon负载均衡算法都需要配置下面的RestTemplate
 * @return
 */
@Bean
@LoadBalanced
RestTemplate restTemplate() {
    return new RestTemplate();
}
```



#### 使用 IRule 替换负载均衡算法

> 默认负载均衡算法是 RoundRobinRule

```java
// 注意：自动IRule一定需要放置到与Application启动类所在的包和子包外，例如：com.future.demo.myrule
// 否则@RibbonClient注解不生效
@Configuration
public class MyRuleRandom {
    @Bean
    public IRule rule() {
        // 随机选择服务负载均衡算法
        return new RandomRule();
    }
}

@SpringBootApplication
@RibbonClient(name = "spring-cloud-helloworld", configuration = MyRuleRandom.class)
@EnableDiscoveryClient
public class ApplicationRibbon {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationRibbon.class, args);
    }

    /**
     * 无论是否何种Ribbon负载均衡算法都需要配置下面的RestTemplate
     * @return
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```



### LoadBalancer



#### 介绍

SpringCloud LoadBalancer是Spring Cloud提供的一个客户端负载均衡器，它取代了传统的Ribbon组件，为微服务架构提供了更加灵活和强大的负载均衡功能。以下是对SpringCloud LoadBalancer的详细解析：

**一、定义与职责**

负载均衡器（LoadBalancer）是一种网络设备或软件机制，用于分发传入的网络流量负载请求到多个后端目标服务器上，从而实现系统资源的均衡利用和提高系统的可用性和性能。SpringCloud LoadBalancer作为客户端负载均衡器，它的主要职责是根据配置的负载均衡策略，从服务注册中心获取的服务实例列表中选择一个实例来处理请求。

**二、工作原理**

客户端负载均衡是一种将请求分发到多个服务实例的机制。每个发起服务调用的客户端都存有完整的目标服务地址列表，根据配置的负载均衡策略，由客户端自己决定向哪台服务器发起调用。这种方式相较于传统的网关层负载均衡，具有网络开销小、配置灵活等优点。

在Spring Cloud中，当客户端发起服务调用请求时，请求首先到达带有`@LoadBalanced`注解的`RestTemplate`或`LBRestTemplate`。`RestTemplate`或`LBRestTemplate`接收到请求后，会先经过一系列的拦截器（Interceptor）处理，这些拦截器可以用于实现认证、限流等功能。拦截器处理完成后，请求会被传送到LoadBalancer组件。LoadBalancer会根据配置的负载均衡策略和后端服务实例列表，选择一个合适的目标服务器。选定的目标服务器地址将被封装在一个新的HTTP请求中，然后由LoadBalancer将这个新的HTTP请求返回给`RestTemplate`或`LBRestTemplate`。最后，`RestTemplate`或`LBRestTemplate`将根据LoadBalancer返回的地址信息，直接与服务网关交互并完成服务调用。

**三、负载均衡策略**

SpringCloud LoadBalancer支持多种负载均衡策略，以满足不同场景的需求。常见的负载均衡策略包括：

1. **轮询（Round Robin）**：按顺序将每个新请求分配给下一个服务器。当到达列表末尾时，它会重新开始。这是最简单的负载均衡策略，适用于服务器性能相似且负载相对均衡的情况。
2. **随机（Random）**：随机选择一个服务器来处理新的请求。适用于服务器数量较多且请求分布均匀的场景。
3. **最少连接（Least Connections）**：选择当前连接数最少的服务器来处理新的请求。这种方法考虑了服务器的当前负载，适用于请求处理时间波动较大的场景。
4. **加权轮询（Weighted Round Robin）**：给每个服务器分配一个权重，服务器的权重越高，分配给该服务器的请求就越多。适用于服务器性能不均或希望给特定服务器更多流量的情况。
5. **加权随机（Weighted Random）**：与加权轮询类似，但是按照权重值来随机选择后端服务器。也可以用来处理后端服务器性能不均衡的情况，但是分发更随机。
6. **最短响应时间（Shortest Response Time）**：测量每个后端服务器的响应时间，并将请求发送到响应时间最短的服务器。这可以确保客户端获得最快的响应，适用于要求低延迟的应用。
7. **IP哈希（IP Hash）**：使用客户端的IP地址来计算哈希值，然后将请求发送到与哈希值对应的后端服务器。这种策略可用于确保来自同一客户端的请求都被发送到同一台后端服务器，适用于需要会话保持的情况。

SpringCloud LoadBalancer默认的负载均衡策略是轮询。如果需要自定义负载均衡策略，可以实现`ReactorServiceInstanceLoadBalancer`接口，并在配置类中注册自定义的负载均衡器。

**四、配置与使用**

要配置和使用SpringCloud LoadBalancer，需要按照以下步骤进行：

1. **添加依赖**：确保项目包含SpringCloud LoadBalancer的依赖。如果使用Maven，可以在`pom.xml`文件中添加相应的依赖项。
2. **创建RestTemplate Bean**：在配置类中，创建一个带有`@LoadBalanced`注解的`RestTemplate` Bean。这个注解会告诉Spring Cloud使用LoadBalancer来处理该`RestTemplate`的请求。
3. **使用服务名称**：在请求URL中使用服务名称而不是具体的IP地址或主机名。例如，如果服务注册在Eureka上，并且服务名称为`my-service`，则请求URL应该是`http://my-service/some-endpoint`。

**五、优势与意义**

SpringCloud LoadBalancer通过客户端负载均衡的方式，实现了更加高效和灵活的服务调用方式。它具有以下优势：

1. **动态配置**：支持动态更新配置，当后端服务实例发生变化时，可以快速响应并调整负载均衡策略。
2. **健康检查**：通过内置的健康检查机制，自动识别并排除故障实例，保证服务的可用性。
3. **集成RestTemplate**：通过给`RestTemplate`打标签的方式，将其转化为经过负载均衡器处理的`LBRestTemplate`，实现了对现有代码的无侵入式改造。
4. **多种负载均衡策略**：支持多种负载均衡算法，以满足不同场景的需求。

总之，SpringCloud LoadBalancer是微服务架构中不可或缺的重要组件之一，对于提升系统的性能和稳定性具有重要意义。



#### 运行示例

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-loadbalancer-parent`

启动 Consul

```bash
docker compose up -d
```

启动 ApplicationLoadBalancer、ApplicationHelloworld（修改端口后启动两个 ApplicationHelloworld 应用）

访问`http://localhost:8081/api/v1/external/sayHello?name=Dexter`测试 LoadBalancer + RestTemplate 负载均衡。



#### 基本配置

pom 引入 LoadBalancer 依赖

```xml
<!-- SpringCloud LoadBalancer 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
```

RestTemplate 使用 @LoadBalanced 注解

```java
// 创建RestTemplate并开启负载均衡
@Bean
@LoadBalanced
RestTemplate restTemplate() {
    return new RestTemplate();
}
```



#### 负载均衡算法切换

```java
@Configuration
@LoadBalancerClients(
        // spring-cloud-helloworld 使用 LoadBalancerConfig 配置的负载均衡算法 RoundRobinLoadBalancer
        @LoadBalancerClient(value = "spring-cloud-helloworld", configuration = LoadBalancerConfig.class))
public class LoadBalancerConfig {
    // 创建RestTemplate并开启负载均衡
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ReactorLoadBalancer<ServiceInstance> loadBalancer(Environment environment,
                                                      LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        // 随机负载均衡算法
        /*return new RandomLoadBalancer(loadBalancerClientFactory
                .getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);*/
        // 轮询负载均衡算法
        return new RoundRobinLoadBalancer(loadBalancerClientFactory
                .getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);
    }
}
```



### OpenFeign

>`https://www.jianshu.com/p/c0cb63e7640c`



#### 定义

OpenFeign是一个声明式的Web服务客户端，它使得编写Web服务客户端变得更加容易。以下是对OpenFeign的详细介绍：

**一、简介与背景**

OpenFeign是在Spring Cloud生态系统中的一个组件，它整合了Ribbon（客户端负载均衡器）和Eureka（服务发现组件），从而简化了微服务之间的调用。通过定义一个接口并使用注解的方式，开发者可以轻松地创建一个Web服务客户端，而不需要编写大量的模板代码。OpenFeign会自动生成接口的实现类，并使用Ribbon来调用相应的服务。

**二、核心组件与功能**

OpenFeign的核心组件包括Encoder（编码器）、Decoder（解码器）、Contract（契约）等。这些组件共同协作，实现了对HTTP请求的封装和调用。其中：

- **Encoder**：负责将请求对象编码为HTTP请求体。
- **Decoder**：负责将HTTP响应体解码为响应对象。
- **Contract**：定义了OpenFeign的注解和它们的含义，例如@FeignClient注解用于声明一个Feign客户端。

**三、使用与配置**

1. **添加依赖**：在Spring Cloud项目中，使用OpenFeign首先需要添加相应的依赖。通常，这可以通过在pom.xml文件中添加spring-cloud-starter-openfeign依赖来实现。
2. **开启OpenFeign**：在主应用类上添加@EnableFeignClients注解，以启用OpenFeign的功能。
3. **创建Feign客户端接口**：通过定义一个接口，并使用@FeignClient注解来指定服务提供者的名称和URL，可以创建一个Feign客户端。在接口中，可以使用Spring MVC的注解来定义需要调用的HTTP方法和路径。
4. **配置**：OpenFeign提供了多种配置选项，以满足不同的需求。例如，可以通过配置文件或配置类来设置日志级别、连接超时时间和请求处理超时时间等。

**四、日志配置**

OpenFeign提供了日志打印功能，通过配置调整日志级别，开发者可以了解请求的细节。这有助于在调试和定位问题时获取更多的信息。日志级别包括：

- **NONE**：不记录任何信息（默认）。
- **BASIC**：仅记录请求方法、URL以及响应状态码和执行时间。
- **HEADERS**：除了记录BASIC级别的信息外，还会记录请求和响应的头信息。
- **FULL**：记录所有请求与响应的明细，包括头信息、请求体、元数据等。

**五、超时配置**

为了避免服务调用连接和处理时间超时，可以对Feign的连接超时时间和请求处理超时时间进行配置。这可以通过在配置类中定义Request.Options对象，或者在配置文件中指定相关属性来实现。

**六、优势与适用场景**

OpenFeign的优势在于其易用性、集成性和轻量级特性。它简化了微服务之间的调用，使得开发者可以更加专注于业务逻辑的实现。同时，由于与Spring Cloud的紧密集成，OpenFeign可以方便地利用Spring Cloud提供的各种功能，如熔断、限流等。这使得OpenFeign在构建轻量级的微服务架构时具有显著的优势。

然而，需要注意的是，OpenFeign可能不适合处理大量并发请求或复杂业务场景。在这些情况下，可能需要考虑使用更强大的RPC框架，如Dubbo等。

**七、总结**

OpenFeign是一个功能强大且易于使用的Web服务客户端，它简化了微服务之间的通信和调用。通过合理的配置和使用，OpenFeign可以帮助开发者构建高效、可靠的微服务架构。



#### 运行示例

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-feign-demo`

启动 Consul

```bash
docker compose up -d
```

启动 ApplicationEureka、ApplicationConsumer、ApplicationProvider（修改端口后启动两个应用）

访问`http://localhost:8080/api/v1/external/product/1`测试应用是否正常



#### 基本配置

pom 引用 SpringCloud OpenFeign 依赖

```xml
<!-- SpringCloud OpenFeign 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <exclusions>
        <!-- 排除 Ribbon 以证明 OpenFeign + Consul 是依赖 SpringCloud LoadBalancer 提供的负载均衡算法支持 -->
        <exclusion>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix-ribbon</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

创建 OpenFeign 客户端

```java
@FeignClient(
        contextId = "productFeign1",
        value = "spring-cloud-feign-demo-provider",
        path = "/api/v1/product")
public interface ProductFeign {
    @GetMapping("{productId}")
    ObjectResponse<Product> info(@PathVariable("productId") Integer productId) throws BusinessException;

    @GetMapping("get")
    Product get(@RequestParam(value = "productId", required = false) Integer productId);

    @PostMapping("add")
    String add(@RequestHeader(value = "customHeader") String customHeader,
               @RequestBody(required = false) Product product);

    @GetMapping("timeout")
    String timeout();
}
```

Application 中启用 OpenFeign 客户端的支持

```java
@SpringBootApplication
// 应用程序中启用Feign客户端的支持
@EnableFeignClients(
        clients = {
                ProductFeign.class
        }
)
@EnableFutureExceptionHandler
public class ApplicationConsumer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConsumer.class, args);
    }
}
```

注入并调用 OpenFeign 客户端

```java
@Resource
ProductFeign productFeign;

@GetMapping("{productId}")
public ObjectResponse<Product> info(@PathVariable("productId") Integer productId) throws BusinessException {
    ObjectResponse<Product> response = this.productFeign.info(productId);
    return response;
}
```



#### 负载均衡

org.springframework.boot:spring-boot-starter-parent:2.2.7.RELEASE + org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR10 + OpenFeign + Eureka 默认使用 OpenFeign + Ribbon（为 OpenFeign 提供负载均衡算法支持），注意：使用 JMeter 压力测试才能够触发 Ribbon 负载均衡起作用。

org.springframework.boot:spring-boot-starter-parent:2.2.7.RELEASE + org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR10 + OpenFeign + Consul 默认使用 OpenFeign + LoadBalancer（为 OpenFeign 提供负载均衡算法支持）。

访问`http://localhost:8080/api/v1/external/product/1`测试



#### 超时设置

org.springframework.boot:spring-boot-starter-parent:3.3.7 + org.springframework.cloud:spring-cloud-dependencies:2023.0.4 版本的 OpenFeign 超时设置

```properties
# 注意：org.springframework.boot:spring-boot-starter-parent:3.3.7 + org.springframework.cloud:spring-cloud-dependencies:2023.0.4 版本的 OpenFeign 超时设置
# 该属性控制Feign客户端在尝试连接到目标服务时等待响应的最长时间。如果在这个时间内没有成功建立连接，
# 则会抛出超时异常。这有助于防止客户端在目标服务不可用时长时间挂起，从而提高系统的健壮性和响应性。
# default 表示全局 OpenFeign 设置
spring.cloud.openfeign.client.config.default.connect-timeout=75000
# 该属性用于控制服务间调用的响应时间，防止因某个服务响应过慢而导致整个调用链路的阻塞或失败。
# 它确保了Feign客户端在发起远程HTTP请求时，能够根据预设的超时时间限制，及时终止那些响应过慢的请求，从而保护系统的稳定性和响应性。
# default 表示全局 OpenFeign 设置
spring.cloud.openfeign.client.config.default.read-timeout=75000

# 指定 productFeign1 Feign 的超时设置
spring.cloud.openfeign.client.config.productFeign1.connect-timeout=75000
spring.cloud.openfeign.client.config.productFeign1.read-timeout=75000
```

org.springframework.boot:spring-boot-starter-parent:2.2.7.RELEASE + org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR10 版本的 OpenFeign 超时设置

```properties
# 注意：org.springframework.boot:spring-boot-starter-parent:2.2.7.RELEASE + org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR10 版本的 OpenFeign 超时设置
feign.client.config.default.connect-timeout=75000
feign.client.config.default.read-timeout=75000
```

访问`http://localhost:8080/api/v1/external/product/timeout`测试



#### 重试机制

配置如下：

```java
// 设置重试机制
@Bean
Retryer retryer() {
    // 不启用重试机制
    // return Retryer.NEVER_RETRY;
    // 每次重试的时间间隔为 1 秒，period 和 maxPeriod 设置为相等，最大重试次数为 3 次
    return new Retryer.Default(1000, 1000, 3);
}
```

访问`http://localhost:8080/api/v1/external/product/timeout`测试



#### 替换底层使用 HttpClient5 通讯

pom 配置

```xml
<!-- 替换底层使用 HttpClient5 通讯依赖 -->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-hc5</artifactId>
</dependency>
```

修改 application.properties 替换 HttpClient5

```properties
# 替换底层使用 HttpClient5 通讯
spring.cloud.openfeign.httpclient.hc5.enabled=true
```

访问`http://localhost:8080/api/v1/external/product/timeout`测试，查看错误堆栈显示使用 HttpClient5 作为底层通讯组件。



#### 日志级别设置

日志级别：

- NONE：不打印日志。
- BASIC：仅记录请求方法和URL以及响应状态码和执行时间。
- HEADERS：记录基本信息以及请求和响应标头。
- FULL：记录请求和响应的标头、正文和元数据。

application.properties 设置日志级别

```properties
# 配置 feign 客户端日志级别为 debug（只支持设置为 debug 级别），再配合使用 Logger.Level 设置不同的日志级别
# https://blog.csdn.net/weixin_43472934/article/details/122253068
logging.level.com.future.demo.spring.cloud.feign.common.feign=debug
```

Java 设置日志级别

```java
@Configuration
public class FeignConfig {
    // 设置 OpenFeign 日志级别
    @Bean
    Logger.Level feignLogLevel() {
        return Logger.Level.FULL;
    }
}
```

访问`http://localhost:8080/api/v1/external/product/1`测试



#### 启用请求和响应压缩

application.properties 添加配置如下：

```properties
# 启用请求和响应压缩
spring.cloud.openfeign.compression.request.enabled=true
spring.cloud.openfeign.compression.response.enabled=true
spring.cloud.openfeign.compression.request.mime-types=text/xml,application/xml,application/json
spring.cloud.openfeign.compression.request.min-request-size=2048
```

访问`http://localhost:8080/api/v1/external/product/1`测试，如果请求头有 Accept-Encoding: gzip 和 Accept-Encoding: deflate 表示已经启用请求和响应压缩。



#### 自定义请求拦截器并添加请求头和请求参数

定义请求拦截器

```java
/**
 * 所有feign调用http头都注入my-header参数
 * https://developer.aliyun.com/article/1058305
 */
@Slf4j
public class MyRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("my-header", "my-value");

        // https://stackoverflow.com/questions/559155/how-do-i-get-a-httpservletrequest-in-my-spring-beans
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest();
            String contextUserId = request.getParameter("contextUserId");
            if (StringUtils.hasText(contextUserId)) {
                template.query("contextUserId", contextUserId);
                log.debug("feign客户端成功注入上下文参数，contextUserId={}", contextUserId);
            }
        }
    }
}
```

注入请求拦截器

```java
// 自定义 OpenFeign 请求拦截器
@Bean
RequestInterceptor requestInterceptor() {
    return new MyRequestInterceptor();
}
```

访问`http://localhost:8080/api/v1/external/product/1`测试，查看日志会输出请求头和请求参数值。



#### Feign 客户端添加请求头

>使用 @RequestHeader 注解`https://www.cnblogs.com/laeni/p/12733920.html`

```java
@FeignClient(
        contextId = "productFeign1",
        value = "spring-cloud-feign-demo-provider",
        path = "/api/v1/product")
public interface ProductFeign {
    @PostMapping("add")
    String add(@RequestHeader(value = "customHeader") String customHeader,
               @RequestBody(required = false) Product product);
}
```

使用 curl 请求接口并查看日志输出的请求头

```bash
curl -X POST http://localhost:8080/api/v1/external/product/add
```



#### 响应错误处理 ErrorDecoder

OpenFeign是一个声明式的Web服务客户端，它使得写HTTP客户端变得更简单，主要用于微服务架构中，以简化服务间的调用。ErrorDecoder是OpenFeign中的一个重要接口，它在处理HTTP响应中的错误时发挥着关键作用。以下是对OpenFeign ErrorDecoder的详细解释：

**一、ErrorDecoder的作用**

ErrorDecoder接口用于处理HTTP响应中的错误。当OpenFeign客户端发送请求并接收到响应时，如果响应状态码表示错误（如4xx或5xx），则ErrorDecoder会被调用以决定是否将响应视为异常。通过自定义ErrorDecoder，可以对错误进行更精细的处理，比如根据不同的错误码返回不同的异常类型，或者在某些情况下忽略错误。

**二、可能使用ErrorDecoder的场景**

1. **特定错误码处理**：根据HTTP响应的不同错误码执行不同的逻辑。
2. **忽略某些错误**：在某些情况下，可能希望忽略某些特定的错误（如404 Not Found），并返回一个默认值或空对象，而不是抛出异常。
3. **增强错误日志**：通过自定义ErrorDecoder来增强错误日志，记录更多的上下文信息，以便于调试和监控。
4. **统一异常处理**：将HTTP错误转换为统一的异常类型，并在应用程序的其他部分进行捕获和处理。

**三、自定义ErrorDecoder的步骤**

1. **创建实现类**：创建一个实现ErrorDecoder接口的类。该类需要实现decode方法，该方法接收一个Response对象作为参数，并返回一个Exception对象。在decode方法中，可以根据响应的状态码和其他信息来决定是否将响应视为异常，并返回相应的异常类型。
2. **配置Feign客户端**：在Feign客户端的配置中指定自定义的ErrorDecoder。这通常是在一个带有@Configuration注解的配置类中，通过@Bean注解来定义一个ErrorDecoder类型的Bean。

**四、示例代码**

以下是一个自定义ErrorDecoder的示例代码：

```java
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.ErrorDecoder;
import feign.RetryableException;
 
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(Response response) {
        if (response.status() >= 400 && response.status() < 500) {
            // 处理客户端错误（如404, 401等）
            if (response.status() == 404) {
                return new ResourceNotFoundException("Resource not found");
            } else if (response.status() == 401) {
                return new UnauthorizedException("Unauthorized");
            }
            // 其他客户端错误可以统一处理或抛出异常
            return new DecodeException(response.request().toString(), response);
        } else if (response.status() >= 500) {
            // 处理服务器错误（如500, 502等）
            return new ServerErrorException("Server error");
        }
        // 对于成功的响应，返回null表示没有错误
        return null;
    }
 
    // 自定义异常类
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
 
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
 
    public static class ServerErrorException extends RuntimeException {
        public ServerErrorException(String message) {
            super(message);
        }
    }
}
```

然后，在Feign客户端的配置中指定这个自定义的ErrorDecoder：

```java
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
@EnableFeignClients
public class FeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
```

**五、注意事项**

1. **执行顺序**：在OpenFeign中，如果同时存在自定义Decoder和ErrorDecoder，当调用服务出现异常时，会先执行ErrorDecoder来处理异常，并将处理结果返回给调用方。如果调用服务正常返回结果，则先执行自定义Decoder对返回结果进行处理。
2. **测试与验证**：为了测试并验证自定义ErrorDecoder是否正常工作，可以编写单元测试或集成测试来模拟不同的HTTP响应，并检查是否抛出了预期的异常。

通过以上步骤和示例代码，可以灵活地自定义OpenFeign中的ErrorDecoder来处理各种HTTP响应错误。



自定义 ErrorDecoder

```java
/**
 * openfeign自定义错误处理
 * 问题：在调用feign过程中，需要经常编写代码判断errorCode是否不等于0，是则编写代码抛出业务异常，否则继续执行当前业务代码
 * 解决：使用openfeign自定义错误处理后，调用feign不再需要编写代码判断errorCode
 */
public class CustomizeErrorDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.BAD_REQUEST.value() ||
                response.status() == HttpStatus.FORBIDDEN.value() ||
                response.status() == HttpStatus.UNAUTHORIZED.value()) {
            String JSON = response.body().toString();
            try {
                JsonNode node = JSONUtil.ObjectMapperInstance.readTree(JSON);
                return new BusinessException(node.get("errorCode").asInt(), node.get("errorMessage").asText());
            } catch (IOException ex) {
                // 当发生http 400错误时，返回数据不为json格式，则继续使用系统默认处理错误
                response = response.toBuilder()
                        .status(response.status())
                        .reason(response.reason())
                        .request(response.request())
                        .headers(response.headers())
                        .body(JSON, Util.UTF_8)
                        .build();
                return super.decode(methodKey, response);
            }
        }
        return super.decode(methodKey, response);
    }
}
```

注入自定义 ErrorDecoder

```java
// 自定义 OpenFeign 错误解码器
@Bean
ErrorDecoder errorDecoder() {
    return new CustomizeErrorDecoder();
}
```



## 分布式事务

### Seata

### LCN



## 服务熔断、降级、限流

### SpringCloud CircuitBreaker

SpringCloud CircuitBreaker是Spring Cloud提供的一个用于处理分布式系统中服务调用的容错机制。以下是对SpringCloud CircuitBreaker的详细介绍：

**一、概述**

SpringCloud CircuitBreaker提供了一个跨越不同断路器实现的抽象，允许开发者选择最适合自己应用程序需求的断路器实现。它支持多种断路器实现，如Resilience4j、Hystrix等。其中，Resilience4j是Spring Cloud官方推荐的断路器实现，它是一个轻量级的容错库，专为Java 8及更高版本设计。

**二、主要功能**

1. **熔断**：当某个服务的错误率达到一定的阈值时，断路器会迅速切换到打开状态，阻止新的请求继续访问该服务，从而防止系统资源的过度消耗和级联失败的发生。
2. **自动恢复**：经过一段时间后，断路器会自动从打开状态切换到半开状态，允许部分请求通过以测试服务是否恢复正常。如果测试请求成功，断路器会关闭；否则，会重新打开。
3. **监控和报警**：SpringCloud CircuitBreaker提供了实时监控和报警功能，可以实时监控断路器的状态、请求成功率、失败率等指标，并在异常情况下发送报警信息。

**三、核心组件**

1. **断路器实例**：每个断路器实例都对应一个具体的服务调用。开发者可以通过配置断路器实例的属性来控制熔断器的行为，如失败率阈值、打开状态下的等待时间等。
2. **熔断器状态**：断路器具有三种普通状态（关闭、打开、半开）和两个特殊状态（禁用和强制打开）。这些状态之间的转换取决于服务的调用情况和配置的策略。

**四、配置与使用**

1. **引入依赖**：在项目的pom.xml文件中引入SpringCloud CircuitBreaker的依赖，如Resilience4j的依赖。
2. **配置属性**：在应用程序的配置文件中配置断路器的属性，如失败率阈值、打开状态下的等待时间、半开状态下允许的最大请求数等。这些配置可以根据实际需求进行调整。
3. **使用注解**：在需要熔断的服务调用方法上使用`@CircuitBreaker`注解，并指定备用方法（fallback）或回退逻辑。当断路器打开时，会调用备用方法或执行回退逻辑。

**五、与其他组件的集成**

SpringCloud CircuitBreaker可以与Spring Cloud的其他组件进行集成，如Spring Cloud Gateway、Spring Cloud OpenFeign等。通过集成这些组件，可以实现更强大的服务治理和容错能力。

**六、注意事项**

1. **合理设置阈值**：在设置失败率阈值时，需要根据服务的实际情况进行合理设置。如果设置得过高，可能会导致服务在正常情况下也被熔断；如果设置得过低，则可能无法有效防止级联失败的发生。
2. **监控和报警**：建议开启实时监控和报警功能，以便在断路器状态发生变化时及时获取通知并进行处理。
3. **定期评估和调整**：随着系统的运行和服务的变化，需要定期评估和调整断路器的配置和策略，以确保其始终能够有效地保护系统。

综上所述，SpringCloud CircuitBreaker是一个强大的容错机制，可以帮助开发者在分布式系统中实现服务调用的容错和降级。通过合理配置和使用断路器，可以提高系统的可用性和稳定性，为用户提供更好的服务体验。



### Hystrix

注意：进入维护模式，使用 Resilience4J 替代。



### Resilience4J

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/demo-spring-cloud-resilience4j`



#### 介绍

Resilience4j是一个专为Java应用设计的轻量级容错库，以下是对Resilience4j的详细介绍：

**一、简介**

Resilience4j受Netflix Hystrix的启发，但专为Java 8和函数式编程而设计。与Hystrix相比，Resilience4j更加轻量级，因为它只使用Vavr库，没有任何其他外部库依赖项。Resilience4j提供了高阶函数（装饰器），以通过断路器、速率限制器、重试或隔板（Bulkhead）增强任何功能接口、lambda表达式或方法引用。此外，Resilience4j可以在任何功能接口、lambda表达式或方法引用上堆叠多个装饰器，且允许开发者选择所需的装饰器。

**二、核心组件**

Resilience4j提供了丰富的模块化且灵活的容错选项，这些核心组件各自承担不同的职责，能够单独使用或组合使用，以应对不同类型的故障场景。具体组件包括：

1. **断路器（Circuit Breaker）**：当下游服务出现问题时，Resilience4j的断路器可以阻止应用程序持续向故障的服务发送请求，从而提高应用程序的整体可用性。
2. **限流器（Rate Limiter）**：Resilience4j可以防止应用程序向下游服务发送过多的请求，从而防止下游服务过载。限流器通过限制单位时间内允许的请求数量，确保系统在高负载下仍能稳定运行。
3. **重试（Retry）**：在网络不稳定或服务暂时不可用的情况下，Resilience4j可以自动重试失败的操作。
4. **隔板（Bulkhead）**：类似于船舶的舱壁设计，用于将系统划分为多个独立的部分，以防止某个部分的故障影响整个系统。Resilience4j的隔板通过限制并发调用的数量，确保关键资源的可用性。
5. **缓存（Cache）**：用于存储请求的响应结果，减少对后端服务的频繁调用，提高系统的响应速度和可用性。Resilience4j的缓存模块可以与断路器和重试机制结合使用，优化容错策略。
6. **时间器（Time Limiter）**：用于限制调用的最大执行时间，防止长时间阻塞导致系统资源被占用。Resilience4j的时间器通过设置超时时间，当调用超过指定时间后自动中断。

**三、特点与优势**

1. **轻量级且模块化**：Resilience4j采用模块化设计，开发者可以根据实际需求选择所需的功能模块，避免引入不必要的依赖，保持项目的轻量性。
2. **函数式编程支持**：Resilience4j完全基于Java 8的函数式编程理念，能够与现代Java应用无缝集成，提升代码的可读性和可维护性。
3. **易于配置和扩展**：通过简单的配置文件或代码方式，开发者可以轻松定制各个模块的行为。此外，Resilience4j提供丰富的扩展点，允许用户根据具体需求进行自定义。
4. **良好的集成能力**：Resilience4j能够与Spring Boot等主流框架良好集成，简化了在现有项目中引入容错机制的过程。同时，它也兼容多种监控工具，方便进行性能监控和故障诊断。
5. **活跃的社区和文档支持**：Resilience4j拥有活跃的开源社区，提供详尽的文档和丰富的示例，帮助开发者快速上手并解决实际问题。

**四、使用场景**

Resilience4j主要应用于微服务架构中，提供服务间调用的稳定性和弹性。例如，在一个微服务应用中，可能需要调用一个可能会失败的远程服务。此时，可以使用Resilience4j的熔断器和重试功能来增强服务调用。如果服务调用失败的次数超过了设定的阈值，熔断器就会打开，阻止进一步的服务调用。然后，可以使用重试对象来自动重试失败的服务调用。

**五、总结**

Resilience4j以其简单易用、灵活配置和良好的性能表现，成为许多Java开发者在构建高可用系统时的首选工具。通过使用Resilience4j，开发者可以更好地应对故障和异常，提高系统的可靠性和可用性。



#### 基本配置

>OpenFeign + Resilience4J 组合，OpenFeign 提供远程调用能力，Resilience4J 提供服务熔断和降级能力。SpringBoot 版本 3.3.7 + SpringCloud 版本 2023.0.4 + JDK 版本 17。

pom 添加如下配置：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- Resilience4J circuitbreaker 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
<!-- Resilience4J 需要 AOP 支持才能够正常运作 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

application.yaml 配置 circuitbreaker Resilience4J

```yaml
spring:
  cloud:
    openfeign:
      circuitbreaker:
        # 启用 OpenFeign 的断路器功能
        enabled: true
        # 支持 default 默认配置和指定 feign 的配置
        group:
          enabled: true
resilience4j:
  timelimiter:
    configs:
      default:
        # 设置了超时持续时间为10秒。这意味着，如果一个远程调用的响应时间超过了10秒，它将触发一个超时异常
        timeout-duration: 10s
  circuitbreaker:
    configs:
      default:
        # 设置了失败率阈值为50%。这意味着，如果在一个滑动窗口时间段内，远程调用的失败率达到了或超过了50%，断路器将会打开，阻止进一步的调用，以保护系统免受进一步的失败影响。
        failure-rate-threshold: 50
        # 设置了慢调用的持续时间阈值为5秒。这意味着，如果一个远程调用的响应时间超过了5秒，它将被视为一个慢调用。
        slow-call-duration-threshold: 1s
        # 设置了慢调用率阈值为30%。这意味着，在一个滑动窗口时间段内，如果远程调用的慢调用比率达到了或超过了30%，断路器将会打开。这
        slow-call-rate-threshold: 30
        # 设置了滑动窗口的类型为基于计数的类型，大小为6。这意味着断路器将使用一个大小为6的计数器来跟踪最近的调用结果。
        sliding-window-type: COUNT_BASED
        # 设置了滑动窗口的大小为6。这意味着断路器将使用一个大小为6的计数器来跟踪最近的调用结果，以便进行失败率计算和慢调用的统计。
        sliding-window-size: 6
        # 设置了在断路器打开之前，至少需要6次调用才能触发断路器的状态转换。这意味着，在断路器完全打开之前，必须收集足够的数据来评估系统的健康状况。
        minimum-number-of-calls: 6
        # 设置了在断路器打开后，自动从开放状态转换到半开状态的标志为true。这意味着，一旦断路器打开，它将尝试在一段时间后重新允许少量的调用通过
        automatic-transition-from-open-to-half-open-enabled: true
        # 设置了在断路器打开状态下，等待5秒后自动转换到半开状态。这意味着，一旦断路器打开，它将等待5秒钟后再尝试允许少量的调用通过
        wait-duration-in-open-state: 5s
        # 设置了在断路器半开状态下，允许2次调用通过。这意味着，当断路器从打开状态转换到半开状态时，它将只允许少量的调用尝试执行远程服务的方法
        permitted-number-of-calls-in-half-open-state: 2
        record-exceptions:
          # 指定了在断路器统计失败率时，哪些异常类型应该被记录为失败的调用。这里包括了所有继承自java.lang.Exception的异常类
          - java.lang.Exception

#      default:
#        # 设置了失败率阈值为50%。这意味着，如果在一个滑动窗口时间段内，远程调用的失败率达到了或超过了50%，断路器将会打开，阻止进一步的调用，以保护系统免受进一步的失败影响。
#        failure-rate-threshold: 50
#        # 设置了慢调用的持续时间阈值为5秒。这意味着，如果一个远程调用的响应时间超过了5秒，它将被视为一个慢调用。
#        slow-call-duration-threshold: 1s
#        # 设置了慢调用率阈值为30%。这意味着，在一个滑动窗口时间段内，如果远程调用的慢调用比率达到了或超过了30%，断路器将会打开。这
#        slow-call-rate-threshold: 30
#        # 设置了滑动窗口的类型为基于时间的类型
#        sliding-window-type: TIME_BASED
#        # 设置了滑动窗口的大小为5。这意味着断路器将使用一个大小为5的时间窗口来跟踪最近的调用结果，以便进行失败率计算和慢调用的统计。
#        sliding-window-size: 5
#        # 设置了在断路器打开之前，至少需要5次调用才能触发断路器的状态转换。这意味着，在断路器完全打开之前，必须收集足够的数据来评估系统的健康状况。
#        minimum-number-of-calls: 5
#        # 设置了在断路器打开后，自动从开放状态转换到半开状态的标志为true。这意味着，一旦断路器打开，它将尝试在一段时间后重新允许少量的调用通过
#        automatic-transition-from-open-to-half-open-enabled: true
#        # 设置了在断路器打开状态下，等待5秒后自动转换到半开状态。这意味着，一旦断路器打开，它将等待5秒钟后再尝试允许少量的调用通过
#        wait-duration-in-open-state: 5s
#        # 设置了在断路器半开状态下，允许2次调用通过。这意味着，当断路器从打开状态转换到半开状态时，它将只允许少量的调用尝试执行远程服务的方法
#        permitted-number-of-calls-in-half-open-state: 2
#        record-exceptions:
#          # 指定了在断路器统计失败率时，哪些异常类型应该被记录为失败的调用。这里包括了所有继承自java.lang.Exception的异常类
#          - java.lang.Exception
    instances:
      # 指定了名为demo-service-provider的远程服务实例，并使用默认配置（default）来配置断路器参数
      demo-service-provider:
        base-config: default
```

Feign 客户端 FeignClientProvider

```java
@FeignClient(value = "demo-service-provider", path = "/api/v1")
public interface FeignClientProvider {
    @GetMapping("test1")
    // 配置 Feign 客户端 circuitbreaker resilience4j 服务熔断和降级
    @CircuitBreaker(name = "demo-service-provider", fallbackMethod = "test1Fallback")
    public ObjectResponse<String> test1(@RequestParam(value = "flag", defaultValue = "") String flag) throws Throwable;

    // 服务降级 fallback 方法
    default public ObjectResponse<String> test1Fallback(Throwable throwable) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(throwable.getMessage());
        return response;
    }
}

```

调用 Feign 客户端

```java
@RestController
@RequestMapping("/api/v1")
public class DemoController {
    @Resource
    FeignClientProvider feignClientProvider;

    @GetMapping("test1")
    public ObjectResponse<String> test1(@RequestParam(value = "flag", defaultValue = "") String flag) throws Throwable {
        return this.feignClientProvider.test1(flag);
    }
}
```

启用 Feign 客户端

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {FeignClientProvider.class})
public class ApplicationConsumer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConsumer.class, args);
    }
}
```

运行 ApplicationTests#testForCountBased 测试用例



#### 运行示例

注意：运行 ApplicationTests 中不同的测试用例时，在每个测试用例之间需要重启 ApplicationConsumer，否则上次的测试会影响此次的测试。

启动 Consul

```bash
docker compose up -d
```

启动 ApplicationProvider、ApplicationConsumer 应用

运行 ApplicationTests#testForCountBased 测试用例



#### 基于计数窗口（CircuitBreaker）

application.yaml 配置如下：

```yaml
spring:
  cloud:
    openfeign:
      circuitbreaker:
        # 启用 OpenFeign 的断路器功能
        enabled: true
        # 支持 default 默认配置和指定 feign 的配置
        group:
          enabled: true
resilience4j:
  timelimiter:
    configs:
      default:
        # 设置了超时持续时间为10秒。这意味着，如果一个远程调用的响应时间超过了10秒，它将触发一个超时异常
        timeout-duration: 10s
  circuitbreaker:
    configs:
      default:
        # 设置了失败率阈值为50%。这意味着，如果在一个滑动窗口时间段内，远程调用的失败率达到了或超过了50%，断路器将会打开，阻止进一步的调用，以保护系统免受进一步的失败影响。
        failure-rate-threshold: 50
        # 设置了慢调用的持续时间阈值为5秒。这意味着，如果一个远程调用的响应时间超过了5秒，它将被视为一个慢调用。
        slow-call-duration-threshold: 1s
        # 设置了慢调用率阈值为30%。这意味着，在一个滑动窗口时间段内，如果远程调用的慢调用比率达到了或超过了30%，断路器将会打开。这
        slow-call-rate-threshold: 30
        # 设置了滑动窗口的类型为基于计数的类型，大小为6。这意味着断路器将使用一个大小为6的计数器来跟踪最近的调用结果。
        sliding-window-type: COUNT_BASED
        # 设置了滑动窗口的大小为6。这意味着断路器将使用一个大小为6的计数器来跟踪最近的调用结果，以便进行失败率计算和慢调用的统计。
        sliding-window-size: 6
        # 设置了在断路器打开之前，至少需要6次调用才能触发断路器的状态转换。这意味着，在断路器完全打开之前，必须收集足够的数据来评估系统的健康状况。
        minimum-number-of-calls: 6
        # 设置了在断路器打开后，自动从开放状态转换到半开状态的标志为true。这意味着，一旦断路器打开，它将尝试在一段时间后重新允许少量的调用通过
        automatic-transition-from-open-to-half-open-enabled: true
        # 设置了在断路器打开状态下，等待5秒后自动转换到半开状态。这意味着，一旦断路器打开，它将等待5秒钟后再尝试允许少量的调用通过
        wait-duration-in-open-state: 5s
        # 设置了在断路器半开状态下，允许2次调用通过。这意味着，当断路器从打开状态转换到半开状态时，它将只允许少量的调用尝试执行远程服务的方法
        permitted-number-of-calls-in-half-open-state: 2
        record-exceptions:
          # 指定了在断路器统计失败率时，哪些异常类型应该被记录为失败的调用。这里包括了所有继承自java.lang.Exception的异常类
          - java.lang.Exception
    instances:
      # 指定了名为demo-service-provider的远程服务实例，并使用默认配置（default）来配置断路器参数
      demo-service-provider:
        base-config: default
```

Feign 客户端注解 @CircuitBreaker

```java
@FeignClient(value = "demo-service-provider", path = "/api/v1")
public interface FeignClientProvider {
    @GetMapping("test1")
    // 配置 Feign 客户端 circuitbreaker resilience4j 服务熔断和降级
    @CircuitBreaker(name = "demo-service-provider", fallbackMethod = "test1Fallback")
    public ObjectResponse<String> test1(@RequestParam(value = "flag", defaultValue = "") String flag) throws Throwable;

    // 服务降级 fallback 方法
    default public ObjectResponse<String> test1Fallback(Throwable throwable) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(throwable.getMessage());
        return response;
    }
}
```

重启 ApplicationConsumer 应用，运行 ApplicationTests#testForCoutBased 测试用例



#### 基于慢调用（CircuitBreaker）

application.yaml 配置如下：

```yaml
spring:
  cloud:
    openfeign:
      circuitbreaker:
        # 启用 OpenFeign 的断路器功能
        enabled: true
        # 支持 default 默认配置和指定 feign 的配置
        group:
          enabled: true
resilience4j:
  timelimiter:
    configs:
      default:
        # 设置了超时持续时间为10秒。这意味着，如果一个远程调用的响应时间超过了10秒，它将触发一个超时异常
        timeout-duration: 10s
  circuitbreaker:
    configs:
      default:
        # 设置了慢调用的持续时间阈值为5秒。这意味着，如果一个远程调用的响应时间超过了5秒，它将被视为一个慢调用。
        slow-call-duration-threshold: 1s
        # 设置了慢调用率阈值为30%。这意味着，在一个滑动窗口时间段内，如果远程调用的慢调用比率达到了或超过了30%，断路器将会打开。这
        slow-call-rate-threshold: 30
    instances:
      # 指定了名为demo-service-provider的远程服务实例，并使用默认配置（default）来配置断路器参数
      demo-service-provider:
        base-config: default
```

Feign 客户端注解 @CircuitBreaker

```java
@FeignClient(value = "demo-service-provider", path = "/api/v1")
public interface FeignClientProvider {
    @GetMapping("test1")
    // 配置 Feign 客户端 circuitbreaker resilience4j 服务熔断和降级
    @CircuitBreaker(name = "demo-service-provider", fallbackMethod = "test1Fallback")
    public ObjectResponse<String> test1(@RequestParam(value = "flag", defaultValue = "") String flag) throws Throwable;

    // 服务降级 fallback 方法
    default public ObjectResponse<String> test1Fallback(Throwable throwable) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(throwable.getMessage());
        return response;
    }
}
```

重启 ApplicationConsumer 应用，运行 ApplicationTests#testForSlowCall 测试用例



#### 基于时间窗口（CircuitBreaker）

application.yaml 配置如下：

```yaml
spring:
  cloud:
    openfeign:
      circuitbreaker:
        # 启用 OpenFeign 的断路器功能
        enabled: true
        # 支持 default 默认配置和指定 feign 的配置
        group:
          enabled: true
resilience4j:
  timelimiter:
    configs:
      default:
        # 设置了超时持续时间为10秒。这意味着，如果一个远程调用的响应时间超过了10秒，它将触发一个超时异常
        timeout-duration: 10s
  circuitbreaker:
    configs:
      default:
        # 设置了失败率阈值为50%。这意味着，如果在一个滑动窗口时间段内，远程调用的失败率达到了或超过了50%，断路器将会打开，阻止进一步的调用，以保护系统免受进一步的失败影响。
        failure-rate-threshold: 50
        # 设置了慢调用的持续时间阈值为5秒。这意味着，如果一个远程调用的响应时间超过了5秒，它将被视为一个慢调用。
        slow-call-duration-threshold: 1s
        # 设置了慢调用率阈值为30%。这意味着，在一个滑动窗口时间段内，如果远程调用的慢调用比率达到了或超过了30%，断路器将会打开。这
        slow-call-rate-threshold: 30
        # 设置了滑动窗口的类型为基于时间的类型
        sliding-window-type: TIME_BASED
        # 设置了滑动窗口的大小为5。这意味着断路器将使用一个大小为5的时间窗口来跟踪最近的调用结果，以便进行失败率计算和慢调用的统计。
        sliding-window-size: 5
        # 设置了在断路器打开之前，至少需要5次调用才能触发断路器的状态转换。这意味着，在断路器完全打开之前，必须收集足够的数据来评估系统的健康状况。
        minimum-number-of-calls: 5
        # 设置了在断路器打开后，自动从开放状态转换到半开状态的标志为true。这意味着，一旦断路器打开，它将尝试在一段时间后重新允许少量的调用通过
        automatic-transition-from-open-to-half-open-enabled: true
        # 设置了在断路器打开状态下，等待5秒后自动转换到半开状态。这意味着，一旦断路器打开，它将等待5秒钟后再尝试允许少量的调用通过
        wait-duration-in-open-state: 5s
        # 设置了在断路器半开状态下，允许2次调用通过。这意味着，当断路器从打开状态转换到半开状态时，它将只允许少量的调用尝试执行远程服务的方法
        permitted-number-of-calls-in-half-open-state: 2
        record-exceptions:
          # 指定了在断路器统计失败率时，哪些异常类型应该被记录为失败的调用。这里包括了所有继承自java.lang.Exception的异常类
          - java.lang.Exception
    instances:
      # 指定了名为demo-service-provider的远程服务实例，并使用默认配置（default）来配置断路器参数
      demo-service-provider:
        base-config: default
```

Feign 客户端注解 @CircuitBreaker

```java
@FeignClient(value = "demo-service-provider", path = "/api/v1")
public interface FeignClientProvider {
    @GetMapping("test1")
    // 配置 Feign 客户端 circuitbreaker resilience4j 服务熔断和降级
    @CircuitBreaker(name = "demo-service-provider", fallbackMethod = "test1Fallback")
    public ObjectResponse<String> test1(@RequestParam(value = "flag", defaultValue = "") String flag) throws Throwable;

    // 服务降级 fallback 方法
    default public ObjectResponse<String> test1Fallback(Throwable throwable) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(throwable.getMessage());
        return response;
    }
}
```

重启 ApplicationConsumer 应用，运行 ApplicationTests#testForTimeBased 测试用例



#### 舱壁隔离（Bulkhead）

>todo 实验未成功

pom 引入舱壁隔离依赖

```xml
<!-- 舱壁隔离（Bulkhead）依赖 -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-bulkhead</artifactId>
</dependency>
```



#### 限流（RateLimiter）

>todo 未做实验



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



### 微服务链路追踪原理

微服务链路追踪原理主要涉及在分布式系统中跟踪和监控微服务间相互调用的过程。以下是对微服务链路追踪原理的详细解释：

一、链路追踪的定义与背景

链路追踪，全称是Microservices Distributed Tracing，是一种用于监测和诊断分布式应用程序的技术。它允许开发人员跟踪一个请求在分布式系统中的完整路径和流程，从而帮助开发人员和运维团队追踪和诊断分布式系统中的性能问题和故障。

随着微服务架构的流行，服务按照不同的维度进行拆分，在复杂的微服务架构系统中，会形成一个复杂的分布式服务调用链路。由于一次请求往往需要涉及多个服务，每个服务可能是由不同的团队开发，使用了不同的编程语言和框架，因此，如何发现问题、如何追踪服务故障等成为难题。而链路追踪正是为了解决这种问题而诞生的。

二、链路追踪的核心概念

1. **Trace（追踪）**：一个完整的请求路径，包括了所有相关的组件和服务。
2. **Span（跨度）**：代表请求路径中的一个组件或服务的操作。每个Span都有一个唯一的ID（Span ID），用于标识该操作，同时记录了一些关键的元数据，如开始时间、结束时间、执行耗时等。
3. **Trace ID（追踪ID）**：在整个链路中唯一标识一个追踪的ID，可以在各个组件间传递。
4. **Annotation（注解）**：用于记录与Span相关的附加信息，如日志、事件、异常等。
5. **Trace Context（追踪上下文）**：包含了当前请求的追踪ID、跨度ID等信息，用于在不同组件间传递和关联追踪信息。

三、链路追踪的原理

链路追踪的原理主要是通过在每个组件中插入唯一的标识符（Trace ID）来追踪请求，并将相关信息（如请求的起始时间、耗时、调用链路等）记录下来。这些信息可以帮助开发人员了解整个请求的流程，从而进行故障排除、性能优化和监控分析。

具体来说，当一个请求到达后端后，在处理业务的过程中，可能还会调用其他多个微服务来实现功能。在这个过程中，链路追踪系统会为每个请求生成一个全局唯一的Trace ID，并为这条链路中的每一次分布式调用生成一个Span ID。通过Trace ID和Span ID，系统可以追踪和记录整个请求路径中的每个组件和服务的操作情况。

四、链路追踪的实现方式

目前市面上有多种链路追踪组件可供选择，如Zipkin、Sleuth、Jaeger、Pinpoint等。这些组件都提供了相应的客户端和服务端工具，用于生成、上报、存储、分析和展示追踪数据。

以Zipkin为例，它是一款开源的分布式实时数据追踪系统，能够收集服务间调用的时序数据，提供调用链路的追踪。Zipkin可以分为Zipkin Server和Zipkin Client两部分。Zipkin Server用于数据的采集存储、数据分析与展示；而Zipkin Client则基于不同的语言及框架封装了一系列客户端工具，这些工具完成了追踪数据的生成与上报功能。

五、链路追踪的应用场景

1. **故障排查**：通过追踪请求的路径和性能指标，可以快速定位和解决故障。
2. **性能优化**：通过分析请求的性能指标，可以找到系统中的性能瓶颈，并进行优化。
3. **依赖分析**：通过分析请求的依赖关系，可以了解系统中各个微服务之间的依赖关系，帮助进行系统设计和架构优化。
4. **监控和警报**：微服务链路追踪可以与监控系统集成，实时监控系统的性能指标，并在达到预设阈值时触发警报。

综上所述，微服务链路追踪原理是通过在每个组件中插入唯一的标识符来追踪请求，并记录相关信息以帮助开发人员了解整个请求的流程。这一技术对于微服务架构中的故障排查、性能优化、依赖分析和监控警报等方面都具有重要意义。



### 微服务链路追踪时间如何计算

在微服务链路追踪中，时间的计算对于性能分析和故障排查至关重要。以下是如何计算微服务链路追踪中的时间：

一、基本时间参数

1. **Client Sent（cs）**：客户端发起调用请求到服务端的时间戳。
2. **Server Received（sr）**：服务端接收到了客户端的调用请求的时间戳。
3. **Server Sent（ss）**：服务端完成了处理，准备将信息返回给客户端的时间戳。
4. **Client Received（cr）**：客户端接收到了服务端的返回信息的时间戳。

二、时间计算方式

1. **网络延迟**：
   - 计算方式：sr - cs
   - 含义：表示请求从客户端发送到服务端所花费的时间，即网络传输的延迟。
2. **服务执行时间**：
   - 计算方式：ss - sr
   - 含义：表示服务端处理请求所花费的时间，即服务执行的时间。
3. **服务响应延迟**：
   - 计算方式：cr - ss
   - 含义：表示服务端完成处理后，将结果返回给客户端所花费的时间，即服务响应的延迟。
4. **整个服务调用执行时间**：
   - 计算方式：cr - cs
   - 含义：表示从客户端发起请求到接收到服务端返回结果所花费的总时间，即整个服务调用的执行时间。

三、时间戳的精度

为了确保时间计算的准确性，时间戳通常需要精确到微秒级。这可以通过使用高精度的时间戳生成器或时钟源来实现。

四、实际应用中的考虑

在实际应用中，除了上述基本时间参数外，还需要考虑以下因素：

1. **时钟同步**：确保所有参与链路追踪的服务节点之间的时钟是同步的，以避免因时钟偏差而导致的时间计算错误。
2. **时间记录点**：在每个服务节点上，需要选择合适的时间记录点来记录cs、sr、ss和cr等时间戳。这些记录点应该能够准确反映请求在各个阶段的处理情况。
3. **数据上传与存储**：将记录的时间戳和相关数据上传到链路追踪系统，并进行存储和分析。这有助于开发人员了解系统的性能瓶颈和潜在问题。

综上所述，微服务链路追踪中的时间计算涉及多个时间参数和计算方式。通过准确记录和分析这些时间参数，开发人员可以更好地了解系统的性能表现，并进行针对性的优化和故障排查。



### Zipkin

#### 什么是 Zipkin？

Zipkin是一个开源的分布式追踪系统，由Twitter公司开发并贡献给开源社区。它主要用于收集和分析分布式系统中各个服务之间的调用关系及时延数据。以下是关于Zipkin的详细介绍：

一、主要功能

1. **分布式追踪**：Zipkin能够追踪请求在分布式系统中的传播路径，包括请求经过的所有服务节点以及每个节点上的处理时间。
2. **可视化分析**：Zipkin提供直观的可视化界面，通过图形化的方式展示请求的调用链路和各个组件的耗时情况，帮助开发人员快速定位性能瓶颈和延迟问题。
3. **异常监控**：Zipkin能够记录请求中发生的异常情况，并提供异常分析功能，帮助开发人员快速定位和解决问题。
4. **依赖分析**：Zipkin可以分析系统中各个组件之间的依赖关系，帮助开发人员了解系统的整体架构和组件之间的通信情况。

二、系统架构

Zipkin的系统架构主要包括以下几个部分：

1. **Collector**：Collector是一个收集数据的守护进程，负责接收来自各个服务节点的追踪数据，并进行验证、存储和索引等操作。
2. **Storage**：Storage用于存储追踪数据，默认情况下，Zipkin将数据存储在内存中，但也可以通过配置将其持久化到数据库（如MySQL、ElasticSearch或Cassandra）中。
3. **Query Service**：Query Service是一个返回JSON数据的REST API，用于查询存储在Storage中的追踪数据。
4. **Web UI**：Web UI是Zipkin自带的Web应用程序，通过它可以查询并浏览存储在Storage中的追踪数据，提供直观的可视化界面。

三、工作原理

1. **数据收集**：在分布式系统中，每个服务节点都需要配置Zipkin客户端（如Brave），用于生成并发送追踪数据到Zipkin服务端。
2. **数据传输**：追踪数据可以通过多种方式（如HTTP、Kafka或Scribe）传输到Zipkin服务端。
3. **数据存储**：Collector接收到追踪数据后，会将其存储到指定的存储系统中。
4. **数据查询与展示**：开发人员可以通过访问Zipkin服务端的Web UI，输入查询条件（如服务名、时间范围等）来搜索追踪数据，并通过可视化界面查看调用链路和各个组件的耗时情况。

四、应用场景

Zipkin广泛应用于微服务架构中，用于监控和调试分布式系统中的服务调用链。通过Zipkin，开发人员可以清晰地看到请求在各个服务节点之间的传播路径和耗时情况，从而快速定位性能瓶颈和延迟问题。此外，Zipkin还可以用于分析系统中各个组件之间的依赖关系，帮助开发人员了解系统的整体架构和组件之间的通信情况。

综上所述，Zipkin是一个功能强大的分布式追踪系统，它能够帮助开发人员监控和调试分布式系统中的服务调用链，提供对系统性能和瓶颈的洞察。通过Zipkin，开发人员可以更加高效地定位和解决性能问题，提升系统的稳定性和响应速度。



#### 使用 Docker 运行 Zipkin

>使用 Docker 运行 Zipkin `https://www.jianshu.com/p/60c6fd6a8106`、`https://www.cnblogs.com/binz/p/12658020.html`

运行 Zipkin 服务的 docker-compose.yaml 文件

```yaml
version: "3.0"

services:
  demo-zipkin-server:
    image: openzipkin/zipkin
    environment:
      - TZ=Asia/Shanghai
    ports:
      - '9411:9411'
```

访问 Zipkin 控制台`http://localhost:9411/zipkin/`



### Sleuth + Zipkin

>Tracing in Distributed Systems with Spring Cloud Sleuth `https://reflectoring.io/tracing-with-spring-cloud-sleuth/`
>
>

注意：逐渐地被 Micrometer Tracing + Zipkin 替代

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-sleuth-parent`



#### 运行示例

启动 Zipkin 服务

```yaml
docker compose up -d
```

启动 ApplicationEureka、ApplicationGateway、ApplicationServiceA、ApplicationServiceB、ApplicationServiceC 应用

访问`http://localhost:8080/api/v1/a/test1?name=Dexter`创建链路追踪日志记录

访问 Zipkin 控制台`http://localhost:9411/zipkin/`查看链路追踪日志。



### Micrometer Tracing + Zipkin

>todo 实验未做完！



### Skywalking

详细用法请参考示例 <a href="/skywalking/" target="_blank">链接</a>



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

