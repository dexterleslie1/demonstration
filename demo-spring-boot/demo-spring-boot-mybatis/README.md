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

## 获取参数值

> 参考 spring-boot-mybatis#ParametersMapper demo

### ${}和#{}区别

> https://blog.csdn.net/li_w_ch/article/details/109754949
>
> 1. 当传入的是字符串参数时，#{}会在值两边加上单引号，而${}不会在值两边加上单引号。
> 2. #{}会进行预编译处理，${}直接替换字符串。
> 3. 尽量使用 #{}，在很大程度上能够防止sql注入。
> 4. ${}方式一般用于传入数据库对象，例如列表和表名

## 查询

> 参考 spring-boot-mybatis#SelectMapper demo

## 模糊查询

> 参考 spring-boot-mybatis#SelectLikeMapper demo

## 获取自增主键id

> 参考 spring-boot-mybatis#GetAutoIncrementPrimaryKeyMapperTests demo

## 字段名和属性名不一致使用ResultMap解决

> 参考 spring-boot-mybatis#ResultMapMapperTests demo

## 关联关系

> 参考 spring-boot-mybatis#RelationshipMapperTests demo
>
> 一对一、多对一、一对多、多对多，延迟加载。

## 动态SQL

> 参考 spring-boot-mybatis#DynamicSqlMapperTests demo
>
> where和if、choose和when和otherwise、foreach、sql标签

## mybatis缓存

> 一级缓存和二级缓存

## mybatis分页插件

> https://blog.csdn.net/qq2844509367/article/details/126427670
>
> todo
