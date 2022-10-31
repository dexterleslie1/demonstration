# bio多线程演示c10k问题

## 编译和执行jar
```shell script
# 编译jar
mvn clean package

# 运行jar
java -jar -XX:+UseG1GC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./ -Xmx7g -Xms7g target/demo-server.jar
```
