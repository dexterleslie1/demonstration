# 演示mybatis-plus使用

## 参考

- [mybatis-plus执行自定义SQL](https://cloud.tencent.com/developer/article/1531517)
- [指定entity id auto_increment类型](https://blog.csdn.net/lzhcoder/article/details/112860695)



## 使用枚举int保存到数据库

> https://blog.csdn.net/qq_50652600/article/details/126135351
>
> 配置步骤:
>
> - 在model对应的枚举字段配置注解@EnumValue
> - 在application.properties配置mybatis-plus.type-enums-package=com.future.demo@EnumValue注解包扫描路径
