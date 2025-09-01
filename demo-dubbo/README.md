## 互联网应用的架构演进历史

好的，互联网应用的架构演进历史是一个精彩纷呈的故事，它伴随着用户量的爆炸式增长、技术的飞速迭代以及开发理念的深刻变革。其核心驱动力始终是：**如何用更高效、更稳定、更经济的方式应对不断增长的业务复杂度和用户访问压力**。

下面我将以几个标志性阶段为您详细解读这段演进史。

---

### 第一阶段：单体架构 (Monolithic Architecture)

这是互联网初期的经典架构，几乎所有应用都始于此种形式。

*   **时期**：20世纪90年代 - 21世纪初
*   **架构描述**：
    *   所有功能模块（Web服务器、业务逻辑、数据访问层等）都紧密耦合在一起，打包成一个完整的应用程序（一个WAR/JAR包）。
    *   通常采用**LAMP**（Linux + Apache + MySQL + PHP/Perl/Python）或**MVC**（Model-View-Controller）框架作为技术栈。
    *   应用、文件、数据库全部部署在一台服务器上。



*   **优点**：
    *   **简单**：开发、测试、部署都非常直接。
    *   **高效**：本地方法调用，性能极高。
*   **缺点**：
    *   **维护成本高**：代码庞大，耦合严重，修改一个地方可能影响全局，后期“牵一发而动全身”。
    *   **技术栈固化**：难以采用新的技术框架。
    *   **扩展性差**：只能进行**垂直扩展（Scale Up）**，即提升单台服务器的性能（CPU、内存），成本高昂且存在物理上限。
    *   **可靠性低**：任何一个微小模块的bug都可能导致整个应用崩溃。
    *   **发布慢**：编译、部署整个单体应用，每次发布都是一个大工程。

---

### 第二阶段：垂直架构 / 应用拆分

随着用户量增长，单体服务器无法承受压力，自然而然地想到将不同的应用拆分开，独立部署。

*   **时期**：大约2000年中期
*   **架构描述**：
    *   将原先单体应用中的不同功能模块（如商城、论坛、搜索）拆分成多个独立的应用。
    *   通过**负载均衡器**（如Nginx、F5）将用户请求分发到不同的应用服务器上。
    *   数据库也开始分离，不同的应用可能使用自己独立的数据库。



*   **优点**：
    *   **解决了应用级别的扩展问题**：可以针对访问量大的应用单独进行**水平扩展（Scale Out）**（增加服务器数量）。
    *   **一定程度上降低了耦合**：应用之间互不影响。
    *   **提升了团队效率**：不同团队可以负责不同的应用。
*   **缺点**：
    *   **应用内部依然是单体**：每个被拆分出来的应用本身可能还是一个庞然大物。
    *   **公共模块重复开发**：每个应用都需要的一些通用功能（如用户登录验证）会在多个应用中重复开发，导致代码冗余和维护困难。

---

### 第三阶段：面向服务架构 (SOA - Service-Oriented Architecture)

为了解决公共模块复用的问题，将重复的功能抽取出来，形成可复用的服务。

*   **时期**：大约2010年左右开始流行
*   **架构描述**：
    *   将核心的、可复用的业务功能抽取出来，作为独立的**服务**（Service）。
    *   应用系统通过**企业服务总线（ESB）** 来调用这些远程服务。ESB负责消息路由、协议转换、服务编排等。
    *   服务之间通过**SOAP**等重量级协议进行通信，通常使用**XML**格式传输数据。



*   **优点**：
    *   **复用性高**：核心服务被抽取，避免了重复开发。
    *   **解耦**：应用系统不需要关心服务的具体实现，只需调用接口。
    *   **异构系统集成**：ESB可以整合不同技术平台的服务。
*   **缺点**：
    *   **ESB本身容易成为瓶颈和单点故障**：所有流量都经过ESB，使其变得臃肿和复杂。
    *   **治理复杂**：服务依赖关系管理、状态监控等变得困难。
    *   **协议笨重**：XML和SOAP使得通信效率不高。

---

### 第四阶段：微服务架构 (Microservices Architecture)

这是当前主流的大型互联网应用架构。它可以说是SOA的一种轻量化和精细化演进。

