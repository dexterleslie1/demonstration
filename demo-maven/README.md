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

