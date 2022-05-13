# 演示使用验证码技术识别客户端是否人类redis单机版

## TODO

* 使用插件方式实现这个逻辑

## 运行demo

**修改.env后端ip地址**

```shell script
# 运行demo
# 启动redis单机版并修改application.properties
mvn tomcat7:run

# 访问首页
http://localhost:8080

# 启用检测机制，使用浏览器访问下面url
http://localhost:8080/api/v1/biz/setEnable.do?enabled=true
```
