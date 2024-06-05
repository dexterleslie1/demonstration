# `mvn`命令

## 安装构件到本地`.m2`中

编译并安装构件到本地

```bash
mvn package install
```

编译并安装构件到本地，单不运行测试

```bash
mvn package -Dmaven.test.skip install
```

在多模块项目的`parent`目录中指定运行`../chatapi`目录中的`spring-boot-maven`插件

```bash
mvn spring-boot:run -pl ../chatapi
```

编译指定模块

> 参考`stackoverflow` [链接](https://stackoverflow.com/questions/1114026/maven-modules-building-a-single-specific-module)
>
> 其中选项`-am`是`--also-make`的缩写，表示自动编译模块所依赖的其他模块。

```bash
mvn -pl ../chat-common -am clean install
```

