## mvn命令使用

### 编译指定模块

```
# https://stackoverflow.com/questions/1114026/maven-modules-building-a-single-specific-module
# -am表示-also-make自动编译依赖的子模块
mvn -pl ../xxx-module -am package
```

