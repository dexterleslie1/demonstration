# 高性能socket客户端用于协助测试socket服务器

## 编译和执行jar
```shell script
# 编译jar
mvn clean package

# 运行jar
java -jar target/demo-socket-client.jar 目标ip 目标端口 并发socket数
```