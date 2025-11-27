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



## `SpringCloud`、`SpringCloud Alibaba` 和 `SpringBoot` 兼容性

SpringCloud Alibaba、SpringCloud 和 SpringBoot 兼容性列表`https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E`

| Spring Cloud Alibaba Version | Spring Cloud Version  | Spring Boot Version |
| ---------------------------- | --------------------- | ------------------- |
| 2022.0.0.0*                  | Spring Cloud 2022.0.0 | 3.0.2               |
| 2022.0.0.0-RC2               | Spring Cloud 2022.0.0 | 3.0.2               |
| 2022.0.0.0-RC1               | Spring Cloud 2022.0.0 | 3.0.0               |

| Spring Cloud Alibaba Version | Spring Cloud Version  | Spring Boot Version |
| ---------------------------- | --------------------- | ------------------- |
| 2021.0.5.0*                  | Spring Cloud 2021.0.5 | 2.6.13              |
| 2021.0.4.0                   | Spring Cloud 2021.0.4 | 2.6.11              |
| 2021.0.1.0                   | Spring Cloud 2021.0.1 | 2.6.3               |
| 2021.1                       | Spring Cloud 2020.0.1 | 2.4.2               |

| Spring Cloud Alibaba Version      | Spring Cloud Version        | Spring Boot Version |
| --------------------------------- | --------------------------- | ------------------- |
| 2.2.10-RC1*                       | Spring Cloud Hoxton.SR12    | 2.3.12.RELEASE      |
| 2.2.9.RELEASE                     | Spring Cloud Hoxton.SR12    | 2.3.12.RELEASE      |
| 2.2.8.RELEASE                     | Spring Cloud Hoxton.SR12    | 2.3.12.RELEASE      |
| 2.2.7.RELEASE                     | Spring Cloud Hoxton.SR12    | 2.3.12.RELEASE      |
| 2.2.6.RELEASE                     | Spring Cloud Hoxton.SR9     | 2.3.2.RELEASE       |
| 2.2.1.RELEASE                     | Spring Cloud Hoxton.SR3     | 2.2.5.RELEASE       |
| 2.2.0.RELEASE                     | Spring Cloud Hoxton.RELEASE | 2.2.X.RELEASE       |
| 2.1.4.RELEASE                     | Spring Cloud Greenwich.SR6  | 2.1.13.RELEASE      |
| 2.1.2.RELEASE                     | Spring Cloud Greenwich      | 2.1.X.RELEASE       |
| 2.0.4.RELEASE(停止维护，建议升级) | Spring Cloud Finchley       | 2.0.X.RELEASE       |
| 1.5.1.RELEASE(停止维护，建议升级) | Spring Cloud Edgware        | 1.5.X.RELEASE       |



## `SpringCloud` 和 `SpringBoot` 兼容性

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

## SpringCloud和SpringBoot集成 - SpringCloud-Hoxton.SR10+SpringBoot-2.2.7.RELEASE

>说明：
>
>- 这个版本组合支持Eureka+Zuul+OpenFeign，详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/spring-cloud/spring-cloud-eureka
>- 这个版本组合支持nacos-server:v2.2.3+OpenFeign，详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/spring-cloud/demo-spring-cloud-alibaba-seata-at、https://gitee.com/dexterleslie/demonstration/tree/main/spring-cloud/spring-cloud-nacos

父POM配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.7.RELEASE</version>
	</parent>

	<groupId>com.future.demo</groupId>
	<artifactId>demo-springcloud-parent</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>pom</packaging>

	...

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>Hoxton.SR10</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
</project>

```

zuul模块POM配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>demo-springcloud-zuul</artifactId>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.future.demo</groupId>
        <artifactId>demo-springcloud-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.future.demo</groupId>
            <artifactId>demo-springcloud-common</artifactId>
            <version>${project.version}</version>
        </dependency>
        <!-- 向eureka注册自己需要引入下面的spring-cloud-starter-netflix-eureka-client依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
        <!-- FeignClient需要引用 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

helloworld微服务模块POM配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>demo-springcloud-helloworld</artifactId>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.future.demo</groupId>
        <artifactId>demo-springcloud-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!-- 向eureka注册自己需要引入下面的spring-cloud-starter-netflix-eureka-client依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

eureka模块POM配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>demo-springcloud-eureka-server</artifactId>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.future.demo</groupId>
        <artifactId>demo-springcloud-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## SpringCloud和SpringBoot集成 - SpringCloud-2021.0.3+SpringBoot-2.7.18

>说明：
>
>- 这个版本组合支持Eureka+Gateway+OpenFeign（不再支持Zuul），详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/spring-cloud/spring-cloud-gateway

父POM配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.future.demo</groupId>
    <artifactId>gateway-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>service-common</module>
        <module>service-eureka</module>
        <module>service-helloworld</module>
        <module>service-gateway</module>
        <module>service-test</module>
    </modules>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-parent</artifactId>
                <version>2.7.18</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>2021.0.3</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>

```

eureka服务器POM配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>service-eureka</artifactId>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.future.demo</groupId>
        <artifactId>gateway-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

helloword微服务（Eureka客户端）POM配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>service-helloworld</artifactId>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.future.demo</groupId>
        <artifactId>gateway-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

gateway网关POM配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>service-gateway</artifactId>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.future.demo</groupId>
        <artifactId>gateway-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

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



### `CAP`定理

>`https://cloud.tencent.com/developer/article/2429547`

**CAP**定理由如下三部分组成。

- **C**（**Consistency**），一致性。每次对数据的读取都是最近一次写入的内容；
- **A**（**Availability**），可用性。每次请求读取数据都能成功读取到数据，但读取到的数据不保证总是最近一次写入的内容；
- **P**（**Partition tolerance**），分区容错性。网络节点之间可能发生网络故障从而导致消息丢失，但这不会影响系统的运行。

**CAP**里面的**C**和**A**都比较好理解，**P**好像有点抽象，其实这么理解就对了，**P**的意思就是允许存在网络故障。

