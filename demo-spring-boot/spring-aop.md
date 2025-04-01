# `AOP`



## 概念

**连接点（Joinpoint）**：

- 定义：程序执行过程中的一些特定点，如方法的调用或异常的抛出，这些点可以被拦截以增强功能。
- 在Spring AOP中，连接点主要是指方法的调用。

**切入点（Pointcut）**：

- 定义：需要处理的连接点，通过切入点可以指定哪些连接点需要被增强。
- 切入点是一个描述信息，它修饰的是连接点，通过切入点表达式可以确定哪些连接点需要被拦截。

**切面（Aspect）**：

- 定义：封装了横切关注点的类，是切入点和通知（增强）的结合。
- 切面包含了要执行的横切逻辑，以及这些逻辑应该被应用到哪些连接点上。

**通知（Advice）**：

- 定义：也被称为增强，是由切面添加到特定的连接点的一段代码。
- 通知指定了在连接点被拦截后要执行的操作。
- 通知类型包括前置通知（Before）、后置通知（AfterReturning）、异常通知（AfterThrowing）、最终通知（After）和环绕通知（Around）。

**引介（Introduction）**：

- 定义：允许在现有的实现类中添加自定义的方法和属性。
- 引介是一种特殊的增强，通过它可以为类动态地添加新的接口或方法实现。

**目标对象（Target Object）**：

- 定义：被通知（增强）的对象，即代理的目标对象。
- 在没有AOP的情况下，目标对象需要实现所有业务逻辑。而在AOP的帮助下，目标对象可以只实现核心逻辑，而横切逻辑则通过AOP动态织入。

**织入（Weaving）**：

- 定义：将切面代码插入到目标对象上，从而生成代理对象的过程。
- 织入可以在不同的时间点进行，包括编译期、类装载期和运行期。Spring AOP主要采用的是运行期织入。

**代理（Proxy）**：

- 定义：是通知应用到目标对象之后被动态创建的对象。
- 代理对象包含了目标对象的所有方法，并在调用这些方法时应用相应的增强逻辑。



## `AOP`编程

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-aop`



### 编程步骤

步骤如下：

- 导入`AOP`依赖
- 编写切面`Aspect`
- 编写通知方法
- 指定切入点表达式
- 测试`AOP`动态脂入



### 切入点表达式

>`https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html`



### 切入点方法的`JoinPoint`、`returning`、`throwing`、`ProceedingJoinPoint`参数

ProceedingJoinPoint 是 Around 通知的方法参数

```java
@Around("pointcut()")
public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    try {
        this.invokedBefore = true;

        Object result = proceedingJoinPoint.proceed();

        this.invokedAfterReturning = true;

        return result;
    } catch (Throwable throwable) {
        this.invokedAfterThrowing = true;

        throw throwable;
    } finally {
        this.invokedAfter = true;
    }
}
```



JoinPoint 是 Before、AfterReturning、AfterThrowing、After 通知方法的参数

```java
// 方法执行前
// * 表示匹配多个任意字符或者任意类型
// .. 表示匹配多个任意参数或者多个层级的包路径
@Before("pointcut()"/* 引用上面定义的pointcut切入点 */)
public void before(JoinPoint joinPoint/* joinpoint获取当前切入点信息 */) {
    this.methodName = getMethodName(joinPoint);
    Object[] argsObject = joinPoint.getArgs();
    this.args = new int[]{(int) argsObject[0], (int) argsObject[1]};
    this.invokedBefore = true;

    this.sharedStore.sharedList.add("aspect1");
}

// 方法执行后没有抛出异常
@AfterReturning(value = "pointcut()",
        returning = "result"/* 指定返回值的形参变量 */)
public void afterReturning(JoinPoint joinPoint, Object result) {
    this.methodName = getMethodName(joinPoint);
    this.result = result;
    this.invokedAfterReturning = true;
}

// 方法执行后抛出异常
@AfterThrowing(value = "pointcut()", throwing = "e")
public void afterThrowing(JoinPoint joinPoint, Throwable e) {
    this.methodName = getMethodName(joinPoint);
    this.throwable = e;
    this.invokedAfterThrowing = true;
}

// 方法执行后无论是否抛出异常
@After("pointcut()")
public void after(JoinPoint joinPoint) {
    this.methodName = getMethodName(joinPoint);
    this.invokedAfter = true;
}
```



throwing 是 AfterThrowing 注解用于指定异常通知方法的 throwable 参数

```java
// 方法执行后抛出异常
@AfterThrowing(value = "pointcut()", throwing = "e")
public void afterThrowing(JoinPoint joinPoint, Throwable e) {
    this.methodName = getMethodName(joinPoint);
    this.throwable = e;
    this.invokedAfterThrowing = true;
}
```



returning 是 AfterReturning 注解用于指定返回通知方法的 return 参数

```java
// 方法执行后没有抛出异常
@AfterReturning(value = "pointcut()",
        returning = "result"/* 指定返回值的形参变量 */)
public void afterReturning(JoinPoint joinPoint, Object result) {
    this.methodName = getMethodName(joinPoint);
    this.result = result;
    this.invokedAfterReturning = true;
}
```



### 多个切面的执行顺序`@Order`用法

```java
// 定义切面的执行顺序，数值越小越先执行
@Order(1)
// 切面
@Component
@Aspect
public class MyAspect {
```

```java
// 定义切面的执行顺序，数值越小越先执行
@Order(2)
@Component
@Aspect
public class AMyAspect {
```



### `@Around`通知用法

```java
package com.future.demo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AroundAspect {
    public boolean invokedBefore = false;
    public boolean invokedAfterReturning = false;
    public boolean invokedAfterThrowing = false;
    public boolean invokedAfter = false;

    @Pointcut("execution(int com.future.demo..MyCalculator.*(int, int))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            this.invokedBefore = true;

            Object result = proceedingJoinPoint.proceed();

            this.invokedAfterReturning = true;

            return result;
        } catch (Throwable throwable) {
            this.invokedAfterThrowing = true;

            throw throwable;
        } finally {
            this.invokedAfter = true;
        }
    }

    public void reset() {
        this.invokedBefore = false;
        this.invokedAfterReturning = false;
        this.invokedAfterThrowing = false;
        this.invokedAfter = false;
    }
}
```