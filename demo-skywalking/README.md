# 演示skywalking使用

## 资料

> https://juejin.cn/post/7012458633455730702
> https://www.cnblogs.com/zhian/p/16475108.html



## 说明

> demo中 service-gateway 使用 springcloud-gateway 实现网关功能用于测试skywalking相关功能
> demo中 service-zuul使用springcloud-zuul实现网关功能用于测试skywalking相关功能



## 调试demo

```
# 启动skywalking相关组件
sh build.sh
docker-compose up -d

# 访问skywalking UI
http://localhost:8088/

# 下载对应skywalking-oap-server版本的agent到本地并解压
https://archive.apache.org/dist/skywalking/

# 需要集成skywalking的服务在vm options里面添加如下skywalking agent启动参数，skywalking.agent.service_name对应显示在skywalking UI中服务的名称，skywalking.collector.backend_service对应skywalking ip地址和端口

-javaagent:/home/dexterleslie/apache-skywalking-apm-bin-es7/agent/skywalking-agent.jar -Dskywalking.agent.service_name=level-first-provider -Dskywalking.collector.backend_service=127.0.0.1:11800

# 启动各个springboot应用

# 请求接口稍等数秒后，通过请求响应头x-request-id查询对应skywalking日志
http://localhost:8081/api/v1/test1?param1=p1
```



## todo

> - springcloud-gateway 上报到 skywalking 的 logback日志traceId=N/A
> - http response返回traceId
