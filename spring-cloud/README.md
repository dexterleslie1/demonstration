## springcloud和springboot版本兼容性

> 通过ctrl+f 搜索Table 1. Release train Spring Boot compatibility定位到spring-cloud和spring-boot兼容表格
> https://spring.io/projects/spring-cloud
> Spring-cloud和spring-boot版本兼容表格
> https://stackoverflow.com/questions/42659920/is-there-a-compatibility-matrix-of-spring-boot-and-spring-cloud

| Release Train | Boot Version |
| ------------- | ------------ |
| Hoxton        | 2.2.x        |
| Greenwich     | 2.1.x        |
| Finchley      | 2.0.x        |
| Edgware       | 1.5.x        |
| Dalston       | 1.5.x        |
| Camden        | 1.4.x        |
| Brixton       | 1.3.x, 1.4.x |
| Angel         | 1.2.x        |
## 服务注册与发现

> 服务注册中心实现包括：
>
> eureka（停止更新，将会抛弃）
>
> zookeeper（用得少）
>
> consul
>
> nacos（阿里巴巴主流注册中心）

### eureka

> eureka服务器配置
>
> eureka客户端服务注册配置
>
> eureka服务器禁用自我保护机制
>
> 参考单机版 spring-cloud/spring-cloud-eureka
>
> 参考集群版 spring-cloud/spring-cloud-eureka-cluster

**禁用自我保护机制，一旦eureka客户端没有心跳马上从eureka实例列表剔除**

```shell
# https://blog.csdn.net/weixin_54707282/article/details/123362629
# 编辑eureka服务器application.properties添加如下内容：
# 服务端是否开启自我保护机制 （默认true）
eureka.server.enable-self-preservation=false
# 扫描失效服务的间隔时间（单位毫秒，默认是60*1000）即60秒
eureka.server.eviction-interval-timer-in-ms=2000

# 编辑eureke客户端application.properties添加如下内容：
# 客户端向注册中心发送心跳的时间间隔，（默认30秒）
eureka.instance.lease-renewal-interval-in-seconds=1
# eureka注册中心（服务端）在收到客户端心跳之后，等待下一次心跳的超时时间，如果在这个时间内没有收到下次心跳，则移除该客户端。（默认90秒）
eureka.instance.lease-expiration-duration-in-seconds=2
```

### consul

> consul服务器启动（参考spring-cloud-consul-parent/docker-compose.yaml使用docker运行consul服务器）
>
> 参考 spring-cloud/spring-cloud-consul-parent demo

**运行 spring-cloud/spring-cloud-consul-parent demo**

```shell
# 启动consul服务器
docker-compose up

# 检查consul服务器是否正常
http://localhost:8500/

# 启动所有springcloud应用

# 测试springcloud服务是否正常
http://localhost:8081/api/v1/a/sayHello?name=dexter
```

## 服务调用

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

## 服务降级

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

## 服务网关

> netflix zuul（进入维护状态，被springcloud抛弃）、netflix zuul2（推迟上线计划，被springcloud抛弃）、gateway（springcloud自研新一代网关）
>
> 参考spring-cloud/spring-cloud-gateway

## 服务配置

> nacos逐渐替代config

### config和bus

> todo 写demo

## 消息驱动stream

## 分布式请求链路跟踪sleuth+zipkin

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

## 分布式事务

### seata

