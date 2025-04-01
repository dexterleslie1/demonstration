# `mvn`命令



## mvn install

安装构件到本地`.m2`中

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

> 参考`stackoverflow`的`https://stackoverflow.com/questions/1114026/maven-modules-building-a-single-specific-module`
>
> 其中选项`-am`是`--also-make`的缩写，表示自动编译模块所依赖的其他模块。

```bash
mvn -pl ../chat-common -am clean install
```



## mvn test

> `https://stackoverflow.com/questions/75939658/how-to-run-tests-using-maven`

运行单元测试

```shell
# 在pom.xml中添加
<build>
    <pluginManagement>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>3.0.0</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
 
# 运行测试
mvn test
```



## mvn deploy

执行 mvn deploy 命令前配置用户凭证

在 ~/.m2/settings.xml 中加入

```xml
<server>
    <id>yyd-nexus</id>
    <username>xxx</username>
    <password>xxx</password>
</server>
```



## mvn dependency:tree

> `https://maven.apache.org/plugins/maven-dependency-plugin/examples/resolving-conflicts-using-the-dependency-tree.html`
>
> `todo` 使用 mvn help:effective-pom 列出 pom 结构分析版本冲突问题。（未通过做实验验证此方法）

解决依赖冲突

显示项目中 logback 版本信息

```sh
mvn dependency:tree -Dincludes="*logback*"
```

根据 artifactId 为 log4j 搜索

```sh
mvn dependency:tree -Dverbose -Dincludes="*:*log4j*"
```



## mvn 命令指定 Spring Profile

```bash
./mvnw test -Dtest=OrderPerfAssistantTests -Dspring.profiles.active=1w
```

- 使用 -Dspring.profiles.active=1w 指定 profile 为 1w

