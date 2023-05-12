# 演示使用docker运行tomcat

## 参考

> https://hub.docker.com/_/tomcat/tags?page=1&name=9.0.74-jdk
>
> 设置tomcat jvm内存，使用环境变量设置JAVA_OPTS=-server -Xmx512m -Xms512m
> https://www.cnblogs.com/caoweixiong/p/12383223.html?ivk_sa=1024320u

## 运行

```
# 编译war
mvn clean package

# 使用docker-compose运行tomcat
docker-compose up

# 打开浏览器访问应用是否正常
http://localhost:8080
```

