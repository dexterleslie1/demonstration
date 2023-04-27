# 演示使用maven docker容器编译源代码

## 为何有这个演示？

> 在一个新环境使用mvn编译java源代码，需要安装和配置jdk、maven等工具，但使用maven docker容器编译java源代码，则不需要在新环境中安装和配置jdk、maven等工具。环境只需要安装docker和docker-compose即可。

## 参考

> https://hub.docker.com/_/maven

## 运行演示

```
# 编译源代码并生成docker镜像
sh build.sh

# 运行容器
docker-compose up
```

