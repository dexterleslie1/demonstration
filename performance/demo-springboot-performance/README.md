# tomcat性能优化

## 经过测试验证，一个nginx代理多个tomcat能够明显提升吞吐量量去有效防御CC攻击

## tomcat调优步骤

- tomcat线程和keepalive调优
- JVM调优
- 操作系统调优

## springboot2内嵌tomcat设置
```text
springboot2支持property和编程方式设置内嵌tomcat，property和编程方式设置内嵌tomcat可以同时存在

当两种设置方式同时存在时，会优先使用编程方式的值作为设置值
```

[两种方式设置springboot2内嵌tomcat](https://blog.csdn.net/luo15242208310/article/details/107353987)

[使用编程式设置springboot2内嵌tomcat](https://www.thinbug.com/q/31461444)

## 官方文档描述tomcat8支持配置的参数

[tomcat8支持配置参数](https://tomcat.apache.org/tomcat-8.5-doc/config/http.html)

## tomcat调优

[Datadog tomcat调优](https://www.datadoghq.com/blog/tomcat-architecture-and-performance/#:~:text=Tomcat%20exposes%20metrics%20through%20JMX,such%20as%20HTTP%20status%20codes.)

[tomcat线程解析和参数设置](http://www.cainiaoxueyuan.com/yunwei/7324.html)

[keepalive设置参考](https://blog.csdn.net/a82514921/article/details/115359606)

## 压力测试tomcat抛出异常解析

### org.apache.catalina.connector.ClientAbortException: java.io.IOException: Connection reset by peer

```text
这个错误是因为客户端未等到服务器response返回就断开TCP连接导致
```

## 代码编译和docker镜像编译与运行

### 编译代码和生成docker镜像

```shell script
mvn clean package
```

### 只编译代码，不生产docker镜像

```shell script
mvn clean package -Ddockerfile.skip=true
```

### 生成docker镜像

```shell script
mvn dockerfile:build
```

### 推送docker镜像到仓库

```shell script
mvn dockerfile:push
```

### 使用docker-compose启动容器

```shell script
docker-compose up -d
```

### 使用docker-compose关闭容器

```shell script
docker-compose down
```