*   **时期**：大约2014年至今（由Netflix、Amazon等公司实践并推广）
*   **架构描述**：
    *   **彻底的服务化拆分**：将单个应用拆分成**一组小而自治的服务**。每个服务都围绕特定的业务能力构建（如用户服务、订单服务、商品服务），可以独立开发、部署和扩展。
    *   **轻量级通信**：服务之间通过**HTTP/RESTful API**或**gRPC**等轻量级协议进行通信，数据格式通常为**JSON**。
    *   **去中心化治理**：没有统一的ESB总线，每个服务可以使用最适合自身的技术栈（如不同的编程语言、数据库）。
    *   **基础设施自动化**：强烈依赖**DevOps、CI/CD（持续集成/持续部署）** 和**容器化技术（如Docker）** 来管理大量的服务。
    *   **独立数据管理**：每个微服务都拥有自己独立的数据存储。



*   **优点**：
    *   **高度解耦**：服务彻底分离，修改和部署一个服务无需影响其他服务。
    *   **技术选型灵活**：团队可以为特定服务选择最合适的技术。
    *   **精准扩展**：只需对压力大的服务进行扩容，非常节省资源。
    *   **容错性高**：单个服务故障不会导致整个系统瘫痪。
*   **缺点**：
    *   **架构极其复杂**：服务治理、监控、链路追踪、故障排查、分布式事务等带来了巨大的挑战。
    *   **运维成本高**：需要强大的自动化运维和容器编排平台（如Kubernetes）支持。
    *   **网络和通信开销大**：远程调用取代了本地调用，延迟和网络问题成为需要考虑的因素。

---

### 第五阶段：服务网格 (Service Mesh) 与无服务器 (Serverless)

这是架构演进的前沿，旨在进一步简化微服务的治理和运维成本。

*   **服务网格 (Service Mesh)**：
    *   **概念**：作为微服务架构的补充，它将服务间的通信、安全性、可观测性（监控、追踪）等逻辑从业务代码中剥离出来，交给一个专用的基础设施层（Sidecar代理，如Envoy）来处理。
    *   **代表技术**：**Istio**, Linkerd。
    *   **价值**：让开发者可以更专注于业务逻辑，而将复杂的网络问题交给统一平台管理。

*   **无服务器架构 (Serverless)**：
    *   **概念**：开发者只需编写函数代码（Function），并上传到云平台（如AWS Lambda）。云平台负责服务器的资源分配、扩容、运维等所有工作，按函数的实际执行时间和资源消耗收费。
    *   **价值**：实现了极致的弹性伸缩和成本优化，真正做到“按需付费”，无需关心任何服务器资源。

---

### 总结与展望

互联网架构的演进路径清晰地体现了 **“拆”与“合”的哲学**：从单一整体，不断拆分成更小、更独立的单元，同时通过新的技术（如ESB、微服务框架、Service Mesh）将这些单元有机地“合”起来管理。

| 阶段 | 架构模式 | 核心思想 | 优点 | 缺点 |
| ：--- | :--- | :--- | :--- | :--- |
| **第一阶段** | **单体架构** | all in one | 简单、高效 | 难维护、扩展性差 |
| **第二阶段** | **垂直架构** | 应用拆分 | 解决应用扩展 | 公共模块冗余 |
| **第三阶段** | **SOA** | 服务复用，ESB集中治理 | 复用、解耦 | ESB瓶颈、治理复杂 |
| **第四阶段** | **微服务** | 彻底拆分，去中心化 | 高度解耦、灵活扩展 | 架构复杂、运维难 |
| **第五阶段** | **Service Mesh/Serverless** | 关注点分离、极致弹性 | 简化治理、成本优化 |  vendor锁定、冷启动 |

**未来的趋势**可能会朝着**异构计算**（AI、量子）、**云原生**（一切在云上）、**智能化运维（AIOps）** 以及**更加细粒度的算力调度**方向发展，但“分而治之”的核心思想不会改变。选择合适的架构，永远是业务需求、团队能力和技术成本之间的平衡艺术。



## 概念

好的，我们来详细解释一下 **Dubbo**。

简单来说，**Dubbo 是一款高性能、轻量级的开源 Java RPC（远程过程调用）框架，它主要用于构建分布式服务架构，是实现微服务化的核心工具之一。**

