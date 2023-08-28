## debezium用法



### 说明

> debezium支持3中模式: 服务器、springboot嵌入式、kafka connector模式。
>
> todo: 完成服务器、kafka connector模式 demo。



### springboot嵌入式模式

> 参考 demo-debezium-embedded
> https://www.baeldung.com/debezium-intro
> https://debezium.io/documentation/reference/stable/development/engine.html
>
> NOTE: 第一次启动springboot应用没有offset信息时会全量读取一次数据库并回调springboot cdc接口。官方这种模式已经废弃，但是future项目还是暂时采用这种模式。