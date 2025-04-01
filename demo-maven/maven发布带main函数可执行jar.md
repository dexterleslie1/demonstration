# 使用`maven`发布带`main`函数可执行的`jar`

>[maven打包指定main函数入口](https://blog.csdn.net/wanbf123/article/details/81536140)

1. 编译演示例子，[例子详细请参考](https://github.com/dexterleslie1/demonstration/tree/master/demo-maven/demo-maven-package-runnable-jar-main)

   ```bash
   mvn package
   ```

2. 运行演示例子

   ```bash
   java -jar target/demo.jar
   ```

   