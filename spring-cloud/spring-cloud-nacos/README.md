# 演示nacos使用

## springcloudalibaba配置nacos官方文档

> https://spring-cloud-alibaba-group.github.io/github-pages/hoxton/zh-cn/index.html

## 测试demo注册中心

```
# 启动nacos服务
docker-compose up

# 访问nacos dashboard，帐号：nacos 密码:nacos
http://localhost:8848/nacos

# 访问http://localhost:8080/api/v1/zuul/test1?param1=dexter
```

## 测试demo服务配置

```
# 启动nacos服务
docker-compose up

# 访问nacos dashboard，帐号：nacos 密码:nacos
http://localhost:8848/nacos

# 手动在nacos配置中心中创建配置文件demo-springcloud-helloworld-dev.properties，内容为：my.config=v1

# 访问http://localhost:8080/api/v1/zuul/test1?param1=dexter查看当前myConfig返回值为v1

# 手动修改demo-springcloud-helloworld-dev.properties my.config=v2
# 再次访问http://localhost:8080/api/v1/zuul/test1?param1=dexter查看当前myConfig返回值为v2
```

## 测试demo服务配置Group、Namespace

```
# 启动nacos服务
docker-compose up

# 访问nacos dashboard，帐号：nacos 密码:nacos
http://localhost:8848/nacos

# 手动在nacos配置中心创建ns_dev命名空间
# 在ns_dev命名空间下创建demo-springcloud-zuul-dev.properties文件，内容为: my.config=ns_dev,DEV_GROUP,demo-springcloud-zuul-dev.properties，Group为:DEV_GROUP

# 访问http://localhost:8080/api/v1/zuul/test2测试
```

