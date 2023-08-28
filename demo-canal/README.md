# 演示canal用法

> canal官方参考
> https://github.com/alibaba/canal
>
> 
>
> http://www.qb5200.com/article/475116.html
>
> NOTE: 
>
> - 走过很多坑，一定要使用canal.properties和instance.properties配置canal-server，一定不能使用环境变量配置canal-server，否则无法正常集成rabbitmq+canal-server同步。
> - canal-server:v1.1.6都不能和mariadb:10.4.19、mysql:5.7集成，rabbitmq没有消息
> - canal-server和mariadb:10.4.19集成insert一次会有两条消息
> - canal-server几乎停止维护更新，学习使用其他CDC方案为宜
> - 使用debezium替换此方案，因为经过demo测试debezium比canal稳定多。
>
> NOTE: 如果同步运行不正常，可以通过进入canal容器分析/home/admin/canal-server/logs/canal/canal.log定位问题。



## 运行demo

```
# 编译docker镜像
sh build.sh

# 运行docker
docker-compose up

# 访问 http://localhost:15672 登录rabbitmq控制台后，手动创建名为 my-exchange，类型为fanout的交换机，再创建名为 my-queue，类型为classic的队列，使用example(对应instance.properties文件中的canal.mq.topic=example) routingKey绑定my-exchange和my-queue

# 再次启动canal-server(因为上一次启动canal-server是rabbitmq未手动配置完成，所以canal-server会退出)
docker-compose up

# 运行springboot项目（如果配置使用canal.serverMode = tcp模式则启动此项目进行测试）
# 如果使用canal.serverMode = rabbitMQ模式，则登录rabbitmq控制台查看消息即可

# 在MySQL中增、删、改，springboot项目控制台有日志输出
insert into t_test(createTime) values(now());
```

