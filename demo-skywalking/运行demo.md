# 运行`skywalking`示例



## 运行只有`springcloud-gateway`示例

注意：

- **有`springcloud-gateway`的项目业务日志不能使用`skywalking`内置的链路日志功能，需要外部自定义`logstash+elasticsearch`方案记录业务日志。**使用`springcloud-gateway`无法通过`org.apache.skywalking.apm.toolkit.log.logback.v1.x.log.GRPCLogClientAppender`集成链路日志到`skywalking log`功能中（尝试过`skywalking-v8.7.0`和`skywalking-v9.3.0`都无法解决此问题），因为`traceId`为`N/A`导致链路日志丢失。所以`springcloud-gateway`使用`String traceId = WebFluxSkyWalkingOperators.continueTracing(exchange, TraceContext::traceId);+mdc`注入`traceId`到日志方式解决链路日志丢失问题。
- 有`springcloud-gateway`的项目因为使用外部自定义`logstash+elasticsearch`方案记录业务日志，所以需要运行`logstash`服务。



### 运行基于`docker compose`示例

下载示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-skywalking/demo-with-gateway`

编译镜像

```bash
./build.sh
```

启动`skywalking`示例

```bash
docker-compose up -d
```

访问`skywalking UI`，`http://localhost:8088/`

访问`eureka`，`http://localhost:9999/`

访问`kibana`，`http://localhost:5601/`

请求接口`http://localhost:8080/api/v1/test1?param1=p1`稍等数秒后，通过请求响应头`x-request-id`查询对应`skywalking`日志

模拟接口调用延迟数据`http://localhost:8080/api/v1/test3`



### 运行基于`k8s`示例

下载示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-skywalking/demo-with-gateway`

编译镜像

```bash
./build.sh
```

推送镜像

```bash
./push.sh
```

运行示例

```bash
./create-k8s.sh
```

等待一段时间服务启动后，测试示例功能是否符合预期

访问`skywalking ui`，`http://192.168.235.145:30001/`

访问`eureka`，`http://192.168.235.145:30002/`

访问`kibana`，`http://192.168.235.145:30003/`

请求接口`http://192.168.235.145:30000/api/v1/test1?param1=p1`稍等数秒后，通过请求响应头`x-request-id`查询对应`skywalking`日志

模拟接口调用延迟数据`http://192.168.235.145:30000/api/v1/test3`

停止示例

```bash
./destroy-k8s.sh
```





## 运行只有`springcloud-zuul`示例

注意：

- **没有`springcloud-gateway`的项目业务日志可以使用`skywalking`内置链路日志功能记录，不需要外部自定义`logstash+elasticsearch`方案记录业务日志。**
- 没有`springcloud-gateway`的项目因为使用`skywalking`内置链路日志功能记录，所以不需要运行`logstash`服务。



### 运行基于`docker compose`示例

下载示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-skywalking/demo-with-zuul`

编译镜像

```bash
./build.sh
```

启动`skywalking`示例

```bash
docker-compose up -d
```

访问`skywalking UI`，`http://localhost:8088/`

访问`eureka`，`http://localhost:9999/`

请求接口`http://localhost:8080/api/v1/test1?param1=p1`稍等数秒后，通过请求响应头`x-request-id`查询对应`skywalking`日志

模拟接口调用延迟数据`http://localhost:8080/api/v1/test3`

使用 `wrk` 协助生成 `CPM` 测试监控数据：

```sh
# 调用正常接口
wrk -t1 -c16 -d150000s --latency --timeout 30 http://localhost:8080/api/v1/test1\?param1\=p1

# 调用慢接口
wrk -t1 -c16 -d150000s --latency --timeout 30 http://localhost:8080/api/v1/test3
```



### 运行基于`k8s`示例

下载示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-skywalking/demo-with-zuul`

编译镜像

```bash
./build.sh
```

推送镜像

```bash
./push.sh
```

运行示例

```bash
./create-k8s.sh
```

等待一段时间服务启动后，测试示例功能是否符合预期

访问`skywalking ui`，`http://192.168.235.145:30001/`

访问`eureka`，`http://192.168.235.145:30002/`

请求接口`http://192.168.235.145:30000/api/v1/test1?param1=p1`稍等数秒后，通过请求响应头`x-request-id`查询对应`skywalking`日志

模拟接口调用延迟数据`http://192.168.235.145:30000/api/v1/test3`

停止示例

```bash
./destroy-k8s.sh
```

