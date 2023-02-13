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
> Feign（停止更新）
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