对于一个[分布式数据存储](https://cloud.tencent.com/product/tdcpg?from_column=20065&from=20065)系统来说，**`如果没有网络故障`**，那么**CAP**的 **`三个特性都是可以满足`** 的。

但分布式系统的 **`网络故障一定是不可避免的`**，所以**P**是一定要满足的，并且此时**C**和**A**只能满足一个，因此就出现了**CP**模型和**AP**模型。

假设我们现在有如下这么一个分布式[数据存储](https://cloud.tencent.com/product/cdcs?from_column=20065&from=20065)系统。

![Nacos-Cap模型示意图.jpg](https://developer.qcloudimg.com/http-save/9379187/7da6cb0568a28284b7c1277420d26db0.webp)

当[数据同步](https://cloud.tencent.com/product/datainlong?from_column=20065&from=20065)因为网络问题而无法实现时，下面来分别看下**CP**模型和**AP**模型的行为是什么。

在**CP**模型下，因为要满足**C**的一致性，所以一旦网络出现问题导致数据同步失败，此时数据的读取就会被拒绝从而导致读取超时或失败，这种情况下，系统变得不可用，即**A**不满足。

在**AP**模型下，因为要满足**A**的可用性，所以就算网络出现问题导致数据同步失败，此时数据的读取还是能够成功读取到数据，但这种情况下节点间的数据是不同的，即**C**不满足。

总结

**CAP**的**C**表示**一致性**，要求节点间的数据要完成数据同步。

**CAP**的**A**表示**可用性**，要求系统对外随时都能提供数据读取的服务。

**CAP**的**P**表示**分区容错性**，要求系统能够在存在网络故障的情况下运行。

因为分布式系统中网络故障是一定会存在的，所以**P**是一定要满足的，并且**C**和**A**是一定无法同时满足的，此时就形成了**CP**模型和**AP**模型。

**CP**模型在网络发生故障时会拒绝对外提供数据读取服务，此时**A**也就是可用性无法满足。

**AP**模型在网络发生故障时还是会对外提供服务，但节点间的数据会不一致，此时**C**也就是一致性无法满足。

在**Nacos**中，对于**临时实例**的服务实例信息获取，**Nacos**采用**AP**模型，具体的实现采用**Distro**协议，而对于**持久化实例**，**Nacos**采用**CP**模型，具体的实现采用**SOFAJRaft**协议。



### `Eureka`、`Zookeeper`、`Consul`、`Nacos`分别是`AP`还是`CP`呢？

Eureka、Zookeeper、Consul、Nacos都是分布式系统中的服务注册与发现组件，它们在CAP（一致性Consistency、可用性Availability、分区容错性Partition Tolerance）理论中的表现各不相同。以下是这四个组件的CAP特性概述：

Eureka

- **一致性（C）**：Eureka更注重可用性和容错性，因此在一致性方面可能有所妥协。在部分节点失效的情况下，Eureka仍能保证服务注册与发现机制的可用性，但数据的新鲜度可能会受到影响。
- **可用性（A）**：Eureka具有高可用性，确保服务的连续性。即使部分节点失效，只要还有Eureka存活，服务注册与发现机制就能继续工作。
- **分区容错性（P）**：Eureka具备分区容错性，能够在网络分区或节点故障时保持系统的可用性。

Zookeeper

- **一致性（C）**：Zookeeper侧重于一致性和容错性。它使用ZAB（Zookeeper Atomic Broadcast）协议确保数据一致性，即使在选举新Leader时会有短暂的不可用性，但一旦完成，数据一致性得到保障。
- **可用性（A）**：虽然Zookeeper在一致性方面表现出色，但在可用性方面可能略逊于Eureka。在选举新Leader期间，系统可能会暂时不可用。
- **分区容错性（P）**：Zookeeper具备分区容错性，能够在网络分区或节点故障时保持系统的运行。

Consul

- **一致性（C）**：Consul在CAP理论中偏向CP，但也会适时调整以维持可用性。它提供了强大的服务注册与发现能力，以及细致入微的健康检查功能。
- **可用性（A）**：虽然Consul在一致性方面有所保障，但在可用性方面可能受到一定影响，尤其是在进行领导节点选举或数据同步时。
- **分区容错性（P）**：Consul具备分区容错性，能够在网络分区或节点故障时保持系统的可用性。

Nacos

- **一致性（C）与可用性（A）**：Nacos灵活地在CP和AP间切换。对于持久化实例，Nacos使用CP架构；对于临时实例，则使用AP架构。这种灵活性使得Nacos能够在不同场景下提供最佳的性能和可用性。
- **分区容错性（P）**：Nacos具备分区容错性，能够在网络分区或节点故障时保持系统的可用性。

总结来说，Eureka、Zookeeper、Consul和Nacos在CAP理论中的表现各有侧重。Eureka更注重可用性和容错性；Zookeeper侧重于一致性和容错性；Consul在一致性和可用性之间取得平衡；而Nacos则灵活地在CP和AP间切换，以适应不同的应用场景。在选择这些组件时，应根据项目的具体需求、系统性能、团队技术水平和未来规划进行综合考虑。



### `Eureka`

> 提示：停止更新，将会抛弃。



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



### `Zookeeper`

> 提示：用得少，所以不做实验。



### `Consul`

详细用法请参考[示例](https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-consul-parent)

启动`Consul`服务

```bash
docker-compose up -d
```

访问 http://localhost:8500/，检查`Consul`服务器是否正常

启动所有应用

访问 http://localhost:8081/api/v1/a/sayHello?name=Dexter 以测试应用服务是否正常



### `Nacos`

> 提示：阿里巴巴主流注册中心。
>
> 详细用法请参考文档 <a href="/spring-cloud/#nacos-3" target="_blank">链接</a>



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

详细用法请参考文档 <a href="/spring-cloud/#nacos-3" target="_blank">链接</a>



## SpringCloud Alibaba

>`https://spring-cloud-alibaba-group.github.io/github-pages/hoxton/zh-cn/index.html`



###  `Nacos`

> 详细用法请参考示例：https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-nacos

#### `Docker` 运行 `Nacos`

>提示：Nacos 默认使用的是其内置的 Derby 数据库作为持久化存储。
>
>参考链接：https://blog.csdn.net/qq_27615455/article/details/125168548
>
>Docker 运行 nacos2.x 需要暴露 9848 和 9849 端口`https://github.com/alibaba/nacos/issues/6154`

docker-compose.yaml 内容如下：

```yaml
version: "3.0"

services:
  # https://blog.csdn.net/qq_27615455/article/details/125168548

  # Docker 运行 nacos2.x 需要暴露 9848 和 9849 端口
  # https://github.com/alibaba/nacos/issues/6154
  nacos-server:
    image: nacos/nacos-server:v2.2.0
    environment:
      - TZ=Asia/Shanghai
      - MODE=standalone
      # 参考官方说明 https://hub.docker.com/r/nacos/nacos-server
      # 指定 jvm 内存为 256m
      - JVM_XMS=256m
      - JVM_XMX=256m
      - JVM_XMN=128m
      #- PREFER_HOST_MODE=hostname
    # 持久化配置
    # volumes:
    #  - data-demo-spring-boot-nacos:/home/nacos
    ports:
      - '8848:8848'
      - '9848:9848'
      #- '9849:9849'

# 持久化配置
# volumes:
#  data-demo-spring-boot-nacos:

```

启动 Nacos

```bash
docker compose up -d
```

访问 http://localhost:8848/nacos 登录 Nacos 控制台，帐号：nacos，密码：nacos。



#### 运行示例

运行 Nacos

```bash
docker compose up -d
```

手动在 Nacos 配置中心中创建配置文件Data ID为：demo-springcloud-helloworld-dev.properties（命名空间为默认public，Group为默认DEFAULT_GROUP），内容为 my.config=v1

手动在 Nacos 配置中心创建 ns_dev 命名空间，在 ns_dev 命名空间下创建文件Data ID为：demo-springcloud-zuul-dev.properties，内容为 my.config=ns_dev,DEV_GROUP,demo-springcloud-zuul-dev.properties，Group 为 DEV_GROUP

启动 ApplicationZuul、ApplicationHelloworld 应用

测试服务注册和发现

- 访问`http://localhost:8080/api/v1/zuul/test1?param1=dexter`

测试服务配置

- 访问`http://localhost:8080/api/v1/zuul/test1?param1=dexter`查看当前 myConfig 返回值为 v1
- 访问 Nacos 控制台手动修改 demo-springcloud-helloworld-dev.properties 内容为 my.config=v2
- 再次访问`http://localhost:8080/api/v1/zuul/test1?param1=dexter`查看当前 myConfig 返回值为 v2

测试服务配置 Group、Namespace

- 访问`http://localhost:8080/api/v1/zuul/test2`测试



#### 服务注册和发现配置

父 pom 配置如下：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.2.7.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Hoxton.SR10</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!-- nacos依赖 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2.2.9.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

各个微服务项目的 pom 配置如下：

```xml
<!-- nacos注册中心依赖配置 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

application.properties 配置服务注册与发现

```properties
# nacos注册中心配置
spring.cloud.nacos.discovery.server-addr=localhost:8848
```

访问 http://localhost:8848/nacos Nacos 控制台查看已注册服务

定义 HelloworldClient Feign 客户端

```java
@FeignClient(value = "demo-springcloud-helloworld")
public interface HelloworldClient {
    @GetMapping(value = "/api/v1/test1")
    ResponseEntity<String> test1(@RequestParam(value = "param1") String param1);
}
```

调用 Feign 客户端

```java
@RestController
@RequestMapping("/api/v1/zuul")
@RefreshScope
public class ApiController {
    @Autowired
    HelloworldClient helloworldClient;

    @GetMapping(value = "test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return helloworldClient.test1(param1);
    }
}
```



#### 服务配置和管理

##### 基本配置

父 pom 配置如下：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.2.7.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Hoxton.SR10</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!-- nacos依赖 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2.2.9.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

各个微服务项目的 pom 配置如下：

```xml
<!-- nacos服务配置依赖配置 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

bootstrap.properties 配置如下：

```properties
# nacos服务配置设置
# 手动在nacos创建配置文件demo-springcloud-helloworld-dev.properties，内容为：my.config=v1
# 默认是public命名空间，Group是DEFAULT_GROUP
spring.cloud.nacos.config.server-addr=localhost:8848
spring.cloud.nacos.config.file-extension=properties
spring.profiles.active=dev
```

Java 中引用 properties 配置

```java
@RestController
// nacos配置修改后会自动刷新
@RefreshScope
public class ApiController {
    @Value("${server.port}")
    private int serverPort;
    @Value("${my.config}")
    private String myConfig;

    @GetMapping(value = "/api/v1/test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return ResponseEntity.ok("你的请求参数param1=" + param1 + "，myConfig配置值:" + myConfig + "，端口: " + serverPort);
    }
}
```



##### 自动刷新配置

在引用配置的类中添加 @RefreshScope 注解

```java
@RestController
// nacos配置修改后会自动刷新
@RefreshScope
public class ApiController {
    @Value("${server.port}")
    private int serverPort;
    @Value("${my.config}")
    private String myConfig;

    @GetMapping(value = "/api/v1/test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return ResponseEntity.ok("你的请求参数param1=" + param1 + "，myConfig配置值:" + myConfig + "，端口: " + serverPort);
    }
}
```



##### Nacos DataId 和配置文件的命名规则

默认情况：${spring.application.name}-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}

指定配置：${spring.cloud.nacos.config.prefix}-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}



##### 根据 Namespace、Group、DataId 加载配置

在Nacos配置管理系统中，Namespace、Group和DataId是三个至关重要的概念。以下是对这三个概念的详细解释：

**Namespace**

Namespace在Nacos中主要用于进行配置隔离。不同的命名空间下，可以存在相同的Group或DataId的配置。Namespace的常用场景之一是不同环境的配置的区分隔离，例如开发测试环境和生产环境的资源（如配置、服务）隔离等。通过Namespace，我们可以轻松实现不同开发环境的配置隔离，确保各个环境的配置互不干扰。

**Group**

Group在Nacos中主要用于区分不同的微服务或应用组件。当不同的应用或组件使用了相同的配置类型时，我们可以利用Group来区分它们。例如，一个应用可能使用了database_url配置和MQ_topic配置，我们可以将这些配置分别划分到不同的Group中，以便更好地管理和维护。默认情况下，所有的配置集都属于DEFAULT_GROUP，但用户可以根据需要创建自定义的分组。

Group在Nacos中的作用主要包括：

- **配置隔离**：通过分组，可以将不同项目或不同环境的配置进行隔离，避免配置之间的冲突。
- **逻辑区分**：对于同名但属于不同项目或不同环境的配置，可以通过分组进行区分，提高配置的可读性和可维护性。
- **权限管理**：在一些场景下，可以通过分组进行权限控制，限制不同用户对配置集的访问和操作。

**DataId**

DataId是Nacos中用于唯一标识配置信息的标识符。每个DataId对应一个具体的配置信息，例如一个数据库连接信息或消息队列的配置。通过DataId，我们可以轻松地查找、获取和更新配置信息。

DataId在Nacos中的主要作用包括：

- **唯一标识配置文件**：在Nacos中，每个配置文件通过DataId进行唯一标识。
- **区分不同业务模块的配置**：通过自定义DataId格式，可以将配置文件与具体业务模块关联，从而更方便地管理和查找配置。

在实际应用中，DataId的设计通常遵循以下原则：

- **描述性强**：DataId应能直观地描述配置的用途、所属模块和环境。
- **可扩展性好**：避免在DataId中使用绝对路径或硬编码的命名方式，确保未来能够根据业务变化灵活扩展。
- **避免歧义**：DataId应尽量避免使用相似或容易混淆的命名方式。

**三者之间的关系**

在Nacos中，Namespace、Group和DataId三者之间可以看作是一个层次结构。最外层的Namespace用于区分不同的开发环境或部署环境，它提供了配置隔离的功能。Group位于Namespace之下，用于区分不同的微服务或应用组件。而DataId则位于最内层，用于唯一标识具体的配置信息。

通过合理地设置Namespace、Group和DataId，我们可以实现配置信息的有效管理和维护。例如，我们可以为每个环境创建一个独立的Namespace，然后在每个Namespace下为每个微服务或应用组件创建一个Group，最后在Group下为每个配置信息创建一个唯一的DataId。这样，我们就可以轻松地管理和维护各个环境的配置信息，确保系统的正常运行。

总的来说，Namespace、Group和DataId是Nacos配置管理系统的核心要素。理解这三者的关系并正确应用它们，是实现高效配置管理的关键。



bootstrap.properties 配置如下：

```properties
# 演示DataId、Group、Namespace用法
# NOTE: 以下配置只能在bootstrap.properties中配置，否则不生效
spring.cloud.nacos.config.server-addr=localhost:8848
# 加载 properties 配置文件
spring.cloud.nacos.config.file-extension=properties
# 配置文件所在的命令空间为 ns_dev
spring.cloud.nacos.config.namespace=ns_dev
# 配置文件所在的 Group 为 DEV_GROUP
spring.cloud.nacos.config.group=DEV_GROUP
spring.profiles.active=dev
```

手动在 Nacos 配置中心创建 ns_dev 命名空间，在 ns_dev 命名空间下创建 demo-springcloud-zuul-dev.properties 文件，内容为 my.config=ns_dev,DEV_GROUP,demo-springcloud-zuul-dev.properties，Group 为 DEV_GROUP



#### 使用 `Java` 客户端操作 `Nacos`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-boot-nacos)

提示：`Nacos` 服务器关闭后，`Java` 客户端调用 `getConfig` 方法依旧能够返回数据，这是因为 `Java` 客户端的本地缓存机制。使用 `LocalConfigInfoProcessor.cleanAllSnapshot();` 清除本地缓存（路径默认 `~/.nacos/config`）。

`POM` 配置：

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>2.2.0</version> <!-- 替换为实际服务端版本（如 2.2.3） -->
</dependency>
```

测试用例：

```java
@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() throws NacosException {
        String serverAddr = "localhost:8848";
        ConfigService configService = NacosFactory.createConfigService(serverAddr);

        // dataId: 配置的唯一标识（如 "sentinel-flow-rules"）
        // group: 配置的分组（如 "DEFAULT_GROUP"）
        String dataId = "sentinel-flow-rules";
        String group = "DEFAULT_GROUP";
        boolean removed = configService.removeConfig(dataId, group);
        Assertions.assertTrue(removed);

        // 参数说明：
        // timeoutMs: 超时时间（毫秒，默认 3000ms）
        String content = configService.getConfig(dataId, group, 3000);
        Assertions.assertNull(content);

//        // 定义监听器（配置变更时触发）
//        Listener listener = new Listener() {
//            @Override
//            public Executor getExecutor() {
//                return null; // 使用默认线程池（或自定义线程池）
//            }
//
//            @Override
//            public void receiveConfigInfo(String configInfo) {
//                System.out.println("配置变更，新内容：" + configInfo);
//            }
//        };
//        // 添加监听器（需指定 dataId 和 group）
//        configService.addListener(dataId, group, listener);

        // 参数说明：
        // dataId: 配置的唯一标识
        // group: 配置的分组
        // content: 配置内容（如 JSON 格式的 Sentinel 规则）
        content = "[\n" +
                "  {\n" +
                "    \"resource\": \"myTest1\",\n" +
                "    \"grade\": 1,\n" +
                "    \"count\": 5,\n" +
                "    \"strategy\": 0,\n" +
                "    \"controlBehavior\": 0,\n" +
                "    \"limitApp\": \"default\"\n" +
                "  }\n" +
                "]";
        boolean success = configService.publishConfig(dataId, group, content, ConfigType.JSON.getType());
        Assertions.assertTrue(success);

        String contentActual = configService.getConfig(dataId, group, 3000);
        Assertions.assertEquals(content, contentActual);
    }

}
```

#### 配置MySQL作为持久化存储

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-nacos

nacos数据库初始化脚本：

```sql
CREATE DATABASE IF NOT EXISTS demo CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE demo;
/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80042
 Source Host           : 127.0.0.1:3306
 Source Schema         : hcp_config

 Target Server Type    : MySQL
 Target Server Version : 80042
 File Encoding         : 65001

 Date: 25/05/2025 21:44:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `gmt_modified` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `c_use` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `effect` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `type` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `c_schema` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL,
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfo_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 109 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'config_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info
-- ----------------------------
INSERT INTO `config_info` VALUES (1, 'application-dev.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n# mail配置\n  mail:\n    # 配置 SMTP 服务器地址\n    host: smtp.qq.com\n    # 发送者邮箱\n    username: 544023561884@qq.com\n    # 配置密码，注意不是真正的密码，而是刚刚申请到的授权码\n    password: csgzzhyuoxtxbega\n    # 端口号465或587\n    port: 465\n    # 默认的邮件编码为UTF-8\n    default-encoding: UTF-8\n    # 配置SSL 加密工厂\n    properties:\n      mail:\n        smtp:\n          ssl:\n            enable: true\n          socketFactoryClass: javax.net.ssl.SSLSocketFactory\n        #表示开启 DEBUG 模式，这样，邮件发送过程的日志会在控制台打印出来，方便排查错误\n        debug: true\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n      vctgo-job:\n        connectTimeout: 100000\n        readTimeout: 100000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\nlogging:\n  level:\n    org.springframework: info\n    com.hcp: debug\n  #log日志文件配置\n  file_basePath: ./logs\n  file_prefix: ${spring.application.name}\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    banner: false\n    refresh-mapper: true\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n  type-handlers-package: com.hcp.common.mybatisplus\nhcp:\n  #多租户配置\n  tenant:\n    ignore-tables:\n      #系统表\n      - sys_config\n      - gen_table\n      - gen_table_column\n      - sys_dict_data\n      - sys_dict_type\n      - sys_menu\n      - sys_tenant\n      - sys_tenant_package\n      - sys_oper_log\n      - sys_logininfor\n      - branch_table\n      - global_table\n      - lock_table\n      - undo_log\n      - xxl_job_group\n      - xxl_job_info\n      - xxl_job_lock\n      - xxl_job_log\n      - xxl_job_log_report\n      - xxl_job_logglue\n      - xxl_job_registry\n      - xxl_job_user\n      - c_city\n      #自定义表\n# 短信服务\naliyun:\n  endpoint: cn-shanghai\n  accessKeyId:  \n  accessKeySecret: \n  signName:  \n  templateCode: \n# 本地文件上传    \nfile:\n  domain: http://127.0.0.1:39300\n  path: /home/upload\n  prefix: /documents\n', '1b732c7edc59738d730ce5d2d25648a6', '2020-05-20 12:00:00', '2025-05-24 22:55:01', 'nacos', '43.134.162.37', '', '', '通用配置', 'null', 'null', 'yaml', 'null', '');
INSERT INTO `config_info` VALUES (2, 'hcp-gateway-dev.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: hcp-auth\n          uri: lb://hcp-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: hcp-gen\n          uri: lb://hcp-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: hcp-system\n          uri: lb://hcp-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: hcp-file\n          uri: lb://hcp-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 监控模块\n        - id: hcp-monitor\n          uri: lb://hcp-monitor\n          predicates:\n            - Path=/monitor/**\n          filters:\n            - StripPrefix=1\n        # 定时模块\n        - id: hcp-job\n          uri: lb://hcp-job\n          predicates:\n            - Path=/job/**\n          filters:\n            - StripPrefix=1\n        # 样例模块\n        - id: hcp-demo\n          uri: lb://hcp-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-operator\n          uri: lb://hcp-operator\n          predicates:\n            - Path=/operator/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-mp\n          uri: lb://hcp-mp\n          predicates:\n            - Path=/mp/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-simulator\n          uri: lb://hcp-simulator\n          predicates:\n            - Path=/simulator/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      - /job/api/callback\n      - /job/api/registry\n      - /job/api/registryRemove\n      - /file/documents/**\n      - /**\n      - /websocket/charge/**', '6b669a0c13611d25f30803996b244c1d', '2020-05-14 14:17:55', '2024-08-20 00:13:55', 'nacos', '127.0.0.1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (3, 'hcp-auth-dev.yml', 'DEFAULT_GROUP', 'spring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        #loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor', '80fd9f3f721e58eb7260e1ef8bcbd629', '2022-11-28 22:04:14', '2024-08-10 16:20:47', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (4, 'hcp-monitor-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    # jedis:\n    #   pool:\n    #     max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n    #     max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n    #     max-idle: 8 # 连接池中的最大  空闲连接\n    #     min-idle: 0 # 连接池中的最小空闲连接\n    lettuce:\n      pool:\n        max-active: 20\n        max-wait: 500ms\n        max-idle: 10\n        min-idle: 2\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n# seata配置\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:', '88df60b06ca60dc71f710f15b54cacdb', '2022-11-28 22:04:14', '2024-08-10 15:58:37', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (5, 'hcp-system-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2022-11-28 22:04:14', '2024-08-10 16:20:59', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (6, 'hcp-gen-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\n# 代码生成\ngen: \n  # 作者\n  author: hcp\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.hcp.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n', 'b58199518dbff233d55574f85ff8beab', '2022-11-28 22:04:14', '2024-08-10 16:35:53', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (7, 'hcp-file-dev.yml', 'DEFAULT_GROUP', '#本地文件服务的配置迁移到公共配置文件里\n# FastDFS配置 -- http://hcp-fastdfs 为hosts映射\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\n', '1c16b84aed9b9053e848c70f9346d899', '2022-11-28 22:04:14', '2024-08-10 16:36:05', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (8, 'sentinel-hcp-gateway', 'DEFAULT_GROUP', '[\n    {\n        \"resource\": \"hcp-auth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"hcp-system\",\n        \"count\": 1000,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"hcp-gen\",\n        \"count\": 200,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]', 'b109301f83310c0c1bf56bfab5f4859e', '2022-11-28 22:04:14', '2024-08-05 11:38:40', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (9, 'hcp-demo-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: localhost\n    port: 6379\n    #password: \n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/vctgo?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: Abdulla1992.\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\nxxl:\n  job:\n    admin:\n      ### xxl-job admin address list, such as \"http://address\" or \"http://address01,http://address02\"\n      addresses: http://localhost/dev-api/job\n    ### xxl-job, access token\n    accessToken: default_token\n    executor: \n      ### xxl-job executor appname\n      appname: xxl-job-executor-sample\n      ### xxl-job executor registry-address: default use address to registry , otherwise use ip:port if address is null\n      address: \n      ### xxl-job executor server-info\n      ip: 127.0.0.1\n      port: 9999\n      ### xxl-job executor log-path\n      logpath: ./logs/xxl-job\n      ### xxl-job executor log-retention-days\n      logretentiondays: 30', 'b0658556d29d38e0d3cf573d4e57eccf', '2022-10-26 20:40:11', '2024-08-05 11:39:29', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (10, 'hcp-job-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root \n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n  mail:\n    host: www.163.com\n    port: 25\n    username: xxxxxx@163.com\n    from: xxxxxx@163.com\n    password: xxxxxx\n    properties: \n      mail: \n        smtp: \n          auth: false\n          starttls: \n            enable: false\n            required: false\n          socketFactory: \n            class: javax.net.ssl.SSLSocketFactory\n          \n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\nxxl:\n  job:\n    accessToken: default_token\n    i18n: zh_CN\n    logretentiondays: 30\n    triggerpool: \n      fast: \n        max: 200\n      slow: \n        max: 100\n\ncustom:\n  log:\n    list:\n      - scheduleJobQuery-error\n      - findFailJobLogIds-error\n      - findByAddressType-error\n\n\n', '078374f0a38fd306318aeb63886391cf', '2022-10-27 21:22:25', '2024-08-10 16:36:22', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (27, 'hcp-operator-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2024-08-06 12:11:29', '2024-08-10 16:36:35', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (28, 'hcp-mp-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2024-08-06 12:22:28', '2024-08-10 16:36:48', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (29, 'hcp-simulator-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\nchargeServer:\n  #测试配置项\n  address: http://wrt.fengzb.pp.ua:9250', '10f4c556935b6b99f074c390218e9427', '2024-08-06 12:26:07', '2024-08-11 21:54:10', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (62, 'ruoyi-gateway.yml', 'DEFAULT_GROUP', '# 安全配置\nsecurity:\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/code\n      - /auth/logout\n      - /auth/login\n      - /auth/binding/*\n      - /auth/social/callback\n      - /auth/register\n      - /auth/tenant/list\n      - /resource/sms/code\n      - /*/v3/api-docs\n      - /*/error\n      - /csrf\n\nspring:\n  cloud:\n    # 网关配置\n    gateway:\n      # 打印请求日志(自定义)\n      requestLog: true\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: ruoyi-auth\n          uri: lb://ruoyi-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            - StripPrefix=1\n        # 代码生成\n        - id: ruoyi-gen\n          uri: lb://ruoyi-gen\n          predicates:\n            - Path=/tool/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: ruoyi-system\n          uri: lb://ruoyi-system\n          predicates:\n            - Path=/system/**,/monitor/**\n          filters:\n            - StripPrefix=1\n        # 资源服务\n        - id: ruoyi-resource\n          uri: lb://ruoyi-resource\n          predicates:\n            - Path=/resource/**\n          filters:\n            - StripPrefix=1\n        # 演示服务\n        - id: ruoyi-demo\n          uri: lb://ruoyi-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        # MQ演示服务\n        - id: ruoyi-stream-mq\n          uri: lb://ruoyi-stream-mq\n          predicates:\n            - Path=/stream-mq/**\n          filters:\n            - StripPrefix=1\n\n    # sentinel 配置\n    sentinel:\n      filter:\n        enabled: false\n      # nacos配置持久化\n      datasource:\n        ds1:\n          nacos:\n            server-addr: ${spring.cloud.nacos.server-addr}\n            dataId: sentinel-${spring.application.name}.json\n            groupId: ${spring.cloud.nacos.config.group}\n            namespace: ${spring.profiles.active}\n            data-type: json\n            rule-type: gw-flow\n', 'ce06342a0e77fe4c34925fcc46be40b5', '2024-01-04 09:47:55', '2024-01-04 09:47:55', 'nacos', '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (65, 'application-dev.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n# mail配置\n  mail:\n    host: smtp.qq.com\n    # 发送者邮箱\n    username: 544023561884@qq.com\n    # 配置密码，注意不是真正的密码，而是刚刚申请到的授权码\n    password: csgzzhyuoxtxbega\n    # 端口号465或587\n    port: 465\n    # 默认的邮件编码为UTF-8\n    default-encoding: UTF-8\n    # 配置SSL 加密工厂\n    properties:\n      mail:\n        smtp:\n          ssl:\n            enable: true\n          socketFactoryClass: javax.net.ssl.SSLSocketFactory\n        #表示开启 DEBUG 模式，这样，邮件发送过程的日志会在控制台打印出来，方便排查错误\n        debug: true\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n      vctgo-job:\n        connectTimeout: 100000\n        readTimeout: 100000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\nlogging:\n  level:\n    org.springframework: info\n    com.hcp: debug\n  #log日志文件配置\n  file_basePath: ./logs\n  file_prefix: ${spring.application.name}\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    banner: false\n    refresh-mapper: true\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n  type-handlers-package: com.hcp.common.mybatisplus\nhcp:\n  #多租户配置\n  tenant:\n    ignore-tables:\n      #系统表\n      - sys_config\n      - gen_table\n      - gen_table_column\n      - sys_dict_data\n      - sys_dict_type\n      - sys_menu\n      - sys_tenant\n      - sys_tenant_package\n      - sys_oper_log\n      - sys_logininfor\n      - branch_table\n      - global_table\n      - lock_table\n      - undo_log\n      - xxl_job_group\n      - xxl_job_info\n      - xxl_job_lock\n      - xxl_job_log\n      - xxl_job_log_report\n      - xxl_job_logglue\n      - xxl_job_registry\n      - xxl_job_user\n      - c_city\n      #自定义表\n# 短信服务\naliyun:\n  endpoint: cn-shanghai\n  accessKeyId:  \n  accessKeySecret: \n  signName:  \n  templateCode: \n# 本地文件上传    \nfile:\n  domain: http://127.0.0.1:39300\n  path: /home/upload\n  prefix: /documents\n', 'fb5950dd96b10da8b7afbe7fb8d1fca9', '2024-08-14 23:58:56', '2025-05-24 22:57:16', 'nacos', '43.134.162.37', '', 'hcp', '通用配置', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (66, 'hcp-gateway-dev.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: hcp-auth\n          uri: lb://hcp-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: hcp-gen\n          uri: lb://hcp-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: hcp-system\n          uri: lb://hcp-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: hcp-file\n          uri: lb://hcp-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 监控模块\n        - id: hcp-monitor\n          uri: lb://hcp-monitor\n          predicates:\n            - Path=/monitor/**\n          filters:\n            - StripPrefix=1\n        # 定时模块\n        - id: hcp-job\n          uri: lb://hcp-job\n          predicates:\n            - Path=/job/**\n          filters:\n            - StripPrefix=1\n        # 样例模块\n        - id: hcp-demo\n          uri: lb://hcp-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-operator\n          uri: lb://hcp-operator\n          predicates:\n            - Path=/operator/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-mp\n          uri: lb://hcp-mp\n          predicates:\n            - Path=/mp/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-simulator\n          uri: lb://hcp-simulator\n          predicates:\n            - Path=/simulator/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      - /job/api/callback\n      - /job/api/registry\n      - /job/api/registryRemove\n      - /file/documents/**\n      - /hcp-mp/**\n      - /hcp-operator/websocket/charge/**', 'e646f92afbf82061ba851d1cc89ab84e', '2024-08-14 23:58:56', '2024-08-20 00:24:23', 'nacos', '127.0.0.1', '', 'hcp', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (67, 'hcp-auth-dev.yml', 'DEFAULT_GROUP', 'spring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        #loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor', '80fd9f3f721e58eb7260e1ef8bcbd629', '2024-08-14 23:58:56', '2024-08-14 23:58:56', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (68, 'hcp-monitor-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    # jedis:\n    #   pool:\n    #     max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n    #     max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n    #     max-idle: 8 # 连接池中的最大  空闲连接\n    #     min-idle: 0 # 连接池中的最小空闲连接\n    lettuce:\n      pool:\n        max-active: 20\n        max-wait: 500ms\n        max-idle: 10\n        min-idle: 2\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n# seata配置\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:', '88df60b06ca60dc71f710f15b54cacdb', '2024-08-14 23:58:57', '2024-08-14 23:58:57', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (69, 'hcp-system-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2024-08-14 23:58:57', '2024-08-14 23:58:57', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (70, 'hcp-gen-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\n# 代码生成\ngen: \n  # 作者\n  author: hcp\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.hcp.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n', 'b58199518dbff233d55574f85ff8beab', '2024-08-14 23:58:57', '2024-08-14 23:58:57', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (71, 'hcp-file-dev.yml', 'DEFAULT_GROUP', '#本地文件服务的配置迁移到公共配置文件里\n# FastDFS配置 -- http://hcp-fastdfs 为hosts映射\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\n', '1c16b84aed9b9053e848c70f9346d899', '2024-08-14 23:58:58', '2024-08-14 23:58:58', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (72, 'sentinel-hcp-gateway', 'DEFAULT_GROUP', '[\n    {\n        \"resource\": \"hcp-auth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"hcp-system\",\n        \"count\": 1000,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"hcp-gen\",\n        \"count\": 200,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]', 'b109301f83310c0c1bf56bfab5f4859e', '2024-08-14 23:58:58', '2024-08-14 23:58:58', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (73, 'hcp-demo-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: localhost\n    port: 6379\n    #password: \n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/vctgo?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: Abdulla1992.\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\nxxl:\n  job:\n    admin:\n      ### xxl-job admin address list, such as \"http://address\" or \"http://address01,http://address02\"\n      addresses: http://localhost/dev-api/job\n    ### xxl-job, access token\n    accessToken: default_token\n    executor: \n      ### xxl-job executor appname\n      appname: xxl-job-executor-sample\n      ### xxl-job executor registry-address: default use address to registry , otherwise use ip:port if address is null\n      address: \n      ### xxl-job executor server-info\n      ip: 127.0.0.1\n      port: 9999\n      ### xxl-job executor log-path\n      logpath: ./logs/xxl-job\n      ### xxl-job executor log-retention-days\n      logretentiondays: 30', 'b0658556d29d38e0d3cf573d4e57eccf', '2024-08-14 23:58:58', '2024-08-14 23:58:58', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (74, 'hcp-job-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root \n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n  mail:\n    host: www.163.com\n    port: 25\n    username: xxxxxx@163.com\n    from: xxxxxx@163.com\n    password: xxxxxx\n    properties: \n      mail: \n        smtp: \n          auth: false\n          starttls: \n            enable: false\n            required: false\n          socketFactory: \n            class: javax.net.ssl.SSLSocketFactory\n          \n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\nxxl:\n  job:\n    accessToken: default_token\n    i18n: zh_CN\n    logretentiondays: 30\n    triggerpool: \n      fast: \n        max: 200\n      slow: \n        max: 100\n\ncustom:\n  log:\n    list:\n      - scheduleJobQuery-error\n      - findFailJobLogIds-error\n      - findByAddressType-error\n\n\n', '078374f0a38fd306318aeb63886391cf', '2024-08-14 23:58:59', '2024-08-14 23:58:59', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (75, 'hcp-operator-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2024-08-14 23:58:59', '2024-08-14 23:58:59', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (76, 'hcp-mp-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2024-08-14 23:58:59', '2024-08-14 23:58:59', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (77, 'hcp-simulator-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\nchargeServer:\n  #测试配置项\n  address: http://wrt.fengzb.pp.ua:9250', '10f4c556935b6b99f074c390218e9427', '2024-08-14 23:59:00', '2024-08-14 23:59:00', 'nacos', '49.118.86.41', '', 'hcp', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (78, 'ruoyi-gateway.yml', 'DEFAULT_GROUP', '# 安全配置\nsecurity:\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/code\n      - /auth/logout\n      - /auth/login\n      - /auth/binding/*\n      - /auth/social/callback\n      - /auth/register\n      - /auth/tenant/list\n      - /resource/sms/code\n      - /*/v3/api-docs\n      - /*/error\n      - /csrf\n\nspring:\n  cloud:\n    # 网关配置\n    gateway:\n      # 打印请求日志(自定义)\n      requestLog: true\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: ruoyi-auth\n          uri: lb://ruoyi-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            - StripPrefix=1\n        # 代码生成\n        - id: ruoyi-gen\n          uri: lb://ruoyi-gen\n          predicates:\n            - Path=/tool/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: ruoyi-system\n          uri: lb://ruoyi-system\n          predicates:\n            - Path=/system/**,/monitor/**\n          filters:\n            - StripPrefix=1\n        # 资源服务\n        - id: ruoyi-resource\n          uri: lb://ruoyi-resource\n          predicates:\n            - Path=/resource/**\n          filters:\n            - StripPrefix=1\n        # 演示服务\n        - id: ruoyi-demo\n          uri: lb://ruoyi-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        # MQ演示服务\n        - id: ruoyi-stream-mq\n          uri: lb://ruoyi-stream-mq\n          predicates:\n            - Path=/stream-mq/**\n          filters:\n            - StripPrefix=1\n\n    # sentinel 配置\n    sentinel:\n      filter:\n        enabled: false\n      # nacos配置持久化\n      datasource:\n        ds1:\n          nacos:\n            server-addr: ${spring.cloud.nacos.server-addr}\n            dataId: sentinel-${spring.application.name}.json\n            groupId: ${spring.cloud.nacos.config.group}\n            namespace: ${spring.profiles.active}\n            data-type: json\n            rule-type: gw-flow\n', 'ce06342a0e77fe4c34925fcc46be40b5', '2024-08-14 23:59:00', '2024-08-14 23:59:00', 'nacos', '49.118.86.41', '', 'hcp', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (79, 'application-local.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n# mail配置\n  mail:\n    # 配置 SMTP 服务器地址\n    host: smtp.qiye.aliyun.com\n    # 发送者邮箱\n    username: admin@abdl.cn\n    # 配置密码，注意不是真正的密码，而是刚刚申请到的授权码\n    password: Abdulla1992.\n    # 端口号465或587\n    port: 465\n    # 默认的邮件编码为UTF-8\n    default-encoding: UTF-8\n    # 配置SSL 加密工厂\n    properties:\n      mail:\n        smtp:\n          ssl:\n            enable: true\n          socketFactoryClass: javax.net.ssl.SSLSocketFactory\n        #表示开启 DEBUG 模式，这样，邮件发送过程的日志会在控制台打印出来，方便排查错误\n        debug: true\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n      vctgo-job:\n        connectTimeout: 100000\n        readTimeout: 100000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\nlogging:\n  level:\n    org.springframework: info\n    com.hcp: debug\n  #log日志文件配置\n  file_basePath: ./logs\n  file_prefix: ${spring.application.name}\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    banner: false\n    refresh-mapper: true\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n  type-handlers-package: com.hcp.common.mybatisplus\nhcp:\n  #多租户配置\n  tenant:\n    ignore-tables:\n      #系统表\n      - sys_config\n      - gen_table\n      - gen_table_column\n      - sys_dict_data\n      - sys_dict_type\n      - sys_menu\n      - sys_tenant\n      - sys_tenant_package\n      - sys_oper_log\n      - sys_logininfor\n      - branch_table\n      - global_table\n      - lock_table\n      - undo_log\n      - xxl_job_group\n      - xxl_job_info\n      - xxl_job_lock\n      - xxl_job_log\n      - xxl_job_log_report\n      - xxl_job_logglue\n      - xxl_job_registry\n      - xxl_job_user\n      - c_city\n      #自定义表\n# 短信服务\naliyun:\n  endpoint: cn-shanghai\n  accessKeyId:  \n  accessKeySecret: \n  signName:  \n  templateCode: \n# 本地文件上传    \nfile:\n  domain: http://127.0.0.1:39300\n  path: /home/upload\n  prefix: /documents\n', '05752428889424ed85a1328e3b3fdcde', '2024-08-18 22:06:20', '2024-08-18 22:06:20', NULL, '0:0:0:0:0:0:0:1', '', '', '通用配置', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (80, 'hcp-gateway-local.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    #password:\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: hcp-auth\n          uri: lb://hcp-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: hcp-gen\n          uri: lb://hcp-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: hcp-system\n          uri: lb://hcp-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: hcp-file\n          uri: lb://hcp-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 监控模块\n        - id: hcp-monitor\n          uri: lb://hcp-monitor\n          predicates:\n            - Path=/monitor/**\n          filters:\n            - StripPrefix=1\n        # 定时模块\n        - id: hcp-job\n          uri: lb://hcp-job\n          predicates:\n            - Path=/job/**\n          filters:\n            - StripPrefix=1\n        # 样例模块\n        - id: hcp-demo\n          uri: lb://hcp-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-operator\n          uri: lb://hcp-operator\n          predicates:\n            - Path=/operator/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-mp\n          uri: lb://hcp-mp\n          predicates:\n            - Path=/mp/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-simulator\n          uri: lb://hcp-simulator\n          predicates:\n            - Path=/simulator/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      - /job/api/callback\n      - /job/api/registry\n      - /job/api/registryRemove\n      - /file/documents/**\n      - /**', '6ae2e4e409886d05a353b9b36b52a0b7', '2024-08-18 22:06:20', '2024-08-19 00:37:07', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (81, 'hcp-auth-local.yml', 'DEFAULT_GROUP', 'spring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    #password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        #loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor', 'fc4d18c4d80daa4c777c04e72bf04330', '2024-08-18 22:06:20', '2024-08-19 00:38:20', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (82, 'hcp-system-local.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    #password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: Abdulla1992.\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '2cda194a71235153669800563b0d2400', '2024-08-18 22:06:20', '2024-08-19 01:47:34', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (83, 'hcp-file-local.yml', 'DEFAULT_GROUP', '#本地文件服务的配置迁移到公共配置文件里\n# FastDFS配置 -- http://hcp-fastdfs 为hosts映射\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\n', '1c16b84aed9b9053e848c70f9346d899', '2024-08-18 22:06:20', '2024-08-18 22:06:20', NULL, '0:0:0:0:0:0:0:1', '', '', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (84, 'hcp-operator-local.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2024-08-18 22:06:44', '2024-08-18 22:06:44', NULL, '0:0:0:0:0:0:0:1', '', '', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (85, 'hcp-mp-local.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2024-08-18 22:06:44', '2024-08-18 22:06:44', NULL, '0:0:0:0:0:0:0:1', '', '', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (86, 'hcp-simulator-local.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\nchargeServer:\n  #测试配置项\n  address: http://wrt.fengzb.pp.ua:9250', '10f4c556935b6b99f074c390218e9427', '2024-08-18 22:06:44', '2024-08-18 22:06:44', NULL, '0:0:0:0:0:0:0:1', '', '', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (94, 'gateway-dev.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: huigecharge-auth\n          uri: lb://huigecharge-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: huigecharge-modules-gen\n          uri: lb://huigecharge-modules-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: huigecharge-modules-system\n          uri: lb://huigecharge-modules-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: huigecharge-modules-file\n          uri: lb://huigecharge-modules-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 监控模块\n        - id: huigecharge-modules-monitor\n          uri: lb://huigecharge-modules-monitor\n          predicates:\n            - Path=/monitor/**\n          filters:\n            - StripPrefix=1\n        # 定时模块\n        - id: huigecharge-modules-job\n          uri: lb://huigecharge-modules-job\n          predicates:\n            - Path=/job/**\n          filters:\n            - StripPrefix=1\n        # 样例模块\n        - id: huigecharge-demo\n          uri: lb://huigecharge-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        - id: huigecharge-modules-operate\n          uri: lb://huigecharge-modules-operate\n          predicates:\n            - Path=/charge/**\n          filters:\n            - StripPrefix=1\n        - id: huigecharge-modules-wechat\n          uri: lb://huigecharge-modules-wechat\n          predicates:\n            - Path=/wechat/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      - /job/api/callback\n      - /job/api/registry\n      - /job/api/registryRemove\n      - /file/documents/**\n      - /huigecharge-modules-operate/**\n      - /huigecharge-modules-wechat/**\n      - /hcp-operator/websocket/charge/**\n      - /charge/**\n      - /wechat/**', '2f9372bb875a0fdc46cb9566ac10f715', '2024-09-05 13:59:10', '2024-09-06 21:23:51', 'nacos', '14.154.63.104', '', '70070f3a-b8c6-45f1-8056-a32a7defae98', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (95, 'application-dev.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n# mail配置\n  mail:\n    host: smtp.qq.com\n    # 发送者邮箱\n    username: 544023561884@qq.com\n    # 配置密码，注意不是真正的密码，而是刚刚申请到的授权码\n    password: csgzzhyuoxtxbega\n    # 端口号465或587\n    port: 465\n    # 默认的邮件编码为UTF-8\n    default-encoding: UTF-8\n    # 配置SSL 加密工厂\n    properties:\n      mail:\n        smtp:\n          ssl:\n            enable: true\n          socketFactoryClass: javax.net.ssl.SSLSocketFactory\n        #表示开启 DEBUG 模式，这样，邮件发送过程的日志会在控制台打印出来，方便排查错误\n        debug: true\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n      vctgo-job:\n        connectTimeout: 100000\n        readTimeout: 100000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\nlogging:\n  level:\n    org.springframework: info\n    com.hcp: debug\n  #log日志文件配置\n  file_basePath: ./logs\n  file_prefix: ${spring.application.name}\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    banner: false\n    refresh-mapper: true\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n  type-handlers-package: com.hcp.common.mybatisplus\nhcp:\n  #多租户配置\n  tenant:\n    ignore-tables:\n      #系统表\n      - sys_config\n      - gen_table\n      - gen_table_column\n      - sys_dict_data\n      - sys_dict_type\n      - sys_menu\n      - sys_tenant\n      - sys_tenant_package\n      - sys_oper_log\n      - sys_logininfor\n      - branch_table\n      - global_table\n      - lock_table\n      - undo_log\n      - xxl_job_group\n      - xxl_job_info\n      - xxl_job_lock\n      - xxl_job_log\n      - xxl_job_log_report\n      - xxl_job_logglue\n      - xxl_job_registry\n      - xxl_job_user\n      - c_city\n      #自定义表\n# 短信服务\naliyun:\n  endpoint: cn-shanghai\n  accessKeyId:  \n  accessKeySecret: \n  signName:  \n  templateCode: \n# 本地文件上传    \nfile:\n  domain: http://127.0.0.1:39300\n  path: /home/upload\n  prefix: /documents\n', 'fb5950dd96b10da8b7afbe7fb8d1fca9', '2025-05-25 00:25:19', '2025-05-25 00:25:19', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '通用配置', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (96, 'hcp-gateway-dev.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: hcp-auth\n          uri: lb://hcp-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: hcp-gen\n          uri: lb://hcp-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: hcp-system\n          uri: lb://hcp-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: hcp-file\n          uri: lb://hcp-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 监控模块\n        - id: hcp-monitor\n          uri: lb://hcp-monitor\n          predicates:\n            - Path=/monitor/**\n          filters:\n            - StripPrefix=1\n        # 定时模块\n        - id: hcp-job\n          uri: lb://hcp-job\n          predicates:\n            - Path=/job/**\n          filters:\n            - StripPrefix=1\n        # 样例模块\n        - id: hcp-demo\n          uri: lb://hcp-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-operator\n          uri: lb://hcp-operator\n          predicates:\n            - Path=/operator/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-mp\n          uri: lb://hcp-mp\n          predicates:\n            - Path=/mp/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-simulator\n          uri: lb://hcp-simulator\n          predicates:\n            - Path=/simulator/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      - /job/api/callback\n      - /job/api/registry\n      - /job/api/registryRemove\n      - /file/documents/**\n      - /hcp-mp/**\n      - /hcp-operator/websocket/charge/**', 'e646f92afbf82061ba851d1cc89ab84e', '2025-05-25 00:25:19', '2025-05-25 00:25:19', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (97, 'hcp-auth-dev.yml', 'DEFAULT_GROUP', 'spring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        #loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor', '80fd9f3f721e58eb7260e1ef8bcbd629', '2025-05-25 00:25:20', '2025-05-25 00:25:20', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (98, 'hcp-monitor-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    # jedis:\n    #   pool:\n    #     max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n    #     max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n    #     max-idle: 8 # 连接池中的最大  空闲连接\n    #     min-idle: 0 # 连接池中的最小空闲连接\n    lettuce:\n      pool:\n        max-active: 20\n        max-wait: 500ms\n        max-idle: 10\n        min-idle: 2\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n# seata配置\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:', '88df60b06ca60dc71f710f15b54cacdb', '2025-05-25 00:25:20', '2025-05-25 00:25:20', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (99, 'hcp-system-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2025-05-25 00:25:21', '2025-05-25 00:25:21', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (100, 'hcp-gen-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\n# 代码生成\ngen: \n  # 作者\n  author: hcp\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.hcp.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n', 'b58199518dbff233d55574f85ff8beab', '2025-05-25 00:25:21', '2025-05-25 00:25:21', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (101, 'hcp-file-dev.yml', 'DEFAULT_GROUP', '#本地文件服务的配置迁移到公共配置文件里\n# FastDFS配置 -- http://hcp-fastdfs 为hosts映射\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\n', '1c16b84aed9b9053e848c70f9346d899', '2025-05-25 00:25:21', '2025-05-25 00:25:21', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (102, 'sentinel-hcp-gateway', 'DEFAULT_GROUP', '[\n    {\n        \"resource\": \"hcp-auth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"hcp-system\",\n        \"count\": 1000,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"hcp-gen\",\n        \"count\": 200,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]', 'b109301f83310c0c1bf56bfab5f4859e', '2025-05-25 00:25:22', '2025-05-25 00:25:22', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (103, 'hcp-demo-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: localhost\n    port: 6379\n    #password: \n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/vctgo?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: Abdulla1992.\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\nxxl:\n  job:\n    admin:\n      ### xxl-job admin address list, such as \"http://address\" or \"http://address01,http://address02\"\n      addresses: http://localhost/dev-api/job\n    ### xxl-job, access token\n    accessToken: default_token\n    executor: \n      ### xxl-job executor appname\n      appname: xxl-job-executor-sample\n      ### xxl-job executor registry-address: default use address to registry , otherwise use ip:port if address is null\n      address: \n      ### xxl-job executor server-info\n      ip: 127.0.0.1\n      port: 9999\n      ### xxl-job executor log-path\n      logpath: ./logs/xxl-job\n      ### xxl-job executor log-retention-days\n      logretentiondays: 30', 'b0658556d29d38e0d3cf573d4e57eccf', '2025-05-25 00:25:22', '2025-05-25 00:25:22', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (104, 'hcp-job-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root \n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n  mail:\n    host: www.163.com\n    port: 25\n    username: xxxxxx@163.com\n    from: xxxxxx@163.com\n    password: xxxxxx\n    properties: \n      mail: \n        smtp: \n          auth: false\n          starttls: \n            enable: false\n            required: false\n          socketFactory: \n            class: javax.net.ssl.SSLSocketFactory\n          \n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\nxxl:\n  job:\n    accessToken: default_token\n    i18n: zh_CN\n    logretentiondays: 30\n    triggerpool: \n      fast: \n        max: 200\n      slow: \n        max: 100\n\ncustom:\n  log:\n    list:\n      - scheduleJobQuery-error\n      - findFailJobLogIds-error\n      - findByAddressType-error\n\n\n', '078374f0a38fd306318aeb63886391cf', '2025-05-25 00:25:22', '2025-05-25 00:25:22', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (105, 'hcp-operator-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2025-05-25 00:25:23', '2025-05-25 00:25:23', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (106, 'hcp-mp-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2025-05-25 00:25:23', '2025-05-25 00:25:23', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (107, 'hcp-simulator-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\nchargeServer:\n  #测试配置项\n  address: http://wrt.fengzb.pp.ua:9250', '10f4c556935b6b99f074c390218e9427', '2025-05-25 00:25:24', '2025-05-25 00:25:24', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (108, 'ruoyi-gateway.yml', 'DEFAULT_GROUP', '# 安全配置\nsecurity:\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/code\n      - /auth/logout\n      - /auth/login\n      - /auth/binding/*\n      - /auth/social/callback\n      - /auth/register\n      - /auth/tenant/list\n      - /resource/sms/code\n      - /*/v3/api-docs\n      - /*/error\n      - /csrf\n\nspring:\n  cloud:\n    # 网关配置\n    gateway:\n      # 打印请求日志(自定义)\n      requestLog: true\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: ruoyi-auth\n          uri: lb://ruoyi-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            - StripPrefix=1\n        # 代码生成\n        - id: ruoyi-gen\n          uri: lb://ruoyi-gen\n          predicates:\n            - Path=/tool/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: ruoyi-system\n          uri: lb://ruoyi-system\n          predicates:\n            - Path=/system/**,/monitor/**\n          filters:\n            - StripPrefix=1\n        # 资源服务\n        - id: ruoyi-resource\n          uri: lb://ruoyi-resource\n          predicates:\n            - Path=/resource/**\n          filters:\n            - StripPrefix=1\n        # 演示服务\n        - id: ruoyi-demo\n          uri: lb://ruoyi-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        # MQ演示服务\n        - id: ruoyi-stream-mq\n          uri: lb://ruoyi-stream-mq\n          predicates:\n            - Path=/stream-mq/**\n          filters:\n            - StripPrefix=1\n\n    # sentinel 配置\n    sentinel:\n      filter:\n        enabled: false\n      # nacos配置持久化\n      datasource:\n        ds1:\n          nacos:\n            server-addr: ${spring.cloud.nacos.server-addr}\n            dataId: sentinel-${spring.application.name}.json\n            groupId: ${spring.cloud.nacos.config.group}\n            namespace: ${spring.profiles.active}\n            data-type: json\n            rule-type: gw-flow\n', 'ce06342a0e77fe4c34925fcc46be40b5', '2025-05-25 00:25:24', '2025-05-25 00:25:24', 'nacos', '124.128.198.123', '', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', NULL, NULL, NULL, 'yaml', NULL, '');

-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'datum_id',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime(0) NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfoaggr_datagrouptenantdatum`(`data_id`, `group_id`, `tenant_id`, `datum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '增加租户字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_aggr
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `gmt_modified` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfobeta_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'config_info_beta' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_beta
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `gmt_modified` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfotag_datagrouptenanttag`(`data_id`, `group_id`, `tenant_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'config_info_tag' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_tag
-- ----------------------------

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation`  (
  `id` bigint(0) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(0) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`) USING BTREE,
  UNIQUE INDEX `uk_configtagrelation_configidtag`(`id`, `tag_name`, `tag_type`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'config_tag_relation' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `gmt_modified` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '集群、各Group容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `nid` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `gmt_modified` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL,
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `op_type` char(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`nid`) USING BTREE,
  INDEX `idx_gmt_create`(`gmt_create`) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified`) USING BTREE,
  INDEX `idx_did`(`data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 113 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '多租户改造' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of his_config_info
-- ----------------------------
INSERT INTO `his_config_info` VALUES (1, 97, 'application-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n# mail配置\n  mail:\n    # 配置 SMTP 服务器地址\n    host: smtp.qiye.aliyun.com\n    # 发送者邮箱\n    username: admin@abdl.cn\n    # 配置密码，注意不是真正的密码，而是刚刚申请到的授权码\n    password: Abdulla1992.\n    # 端口号465或587\n    port: 465\n    # 默认的邮件编码为UTF-8\n    default-encoding: UTF-8\n    # 配置SSL 加密工厂\n    properties:\n      mail:\n        smtp:\n          ssl:\n            enable: true\n          socketFactoryClass: javax.net.ssl.SSLSocketFactory\n        #表示开启 DEBUG 模式，这样，邮件发送过程的日志会在控制台打印出来，方便排查错误\n        debug: true\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n      vctgo-job:\n        connectTimeout: 100000\n        readTimeout: 100000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\nlogging:\n  level:\n    org.springframework: info\n    com.hcp: debug\n  #log日志文件配置\n  file_basePath: ./logs\n  file_prefix: ${spring.application.name}\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    banner: false\n    refresh-mapper: true\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n  type-handlers-package: com.hcp.common.mybatisplus\nhcp:\n  #多租户配置\n  tenant:\n    ignore-tables:\n      #系统表\n      - sys_config\n      - gen_table\n      - gen_table_column\n      - sys_dict_data\n      - sys_dict_type\n      - sys_menu\n      - sys_tenant\n      - sys_tenant_package\n      - sys_oper_log\n      - sys_logininfor\n      - branch_table\n      - global_table\n      - lock_table\n      - undo_log\n      - xxl_job_group\n      - xxl_job_info\n      - xxl_job_lock\n      - xxl_job_log\n      - xxl_job_log_report\n      - xxl_job_logglue\n      - xxl_job_registry\n      - xxl_job_user\n      - c_city\n      #自定义表\n# 短信服务\naliyun:\n  endpoint: cn-shanghai\n  accessKeyId:  \n  accessKeySecret: \n  signName:  \n  templateCode: \n# 本地文件上传    \nfile:\n  domain: http://127.0.0.1:39300\n  path: /home/upload\n  prefix: /documents\n', '05752428889424ed85a1328e3b3fdcde', '2025-05-24 22:55:01', '2025-05-24 14:55:01', 'nacos', '43.134.162.37', 'U', '', '');
INSERT INTO `his_config_info` VALUES (65, 98, 'application-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n# mail配置\n  mail:\n    # 配置 SMTP 服务器地址\n    host: smtp.qiye.aliyun.com\n    # 发送者邮箱\n    username: admin@abdl.cn\n    # 配置密码，注意不是真正的密码，而是刚刚申请到的授权码\n    password: Abdulla1992.\n    # 端口号465或587\n    port: 465\n    # 默认的邮件编码为UTF-8\n    default-encoding: UTF-8\n    # 配置SSL 加密工厂\n    properties:\n      mail:\n        smtp:\n          ssl:\n            enable: true\n          socketFactoryClass: javax.net.ssl.SSLSocketFactory\n        #表示开启 DEBUG 模式，这样，邮件发送过程的日志会在控制台打印出来，方便排查错误\n        debug: true\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n      vctgo-job:\n        connectTimeout: 100000\n        readTimeout: 100000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\nlogging:\n  level:\n    org.springframework: info\n    com.hcp: debug\n  #log日志文件配置\n  file_basePath: ./logs\n  file_prefix: ${spring.application.name}\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    banner: false\n    refresh-mapper: true\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n  type-handlers-package: com.hcp.common.mybatisplus\nhcp:\n  #多租户配置\n  tenant:\n    ignore-tables:\n      #系统表\n      - sys_config\n      - gen_table\n      - gen_table_column\n      - sys_dict_data\n      - sys_dict_type\n      - sys_menu\n      - sys_tenant\n      - sys_tenant_package\n      - sys_oper_log\n      - sys_logininfor\n      - branch_table\n      - global_table\n      - lock_table\n      - undo_log\n      - xxl_job_group\n      - xxl_job_info\n      - xxl_job_lock\n      - xxl_job_log\n      - xxl_job_log_report\n      - xxl_job_logglue\n      - xxl_job_registry\n      - xxl_job_user\n      - c_city\n      #自定义表\n# 短信服务\naliyun:\n  endpoint: cn-shanghai\n  accessKeyId:  \n  accessKeySecret: \n  signName:  \n  templateCode: \n# 本地文件上传    \nfile:\n  domain: http://127.0.0.1:39300\n  path: /home/upload\n  prefix: /documents\n', '05752428889424ed85a1328e3b3fdcde', '2025-05-24 22:57:16', '2025-05-24 14:57:16', 'nacos', '43.134.162.37', 'U', 'hcp', '');
INSERT INTO `his_config_info` VALUES (0, 99, 'application-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n# mail配置\n  mail:\n    host: smtp.qq.com\n    # 发送者邮箱\n    username: 544023561884@qq.com\n    # 配置密码，注意不是真正的密码，而是刚刚申请到的授权码\n    password: csgzzhyuoxtxbega\n    # 端口号465或587\n    port: 465\n    # 默认的邮件编码为UTF-8\n    default-encoding: UTF-8\n    # 配置SSL 加密工厂\n    properties:\n      mail:\n        smtp:\n          ssl:\n            enable: true\n          socketFactoryClass: javax.net.ssl.SSLSocketFactory\n        #表示开启 DEBUG 模式，这样，邮件发送过程的日志会在控制台打印出来，方便排查错误\n        debug: true\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n      vctgo-job:\n        connectTimeout: 100000\n        readTimeout: 100000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\nlogging:\n  level:\n    org.springframework: info\n    com.hcp: debug\n  #log日志文件配置\n  file_basePath: ./logs\n  file_prefix: ${spring.application.name}\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    banner: false\n    refresh-mapper: true\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n  type-handlers-package: com.hcp.common.mybatisplus\nhcp:\n  #多租户配置\n  tenant:\n    ignore-tables:\n      #系统表\n      - sys_config\n      - gen_table\n      - gen_table_column\n      - sys_dict_data\n      - sys_dict_type\n      - sys_menu\n      - sys_tenant\n      - sys_tenant_package\n      - sys_oper_log\n      - sys_logininfor\n      - branch_table\n      - global_table\n      - lock_table\n      - undo_log\n      - xxl_job_group\n      - xxl_job_info\n      - xxl_job_lock\n      - xxl_job_log\n      - xxl_job_log_report\n      - xxl_job_logglue\n      - xxl_job_registry\n      - xxl_job_user\n      - c_city\n      #自定义表\n# 短信服务\naliyun:\n  endpoint: cn-shanghai\n  accessKeyId:  \n  accessKeySecret: \n  signName:  \n  templateCode: \n# 本地文件上传    \nfile:\n  domain: http://127.0.0.1:39300\n  path: /home/upload\n  prefix: /documents\n', 'fb5950dd96b10da8b7afbe7fb8d1fca9', '2025-05-25 00:25:19', '2025-05-24 16:25:19', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 100, 'hcp-gateway-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: hcp-auth\n          uri: lb://hcp-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: hcp-gen\n          uri: lb://hcp-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: hcp-system\n          uri: lb://hcp-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: hcp-file\n          uri: lb://hcp-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 监控模块\n        - id: hcp-monitor\n          uri: lb://hcp-monitor\n          predicates:\n            - Path=/monitor/**\n          filters:\n            - StripPrefix=1\n        # 定时模块\n        - id: hcp-job\n          uri: lb://hcp-job\n          predicates:\n            - Path=/job/**\n          filters:\n            - StripPrefix=1\n        # 样例模块\n        - id: hcp-demo\n          uri: lb://hcp-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-operator\n          uri: lb://hcp-operator\n          predicates:\n            - Path=/operator/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-mp\n          uri: lb://hcp-mp\n          predicates:\n            - Path=/mp/**\n          filters:\n            - StripPrefix=1\n        - id: hcp-simulator\n          uri: lb://hcp-simulator\n          predicates:\n            - Path=/simulator/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      - /job/api/callback\n      - /job/api/registry\n      - /job/api/registryRemove\n      - /file/documents/**\n      - /hcp-mp/**\n      - /hcp-operator/websocket/charge/**', 'e646f92afbf82061ba851d1cc89ab84e', '2025-05-25 00:25:19', '2025-05-24 16:25:19', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 101, 'hcp-auth-dev.yml', 'DEFAULT_GROUP', '', 'spring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        #loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor', '80fd9f3f721e58eb7260e1ef8bcbd629', '2025-05-25 00:25:19', '2025-05-24 16:25:20', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 102, 'hcp-monitor-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    # jedis:\n    #   pool:\n    #     max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n    #     max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n    #     max-idle: 8 # 连接池中的最大  空闲连接\n    #     min-idle: 0 # 连接池中的最小空闲连接\n    lettuce:\n      pool:\n        max-active: 20\n        max-wait: 500ms\n        max-idle: 10\n        min-idle: 2\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n# seata配置\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:', '88df60b06ca60dc71f710f15b54cacdb', '2025-05-25 00:25:20', '2025-05-24 16:25:20', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 103, 'hcp-system-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2025-05-25 00:25:20', '2025-05-24 16:25:21', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 104, 'hcp-gen-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\n# 代码生成\ngen: \n  # 作者\n  author: hcp\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.hcp.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n', 'b58199518dbff233d55574f85ff8beab', '2025-05-25 00:25:20', '2025-05-24 16:25:21', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 105, 'hcp-file-dev.yml', 'DEFAULT_GROUP', '', '#本地文件服务的配置迁移到公共配置文件里\n# FastDFS配置 -- http://hcp-fastdfs 为hosts映射\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\n', '1c16b84aed9b9053e848c70f9346d899', '2025-05-25 00:25:21', '2025-05-24 16:25:21', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 106, 'sentinel-hcp-gateway', 'DEFAULT_GROUP', '', '[\n    {\n        \"resource\": \"hcp-auth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"hcp-system\",\n        \"count\": 1000,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"hcp-gen\",\n        \"count\": 200,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]', 'b109301f83310c0c1bf56bfab5f4859e', '2025-05-25 00:25:21', '2025-05-24 16:25:22', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 107, 'hcp-demo-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring: \n  redis:\n    host: localhost\n    port: 6379\n    #password: \n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/vctgo?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: Abdulla1992.\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\nxxl:\n  job:\n    admin:\n      ### xxl-job admin address list, such as \"http://address\" or \"http://address01,http://address02\"\n      addresses: http://localhost/dev-api/job\n    ### xxl-job, access token\n    accessToken: default_token\n    executor: \n      ### xxl-job executor appname\n      appname: xxl-job-executor-sample\n      ### xxl-job executor registry-address: default use address to registry , otherwise use ip:port if address is null\n      address: \n      ### xxl-job executor server-info\n      ip: 127.0.0.1\n      port: 9999\n      ### xxl-job executor log-path\n      logpath: ./logs/xxl-job\n      ### xxl-job executor log-retention-days\n      logretentiondays: 30', 'b0658556d29d38e0d3cf573d4e57eccf', '2025-05-25 00:25:22', '2025-05-24 16:25:22', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 108, 'hcp-job-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring: \n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root \n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource: \n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n  mail:\n    host: www.163.com\n    port: 25\n    username: xxxxxx@163.com\n    from: xxxxxx@163.com\n    password: xxxxxx\n    properties: \n      mail: \n        smtp: \n          auth: false\n          starttls: \n            enable: false\n            required: false\n          socketFactory: \n            class: javax.net.ssl.SSLSocketFactory\n          \n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.cn\n\nxxl:\n  job:\n    accessToken: default_token\n    i18n: zh_CN\n    logretentiondays: 30\n    triggerpool: \n      fast: \n        max: 200\n      slow: \n        max: 100\n\ncustom:\n  log:\n    list:\n      - scheduleJobQuery-error\n      - findFailJobLogIds-error\n      - findByAddressType-error\n\n\n', '078374f0a38fd306318aeb63886391cf', '2025-05-25 00:25:22', '2025-05-24 16:25:22', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 109, 'hcp-operator-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2025-05-25 00:25:22', '2025-05-24 16:25:23', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 110, 'hcp-mp-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\n', '7eaac6eba55f3155dfd8fb10aec9c540', '2025-05-25 00:25:23', '2025-05-24 16:25:23', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 111, 'hcp-simulator-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    database: 5\n    password: root\n    timeout: 1000 # 连接超时时间（毫秒）\n    jedis:\n      pool:\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）\n        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-idle: 8 # 连接池中的最大  空闲连接\n        min-idle: 0 # 连接池中的最小空闲连接\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 50\n        maxActive: 100\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 300000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 50\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/hcp_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\nseata:\n  # 默认关闭，如需启用spring.datasource.dynami.seata需要同时开启\n  enabled: false\n  # Seata 应用编号，默认为 ${spring.application.name}\n  application-id: ${spring.application.name}\n  # Seata 事务组编号，用于 TC 集群名\n  tx-service-group: ${spring.application.name}-group\n  # 关闭自动代理\n  enable-auto-data-source-proxy: false\n  # 服务配置项\n  service:\n    # 虚拟组和分组的映射\n    vgroup-mapping:\n      hcp-system-group: default\n  config:\n    type: nacos\n    nacos:\n      serverAddr: 127.0.0.1:8848\n      group: SEATA_GROUP\n      namespace:\n  registry:\n    type: nacos\n    nacos:\n      application: seata-server\n      server-addr: 127.0.0.1:8848\n      namespace:\n\n# mybatis配置\n# mybatis:\n#     搜索指定包别名\n#     typeAliasesPackage: com.hcp.system\n#     配置mapper的扫描，找到所有的mapper.xml映射文件\n#     mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hcp\n  licenseUrl: https://hcp.vip\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n        loggerLevel: full\n        requestInterceptors: com.hcp.common.security.feign.FeignRequestInterceptor\nchargeServer:\n  #测试配置项\n  address: http://wrt.fengzb.pp.ua:9250', '10f4c556935b6b99f074c390218e9427', '2025-05-25 00:25:23', '2025-05-24 16:25:24', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');
INSERT INTO `his_config_info` VALUES (0, 112, 'ruoyi-gateway.yml', 'DEFAULT_GROUP', '', '# 安全配置\nsecurity:\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/code\n      - /auth/logout\n      - /auth/login\n      - /auth/binding/*\n      - /auth/social/callback\n      - /auth/register\n      - /auth/tenant/list\n      - /resource/sms/code\n      - /*/v3/api-docs\n      - /*/error\n      - /csrf\n\nspring:\n  cloud:\n    # 网关配置\n    gateway:\n      # 打印请求日志(自定义)\n      requestLog: true\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: ruoyi-auth\n          uri: lb://ruoyi-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            - StripPrefix=1\n        # 代码生成\n        - id: ruoyi-gen\n          uri: lb://ruoyi-gen\n          predicates:\n            - Path=/tool/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: ruoyi-system\n          uri: lb://ruoyi-system\n          predicates:\n            - Path=/system/**,/monitor/**\n          filters:\n            - StripPrefix=1\n        # 资源服务\n        - id: ruoyi-resource\n          uri: lb://ruoyi-resource\n          predicates:\n            - Path=/resource/**\n          filters:\n            - StripPrefix=1\n        # 演示服务\n        - id: ruoyi-demo\n          uri: lb://ruoyi-demo\n          predicates:\n            - Path=/demo/**\n          filters:\n            - StripPrefix=1\n        # MQ演示服务\n        - id: ruoyi-stream-mq\n          uri: lb://ruoyi-stream-mq\n          predicates:\n            - Path=/stream-mq/**\n          filters:\n            - StripPrefix=1\n\n    # sentinel 配置\n    sentinel:\n      filter:\n        enabled: false\n      # nacos配置持久化\n      datasource:\n        ds1:\n          nacos:\n            server-addr: ${spring.cloud.nacos.server-addr}\n            dataId: sentinel-${spring.application.name}.json\n            groupId: ${spring.cloud.nacos.config.group}\n            namespace: ${spring.profiles.active}\n            data-type: json\n            rule-type: gw-flow\n', 'ce06342a0e77fe4c34925fcc46be40b5', '2025-05-25 00:25:24', '2025-05-24 16:25:24', 'nacos', '124.128.198.123', 'I', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', '');

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `action` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  UNIQUE INDEX `uk_role_permission`(`role`, `resource`, `action`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  UNIQUE INDEX `idx_user_role`(`username`, `role`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('nacos', 'ROLE_ADMIN');

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `gmt_modified` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '租户容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(0) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(0) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_info_kptenantid`(`kp`, `tenant_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'tenant_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_info
-- ----------------------------
INSERT INTO `tenant_info` VALUES (1, '1', 'hcp', 'hcp', 'hcp', 'nacos', 1723651120141, 1723651120141);
INSERT INTO `tenant_info` VALUES (2, '1', '70070f3a-b8c6-45f1-8056-a32a7defae98', 'yu', '余总测试', 'nacos', 1725515892210, 1725515892210);
INSERT INTO `tenant_info` VALUES (3, '1', 'b1abcbe9-3886-4726-8005-5e122e88ccb2', 'hcp_lwh', 'lwh', 'nacos', 1748103817679, 1748103817679);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('19ILXG', '$2a$10$kVqe0x9W7QBmsTLgQiosKufvdbAJrP20ytYjTLPg3zq6Qy.pF/wyS', 1);
INSERT INTO `users` VALUES ('2oRH5Zu5Re58fqwHUpzx5OlxPxA', '$2a$10$3Y9xCWHqSN2bgWzZ0No1BuNwvKCkHMBBNLLyNQjE5ryQuq68WFS8S', 1);
INSERT INTO `users` VALUES ('J5WV38', '$2a$10$UN58AaB4r3biQgHipOV1a.2utSXdzaX9nV7wZW/mfJV6pfiN80HpK', 1);
INSERT INTO `users` VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

SET FOREIGN_KEY_CHECKS = 1;
```

docker-compose.yaml：

```yaml
version: "3.0"

services:
  #-------------------------------- 配置MySQL作为持久化存储 --------------------------------
  nacos-server-db:
    image: mariadb:11.4.8
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_general_ci
      - --skip-character-set-client-handshake
    environment:
      - LANG=C.UTF-8
      - TZ=Asia/Shanghai
      - MYSQL_ROOT_PASSWORD=123456
    volumes:
      - ./db-nacos.sql:/docker-entrypoint-initdb.d/db-nacos.sql:ro
    network_mode: host
  nacos-server:
    image: nacos/nacos-server:v2.2.0
    environment:
      - TZ=Asia/Shanghai
      - MODE=standalone
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=localhost
      - MYSQL_SERVICE_DB_NAME=demo
      - MYSQL_SERVICE_PORT=3306
      - MYSQL_SERVICE_USER=root
      - MYSQL_SERVICE_PASSWORD=123456
      # 参考官方说明 https://hub.docker.com/r/nacos/nacos-server
      # 指定 jvm 内存为 256m
      - JVM_XMS=256m
      - JVM_XMX=256m
      - JVM_XMN=128m
    network_mode: host

```

### `Sentinel`

>SpringCloud Alibaba 配置 Sentinel 官方文档`https://spring-cloud-alibaba-group.github.io/github-pages/hoxton/zh-cn/index.html`



#### 介绍

Alibaba Sentinel是阿里巴巴开源的一款面向分布式微服务架构的轻量级高可用流量控制组件。以下是对Alibaba Sentinel的详细介绍：

一、诞生与发展

- 2012年，Sentinel诞生于阿里巴巴，其主要目标是流量控制。
- 2013~2017年，Sentinel迅速发展，并成为阿里巴巴所有微服务的基本组成部分。它已在6000多个应用程序中使用，涵盖了几乎所有核心电子商务场景。
- 2018年，Sentinel演变为一个开源项目。
- 2020年，Sentinel Golang发布。

二、主要功能

Sentinel以流量为切入点，从多个维度帮助用户保护服务的稳定性，其主要功能包括：

- **流量控制**：Sentinel可以监控和控制服务的流量，防止突发流量导致系统崩溃。它支持多种流控模式，如直接流控、关联流控、链路流控等，可以根据实际场景灵活选择。
- **熔断降级**：当调用链路中的某个资源出现不稳定状态时（如调用超时或异常比例升高），Sentinel可以对该资源的调用进行限制，让请求快速失败，避免影响到其它资源而导致级联错误。
- **系统负载保护**：Sentinel可以监控系统的负载情况，当系统负载过高时，自动触发保护策略，防止系统崩溃。

三、应用场景

Sentinel承接了阿里巴巴近10年的双十一大促流量的核心场景，如秒杀（即突发流量控制在系统容量可以承受的范围）、消息削峰填谷、集群流量控制、实时熔断下游不可用应用等。

四、主要特性

- **丰富的实时监控**：Sentinel提供实时的监控功能，可以在控制台中看到接入应用的单台机器秒级数据，甚至500台以下规模的集群的汇总运行情况。
- **广泛的开源生态**：Sentinel提供开箱即用的与其它开源框架/库的整合模块，例如与Spring Cloud、Apache Dubbo、gRPC、Quarkus等的整合。用户只需要引入相应的依赖并进行简单的配置即可快速地接入Sentinel。
- **完善的SPI扩展机制**：Sentinel提供简单易用、完善的SPI扩展接口，用户可以通过实现扩展接口来快速地定制逻辑，如定制规则管理、适配动态数据源等。

五、核心组件

- **核心库（Java客户端）**：不依赖任何框架/库，能够运行于所有Java运行时环境，同时对Dubbo/Spring Cloud等框架也有较好的支持。
- **控制台（Dashboard）**：基于Spring Boot开发，打包后可以直接运行，不需要额外的Tomcat等应用容器。控制台主要负责管理推送规则、监控、集群限流分配管理、机器发现等。

六、使用与配置

- 用户可以从Sentinel的[官方GitHub页面](https://github.com/alibaba/Sentinel/releases)下载最新版本的控制台jar包，或者使用源码构建。
- 启动控制台后，用户可以通过浏览器访问控制台页面，并使用默认的用户名和密码（均为sentinel，从1.6.0版本起支持自定义）进行登录。
- 在项目中引入Sentinel，需要在项目的pom文件中添加相应的依赖，并在配置文件中进行简单的配置。
- 用户可以通过Sentinel提供的API来定义一个资源，使其能够被Sentinel保护起来。通常情况下，可以使用方法名、URL甚至是服务名来作为资源名来描述某个资源。
- Sentinel支持多种规则配置，如流量控制规则、熔断降级规则等，这些规则都可以在控制台中进行动态调整。

总的来说，Alibaba Sentinel是一款功能强大、易于使用且生态广泛的微服务流量控制组件，它能够帮助用户有效地保护服务的稳定性并提高系统的可用性。



#### `Docker` 运行 `Sentinel`

>注意：Sentinel 没有官方的 Docker 镜像，目前实验使用的非官方镜像为 bladex/sentinel-dashboard:1.7.0，事实上可以自己编译 Sentinel Docker 镜像。

docker-compose.yaml 配置如下：

```yaml
version: "3.0"

services:
  demo-spring-cloud-sentinel:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/future-sentinel-dashboard:1.8.0
    environment:
      - TZ=Asia/Shanghai
      # 指定 jvm 内存为 256m
      - JAVA_OPTS=-Xmx256m
    ports:
      - '8858:8858'
```

启动 Sentinel 服务

```bash
docker compose up -d
```

访问 Sentinel 控制台`http://localhost:8858/`，帐号：sentinel，密码：sentinel



#### 和 `SpringCloud Alibaba` 集成

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-sentinel)

父 `POM` 配置：

```xml
<dependencyManagement>
    <dependencies>
       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.2.7.RELEASE</version>
          <type>pom</type>
          <scope>import</scope>
       </dependency>
       <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-dependencies</artifactId>
          <version>Hoxton.SR10</version>
          <type>pom</type>
          <scope>import</scope>
       </dependency>
       <!-- SpringCloud Alibaba 依赖 -->
       <dependency>
          <groupId>com.alibaba.cloud</groupId>
          <artifactId>spring-cloud-alibaba-dependencies</artifactId>
          <version>2.2.9.RELEASE</version>
          <type>pom</type>
          <scope>import</scope>
       </dependency>
    </dependencies>
</dependencyManagement>
```

各个微服务 `POM` `Sentinel` 依赖配置

```xml
<!-- sentinel依赖配置 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<!-- Sentinel 持久化配置到 Nacos 依赖 -->
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
</dependency>
```

各个微服务 `application.properties` 配置

```properties
# 应用的名称会显示在 sentinel dashboard 中
spring.application.name=xxx
# sentinel配置
spring.cloud.sentinel.transport.dashboard=localhost:8858

# Sentinel 持久化配置到 Nacos 配置
spring.cloud.sentinel.datasource.ds1.nacos.server-addr=localhost:8848
spring.cloud.sentinel.datasource.ds1.nacos.group-id=DEFAULT_GROUP
spring.cloud.sentinel.datasource.ds1.nacos.data-id=${spring.application.name}
spring.cloud.sentinel.datasource.ds1.nacos..data-type=json
# flow：流控规则。这是 Sentinel 最核心、最直观的一种规则，主要用于控制请求的 QPS（每秒查询率）或并发线程数，以防止系统被过大的流量压垮。
# degrade：熔断规则。当系统的某个资源不稳定或出现故障时，为了防止故障的进一步扩散，可以使用熔断规则来快速失败这个资源的请求。熔断规则通常基于一些条件（如慢调用比例、异常比例或异常数）来触发。
# param-flow：热点规则。热点规则用于对某个资源中的某个或某些参数进行单独的流控。这可以帮助系统保护那些因为某些特殊参数值而导致的高并发请求。
# system：系统规则。系统规则是从系统的整体角度出发，对系统的入口流量、CPU 使用率、线程数等指标进行整体控制，以防止系统整体过载。
# authority：授权规则。授权规则用于对资源的访问进行黑白名单控制。这可以帮助系统实现细粒度的访问控制。
spring.cloud.sentinel.datasource.ds1.nacos.rule-type=flow
```

运行示例：

- 启动 `Sentinel` 和 `Nacos` 服务

  ```
  docker compose up -d
  ```

- 启动 `ApplicationGateway` 应用

- 使用 `api.http` 访问 `http://localhost:8080/api/v1/test3` 产生 `Sentinel` 测试数据

- 访问`http://localhost:8858/` `Sentinel` 控制台查看测试数据

- 访问 `http://localhost:8848` `Nacos` 控制台调整 `Sentinel` 规则。

- 可以针对特定的接口在 `Nacos` 中创建特定的规则。



#### 和 `SpringBoot` 集成

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-boot-sentinel)

使用 `Docker Compose` 运行 `Sentinel Dashboard`：

```yaml
version: "3.1"

services:
  sentinel-dashboard:
    image: bladex/sentinel-dashboard:1.8.0
    environment:
      - TZ=Asia/Shanghai
#    ports:
#      - '8858:8858'
    network_mode: host

  nacos-server:
    image: nacos/nacos-server:v2.2.0
    environment:
      - TZ=Asia/Shanghai
      - MODE=standalone
    ports:
      - '8848:8848'
      - '9848:9848'
      #- '9849:9849'

```

`POM` 添加配置：

```xml
<!-- sentinel依赖配置 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    <!-- SpringCloud Alibaba Sentinel 版本需要和 SpringBoot 版本兼容，否则 SpringBoot 应用启动时报错 -->
    <version>2021.0.6.2</version>
</dependency>

<!-- Sentinel 持久化配置到 Nacos 依赖 -->
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
    <version>1.8.6</version>
</dependency>
```

- `SpringBoot 2.7.18` 和 `spring-cloud-starter-alibaba-sentinel 2021.0.6.2` 兼容，查看更多 `SpringBoot` 和 `SpringCloud Alibaba` 版本兼容性请参考本站 <a href="/springcloud/README.html#springcloud、springcloud-alibaba-和-springboot-兼容性" target="_blank">链接</a>

`application.properties` 配置：

```properties
# 应用的名称会显示在 sentinel dashboard 中
# 如果不配置，则显示 SpringBoot 启动类的完整路径，例如：com.future.demo.ApplicationService
spring.application.name=demo-spring-boot-sentinel
# sentinel配置
spring.cloud.sentinel.transport.dashboard=localhost:8858
# 当spring.cloud.sentinel.web-context-unify设置为false时，Sentinel会区分每一个具体的URL路径，
# 每个不同的URL都会被视为一个独立的资源。这提供了更细粒度的控制，允许开发者为每一个具体的URL路径定义不同的流控、熔断等规则。
spring.cloud.sentinel.web-context-unify=false

# Sentinel 持久化配置到 Nacos 配置
spring.cloud.sentinel.datasource.ds1.nacos.server-addr=localhost:8848
spring.cloud.sentinel.datasource.ds1.nacos.group-id=DEFAULT_GROUP
spring.cloud.sentinel.datasource.ds1.nacos.data-id=${spring.application.name}
spring.cloud.sentinel.datasource.ds1.nacos..data-type=json
# flow：流控规则。这是 Sentinel 最核心、最直观的一种规则，主要用于控制请求的 QPS（每秒查询率）或并发线程数，以防止系统被过大的流量压垮。
# degrade：熔断规则。当系统的某个资源不稳定或出现故障时，为了防止故障的进一步扩散，可以使用熔断规则来快速失败这个资源的请求。熔断规则通常基于一些条件（如慢调用比例、异常比例或异常数）来触发。
# param-flow：热点规则。热点规则用于对某个资源中的某个或某些参数进行单独的流控。这可以帮助系统保护那些因为某些特殊参数值而导致的高并发请求。
# system：系统规则。系统规则是从系统的整体角度出发，对系统的入口流量、CPU 使用率、线程数等指标进行整体控制，以防止系统整体过载。
# authority：授权规则。授权规则用于对资源的访问进行黑白名单控制。这可以帮助系统实现细粒度的访问控制。
spring.cloud.sentinel.datasource.ds1.nacos.rule-type=flow
```

启动应用请求接口后，`Sentinel Dashboard` 的簇点链路才会显示请求路径数据。

访问 `Sentinel` 控制台`http://localhost:8858/`，帐号：`sentinel`，密码：`sentinel`

访问 `Nacos` 控制台`http://localhost:8848/`，帐号：`nacos`，密码：`nacos`

可以针对特定的接口在 `Nacos` 中创建特定的规则。



#### 流控规则

##### 介绍

Sentinel是一个开源的系统保护和流量控制组件，主要设计用于保护微服务架构中的服务。它提供了丰富的流量控制和熔断机制，通过流量控制规则来限制系统的访问频率和并发量，从而确保服务的稳定性和可用性。以下是对Sentinel流控规则的详细介绍：

一、流控规则的基本属性

流控规则主要由以下几个关键属性组成：

1. **资源名**：唯一名称，默认请求路径，是限流规则的作用对象。
2. **针对来源**：Sentinel可以针对调用者进行限流，填写微服务名，默认为default（不区分来源）。
3. **阈值类型/单机阈值**：包括QPS（每秒钟的请求数量）和并发线程数。当调用该API的QPS或并发线程数达到阈值时，会触发限流。
4. **流控模式**：
   - **直接**：API达到限流条件时，直接限流。
   - **关联**：当关联的资源达到阈值时，限流自己。
   - **链路**：只记录指定链路上的流量，指定资源从入口资源进来的流量如果达到阈值，则进行限流。这类似于针对来源的配置项，但区别在于链路流控是针对上级接口，粒度更细。
5. **流控效果**：
   - **快速失败**：直接失败，抛异常。
   - **Warm Up**：根据冷加载因子（codeFactor，默认3）的值，从阈值/codeFactor开始，经过预热时长后，逐渐达到设置的QPS阈值。这种方式适用于流量突然增加时，给系统一个预热的时间，避免系统被压垮。
   - **排队等待**：匀速排队，让请求以匀速的速度通过。这种方式适用于处理间隔性突发的流量，例如消息队列。但需要注意的是，匀速排队模式暂时不支持QPS大于1000的场景。阈值类型必须设置为QPS，否则无效。

二、流控规则的具体应用

1. **QPS限流**：通过设定QPS阈值来控制接口的访问频率。例如，单机阈值设定为1，表示当前这个接口1秒只能被访问一次，超过这个阈值就会被Sentinel阻塞。
2. **并发线程数限流**：通过设定并发线程数阈值来控制同时访问接口的线程数量。例如，阈值设置为5，表示同时进入到此方法的线程最多有5个，超过5个的线程都会被拒绝。
3. **关联流控**：当A接口关联的资源B达到阈值后，就限流A接口本身。这种方式适用于需要保护关键资源或接口的场景。
4. **链路流控**：当从某个接口过来的资源达到限流条件时，开启限流。这种方式适用于需要从整体上控制应用入口流量的场景。
5. **Warm Up流控**：通过冷启动方式，让流量缓慢增加，在一定时间内逐渐增加到阈值上限。这种方式适用于防止秒杀瞬间造成系统崩溃的场景。
6. **匀速排队流控**：严格控制请求通过的间隔时间，让请求以均匀的速度通过。这种方式适用于处理间隔性突发的流量场景。

三、流控规则的配置方式

流控规则可以通过Sentinel控制台进行配置和管理。开发者可以根据实际需求，灵活地组合使用不同的流控规则和效果，以实现更加复杂的流量控制策略。

综上所述，Sentinel的流控规则为微服务架构中的服务提供了强大的流量控制和保护能力。通过合理配置流控规则，可以有效地避免系统被突发的流量高峰冲垮，从而保障应用的高可用性和稳定性。



##### 流控模式 - 直接

API 达到限流条件时，直接限流。

访问`http://localhost:8858/#/dashboard/flow/demo-springcloud-gateway`新增流控规则，流控规则信息如下：

- 资源名为`/api/v1/test1`
- 针对来源为`default`
- 阈值类型为`QPS`
- 单机阈值为`1`
- 是否集群为`不勾选`
- 流控模式为`直接`
- 流控效果为`快速失败`

点击`新增`按钮新增流控规则，1 秒内连续访问`http://localhost:8080/api/v1/test1`会报告`Blocked by Sentinel: FlowException`错误。



##### 流控模式 - 关联

当关联的资源达到阈值时，限流自己。

访问`http://localhost:8858/#/dashboard/flow/demo-springcloud-gateway`新增流控规则，流控规则信息如下：

- 资源名为`/api/v1/test1`
- 针对来源为`default`
- 阈值类型为`QPS`
- 单机阈值为`1`
- 是否集群为`不勾选`
- 流控模式为`关联`
- 关联资源为`/api/v1/test2`
- 流控效果为`快速失败`

点击`新增`按钮新增流控规则（表示资源`/api/v1/test2`达到阈值时资源`/api/v1/test1`限流）。

启动 JMeter 给资源`/api/v1/test2`压力，此时请求资源`/api/v1/test1`会被限流并报告`Blocked by Sentinel: FlowException`错误。



##### 流控模式 - 链路

只记录指定链路上的流量，指定资源从入口资源进来的流量如果达到阈值，则进行限流。这类似于针对来源的配置项，但区别在于链路流控是针对上级接口，粒度更细。

application.properties 添加如下配置：

```properties
# 当spring.cloud.sentinel.web-context-unify设置为false时，Sentinel会区分每一个具体的URL路径，
# 每个不同的URL都会被视为一个独立的资源。这提供了更细粒度的控制，允许开发者为每一个具体的URL路径定义不同的流控、熔断等规则。
spring.cloud.sentinel.web-context-unify=false
```

通过资源`/api/v1/test1`和`/api/v2/test2`调用资源`common1`

```java
@RestController
@RequestMapping("/api/v1")
public class ApiController {
    @Resource
    CommonService commonService;

    @GetMapping(value = "test1")
    public ResponseEntity<String> test1() {
        this.commonService.test1();
        return ResponseEntity.ok("/api/v1/test1 " + UUID.randomUUID());
    }

    @GetMapping(value = "test2")
    public ResponseEntity<String> test2() {
        this.commonService.test1();
        return ResponseEntity.ok("/api/v1/test2 " + UUID.randomUUID());
    }
}
```

```java
@Service
public class CommonService {
    @SentinelResource(value="common1")
    public void test1() {

    }
}
```

访问`http://localhost:8858/#/dashboard/flow/demo-springcloud-gateway`新增流控规则，流控规则信息如下：

- 资源名为`common1`
- 针对来源为`default`
- 阈值类型为`QPS`
- 单机阈值为`1`
- 是否集群为`不勾选`
- 流控模式为`链路`
- 入口资源为`/api/v1/test1`
- 流控效果为`快速失败`

点击`新增`按钮新增流控规则（表示通过资源`/api/v1/test1`调用资源`common1`限流，通过资源`/api/v1/test2`调用资源`common1`不限流）。

访问`http://localhost:8080/api/v1/test1`限流，访问`http://localhost:8080/api/v1/test2`不限流。



##### 流控效果 - 快速失败

直接失败，抛 FlowException 异常。

访问`http://localhost:8858/#/dashboard/flow/demo-springcloud-gateway`新增流控规则，流控规则信息如下：

- 资源名为`/api/v1/test1`
- 针对来源为`default`
- 阈值类型为`QPS`
- 单机阈值为`1`
- 是否集群为`不勾选`
- 流量模式为`直接`
- 流控效果为`快速失败`

点击`新增`按钮新增流控规则。

访问`http://localhost:8080/api/v1/test1`限流报告`Blocked by Sentinel (flow limiting)`错误。



##### 流控效果 - Warm Up

根据冷加载因子（codeFactor，默认3）的值，从阈值/codeFactor开始，经过预热时长后，逐渐达到设置的QPS阈值。这种方式适用于流量突然增加时，给系统一个预热的时间，避免系统被压垮。

访问`http://localhost:8858/#/dashboard/flow/demo-springcloud-gateway`新增流控规则，流控规则信息如下：

- 资源名为`/api/v1/test1`
- 针对来源为`default`
- 阈值类型为`QPS`
- 单机阈值为`10`
- 是否集群为`不勾选`
- 流量模式为`直接`
- 流控效果为`Warm Up`
- 预热时长为`10`秒

点击`新增`按钮新增流控规则。

访问`http://localhost:8080/api/v1/test1`，前 10 秒会单机阈值为 10/3 = 3.3 QPS（连续请求 /api/v1/test1 会报告限流错误），10 秒后单机阈值恢复为 10 QPS（连续请求 /api/v1/test1 不会报告限流错误，因为阈值恢复为 10 QPS）。



##### 流控效果 - 排队等待

匀速排队，让请求以匀速的速度通过。这种方式适用于处理间隔性突发的流量，例如消息队列。但需要注意的是，匀速排队模式暂时不支持QPS大于1000的场景。阈值类型必须设置为QPS，否则无效。

访问`http://localhost:8858/#/dashboard/flow/demo-springcloud-gateway`新增流控规则，流控规则信息如下：

- 资源名为`/api/v1/test1`
- 针对来源为`default`
- 阈值类型为`QPS`
- 单机阈值为`1`
- 是否集群为`不勾选`
- 流量模式为`直接`
- 流控效果为`排队等待`
- 超时时间为`10000`毫秒

点击`新增`按钮新增流控规则。

使用 JMeter 创建线程数为 20，0 秒启动完毕，循环 1 次，请求`http://localhost:8080/api/v1/test1`。有 11 个请求以 1 QPS 速率被处理，9 个请求被拒绝（因为超时时间为`10000`毫秒，阈值为`1 QPS`）。



##### 阈值类型 - 线程数

通过设定并发线程数阈值来控制同时访问接口的线程数量。例如，阈值设置为5，表示同时进入到此方法的线程最多有5个，超过5个的线程都会被拒绝。

访问`http://localhost:8858/#/dashboard/flow/demo-springcloud-gateway`新增流控规则，流控规则信息如下：

- 资源名为`/api/v1/test1`
- 针对来源为`default`
- 阈值类型为`线程数`
- 单机阈值为`1`
- 是否集群为`不勾选`
- 流量模式为`直接`

点击`新增`按钮新增流控规则。

使用 JMeter 创建线程数为 20，0 秒启动完毕，无限循环，请求`http://localhost:8080/api/v1/test1`。在 JMeter 压力测试过程中访问`http://localhost:8080/api/v1/test1`因为没有足够的线程处理请求所以会被限流。



#### 熔断规则

##### 介绍

Sentinel是一款面向分布式服务架构的轻量级流量控制组件，主要通过流量控制、熔断降级和系统自适应保护等手段，确保服务的稳定性和可用性。在Sentinel中，熔断机制是一种重要的保护策略，它类似于电路中的熔断器，能够在服务出现故障或负载过高时切断请求，从而防止系统崩溃。以下是关于Sentinel熔断规则的详细介绍：

一、熔断机制的工作原理

熔断机制的工作原理通常包括以下几个步骤：

1. **监控服务**：持续监控服务的健康状态和负载情况。
2. **触发熔断**：当服务出现故障（如响应时间过长、异常比例过高等）或负载过高时，触发熔断机制。
3. **熔断动作**：执行熔断动作，例如拒绝新的请求、调用降级策略等，以保护系统免受进一步影响。
4. **恢复服务**：在一段时间后（即熔断时长结束后），尝试恢复服务，并根据恢复情况决定是否继续熔断。此时服务会进入半开状态（HALF-OPEN状态），允许一定量的请求通过以测试服务是否已恢复。如果请求成功，则关闭熔断器；如果请求失败，则再次触发熔断。

二、常见的熔断规则类型

Sentinel提供了多种熔断规则类型，以适应不同的业务场景和需求。以下是几种常见的熔断规则：

1. **慢调用比例（SLOW_REQUEST_RATIO）**：
   - **定义**：选择以慢调用比例作为阈值，需要设置允许的慢调用RT（即最大的响应时间）。请求的响应时间大于该值则统计为慢调用。
   - **触发条件**：当单位统计时长内请求数目大于设置的最小请求数目，并且慢调用的比例大于阈值时，触发熔断。
   - **恢复条件**：经过熔断时长后，服务进入半开状态。若接下来的一个请求响应时间小于设置的慢调用RT，则结束熔断；否则，会再次被熔断。
2. **异常比例（ERROR_RATIO）**：
   - **定义**：当单位统计时长内请求数目大于设置的最小请求数目，并且异常的比例大于阈值时，触发熔断。
   - **触发条件**：异常比率的阈值范围是[0.0, 1.0]，代表0%至100%。
   - **恢复条件**：经过熔断时长后，服务进入半开状态。若接下来的一个请求成功完成（没有错误），则结束熔断；否则，会再次被熔断。
3. **异常数（ERROR_COUNT）**：
   - **定义**：当单位统计时长内的异常数目超过阈值时，触发熔断。
   - **触发条件**：无需考虑请求数目和异常比例，只需异常数目超过设定阈值。
   - **恢复条件**：经过熔断时长后，服务进入半开状态。若接下来的一个请求成功完成（没有错误），则结束熔断；否则，会再次被熔断。

三、熔断规则的配置与应用

在Sentinel中，熔断规则可以通过配置文件或动态配置中心进行设置。配置完成后，Sentinel将自动根据这些规则对流量进行控制和降级操作。同时，Sentinel还提供了实时监控功能，可以实时查看系统的运行状态和各项指标，便于问题排查和规则优化。

此外，为了获得最佳效果，用户需要根据实际业务场景和系统特点进行合理的规则配置和优化工作。例如，可以根据服务的响应时间、异常比例、请求量等指标来设置合适的熔断阈值，以确保服务在高并发和故障场景下仍能保持良好的运行状态。

综上所述，Sentinel的熔断规则是保护分布式服务架构稳定性和可用性的重要手段。通过合理配置和应用这些规则，用户可以有效地防止系统因流量过大或故障而崩溃，从而提高服务的稳定性和可用性。



##### 慢调用比例

访问`http://localhost:8858/#/dashboard/degrade/demo-springcloud-gateway`新增规则，规则信息如下：

- 资源名为`/api/v1/test1`
- 熔断策略为`慢调用比例`
- 最大 RT 为`1000`毫秒（超过 1 秒的接口被认为慢调用）
- 比例阈值为`0.5`（慢调用比例超过 50% 触发熔断）
- 熔断时长为`10`秒（熔断持续时间为 10 秒，之后自动切换为半开状态）
- 最小请求数`5`（最小请求数为 5 次才触发熔断机制）
- 统计时长`2000`毫秒（统计时间窗口为 2 秒）

使用 JMeter 创建线程数为 10，0 秒启动完毕，无限循环，请求`http://localhost:8080/api/v1/test1?sleepInSeconds=3`。在 JMeter 压力测试过程中访问`http://localhost:8080/api/v1/test1`熔断报告错误。



##### 异常比例

访问`http://localhost:8858/#/dashboard/degrade/demo-springcloud-gateway`新增规则，规则信息如下：

- 资源名为`/api/v1/test2`
- 熔断策略为`异常比例`
- 比例阈值为`0.5`（比例超过 50% 触发熔断）
- 熔断时长为`10`秒（熔断持续时间为 10 秒，之后自动切换为半开状态）
- 最小请求数`5`（最小请求数为 5 次才触发熔断机制）
- 统计时长`2000`毫秒（统计时间窗口为 2 秒）

使用 JMeter 创建线程数为 10，0 秒启动完毕，无限循环，请求`http://localhost:8080/api/v1/test2?flag=exception`。在 JMeter 压力测试过程中访问`http://localhost:8080/api/v1/test2`熔断报告错误。



##### 异常数

访问`http://localhost:8858/#/dashboard/degrade/demo-springcloud-gateway`新增规则，规则信息如下：

- 资源名为`/api/v1/test2`
- 熔断策略为`异常数`
- 异常数`3`（异常数量超过 3 触发熔断）
- 熔断时长为`10`秒（熔断持续时间为 10 秒，之后自动切换为半开状态）
- 最小请求数`5`（最小请求数为 5 次才触发熔断机制）
- 统计时长`2000`毫秒（统计时间窗口为 2 秒）

使用 JMeter 创建线程数为 10，0 秒启动完毕，无限循环，请求`http://localhost:8080/api/v1/test2?flag=exception`。在 JMeter 压力测试过程中访问`http://localhost:8080/api/v1/test2`熔断报告错误。



#### @SentinelResource 注解



##### 介绍

`@SentinelResource` 是 Alibaba Sentinel 提供的一个注解，用于定义资源，并可以与 Sentinel 的流控、降级等规则进行关联。通过该注解，开发者可以非常方便地在代码中对某些方法进行保护，防止在高并发或异常情况下导致系统崩溃。

以下是 `@SentinelResource` 注解的一些关键属性和用法：

**关键属性**

1. **value**：资源的唯一标识名称。这个名称用于在 Sentinel 控制台配置规则。如果不指定，则默认为方法的全限定名（类名+方法名）。
2. **entryType**：指定资源的入口类型。可选值有 `EntryType.IN`（表示普通入口，默认值）和 `EntryType.OUT`（表示异步出口）。
3. **blockHandler**：指定处理被限流或被降级逻辑的处理器方法名。该方法必须与原方法在同一个类中，且返回类型、方法参数列表（除了第一个参数类型为 `BlockException`）需要与原方法一致。
4. **fallback**：指定默认的降级返回值。当方法被限流、降级、系统异常时，会直接返回该值。注意，`fallback` 和 `blockHandler` 是互斥的，不能同时使用。
5. **defaultFallback**：指定类级别的默认降级返回值。当方法被限流、降级、系统异常时，如果该方法没有定义 `fallback`，则会使用这个类级别的默认降级值。`defaultFallback` 需要定义在类上。
6. **exceptionsToIgnore**：指定哪些异常类型会被忽略，不进行降级处理。

**使用示例**

基本使用

```java
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
 
public class MyService {
 
    @SentinelResource(value = "myMethod", fallback = "defaultFallback")
    public String myMethod() {
        // 业务逻辑
        return "Hello, Sentinel!";
    }
 
    public String defaultFallback() {
        return "Fallback response for myMethod";
    }
}
```

在这个例子中，如果 `myMethod` 被限流或降级，将会调用 `defaultFallback` 方法。

使用 blockHandler

```java
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
 
public class MyService {
 
    @SentinelResource(value = "myMethod", blockHandler = "handleBlock")
    public String myMethod() {
        // 业务逻辑
        return "Hello, Sentinel!";
    }
 
    public String handleBlock(BlockException ex) {
        // 处理被限流的逻辑
        return "Blocked by Sentinel: " + ex.getMessage();
    }
}
```

在这个例子中，如果 `myMethod` 被限流，将会调用 `handleBlock` 方法来处理限流逻辑。

**注意事项**

- `blockHandler` 和 `fallback` 方法的参数列表必须与原方法一致（除了 `blockHandler` 方法第一个参数是 `BlockException` 类型）。
- 如果使用了 `blockHandler` 或 `fallback` 方法，需要确保这些方法在同一个类中定义。
- `SentinelResource` 注解主要用于对单个方法的保护，对于更复杂的场景，可以考虑使用 Sentinel 的编程 API 来定义资源。

通过使用 `@SentinelResource` 注解，开发者可以非常方便地在代码中集成 Sentinel 的流量控制和降级功能，提高系统的稳定性和可用性。



##### 自定义资源名称限流

```java
@GetMapping(value = "test3")
@SentinelResource(value = "test3")
public ResponseEntity<String> test3() {
    return ResponseEntity.ok("/api/v1/test3 " + UUID.randomUUID());
}
```

流控规则信息：

- 资源名为`test3`
- 针对来源为`default`
- 阈值类型为`QPS`
- 单机阈值为`1`
- 是否集群为`不勾选`
- 流控模式为`直接`
- 流控效果为`快速失败`

请求`http://localhost:8080/api/v1/test3`测试自定义资源名称限流效果。



##### 自定义限流返回 blockHandler

`blockHandler` 是在流量控制触发时调用

```java
@GetMapping(value = "test3")
@SentinelResource(value = "test3", blockHandler = "blockHandler")
public ObjectResponse<String> test3() {
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("/api/v1/test3 " + UUID.randomUUID());
    return response;
}

public ObjectResponse<String> blockHandler(BlockException ex) {
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("被限流降级了");
    return response;
}
```

流控规则信息：

- 资源名为`test3`
- 针对来源为`default`
- 阈值类型为`QPS`
- 单机阈值为`1`
- 是否集群为`不勾选`
- 流控模式为`直接`
- 流控效果为`快速失败`

请求`http://localhost:8080/api/v1/test3`测试效果。



##### 自定义服务降级 fallback

`fallback` 是在熔断降级触发时调用

```java
@GetMapping(value = "test3")
@SentinelResource(value = "test3", fallback = "fallback")
public ObjectResponse<String> test3(@RequestParam(value = "flag", defaultValue = "") String flag) {
    if ("exception".equals(flag)) {
        throw new RuntimeException("预期异常");
    }
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("/api/v1/test3 " + UUID.randomUUID());
    return response;
}

public ObjectResponse<String> fallback(@RequestParam(value = "flag", defaultValue = "") String flag, Throwable ex) {
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("服务降级了");
    return response;
}
```

熔断规则信息：

- 资源名为`test3`
- 熔断策略为`异常比例`
- 比例阈值为`0.5`
- 熔断时长为`10`秒
- 最小请求数为`5`
- 统计时长为`1000`毫秒

使用 JMeter 创建线程数为 10，0 秒启动完毕，无限循环，请求`http://localhost:8080/api/v1/test3?flag=exception`。在 JMeter 压力测试过程中访问`http://localhost:8080/api/v1/test3`熔断报告错误。



##### blockHandler 和 fallback 区别

在微服务架构中，Sentinel 是一个非常流行的开源流量控制、熔断降级组件，由阿里巴巴开源。它主要用于服务的稳定性保障，确保在高并发、系统不稳定等场景下，服务不会崩溃，并能够在必要时进行降级处理。在 Sentinel 中，`@SentinelResource` 注解是一个关键的工具，用于对方法调用进行流量控制和熔断降级处理。

`@SentinelResource` 注解提供了两个重要的属性：`blockHandler` 和 `fallback`，它们用于定义在特定条件下调用的降级逻辑。

**`blockHandler`**

`blockHandler` 用于处理流量控制（如 QPS 超出限制）的情况。当方法的调用被 Sentinel 流量控制规则拦截时，会调用 `blockHandler` 指定的方法。

- **使用方式**：`blockHandler` 属性值应该是一个方法名，该方法需要在同一个类中定义，并且方法签名要与原方法一致，或者多一个 `BlockException` 类型的参数。
- **返回值**：`blockHandler` 方法的返回值类型必须与原方法一致。

```java
@SentinelResource(value = "exampleMethod", blockHandler = "blockHandlerMethod")
public String exampleMethod() {
    // 原始业务逻辑
    return "Hello, Sentinel!";
}
 
public String blockHandlerMethod(BlockException ex) {
    // 降级处理逻辑
    return "Blocked by Sentinel!";
}
```

**`fallback`**

`fallback` 用于处理熔断降级的情况，比如服务调用失败、异常抛出等。当方法的调用被 Sentinel 熔断规则拦截时，会调用 `fallback` 指定的方法。

- **使用方式**：`fallback` 属性值也应该是一个方法名，该方法需要在同一个类中定义，并且方法签名要与原方法一致，或者多一个 `Throwable` 类型的参数（用于接收原始方法抛出的异常）。
- **返回值**：`fallback` 方法的返回值类型必须与原方法一致。

```java
@SentinelResource(value = "exampleMethod", fallback = "fallbackMethod")
public String exampleMethod() {
    // 原始业务逻辑
    return "Hello, Sentinel!";
}
 
public String fallbackMethod(Throwable ex) {
    // 降级处理逻辑
    return "Fallback by Sentinel!";
}
```

**区别与选择**

- **触发条件**：`blockHandler` 是在流量控制触发时调用，而 `fallback` 是在熔断降级触发时调用。
- **参数**：`blockHandler` 方法多一个 `BlockException` 参数，`fallback` 方法多一个 `Throwable` 参数。
- **使用场景**：根据实际需求选择使用哪个属性。如果需要处理流量控制的情况，使用 `blockHandler`；如果需要处理服务异常或熔断的情况，使用 `fallback`。

**注意事项**

- `blockHandler` 和 `fallback` 方法必须定义在同一个类中，并且不能是静态方法。
- 方法签名需要匹配或增加一个特定的异常参数。
- 返回值类型必须与原方法一致。

通过合理使用 `@SentinelResource` 注解的 `blockHandler` 和 `fallback` 属性，可以大大提高微服务系统的稳定性和可靠性。



##### `exceptionsToIgnore`

>`todo` 没有成功实验演示其作用。

在 Sentinel 的 `@SentinelResource` 注解中，`exceptionsToIgnore` 属性的作用是**指定需要被忽略的异常类型**。当被 `@SentinelResource` 标记的方法在执行过程中抛出这些指定类型的异常时，Sentinel 不会对这些异常进行额外处理（例如不会触发限流、降级的回调逻辑），而是直接让异常按照原本的路径向上传播，由上层调用者处理。

**具体场景说明：**

假设你通过 `@SentinelResource` 定义了一个资源（如接口方法），并配置了限流、降级等规则。当方法执行时：
- 如果抛出 **Sentinel 内置的 `BlockException`**（例如因流量超过阈值被限流、因服务降级被触发），Sentinel 会默认调用 `blockHandler` 方法处理该异常。
- 如果抛出 **其他未被忽略的异常**（如业务逻辑中的 `RuntimeException`），Sentinel 可能会根据降级规则触发 `fallback` 方法（如果配置了的话）。
- **但如果抛出的异常类型被明确配置在 `exceptionsToIgnore` 中**（例如 `BusinessException`），Sentinel 会直接忽略这些异常，不会触发任何限流/降级的处理逻辑，异常会继续向上抛出，由业务代码自己处理。

**示例：**

```java
@SentinelResource(
    value = "createOrderFlashSale",
    blockHandler = "createFlashSaleBlockHandler", // 处理 BlockException（限流/降级）
    fallback = "createFlashSaleFallback",         // 处理其他异常（降级）
    exceptionsToIgnore = {BusinessException.class} // 忽略 BusinessException
)
public Order createOrderFlashSale(User user, Product product) {
    // 业务逻辑
    if (product.getStock() <= 0) {
        throw new BusinessException("商品已售罄"); // 该异常会被 Sentinel 忽略，直接向上传播
    }
    // ...
}
```
在上述示例中：
- 当抛出 `BusinessException` 时，Sentinel 不会触发 `blockHandler` 或 `fallback`，而是直接将 `BusinessException` 抛给调用方。
- 当抛出其他异常（如 `NullPointerException`）时，若未配置 `fallback`，则可能被 Sentinel 捕获并根据降级规则处理；若配置了 `fallback`，则会调用 `fallback` 方法。

**注意事项：**

- `exceptionsToIgnore` 仅影响 Sentinel 对异常的**主动处理逻辑**（如限流、降级的回调），但不会阻止异常本身的传播。即使被忽略，异常仍会正常抛出到方法调用栈。
- 该属性适用于需要区分「Sentinel 触发的异常」（如限流）和「业务自身的异常」（如参数校验失败）的场景，避免业务异常被误判为 Sentinel 的流量控制或降级结果。


总结：`exceptionsToIgnore` 是 Sentinel 提供的「异常白名单」机制，用于让特定类型的异常跳过 Sentinel 的处理逻辑，保持业务异常的原有传播行为。



#### 热点规则

##### 介绍

Sentinel热点规则主要用于对热点参数进行流量控制，以保护系统免受突发高流量的冲击。以下是对Sentinel热点规则的详细介绍：

一、热点参数限流概述

热点参数限流是一种特殊的流量控制策略，它针对的是那些经常被访问的热点数据。通过统计传入参数中的热点参数，并根据配置的限流阈值与模式，对包含热点参数的资源调用进行限流。这种策略可以有效地防止某些热点数据被过度访问，从而保护系统的稳定性和可用性。

二、热点规则配置

在Sentinel中，热点规则的配置通常包括以下几个关键要素：

1. **资源名称**：需要限流的资源名称，通常与接口路径或方法名称相对应。
2. **热点参数索引**：指定要进行限流的热点参数在请求参数中的位置（从0开始计数）。
3. **限流阈值**：在指定时间窗口内，允许该热点参数被访问的最大次数。一旦超过这个阈值，就会触发限流策略。
4. **限流模式**：Sentinel支持多种限流模式，如直接失败、预热模式、排队等待等。不同的限流模式适用于不同的场景和需求。

三、热点规则示例

假设有一个电商系统，需要对商品ID进行热点参数限流。以下是一个简单的示例：

1. **配置热点规则**：
   - 资源名称：`getProductById`
   - 热点参数索引：0（假设商品ID是请求的第一个参数）
   - 限流阈值：10（每秒允许访问10次）
   - 限流模式：直接失败（超过阈值后直接返回限流提示）
2. **代码示例**：

```java
@RestController
public class ProductController {
 
    @GetMapping("/product/{id}")
    @SentinelResource(value = "getProductById", blockHandler = "handleBlock")
    public Product getProductById(@PathVariable("id") String productId) {
        // 查询商品信息的逻辑
        return new Product(productId, "商品名称", "商品描述", 100.0);
    }
 
    public Product handleBlock(String productId, BlockException ex) {
        // 限流处理逻辑，如返回默认商品信息或提示用户稍后再试
        return new Product("defaultId", "默认商品", "默认描述", 0.0);
    }
}
```

在上述代码中，`@SentinelResource`注解用于定义资源，并指定了限流处理的方法`handleBlock`。当请求的商品ID超过限流阈值时，会触发`handleBlock`方法，返回默认的商品信息或提示用户稍后再试。

四、热点参数例外项

Sentinel还支持热点参数例外项的配置，允许对特定的热点参数值设置不同的限流阈值。例如，对于某些热门商品，可以设置更高的访问阈值以满足用户的正常需求。

五、注意事项

1. **参数类型支持**：Sentinel热点规则支持多种参数类型，但具体支持哪些类型取决于Sentinel的版本和配置。
2. **性能影响**：热点参数限流会对系统性能产生一定的影响，特别是在高并发场景下。因此，在配置热点规则时，需要权衡限流效果和性能开销。
3. **规则持久化**：为了确保热点规则在重启后仍然有效，建议将规则持久化到数据库或配置中心等持久化存储中。

综上所述，Sentinel热点规则是一种有效的流量控制策略，可以帮助开发者保护系统免受突发高流量的冲击。通过合理配置热点规则，可以确保系统的稳定性和可用性。



##### 基本使用

```java
@GetMapping(value = "test3")
@SentinelResource(value = "test3")
public ObjectResponse<String> test3(@RequestParam(value = "flag", required = false) String flag,
                                    @RequestParam(value = "p2", required = false) String p2) {
    if ("exception".equals(flag)) {
        throw new RuntimeException("预期异常");
    }
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("/api/v1/test3 " + UUID.randomUUID());
    return response;
}
```

热点规则信息：

- 资源名为`test3`
- 限流模式为`QPS模式`
- 参数索引为`0`（表示带第一个参数的请求被限流）
- 单机阈值为`1`
- 统计窗口时长为`1`秒
- 是否集群为`不勾选`

带参数 flag 的请求被限流`http://localhost:8080/api/v1/test3?flag=1`，不带参数的 flag 的请求不会被限流`http://localhost:8080/api/v1/test3?p2=1`



##### 参数例外项

```java
@GetMapping(value = "test3")
@SentinelResource(value = "test3")
public ObjectResponse<String> test3(@RequestParam(value = "flag", required = false) String flag,
                                    @RequestParam(value = "p2", required = false) String p2) {
    if ("exception".equals(flag)) {
        throw new RuntimeException("预期异常");
    }
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("/api/v1/test3 " + UUID.randomUUID());
    return response;
}
```

热点规则信息：

- 资源名为`test3`
- 限流模式为`QPS模式`
- 参数索引为`0`（表示带第一个参数的请求被限流）
- 单机阈值为`1`
- 统计窗口时长为`1`秒
- 是否集群为`不勾选`
- 参数例外项的参数类型为`java.lang.String`
- 参数例外项的参数值为`2`
- 参数例外项的限流阈值为`200`

参数 flag=1 的请求被限流为`1 QPS http://localhost:8080/api/v1/test3?flag=1`，参数 flag=2 的请求被限流为`200 QPS http://localhost:8080/api/v1/test3?flag=2`



#### 授权规则

##### 介绍

Sentinel的授权规则是一种用于控制服务调用方来源的访问控制机制。以下是关于Sentinel授权规则的详细介绍：

一、基本概念

- **授权规则**：用于对请求方的来源进行判断和控制，决定是否允许访问受保护的资源。
- **白名单**：只有来源在白名单内的调用者才允许访问资源。
- **黑名单**：来源在黑名单内的调用者不允许访问资源，其余来源的调用者则可以访问。

二、如何获取请求来源（origin）

Sentinel通过`RequestOriginParser`接口的`parseOrigin`方法来获取请求的来源。默认情况下，Sentinel不管请求者从哪里来，返回值都是"default"，即所有请求的来源都被认为是一样的值"default"。因此，需要自定义这个接口的实现，让不同的请求返回不同的origin。

自定义`RequestOriginParser`接口的实现类时，可以从请求的Header中获取某个参数来标明调用方的身份，例如从请求的Header中获取"origin"字段或"source"字段的值作为调用方的身份标识。

三、配置授权规则

在Sentinel控制台中，可以新增授权规则，配置受保护的资源、调用方名单（origin）以及授权类型（白名单或黑名单）。

- **资源名**：受保护的资源，例如`/order/query`。
- **流控应用**：调用方名单（origin），可以配置多个来源，用逗号分隔。
- **授权类型**：设置调用方名单是白名单还是黑名单。

四、自定义异常处理

当请求被Sentinel拦截时，会抛出`BlockException`异常。为了更好地向调用方提供错误信息，可以自定义异常处理逻辑。

实现`BlockExceptionHandler`接口，并重写其`handle`方法。该方法有三个参数：`HttpServletRequest request`、`HttpServletResponse response`和`BlockException e`。其中，`BlockException`是Sentinel拦截时抛出的异常，包含多个不同的子类，如`FlowException`（限流异常）、`DegradeException`（降级异常）等。根据异常的类型，可以返回不同的错误信息和状态码。

五、规则持久化

Sentinel默认将规则保存在内存中，重启服务后规则会丢失。为了实现规则的持久化，可以采用以下两种方式：

- **Pull模式**：Sentinel控制台将配置的规则推送到Sentinel客户端，客户端将配置规则保存在本地文件或数据库中，以后会定时去本地文件或数据库中查询，更新本地规则。
- **Push模式**：Sentinel控制台将配置规则推送到远程配置中心（如Nacos），Sentinel客户端监听远程配置中心，获取配置变更的推送消息，完成本地配置更新。

通过以上方式，可以实现Sentinel授权规则的持久化，确保规则在服务重启后仍然有效。

综上所述，Sentinel的授权规则是一种强大的访问控制机制，可以根据请求方的来源进行细粒度的控制。通过自定义请求来源的获取方式、配置授权规则以及自定义异常处理逻辑，可以实现对服务调用方的有效管理和控制。



##### 黑名单

自定义请求解析器，用于解析请求参数并自动注入到 Sentinel 上下文中

```java
// Sentinel 授权规则的请求参数自定义解析器
@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameter("myp1");
    }
}
```

授权规则信息：

- 资源名为`test3`
- 流控应用为`1,5`
- 授权类型为`黑名单`

请求`http://localhost:8080/api/v1/test3?myp1=`白名单，请求`http://localhost:8080/api/v1/test3?myp1=1`黑名单



##### 白名单

和黑名单用法类似。



#### 规则持久化

>提示：`Nacos` 持久化 `Sentinel` 的规则，在 `Sentinel` 中修改规则不会持久化到 `Nacos` 中（只会修改 `Sentinel` 内存中的规则，`Sentinel` 重启会丢失临时修改的规则）。应该修改 `Nacos` 中对应的 `Sentinel` 规则，修改后会自动更新到 `Sentinel` 中，重启 `Sentinel` 后也不会丢失被修改的规则数据。



##### 介绍

Sentinel规则持久化是指将配置在Sentinel控制台的流量控制、熔断降级等规则存储到持久化存储系统中，使得即使服务重启或者Sentinel守护进程重启，规则也能自动恢复，无需重新手动配置。以下是对Sentinel规则持久化的详细介绍：

一、持久化方式

1. **Push模式**：
   - 规则中心统一推送，客户端通过注册监听器的方式时刻监听变化。例如，使用Nacos、Zookeeper等配置中心。
   - 当规则出现修改时，Sentinel控制台将规则推送至客户端，接着客户端会更新内存中的信息，并且将这部分数据写入到本地磁盘文件（或其他持久化存储）进行持久化保存。但这种方式是基于定时更新实现的，所以规则更新会存在延迟。
   - 当采用Push模式时，规则是存储在远程配置中心的，因此当出现服务迁移时，不影响规则信息。这种方式有更好的实时性和一致性保证，是生产环境下一般采用的方式。
2. **Pull模式**：
   - 客户端定期轮询拉取规则。
   - 这种方式简单且无任何依赖，但不保证一致性，实时性也无法保证。此外，拉取过于频繁也可能会有性能问题。

二、Push模式实现示例（以Nacos为例）

1. **引入依赖**：

   在需要持久化规则的服务中引入Sentinel监听Nacos的依赖。例如，在`pom.xml`中添加以下依赖：

   ```xml
   <dependency>
   	<groupId>com.alibaba.csp</groupId>
   	<artifactId>sentinel-datasource-nacos</artifactId>
   </dependency>
   ```

2. **配置Nacos地址**：

   在服务中的`application.yml`或`bootstrap.yml`文件中配置Nacos地址及监听的配置信息。例如：

   ```yaml
   spring:
     cloud:
   	sentinel:
   	  datasource:
   		flow:
   		  nacos:
   			server-addr: localhost:8848 # Nacos地址
   			dataId: orderservice-flow-rules # 数据ID
   			groupId: SENTINEL_GROUP # 分组ID
   			rule-type: flow # 规则类型
   ```

3. **修改Sentinel控制台源码**（如果需要）：

   如果Sentinel控制台默认不支持Nacos持久化，则需要修改其源码。具体步骤包括解压源码包、修改`pom.xml`文件中的Nacos依赖、将测试包下的Nacos支持代码拷贝到主包下、修改Nacos地址配置、配置Nacos数据源以及修改前端页面等。

4. **启动服务**：

   启动修改后的Sentinel控制台和需要持久化规则的服务。服务会从Nacos中读取规则并应用到Sentinel框架内。

5. **测试**：

   在Sentinel控制台中创建或更新规则后，这些规则会被推送到Nacos中保存。客户端启动时会从Nacos中读取规则。可以测试重启服务后规则是否仍然有效，以验证持久化是否成功。

三、注意事项

1. **规则一致性**：

   在Push模式下，由于规则是统一推送和监听的，因此可以保证规则的一致性。但在Pull模式下，由于客户端是定期轮询拉取规则的，因此可能存在规则不一致的情况。

2. **性能开销**：

   Push模式需要客户端监听远程配置中心的变化并实时更新本地规则，这可能会增加一定的性能开销。而Pull模式则相对简单，但可能会因为频繁拉取规则而影响性能。

3. **服务迁移**：

   当采用Push模式时，由于规则是存储在远程配置中心的，因此服务迁移时不需要迁移规则文件。而采用Pull模式时，则需要确保规则文件在服务迁移时能够正确迁移。

4. **版本兼容性**：

   在升级Sentinel或相关依赖时，需要注意版本兼容性问题。不同版本的Sentinel可能支持不同的持久化方式和配置方式。

综上所述，Sentinel规则持久化是确保服务稳定性和可用性的重要手段之一。在生产环境下，建议采用Push模式进行规则持久化，并结合具体的业务场景和需求选择合适的配置中心和持久化存储方式。



##### 配置

pom 依赖配置

```xml
<!-- Sentinel 持久化配置到 Nacos 依赖 -->
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
</dependency>
```

application.properties 配置

```properties
# Sentinel 持久化配置到 Nacos 配置
spring.cloud.sentinel.datasource.ds1.nacos.server-addr=localhost:8848
spring.cloud.sentinel.datasource.ds1.nacos.group-id=DEFAULT_GROUP
spring.cloud.sentinel.datasource.ds1.nacos.data-id=${spring.application.name}
spring.cloud.sentinel.datasource.ds1.nacos..data-type=json
# flow：流控规则。这是 Sentinel 最核心、最直观的一种规则，主要用于控制请求的 QPS（每秒查询率）或并发线程数，以防止系统被过大的流量压垮。
# degrade：熔断规则。当系统的某个资源不稳定或出现故障时，为了防止故障的进一步扩散，可以使用熔断规则来快速失败这个资源的请求。熔断规则通常基于一些条件（如慢调用比例、异常比例或异常数）来触发。
# param-flow：热点规则。热点规则用于对某个资源中的某个或某些参数进行单独的流控。这可以帮助系统保护那些因为某些特殊参数值而导致的高并发请求。
# system：系统规则。系统规则是从系统的整体角度出发，对系统的入口流量、CPU 使用率、线程数等指标进行整体控制，以防止系统整体过载。
# authority：授权规则。授权规则用于对资源的访问进行黑白名单控制。这可以帮助系统实现细粒度的访问控制。
spring.cloud.sentinel.datasource.ds1.nacos.rule-type=flow
```

登录 Nacos 创建 Sentinel 规则的配置如下：

- Data ID 为 demo-springcloud-gateway（demo-springcloud-gateway 为微服务名称）

- Group 为 DEFAULT_GROUP

- Format 为 JSON

- Configuration Content 为

  ```json
  [{
      "resource": "test3",
      "limitApp": "default",
      "grade": 1,
      "count": 1,
      "strategy": 0,
      "controlBehavior": 0,
      "clusterMode": false
  },{
      "resource": "/api/v1/test1",
      "limitApp": "default",
      "grade": 1,
      "count": 2,
      "strategy": 0,
      "controlBehavior": 0,
      "clusterMode": false
  }]
  ```

请求`http://localhost:8080/api/v1/test1`和`http://localhost:8080/api/v1/test3`测试效果。

提示：修改 `Nacos` 中的规则会自动同步到 `Sentinel` 中，`Nacos` 中的规则数据会被持久化，`Sentinel` 服务重启后也不会丢失规则数据。



#### 应用启动时自动创建规则

>提示：使用 `com.alibaba.nacos:nacos-client` 在应用启动时连接 `Nacos` 服务并创建规则。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-boot-sentinel)

`com.alibaba.nacos:nacos-client` 操作 `Nacos` 请参考本站 <a href="/springcloud/README.html#使用-java-客户端操作-nacos" target="_blank">链接</a>

`POM` 配置：

```xml
<!-- Sentinel 持久化配置到 Nacos 依赖 -->
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
</dependency>
```

如果应用没有配置上面依赖从 `Nacos` 读取规则数据，则添加下面的依赖（因为上面的依赖自动引入 `nacos-client`）：

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>2.2.0</version> <!-- 替换为实际服务端版本（如 2.2.3） -->
</dependency>
```

启动时在 `Nacos` 中自动创建 `Sentinel` 规则：

```java
@Component
@Slf4j
public class SentinelRuleInitializer implements CommandLineRunner {

    @Value("${spring.cloud.sentinel.datasource.ds1.nacos.server-addr:localhost:8848}")
    String sentinelNacosServerAddr;
    @Value("${spring.application.name}")
    String springApplicationName;

    @Override
    public void run(String... args) throws Exception {
        // region 初始化阿里 Sentinel 规则

        // 清除本地缓存（路径默认 ~/.nacos/config）
        LocalConfigInfoProcessor.cleanAllSnapshot();

        ConfigService configService = NacosFactory.createConfigService(sentinelNacosServerAddr);

        String dataId = springApplicationName;
        String group = "DEFAULT_GROUP";
        int timeoutMilliseconds = 3000;

        String config = configService.getConfig(dataId, group, timeoutMilliseconds);
        if (StringUtils.isBlank(config)) {
            String content = "[{\n" +
                    "    \"resource\": \"myTest1\",\n" +
                    "    \"limitApp\": \"default\",\n" +
                    "    \"grade\": 1,\n" +
                    "    \"count\": 1,\n" +
                    "    \"strategy\": 0,\n" +
                    "    \"controlBehavior\": 0,\n" +
                    "    \"clusterMode\": false\n" +
                    "}]";
            boolean result = configService.publishConfig(dataId, group, content, ConfigType.JSON.getType());
            Assert.isTrue(result, "设置 Nacos 中默认的 Sentinel 规则失败");
            if (log.isDebugEnabled()) {
                log.debug("成功设置 Nacos 中默认的 Sentinel 规则");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Nacos 中已经存在 Sentinel 规则");
            }
        }

        // endregion
    }

}

```

启动应用后在 `Nacos` 服务中自动创建 `Sentinel` 规则

访问 `Sentinel` 控制台：`http://localhost:8858/`，帐号：`sentinel`，密码：`sentinel`

访问 `Nacos` 控制台：`http://localhost:8848/nacos`，帐号：`nacos`，密码：`nacos`

使用性能测试工具测试协助测试：

```sh
wrk -t1 -c1 -d300000000s --latency --timeout 60 http://localhost:8080/api/v1/sentinel/test1
```



#### 和 SpringCloud Gateway 集成

>todo `https://github.com/alibaba/Sentinel/wiki/%E7%BD%91%E5%85%B3%E9%99%90%E6%B5%81`



#### `Nacos` 配置 `Sentinel` 规则 `json` 格式

```json
[{
    "resource": "myTest1",
    "limitApp": "default",
    "grade": 1,
    "count": 1,
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
}]
```

该配置是 Nacos 中存储的 Sentinel **流控规则（Flow Rule）**，用于对特定资源进行流量控制。以下是对各字段的详细解析：

**1. 资源标识（resource）**

- 值：`"myTest1"`  
- 含义：表示该流控规则作用的**资源名称**（即 Sentinel 中需要限流的入口标识，通常对应一段业务逻辑或接口）。

**2. 来源应用（limitApp）**

- 值：`"default"`  
- 含义：表示流控规则的**请求来源限制**。`default` 是 Sentinel 的默认值，含义是“不区分调用来源”，即所有调用该资源的请求都会被限流规则约束。  
- 其他可能值：若指定具体应用名（如 `"appA"`），则仅限制来自 `appA` 的请求；`"*"` 表示拒绝所有来源（不常用）。

**3. 阈值类型（grade）**

- 值：`1`  
- 含义：表示流控阈值的**统计维度类型**。Sentinel 中 `grade` 的取值由 `RuleConstant` 定义：  
  - `0`：线程数限流（基于当前请求的线程数）；  
  - `1`：QPS 限流（基于每秒请求数，最常用）；  
  - `2`：线程数限流（已弃用）。  
  此处 `grade=1` 表示规则基于 **QPS（每秒请求数）** 进行限流。

**4. 阈值数量（count）**

- 值：`1`  
- 含义：表示流控的**具体阈值**。结合 `grade=1`（QPS 限流），此处含义是“每秒最多允许通过 1 个请求”。若实际 QPS 超过 1，触发流控效果。

**5. 流控策略（strategy）**

- 值：`0`  
- 含义：表示**超出阈值时的处理策略**（流控效果）。Sentinel 中 `strategy` 的取值由 `RuleConstant` 定义：  
  - `0`：快速失败（默认策略，直接拒绝超出阈值的请求，抛出 `BlockException`）；  
  - `1`：Warm Up（冷启动，逐渐提高阈值，适用于服务启动初期）；  
  - `2`：排队等待（请求匀速通过，超出阈值的请求排队等待，直到超时）；  
  - `3`：预热排队（结合 Warm Up 和排队等待，更平滑的流量过渡）。  
  此处 `strategy=0` 表示当 QPS 超过 1 时，直接拒绝后续请求。

**6. 控制行为（controlBehavior）**

- 值：`0`  
- 注意：Sentinel 标准流控规则中**无此字段**，可能是配置冗余或笔误。实际生效的是 `strategy` 字段（二者值相同，均为 `0` 表示快速失败）。

**7. 集群模式（clusterMode）**

- 值：`false`  
- 含义：表示是否开启**集群流控模式**。`false` 为单机模式（限流阈值基于单个实例的流量），`true` 为集群模式（阈值基于整个集群的总流量，需配合 Sentinel 集群中心使用）。此处为单机限流。

**总结**

该规则对资源 `myTest1` 生效，不区分调用来源，基于 QPS 限流（阈值 1 次/秒），当 QPS 超过阈值时直接拒绝请求（快速失败），采用单机模式。

**注意**：实际使用中需确保 Sentinel 客户端已正确加载 Nacos 中的规则（通过 `NacosConfigUtil` 配置数据源），否则规则不会生效。



#### 注意说明

##### `@SentinelResource` 注解中的 `blockHandler`、`fallback`  需要和源函数参数列表、返回值一致

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/spring-cloud-sentinel)

```java
@GetMapping(value = "test3")
@SentinelResource(value = "test3", blockHandler = "blockHandler", fallback = "fallback")
public ObjectResponse<String> test3(@RequestParam(value = "flag", required = false) String flag,
                                    @RequestParam(value = "p2", required = false) String p2) {
    if ("exception".equals(flag)) {
        throw new RuntimeException("预期异常");
    }
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("/api/v1/test3 " + UUID.randomUUID());
    return response;
}

public ObjectResponse<String> blockHandler(@RequestParam(value = "flag", defaultValue = "") String flag,
                                           @RequestParam(value = "p2", required = false) String p2,
                                           BlockException ex) {
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("被限流了");
    return response;
}

public ObjectResponse<String> fallback(@RequestParam(value = "flag", defaultValue = "") String flag,
                                       @RequestParam(value = "p2", required = false) String p2,
                                       Throwable ex) {
    ObjectResponse<String> response = new ObjectResponse<>();
    response.setData("服务降级了");
    return response;
}
```

- 上面的 `test3` 函数参数列表需要和 `blockHandler`、`fallback` 函数的参数列表、返回值一致，否则在触发限流或者熔断时会报告其他异常，导致不能返回预期的响应。
