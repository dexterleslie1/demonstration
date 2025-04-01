# 使用pushy推送apns消息

## pushy资料参考
>[pushy官网](https://pushy-apns.org/)  
>[pushy源代码](https://github.com/jchambers/pushy)  
>[pushy console](https://github.com/jchambers/pushy-console)  

## 运行demo

### 方式1

> 运行PushyTests进行junit测试

### 方式2

```
# 编译docker容器
mvn package

# 配置.env

# 启动项目
docker-compose up

# 请求接口推送apns消息
http://localhost:8080/api/v1/test1
```

