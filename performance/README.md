##### springboot2 内嵌tomcat设置
https://www.thinbug.com/q/31461444

##### 编译代码和生成docker镜像
```shell script
mvn clean package
```

##### 只编译代码，不生产docker镜像
```shell script
mvn clean package -Ddockerfile.skip=true
```

##### 生成docker镜像
```shell script
mvn dockerfile:build
```

##### 推送docker镜像到仓库
```shell script
mvn dockerfile:push
```

##### 使用docker-compose启动容器
```shell script
docker-compose up -d
```

##### 使用docker-compose关闭容器
```shell script
docker-compose down
```