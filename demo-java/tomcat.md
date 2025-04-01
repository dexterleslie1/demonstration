# tomcat



## 使用 docker 运行 tomcat

>`https://hub.docker.com/_/tomcat/tags?page=1&name=9.0.74-jdk`
>
>设置 tomcat jvm 内存，使用环境变量设置 JAVA_OPTS=-server -Xmx512m -Xms512m `https://www.cnblogs.com/caoweixiong/p/12383223.html?ivk_sa=1024320u`

编译 war

```bash
mvn clean package
```

使用 docker compose 运行 tomcat

```bash
docker-compose up
```

打开浏览器访问应用是否正常`http://localhost:8080`