您可以把它理解成 **“分布式服务的管家”**。

---

### 1. 核心定位：解决什么问题？

在微服务架构中，一个大型应用被拆分成多个小的、独立的服务。这些服务需要相互调用和通信。Dubbo 就是为了让这种**服务之间的调用**像调用本地方法一样简单、高效和可靠。

如果没有 Dubbo，服务调用可能会变得非常麻烦（需要自己处理网络通信、寻址、负载均衡等），而 Dubbo 提供了一整套完善的解决方案。

### 2. 核心架构与工作原理

Dubbo 采用了经典的**服务提供者（Provider）** 和**服务消费者（Consumer）** 模型，并依赖一个**注册中心（Registry）** 来协调两者。其工作流程如下图所示：



1.  **服务提供者 (Provider)**：
    *   提供服务（比如“用户查询服务”）的实现。
    *   启动时，向**注册中心**注册自己的地址和提供的服务列表。

2.  **服务消费者 (Consumer)**：
    *   调用远程服务的应用。
    *   启动时，向**注册中心**订阅自己所需的服务。注册中心会将提供者的地址列表返回给消费者。
    *   当需要调用服务时，消费者根据从注册中心获取的地址列表，直接调用提供者。

3.  **注册中心 (Registry)**：
    *   核心的协调者，负责服务的注册与发现。
    *   常见的注册中心有：**Zookeeper**、**Nacos**、**Redis** 等。
    *   当提供者宕机或下线时，注册中心会通知消费者，确保消费者不会调用到无效的节点。

4.  **监控中心 (Monitor)**（可选但重要）：
    *   统计服务调用次数、调用时间等监控信息，用于优化和运维。

### 3. 核心特性与优势

Dubbo 之所以流行，是因为它提供了远超基本 RPC 调用的强大功能：

*   **面向接口的远程方法调用**：就像调用本地方法一样调用远程服务，对开发者非常友好。
*   **智能容错**：提供多种容错机制，比如：
    *   **失败自动切换**（Failover）：调用失败后自动重试其他服务器。
    *   **快速失败**（Failfast）：只调用一次，失败立即报错。
    *   **失败安全**（Failsafe）：调用失败后忽略异常。
*   **负载均衡**：自动将请求分摊到多个提供者服务器上，避免单一服务器压力过大。策略包括：
    *   随机（Random）
    *   轮询（RoundRobin）
    *   最少活跃调用（LeastActive）
    *   一致性哈希（ConsistentHash）
*   **服务自动注册与发现**：服务可以动态上下线，消费者能自动感知，无需修改任何配置。
*   **高性能**：基于 Netty 等 NIO 框架，通信协议紧凑，序列化效率高，使其在网络传输性能上表现优异。

### 4. Dubbo 的历史与生态

*   **起源**：由阿里巴巴公司开源，在国内得到了极其广泛的应用，经历了多年“双十一”超高并发场景的考验。
*   **发展**：有一段时间曾停止维护，但之后又重新焕发活力。现在由 Apache 软件基金会进行孵化，成为了 **Apache Dubbo**，是一个更加开放和活跃的开源项目。
*   **相关技术**：
    *   **Dubbo vs. Spring Cloud**：Spring Cloud 是一整套微服务生态的**标准集合**（包含服务发现、配置管理、断路器等），可以使用多种组件（Eureka, Consul等）来实现。而 Dubbo 最初核心是一个**高性能的 RPC 框架**，其生态也在逐步完善（例如通过 Dubbo + Nacos + Sentinel 的组合也可以实现与 Spring Cloud 类似的全家桶功能）。
    *   **Dubbo 3.0**：最新版本提出了**应用级服务发现**等新特性，更好地拥抱云原生，与 Kubernetes 等基础设施集成得更紧密。

### 总结

**Dubbo 的本质是一个强大的 RPC 框架，它是构建分布式系统和微服务架构的基石。它让服务之间的远程调用变得简单、高效、可控且高可用。**

如果您听到一个技术项目在使用 Dubbo，那基本可以断定：
1.  它是一个**分布式系统**。
2.  它的架构很可能是 **微服务** 形式的。
3.  它非常关注服务的 **性能和稳定性**。



