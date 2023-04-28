# 演示使用docker运行tomcat

## 参考

> https://hub.docker.com/_/tomcat/tags?page=1&name=9.0.74-jdk

## 运行

```
# 编译war
mvn clean package

# 使用docker-compose运行tomcat
docker-compose up

# 打开浏览器访问应用是否正常
http://localhost:8080/tomcat-java-web
```

