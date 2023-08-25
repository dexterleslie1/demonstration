## kafka使用



### 使用docker-compose运行kafka

> https://blog.csdn.net/yudaonihaha/article/details/130768061

```
# 启动kafka
docker-compose up -d


# 查看kafka是否成功启动
docker-compose logs -f kafka

# 查看kafka状态
http://192.168.1.181:9000/

# 查看kafka相关日志
tail -f /opt/kafka/logs/xxx.log

# 关闭kafka
docker-compose down
```



### topic管理

```
# 进入kafka容器
docker exec -it demo-kafka-server /bin/bash

# 切换到kafka bin目录
cd /opt/kafka/bin

# 创建名为 topic1，分区数为1，副本数为1的topic
./kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic topic1

# 显示topic1详情
./kafka-topics.sh --describe --zookeeper zookeeper:2181 --topic topic1

# 修改topic1分区数
./kafka-topics.sh --alter --zookeeper zookeeper:2181 --topic topic1 --partitions 6

# 查询所有topic列表
./kafka-topics.sh --zookeeper zookeeper:2181 --list

# 删除topic
./kafka-topics.sh --delete --zookeeper zookeeper:2181 --topic topic1


```



### 生产消费

```
# 启动生产端
./kafka-console-producer.sh --topic topic1 --bootstrap-server demo-kafka-server:9092

# 启动消费端，--from-beiginning表示所有消息重新接收一次
./kafka-console-consumer.sh --topic topic1 --bootstrap-server demo-kafka-server:9092 --from-beginning
```



### springboot集成kafka

> NOTE: 参考当前目录的maven项目
