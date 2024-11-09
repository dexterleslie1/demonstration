# 运行`skywalking`示例



## 运行基于`docker compose`示例

下载示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-skywalking`

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

请求接口`http://localhost:8081/api/v1/test1?param1=p1`稍等数秒后，通过请求响应头`x-request-id`查询对应`skywalking`日志

模拟接口调用延迟数据`http://localhost:8081/api/v1/test3`



## 运行基于`k8s`示例

下载示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-skywalking`

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



## `todo`

- `springcloud-gateway`上报到`skywalking`的`logback`日志`traceId=N/A`
- `elk`日志能够支持代码行定位