## 只能微服务间内部调用吗？不能用于互联网向外暴露`api`接口吗？

这是一个非常好的问题，它触及了 Dubbo 的核心设计理念和常见的使用场景。

**直接回答：不是的。** Dubbo 本身并不仅限于微服务内部调用。从技术上讲，它**可以**用于对外暴露 API 接口，但这**不是它的主要设计目标**，通常也不是最佳实践。

下面我们来详细解释为什么，以及如何实现。

### 核心区别：RPC vs. RESTful HTTP API

*   **Dubbo 是 RPC (Remote Procedure Call) 框架**：它的核心目标是让**服务之间的调用**像调用本地方法一样简单、高效。它高度优化了服务治理（如负载均衡、熔断、降级、链路追踪）、序列化性能（如 Hessian2, Kryo）和通信协议（默认是 Dubbo 自定义的 TCP 协议）。它关注的是**服务**和**方法**。
*   **对外 API 通常是 RESTful HTTP API**：它们面向的是外部客户端（浏览器、移动App、第三方开发者）。它们更关注的是**资源**和**状态**，使用标准的 HTTP 方法（GET, POST, PUT, DELETE）、状态码（200, 404, 500）和通用协议（HTTP/HTTPS）。这使得它们对客户端环境没有强依赖，兼容性极好。

### 为什么一般不直接使用 Dubbo 对外暴露 API？

1.  **协议兼容性问题**：
    *   Dubbo 默认使用自定义的 **TCP 二进制协议**，而不是 HTTP。这意味着普通的浏览器、curl命令或Postman无法直接理解和调用这个接口。它需要特定的 Dubbo 客户端库。
    *   虽然 Dubbo 3 开始支持 **gRPC 协议**和 **Triple 协议（基于 HTTP/2）**，使其更标准化，但对外提供 API 仍然需要额外的网关来转换。

2.  **服务治理边界**：
    *   微服务内部调用是**高信任度环境**。服务之间可以共享更复杂的DTO（数据传输对象）、依赖更多的治理功能。
    *   对外 API 是**低信任度环境**。你需要进行严格的**身份认证（Authentication）、授权（Authorization）、限流（Rate Limiting）、防篡改**等安全措施。将这些安全逻辑侵入到业务服务中会使其变得臃肿且难以维护。

3.  **耦合性与演化**：
    *   内部服务的接口可以为了性能而频繁变化（例如，改变一个序列化字段名）。
    *   对外 API 需要保持**高度的稳定性和向后兼容性**，因为你不控制外部客户端。内部服务的频繁变动不应直接影响外部契约。

### 如何将 Dubbo 服务对外暴露为 API？（正确做法）

正确的架构是在你的 Dubbo 微服务集群之前，增加一个 **API 网关（API Gateway）**。这是业界标准的做法。



在这个架构中：

1.  **内部通信**：微服务之间使用原生的、高性能的 Dubbo RPC 协议进行调用，享受其所有的治理能力。
2.  **对外暴露**：所有来自互联网的请求首先到达 **API 网关**。
3.  **网关的角色**：
    *   **协议转换**：网关接收标准的 HTTP/HTTPS/RESTful/JSON 请求，然后由它内部通过 Dubbo 客户端**调用后端的 Dubbo 服务**。网关充当了一个“翻译官”的角色。
    *   **统一安全管控**：在网关层统一实现鉴权（如 JWT 验证）、限流、日志记录、参数校验等跨领域关注点（Cross-Cutting Concerns）。
    *   **路由与聚合**：将外部请求路由到正确的内部 Dubbo 服务，甚至可以将多个 Dubbo 服务的调用结果聚合成一个响应返回给客户端。

**常见的用于暴露 Dubbo 服务的网关技术选型：**

*   **Apache ShenYu (Incubating)**：一个高性能的Java网关，**对 Dubbo 提供了原生的大力支持**，是其核心插件之一。这是非常流行和推荐的选择。
*   **Spring Cloud Gateway**：Spring 生态的网关，可以通过编写自定义的过滤器或集成相关组件来调用 Dubbo 服务。
*   **Kong / Apache APISIX**：基于 Nginx/OpenResty 的高性能网关，可以通过编写插件（如使用 Lua 脚本）来集成 Dubbo 客户端，实现协议转换。
*   **Nginx + 自定义模块**：也可以实现，但开发成本较高。
*   **Dubbo 官方提供的 Mesh方案**：与 Istio 等 Service Mesh 方案集成，由 Sidecar 来代理流量，但这种方式更复杂。

