## 概念

这是一个非常核心的AOP（面向切面编程）问题。这些概念共同构成了AOP的基石。它们之间的关系可以用一个生动的比喻来理解，然后再深入技术细节。

### 一、 核心关系比喻：**医生与病人**

让我们用一个“医生看病”的比喻来串联这些概念：

- **Target Object （目标对象）**： 一个**病人**。
- **Joinpoint （连接点）**： 病人身上所有可能被检查或治疗的**部位**，比如喉咙、心脏、膝盖等。在代码中，这通常是类内部所有可以“增强”的点，如**方法调用、异常抛出、字段修改**等。
- **Pointcut （切点）**： 医生的**诊断**，它从所有可能的部位中，精确地定位到生病的部位。比如“病人的左膝盖”。在代码中，它是一个表达式，用来匹配和筛选出需要增强的特定 Joinpoint（例如，所有以 `delete`开头的方法）。
- **Advice （通知/增强）**： 医生采取的**具体治疗行为**，比如“给左膝盖打一针封闭针”。这是在切点（Pointcut）处（Joinpoint）要执行的**增强逻辑**。
- **Aspect （切面）**： 这位**骨科专家医生**本身。他掌握了“诊断膝盖问题（Pointcut）”的知识和“打封闭针（Advice）”的技能。切面 = 切点 + 通知。
- **Introduction （引入）**： 给这位病人（Target Object）**赋予一个新的身份或能力**。比如，在治疗膝盖的同时，宣布这位病人现在也是一位“医院荣誉会员”，拥有了新的会员权益（即实现了新的接口）。
- **Weaving （织入）**： **治疗的过程**。将“打封闭针”这个操作作用到“病人的左膝盖”上。在代码中，这是将切面代码应用到目标对象，从而创建代理对象的过程。
- **Proxy （代理）**： 治疗后的病人，他看起来还是原来那个人，但他的膝盖已经被增强了。当他想走路（调用方法）时，会先经过“绷带”或“护膝”（代理）的过滤和保护。这个代理对象就是AOP最终产生的、被增强过的目标对象。

------

### 二、 严谨的技术定义与关系

现在，我们脱离比喻，看它们的正式定义和关系。

1. **Target Object （目标对象）** **定义**： 被一个或多个切面所增强的对象。也叫做“被增强的对象”或“ advised object”。 **关系**： 是整个AOP过程的**核心目标**，其他所有概念都是围绕它展开的。
2. **Joinpoint （连接点）** **定义**： 在程序执行过程中一个明确的点，如方法调用、异常抛出、字段修改等。在Spring AOP中，连接点**永远代表方法的执行**。 **关系**： 是AOP能够进行增强的**所有可能的位置集合**，是“原材料”。
3. **Pointcut （切点）** **定义**： 一个匹配连接点的谓词（表达式）。它通过一个表达式来精确挑选出需要被增强的特定连接点。 **关系**： 对 Joinpoint 进行**筛选和定位**。**Pointcut 描述了“在何处（Where）”进行增强**。
4. **Advice （通知/增强）** **定义**： 切面在特定的连接点上执行的动作。Advice 类型包括 `around`（环绕）、`before`（前置） 和 `after`（后置）等。 **关系**： 是增强的**具体逻辑**。**Advice 描述了“做什么（What）”和“何时（When）做”**（Before， After等）。
5. **Aspect （切面）** **定义**： 一个关注点的模块化，这个关注点可能会横切多个对象。它封装了切点和通知。 **关系**： AOP的核心模块。**Aspect = Pointcut + Advice**。它将“在何处”和“做什么”组合成了一个可重用的单元。
6. **Introduction （引入）** **定义**： 允许向现有的类添加新的方法或属性。实际上，就是为类实现新的接口，从而在不修改原有类代码的情况下，为其动态添加新功能。 **关系**： 一种特殊的增强（Advice），它增强的是**类的结构**（添加新的方法），而不是业务逻辑的执行过程。它改变了对象的类型。
7. **Weaving （织入）** **定义**： 将切面应用到目标对象并创建代理对象的过程。这个过程可以在编译期、类加载期或运行期进行。Spring AOP默认使用**运行期织入**。 **关系**： 是AOP的“引擎”或“动作”，是**实现增强的物理过程**。没有织入，切面就只是静态的代码，无法发挥作用。
8. **Proxy （代理）** **定义**： 由AOP框架（如Spring）创建的一个对象，它包装了目标对象，并在目标对象的方法执行前后，加入了切面的逻辑。 **关系**： 是织入（Weaving）过程的**最终产物**。客户端代码调用的不是原始的目标对象，而是这个被增强过的代理对象。

------

### 三、 关系流程图

为了更清晰地展示它们的关系，可以看下面的流程图：

```
graph TD
    A[目标对象 Target Object] --> B{包含多个<br>连接点 Joinpoint}；
    C[切面 Aspect] --> D[定义切点 Pointcut]；
    C --> E[定义通知 Advice]；
    D --> F{匹配/筛选<br>特定的 Joinpoint}；
    F --> G[织入 Weaving]；
    E --> G；
    A --> G；
    G --> H[生成代理 Proxy]；
    
    I[引入 Introduction<br>（一种特殊增强）] --> C；
```

**总结一下核心逻辑：**

一个**切面（Aspect）** 定义了在**何处（通过切点-Pointcut匹配连接点-Joinpoint）** 以及**做什么（通知-Advice）**。AOP框架通过**织入（Weaving）** 过程，将这个切面应用到**目标对象（Target Object）** 上，最终生成一个功能被增强了的**代理对象（Proxy）**。而**引入（Introduction）** 是一种特殊的增强，它为类动态地引入了新的接口实现。



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

### 注解切面

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-boot-aop

自定义注解

```java
package com.future.demo;

import java.lang.annotation.*;

// 指定该注解只能用于方法
@Target(ElementType.METHOD)
// 注解在运行时可用，可以通过反射机制读取注解
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation {
    /**
     * 注解配置属性1
     *
     * @return
     */
    String p1() default "";

    /**
     * 注解配置属性2
     *
     * @return
     */
    String p2() default "";
}
```

在业务方法上使用自定义注解

```java
package com.future.demo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyAnnotationService {
    /**
     * 用于协助测试自定义注解aop
     */
    @MyAnnotation(p1 = "属性1", p2 = "属性2")
    public List<String> method1(List<String> paramList) {
        return paramList;
    }
}
```

编写自定义切面解析自定义注解

```java
package com.future.demo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Aspect
public class MyAnnotationAspect {

    // 定义Pointcut
    // 定义拦截所有@MyAnnotation方法的Pointcut表达式
    @Pointcut("@annotation(com.future.demo.MyAnnotation)")
    public void pointcut() {

    }

    // 定义Advice
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        Object param = joinPoint.getArgs()[0];
        if (param != null && param instanceof List) {
            // 在Advice中获取注解属性值
            // 获取方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 从方法签名中获取MyAnnotation注解实例
            MyAnnotation myAnnotation = signature.getMethod().getAnnotation(MyAnnotation.class);
            // 获取注解中的属性值
            String p1Value = myAnnotation.p1();
            String p2Value = myAnnotation.p2();

            // 把注解中的p1和p2值加入到参数中
            ((List) param).add(p1Value);
            ((List) param).add(p2Value);
        }
    }
}
```

测试自定义注解aop

```java
// 测试自定义注解aop
List<String> list = myAnnotationService.method1(new ArrayList<>() {{
    add("x1");
}});
Assertions.assertArrayEquals(Arrays.asList("x1", "属性1", "属性2").toArray(), list.toArray());
```