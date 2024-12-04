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



## 知识点

- `AOP`切入点表达式`https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html`
- 切入点方法的`JointPoint`、`returning`、`throwing`参数
- 多个切面的执行顺序`@Order`用法
- `@Around`通知用法



## `AOP`编程

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-aop`

编程步骤：

- 导入`AOP`依赖
- 编写切面`Aspect`
- 编写通知方法
- 指定切入点表达式
- 测试`AOP`动态脂入