# 演示skywalking使用

## 注意

> 容器部署java应用集成skywalking需要挂载agent.config到agent对应目录中，并且agent-v8以上需要手动添加以下配置到agent.config中
>
> ```properties
> plugin.toolkit.log.grpc.reporter.server_host=${SW_GRPC_LOG_SERVER_HOST:demo-skywalking-oap-server}
> plugin.toolkit.log.grpc.reporter.server_port=${SW_GRPC_LOG_SERVER_PORT:11800}
> plugin.toolkit.log.grpc.reporter.max_message_size=${SW_GRPC_LOG_MAX_MESSAGE_SIZE:10485760}
> plugin.toolkit.log.grpc.reporter.upstream_timeout=${SW_GRPC_LOG_GRPC_UPSTREAM_TIMEOUT:30}
> ```

## 资料

> https://juejin.cn/post/7012458633455730702
> https://www.cnblogs.com/zhian/p/16475108.html
> https://github.com/apache/skywalking/discussions/8058
>
> **java -jar启动skywalking agent**
> java -javaagent:/usr/share/apache-skywalking-apm-bin-es7/agent/skywalking-agent.jar -Dskywalking.agent.service_name=xxx-service -Dskywalking.collector.backend_service=skywalking-oap-server:11800 ${JAVA_OPTS} -jar /usr/share/xxx.jar --spring.profiles.active=prod
> https://blog.csdn.net/qq_43437874/article/details/108620416



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
