# `spring-boot`的`DataSourceInitializer`使用

> 例子的详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-datasourceinitializer)
>
> 例子中演示创建两个`DataSourceInitializer`分别用户执行不同的`SQL`脚本，以模拟引用不同的插件执行不同的`SQL`脚本情况

`pom.xml`配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 通过指向parent的方式管理spring-boot依赖 -->
    <!--<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.7.RELEASE</version>
        <relativePath/>
    </parent>-->

    <groupId>com.future.demo</groupId>
    <artifactId>demo-spring-boot-datasourceinitializer</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <!-- DataSourceInitializer依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <!-- 通过dependencyManagement方式管理spring-boot依赖 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.2.7.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>
```

`MyDataSourceInitializer1`用于执行`db1.sql`和`store_procedure.sql`脚本

```java
public class MyDataSourceInitializer1 extends DataSourceInitializer {
    public MyDataSourceInitializer1(DataSource dataSource) {
        this.setDataSource(dataSource);

        // 初始化数据库DatabasePopulator
        // 组合populator，用于组合普通脚本和存储过程populator
        CompositeDatabasePopulator compositeDatabasePopulator = new CompositeDatabasePopulator();
        // 普通脚本populator
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        List<String> sqlFileList = Arrays.asList("db1.sql");
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        compositeDatabasePopulator.addPopulators(databasePopulator);

        // 存储过程populator
        databasePopulator = new ResourceDatabasePopulator();
        sqlFileList = Arrays.asList("store_procedure.sql");
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        // 设置存储过程sql文件内的语句结束符号为$$
        databasePopulator.setSeparator("$$");
        compositeDatabasePopulator.addPopulators(databasePopulator);

        this.setDatabasePopulator(compositeDatabasePopulator);

        // 清除数据库DatabasePopulator
        ResourceDatabasePopulator databaseCleaner = new ResourceDatabasePopulator();
        sqlFileList = null;
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        //在DataSourceInitializer中，setDatabaseCleaner方法的作用主要是用于在数据源（DataSource）销毁时执行一个清理脚本，确保数据库被清理并处于一个已知的状态，为其他使用者或者后续的测试留下干净的数据库环境。
        //具体来说，setDatabaseCleaner方法允许你设置一个DatabasePopulator对象，该对象会在DataSourceInitializer的destroy方法被调用时执行。这个DatabasePopulator可以配置为执行一个或多个SQL脚本，这些脚本用于清理或重置数据库。
        //在Spring框架中，DataSourceInitializer实现了InitializingBean和DisposableBean接口，因此它可以在Spring容器初始化数据源时执行初始化脚本，并在Spring容器销毁数据源时执行清理脚本。
        this.setDatabaseCleaner(databaseCleaner);
    }
}
```

`DataSourceInitializer2`用于执行`db2.sql`

```java
public class MyDataSourceInitializer2 extends DataSourceInitializer {
    public MyDataSourceInitializer2(DataSource dataSource) {
        this.setDataSource(dataSource);

        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        List<String> sqlFileList = Arrays.asList("db2.sql");
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        this.setDatabasePopulator(databasePopulator);

        // 清除数据库DatabasePopulator
        ResourceDatabasePopulator databaseCleaner = new ResourceDatabasePopulator();
        sqlFileList = null;
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        this.setDatabaseCleaner(databaseCleaner);
    }
}
```

把`DataSourceInitializer`注入到`spring`容器中才会被执行`SQL`脚本逻辑

```java
@Configuration
public class Config {
    // 注入DataSourceInitializer到spring容器中才会执行DataSourceInitializer中的逻辑
    // 注意：spring容器启动后每次都会执行DataSourceInitializer中配置的脚本，所以在编写脚本时需要保证脚本是幂等的
    @Bean
    public DataSourceInitializer dataSourceInitializer1(DataSource dataSource) {
        return new MyDataSourceInitializer1(dataSource);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer2(DataSource dataSource) {
        return new MyDataSourceInitializer2(dataSource);
    }
}
```

