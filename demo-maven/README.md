## mvn命令使用

### 编译指定模块

```
# https://stackoverflow.com/questions/1114026/maven-modules-building-a-single-specific-module
# -am表示-also-make自动编译依赖的子模块
mvn -pl ../xxx-module -am package
```



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



## maven wrapper 用法

参考 https://maven.apache.org/wrapper/

运行以下命令自动设置 maven wrapper， -Dmaven=3.5.4 指定 maven 版本为 3.5.4

```shell
mvn wrapper:wrapper -Dmaven=3.5.4
```

替换 .mvn/wrapper/maven-wrapper.properties 中 distributionUrl 为 https://bucketxyh.oss-cn-hongkong.aliyuncs.com/maven/apache-maven-3.5.4-bin.zip，因为在国内下载 https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.5.4/apache-maven-3.5.4-bin.zip 很慢

使用 maven wrapper 编译项目，如果本地没有 maven 则会自动下载并配置 maven 环境，参考此链接指定 maven settings.xml 文件 https://gist.github.com/kbastani/d4b4c92969ec5a22681bb3daa4a80343

```shell
./mvnw clean package -s .mvn/wrapper/settings.xml
```



## maven 插件



### 使用 maven-clean-plugin 插件删除指定文件

参考

> demo-mave-clean-plugin

执行 mvn clean 时 maven-clean-plugin 会根据配置删除指定文件

```sh
mvn clean
```



## maven设置编译jdk版本

```xml
<properties>
    <maven.compiler.target>1.8</maven.compiler.target>
    <maven.compiler.source>1.8</maven.compiler.source>
</properties>
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

