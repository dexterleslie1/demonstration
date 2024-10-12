## mvn命令使用

## 使用mvn命令运行unittest

> https://stackoverflow.com/questions/75939658/how-to-run-tests-using-maven

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



## 配置maven的mvn deploy用户凭证

在~/.m2/settings.xml中加入

```xml
<server>
    <id>yyd-nexus</id>
    <username>xxx</username>
    <password>xxx</password>
</server>
```



## maven 3.8.1之后版本blocked http

参考 https://gist.github.com/vegaasen/1d545aafeda867fcb48ae3f6cd8fd7c7

~/.m2/settings.xml添加如下mirror

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 http://maven.apache.org/xsd/settings-1.2.0.xsd">
  ...
    <mirrors>
        <mirror>
            <id>maven-default-http-blocker</id>
            <mirrorOf>external:dont-match-anything-mate:*</mirrorOf>
            <name>Pseudo repository to mirror external repositories initially using HTTP.</name>
            <url>http://0.0.0.0/</url>
            <blocked>false</blocked>
        </mirror>
    </mirrors>
  ...
</settings>
```



## 通过 dependency tree 解决依赖冲突

TODO：使用mvn help:effective-pom列出pom结构分析版本冲突问题。（未通过做实验验证此方法）

参考
https://maven.apache.org/plugins/maven-dependency-plugin/examples/resolving-conflicts-using-the-dependency-tree.html

显示项目中logback版本信息

```sh
mvn dependency:tree -Dincludes=*logback*
```

根据artifactId为log4j搜索

```sh
mvn dependency:tree -Dverbose -Dincludes=*:*log4j*
```

