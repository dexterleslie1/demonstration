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

