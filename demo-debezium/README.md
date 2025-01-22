# debezium

> debezium支持三种模式: 服务器、SpringBoot 嵌入式、Kafka Connector 模式。
>
> todo: 完成服务器、Kafka Connector 模式 demo。



## SpringBoot 嵌入式模式

>  `https://www.baeldung.com/debezium-intro`
> 
> `https://debezium.io/documentation/reference/stable/development/engine.html`

注意：第一次启动 SpringBoot 应用没有 offset 信息时会全量读取一次数据库并回调 SpringBoot CDC 接口。官方这种模式已经废弃，但是 future 项目还是暂时采用这种模式。

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-debezium/demo-debezium-embedded`

启动相关容器

```bash
docker compose up -d
```

启动 SpringBoot 服务

在数据库中执行以下 SQL 后，观察 Debezium 在 SpringBoot 应用输出的日志

```sql
insert into t_user(username,`password`,createTime) values('user2','123456',now());

update t_user set username='userx' where username='user2';

delete from t_user where username='userx';
```

