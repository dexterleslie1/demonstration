# tomcat java web传统应用，用户生成war部署到tomcat服务器后协助cc测试

## 编译war
```shell script
mvn clean package
```

## 开发阶段使用maven tomcat插件调试应用
> [参考](https://blog.csdn.net/xiaojin21cen/article/details/78570254)

```shell script
# 运行tomcat7-maven-plugin
mvn tomcat7:run
```