### 总结

| 特性         | 内部 Dubbo RPC 调用        | 对外暴露 API (通过网关)                     |
| :----------- | :------------------------- | :------------------------------------------ |
| **协议**     | 自定义 TCP / Triple / gRPC | **HTTP / HTTPS / RESTful**                  |
| **客户端**   | Dubbo Client               | **任何 HTTP 客户端** (浏览器, App, Postman) |
| **主要目标** | **性能、治理、效率**       | **通用、安全、稳定**                        |
| **安全**     | 相对信任，较弱鉴权         | **不信任环境，强鉴权/授权/限流**            |
| **最佳实践** | 直接调用                   | **通过 API 网关进行协议转换**               |

**结论：** 你可以通过 **API 网关** 将 Dubbo 服务安全、高效、标准化地暴露给互联网。**直接让 Dubbo 服务监听公网**在技术上是可行的（例如配置 Triple 协议并监听公网IP），但在架构上是不推荐的，因为它混清了内外部的边界，带来了安全性和耦合性的巨大挑战。网关模式是更专业、更标准的选择。



## `Dubbo vs OpenFeign`

Dubbo 和 OpenFeign 都是用于实现**服务间远程调用（RPC）**的框架，但它们在设计理念、适用场景和技术实现上有显著区别。以下是它们的核心对比：

---

### 1. **定位与背景**
| **对比项**   | **Dubbo**                                  | **OpenFeign**                          |
| ------------ | ------------------------------------------ | -------------------------------------- |
| **定位**     | **高性能分布式服务框架**（RPC + 服务治理） | **声明式 HTTP 客户端**（基于 RESTful） |
| **背景**     | 阿里巴巴开源，专注微服务通信和治理         | Netflix 开源，Spring Cloud 生态组件    |
| **核心目标** | 解决大规模分布式系统的服务调用和治理问题   | 简化 HTTP API 的远程调用（轻量级）     |

---

### 2. **通信协议与传输**
| **对比项**   | **Dubbo**                                                    | **OpenFeign**                                |
| ------------ | ------------------------------------------------------------ | -------------------------------------------- |
| **协议**     | 默认使用 **Dubbo 协议**（TCP + 二进制序列化），高性能；支持 HTTP、gRPC 等 | 仅支持 **HTTP**（通常是 RESTful + JSON/XML） |
| **序列化**   | 支持多种高性能序列化（Hessian2、Kryo、Protobuf）             | 通常用 JSON（如 Jackson），性能较弱          |
| **网络模型** | 基于 **Netty** 的 NIO，长连接，低延迟                        | 基于 HTTP 短连接（可配合 Ribbon 优化）       |

---

### 3. **服务治理能力**
| **对比项**         | **Dubbo**                                     | **OpenFeign**                                      |
| ------------------ | --------------------------------------------- | -------------------------------------------------- |
| **服务注册发现**   | 内置支持（需搭配 Zookeeper/Nacos 等注册中心） | 依赖 **Spring Cloud Netflix**（如 Eureka）或 Nacos |
| **负载均衡**       | 内置多种策略（随机、轮询、一致性哈希等）      | 依赖 **Ribbon**（客户端负载均衡）                  |
| **容错机制**       | 支持集群容错（Failover/Failfast 等）          | 需配合 **Hystrix** 或 **Sentinel**                 |
| **监控与链路追踪** | 提供完善的服务监控和调用链追踪                | 需额外集成 Sleuth + Zipkin                         |

---

### 4. **使用方式与开发体验**
| **对比项**     | **Dubbo**                                                  | **OpenFeign**                                        |
| -------------- | ---------------------------------------------------------- | ---------------------------------------------------- |
| **API 风格**   | 需定义服务接口（Java Interface），通过 **@Reference** 注入 | 声明式接口（**@FeignClient**），类似 Spring MVC 注解 |
| **代码侵入性** | 较高（需依赖 Dubbo 的 API）                                | 极低（纯注解驱动，与 Spring 无缝集成）               |
| **配置复杂度** | 较高（需配置注册中心、协议、序列化等）                     | 极简（默认 HTTP + Spring Boot 自动配置）             |

