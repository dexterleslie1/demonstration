# `spring boot`



## 打包、部署、运维

使用 `https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mvc` 测试 spring boot 应用打包、部署、运维

打包

```bash
./mvnw package -Ddockerfile.skip=false
```

启动应用

```bash
java -jar target/demo-spring-boot-mvc-0.0.1-SNAPSHOT.jar
```

访问`http://localhost:8080/hello`返回 Hello! 表示应用运行正常
