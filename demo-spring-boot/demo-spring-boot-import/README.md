# 演示@Import用法

## 三种用法

### 引入普通类

> 参考demo pkg1

### 引入ImportSelector的实现类

> 参考demo pkg2

### 引入ImportBeanDefinitionRegistrar的实现类

> 作用
>
> - 利用ImportBeanDefinitionRegistrar手动向Spring容器注入Bean
> - 获取 @EnableXxx(name = "xxx") 注解中name值并注入到 Bean 实例中，实现Bean实例参数由注解提供
>
> 参考demo pkg3