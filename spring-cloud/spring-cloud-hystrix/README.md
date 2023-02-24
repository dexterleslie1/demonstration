# 演示zuul route hystrix、feign hystrix、resttemplate hystrix用法

## 服务降级测试

### 服务提供者端服务降级测试

```
# 打开浏览器访问http://localhost:8081/api/v1/user/test1正常

# 打开浏览器访问http://localhost:8081/api/v1/user/test1?milliseconds=3000结果返回{"errorCode":5000,"errorMessage":null,"data":"调用ProviderSideFallbackController1#test接口失败"}表示触发服务降级
# 因为sleep时间持续3000毫秒大于execution.isolation.thread.timeoutInMilliseconds配置的2000毫秒导致触发服务降级
# NOTE: 虽然因为超时服务降级，但是陆续进来的请求依旧被服务器处理，并且将会导致更多的服务调用失败。
```

### 服务提供者端服务降级测试之统一fallback

```
# 打开浏览器访问http://localhost:8081/api/v1/user/test2正常

# 打开浏览器访问http://localhost:8081/api/v1/user/test2?milliseconds=3000结果返回{"errorCode":5000,"errorMessage":null,"data":"调用ProviderSideFallbackController2#test接口失败"}表示成功触发统一fallback
```

### 服务调用者feign服务降级测试

```
# 打开浏览器访问http://localhost:8080/api/v1/user/timeoutWithFeignFallback正常

# 打开浏览器访问http://localhost:8080/api/v1/user/timeoutWithFeignFallback?milliseconds=10100会触发feign fallback，因为在application.properties里面配置feign ApiUser.timeout超时时间为10000
```

## 服务熔断测试

```
# 访问http://localhost:8081/api/v1/user/testCircuitBreaker1刷新5次后触发熔断

# 马上访问http://localhost:8081/api/v1/user/testCircuitBreaker1?id=22发现接口错误返回说明熔断已经被打开，大概经过30秒后服务恢复正常
```

turbine hystrix集群监控
https://www.jianshu.com/p/590bad4c8947
使用 turbine hystrix集群监控
访问 trubine 服务http://localhost:8083/hystrix后，填入http://localhost:8083/turbine.stream，点击monitor按钮

springcloud feign fallback，通过feign、HystrixCommand、FallbackProvider
https://www.cnblogs.com/cearnach/p/9341593.html

springcloud zuul FallbackProvider基本使用，能够根据serviceId调用不同的fallback，不能实现针对route调用不同的fallback
fallback的FallbackProvider使用方法只作用于springcloud zuull，不能作用于fallback的feign、HystrixCommand使用方法
https://juejin.cn/post/6844903862470443015
https://thepracticaldeveloper.com/hystrix-fallback-with-zuul-and-spring-boot/

springcloud hystrix配置
https://blog.csdn.net/hry2015/article/details/78554846

HystrixCommand配置
https://github.com/Netflix/Hystrix/wiki/Configuration#intro

springcloud zuul hystrix超时使用FallbackProvider统一处理
feign hystrix超时使用@ControllerAdvice、@ExceptionHandler统一处理

hystrix资源隔离原理图
https://www.cnblogs.com/-beyond/p/12856421.html

RestTemplate hystrix整合
https://blog.51cto.com/13538361/2426289

feignclient FallbackFactory使用
https://blog.csdn.net/qq_24504315/article/details/79120904

设置 feignclient 各个方法hystrix超时
http://www.saily.top/2020/04/19/springcloud/hystrix05/

The Hystrix timeout of ***ms for the command *** is set lower than the combination of the Ribbon
https://blog.csdn.net/it_beecoder/article/details/106028256

TODO: 写demo演示关闭ribbon、feign、resttemplate retry机制