---

### 5. **适用场景**
| **场景**       | **Dubbo**                                                    | **OpenFeign**                                               |
| -------------- | ------------------------------------------------------------ | ----------------------------------------------------------- |
| **微服务类型** | 适合**高性能、高并发**的内部服务调用（如订单、支付等核心服务） | 适合**轻量级 RESTful API 调用**（如前后端分离、多语言系统） |
| **技术栈**     | 适合 **Java 技术栈** 的深度微服务治理                        | 适合 **Spring Cloud 生态**，尤其是多云环境或混合技术栈      |
| **性能要求**   | 低延迟、高吞吐（如 10W+ QPS）                                | 中等性能（HTTP 协议开销较大）                               |

---

### 6. **代码示例对比
#### **Dubbo 示例**
```java
// 1. 服务提供者接口
public interface UserService {
    User getUserById(Long id);
}

// 2. 服务提供者实现（Dubbo @Service 注解）
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Long id) { ... }
}

// 3. 服务消费者调用（Dubbo @Reference 注解）
@RestController
public class UserController {
    @Reference
    private UserService userService;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
```

#### **OpenFeign 示例**
```java
// 1. 声明 Feign 客户端接口
@FeignClient(name = "user-service", url = "http://user-service:8080")
public interface UserService {
    @GetMapping("/user/{id}")
    User getUserById(@PathVariable Long id);
}

// 2. 直接注入并调用
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
```

---

### 总结：如何选择？
| **选择 Dubbo** 当：                           | **选择 OpenFeign** 当：                                   |
| --------------------------------------------- | --------------------------------------------------------- |
| 1. 需要**高性能 RPC**（如内部核心服务通信）   | 1. 需要快速集成 **RESTful API**（如对接前端或第三方系统） |
| 2. 需要**完整的服务治理**（负载均衡、熔断等） | 2. 项目基于 **Spring Cloud** 生态                         |
| 3. 系统为**纯 Java 技术栈**且规模庞大         | 3. 追求**极简配置**和声明式开发体验                       |

**补充建议**：  
- 在 Spring Cloud 项目中，可以结合两者：用 **OpenFeign 对外暴露 HTTP API**，用 **Dubbo 实现内部高性能服务调用**。  
- 如果团队熟悉 Spring 生态且无需极端性能，OpenFeign 是更轻量的选择。



## 与`SpringBoot`集成

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-dubbo/demo-spring-boot-dubbo)

运行基础服务 `zookeeper（注册中心）`、`dubbo-admin`

```yaml
version: "3.0"

services:
  demo-zookeeper:
    # image: zookeeper:3.4.9
    # image: zookeeper:3.8.4
    image: zookeeper:3.5.6
    environment:
      - TZ=Asia/Shanghai
      - JVMFLAGS=-Xmx512m -Xms512m -server
      # 禁用 zookeeper AdminServer
      # https://hub.docker.com/_/zookeeper
      - ZOO_ADMINSERVER_ENABLED=false
    network_mode: host
  demo-dubbo-admin:
    image: apache/dubbo-admin:0.6.0
    environment:
      - admin.registry.address=zookeeper://127.0.0.1:2181
      - admin.config-center=zookeeper://127.0.0.1:2181
      - admin.metadata-report.address=zookeeper://127.0.0.1:2181
      - server.port=8081
      - JAVA_OPTS=-Xmx512m
    depends_on:
      - demo-zookeeper
    network_mode: host

```

`SpringBoot 2.7.18+Dubbo 2.7.5 POM` 配置：

```xml
<!-- SpringBoot Dubbo 集成依赖 -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>2.7.5</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>4.2.0</version>
</dependency>
```

开发 `provider`：

>使用普通方式开发 `interface` 及其实现类。

- `application.properties`

  ```properties
  # 配置 dubbo 应用名称
  dubbo.application.name=${spring.application.name}
  # 配置 dubbo 配置中心类型为 zookeeper
  dubbo.registry.protocol=zookeeper
  # 配置 dubbo 配置中心地址
  dubbo.registry.address=${zookeeper_address:127.0.0.1:2181}
  # 配置 dubbo rpc 调用使用的协议
  dubbo.protocol.name=dubbo
  # 配置 dubbo rpc 调用的端口
  dubbo.protocol.port=20880
  ```

