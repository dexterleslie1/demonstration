# 演示mybatis使用

## 运行demo

```shell script
# 编译镜像
sh build.sh

# 运行容器
docker-compose up -d

# 运行单元测试
```

## 配置注解和xml并存的mapper编写方式

> https://blog.csdn.net/m0_67322837/article/details/126058576
>
> https://www.cnblogs.com/haha12/p/11839872.html

```shell
# 注解扫描方式，在Application启动类中添加@MapperScan注解
// 启用Mapper扫描
@MapperScan(value = {"com.future.demo.mybatis.mapper"}, annotationClass = Mapper.class)
public class Application {

# xml方式，编辑application.properties配置文件中添加如下内容
mybatis.mapper-locations=classpath:mapper/*.xml,classpath:mapper/**/*.xml
```

