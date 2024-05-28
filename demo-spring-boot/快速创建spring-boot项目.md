# 快速创建`spring-boot`项目

## 复制预先配置好的`spring-boot`模板项目

> 模板项目包括以下预配置：
>
> - 基于`docker`的`MariaDB`
> - 基于`docker`的`redis`集群

1. 克隆演示用途的仓库`demonstration`

   ```bash
   git clone --depth 0 https://github.com/dexterleslie1/demonstration.git
   ```

2. 复制仓库中的`demo-spring-boot-scaffold`项目

   ```bash
   cp -r demonstration/demo-spring-boot/demo-spring-boot-scaffold .
   ```

## 使用`IntelliJ IDEA`快速创建基于`maven`的`spring-boot`项目

1. 打开`IntelliJ IDEA`，点击`“Create New Project”`。
2. 在左侧面板中选择`“Spring Initializr”`。
3. 输入你的`Group`、`Artifact`、`Version`、选择你的`spring-boot`版本、选择项目类型为`maven`。
4. 暂时不需要添加任何依赖，因为我们想创建一个空项目。
5. 点击`“Next”`和`“Finish”`来创建项目。
6. 创建项目后运行`DemoSpringBootEmptyApplicationTests`中的测试用例验证项目是否正常运行。