- `TestDubboPerfAssistService.java`

  ```java
  public interface TestDubboPerfAssistService {
  
      /**
       * 用于协助 Dubbo 性能测试
       *
       * @return
       */
      String getRandomUuid();
  
  }
  
  ```

- `TestDubboPerfAssistServiceImpl.java`

  ```java
  // 需要使用 Dubbo 的 Service 注解暴露服务
  @org.apache.dubbo.config.annotation.Service
  public class TestDubboPerfAssistServiceImpl implements TestDubboPerfAssistService {
      private int totalKey = 100000;
      private List<String> keyList = new ArrayList<>();
  
      @PostConstruct
      public void init() {
          // 生成10w个随机key放到内中
          for (int i = 0; i < totalKey; i++) {
              String key = UUID.randomUUID().toString();
              keyList.add(key);
          }
      }
  
      @Override
      public String getRandomUuid() {
          int randomInt = RandomUtil.randomInt(0, totalKey);
          String uuid = this.keyList.get(randomInt);
          return "UUID:" + uuid;
      }
  }
  ```

- 启用 `Dubbo` 服务

  ```java
  @SpringBootApplication
  @EnableFutureExceptionHandler
  // 启用 Dubbo 服务
  @EnableDubbo
  public class ApplicationProvider {
      public static void main(String[] args) {
          SpringApplication.run(ApplicationProvider.class, args);
      }
  }
  ```

- 启动 `Provider` 后访问 http://localhost:8081/ （帐号：`root`，密码：`root`）查看 `Dubbo` 服务注册情况。

开发 `Consumer`：

>通过 `RestController` 注入 `Service` 调用远程的 `Provider` 服务。

- `application.properties`

  ```properties
  spring.application.name=demo-spring-boot-dubbo-consumer
  
  dubbo.application.name=${spring.application.name}
  dubbo.registry.protocol=zookeeper
  dubbo.registry.address=${zookeeper_address:127.0.0.1:2181}
  ```

- `ApiController.java`

  ```java
  @Slf4j
  @RestController
  @RequestMapping("/api/v1/external/product")
  public class ApiController {
      // 通过注解 @Reference 注入远程服务
      @Reference
      TestDubboPerfAssistService testDubboPerfAssistService;
  
      /**
       * 用于协助 Dubbo 性能测试
       *
       * @return
       */
      @GetMapping("testPerfAssist")
      public ObjectResponse<String> testPerfAssist() {
          return ResponseUtils.successObject(this.testDubboPerfAssistService.getRandomUuid());
      }
  }
  ```

- 启用 `Dubbo` 服务

  ```java
  @SpringBootApplication
  @EnableFutureExceptionHandler
  // 启用 Dubbo 服务
  @EnableDubbo
  public class ApplicationConsumer {
      public static void main(String[] args) {
          SpringApplication.run(ApplicationConsumer.class, args);
      }
  }
  ```

- 启动 `Consumer` 后访问 http://localhost:8081/ （帐号：`root`，密码：`root`）查看 `Dubbo` 服务注册情况。

测试服务是否正常

```sh
curl http://localhost:8080/api/v1/external/product/testPerfAssist
```



## 性能测试

使用本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-dubbo/demo-spring-boot-dubbo) 协助测试

编译镜像

```sh
./build.sh && ./push.sh
```

复制部署配置

```sh
ansible-playbook playbook-deployer-config.yml --inventory inventory.ini
```

运行测试目标

```sh
ansible-playbook playbook-service-start.yml --inventory inventory.ini
```

测试目标是否正常运行

```sh
curl http://192.168.235.50/api/v1/external/product/testPerfAssist
```

测试

```
$ wrk -t8 -c2048 -d30s --latency --timeout 60 http://192.168.235.50/api/v1/external/product/testPerfAssist
```

查看 `Prometheus` 监控：http://192.168.235.53:3000/

销毁测试目标

```sh
ansible-playbook playbook-service-destroy.yml --inventory inventory.ini
```

