# 演示eureka集群版的使用

> https://blog.csdn.net/weixin_43907332/article/details/94473626

## 启动和测试

```shell
# 编辑/etc/hosts添加如下内容
127.0.0.1 abc1.com
127.0.0.1 abc2.com
127.0.0.1 abc3.com

# 启动所有服务

# 分别访问eureka面板，面板中DS Replicas会显示其他eureka节点
http://localhost:9999/
http://localhost:10000/
http://localhost:10001/

# 关闭其中两台eureka后重启zuul和helloworld服务，访问如下url服务依旧正常
http://localhost:8080/api/v1/sayHello
```

