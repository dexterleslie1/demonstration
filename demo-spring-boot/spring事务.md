# 事务

>`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-transaction`



## 知识点

- `@Transactional timeout`用法
- `@Transactional rollbackFor noRollbackFor`用法
- `@Transactional isolation`用法
- 事物传播行为用法



## `@Transactional rollbackFor`

在Spring框架中，`@Transactional`注解用于声明一个方法或类的事务边界。`rollbackFor`属性用于指定哪些异常类型会导致事务回滚。

默认情况下，`@Transactional`注解的`rollbackFor`属性并没有显式设置，但Spring有一套默认的回滚规则：

1. **运行时异常（RuntimeException）**：默认情况下，所有未捕获的运行时异常都会导致事务回滚。这包括`RuntimeException`及其子类，例如`NullPointerException`、`IllegalArgumentException`等。
2. **错误（Error）**：所有未捕获的错误（`Error`及其子类）也会导致事务回滚。`Error`通常表示JVM级别的严重问题，比如`OutOfMemoryError`、`StackOverflowError`等。
3. **检查型异常（Checked Exception）**：默认情况下，检查型异常（即继承自`Exception`但不继承自`RuntimeException`的异常）不会导致事务回滚。如果你希望某个检查型异常也能导致事务回滚，你需要在`rollbackFor`属性中显式指定该异常类型。

例如：

```java
@Transactional(rollbackFor = CustomCheckedException.class)
public void someMethod() throws CustomCheckedException {
    // 方法实现
}
```

在这个例子中，如果`someMethod`抛出了`CustomCheckedException`，事务将会回滚，即使`CustomCheckedException`是一个检查型异常。

总结来说，`@Transactional`注解的默认回滚行为是对于所有未捕获的运行时异常和错误进行回滚，而检查型异常则不会触发回滚，除非在`rollbackFor`属性中显式指定。



## 事务传播行为

在Spring Boot中，事务管理是一个核心功能，它帮助开发者确保数据库操作的完整性，特别是在处理多步操作时。事务传播行为定义了当一个方法被调用时，如何与当前正在进行的事务进行交互。Spring Boot支持7种主要的事务传播行为，这些行为由`Propagation`枚举类中的常量表示，下面将详细解释：

1. **PROPAGATION_REQUIRED**（默认值）：
   - 如果当前存在事务，则加入该事务。
   - 如果当前没有事务，则创建一个新的事务。
   - 这是最常用的事务传播行为。
2. **PROPAGATION_SUPPORTS**：
   - 如果当前存在事务，则加入该事务。
   - 如果当前没有事务，则以非事务方式执行。
3. **PROPAGATION_MANDATORY**：
   - 如果当前存在事务，则加入该事务。
   - 如果当前没有事务，则抛出异常。
4. **PROPAGATION_REQUIRES_NEW**：
   - 创建一个新的事务，如果当前存在事务，则将当前事务挂起。
   - 这意味着新的事务将独立于当前事务运行，且不受其影响。
5. **PROPAGATION_NOT_SUPPORTED**：
   - 以非事务方式执行操作，如果当前存在事务，则将当前事务挂起。
   - 这意味着被调用的方法将在没有事务的上下文中运行。
6. **PROPAGATION_NEVER**：
   - 以非事务方式执行，如果当前存在事务，则抛出异常。
   - 这意味着被调用的方法不能在事务上下文中运行。
7. **PROPAGATION_NESTED**：
   - 如果当前存在事务，则在嵌套事务内执行。嵌套事务可以独立于外部事务回滚或提交，但如果外部事务回滚，所有更改也会被回滚。
   - 如果当前没有事务，则创建一个新的事务。

事务传播行为的选择对于确保系统数据完整性和一致性至关重要。开发者需要根据实际情况选择最适合的传播行为，以确保事务的正确性。配置事务传播行为通常通过在服务层的方法上使用`@Transactional`注解，并设置`propagation`属性来实现。例如：

```java
@Transactional(propagation=Propagation.REQUIRES_NEW)
public void performOperation() {
    // 方法体
}
```

上述代码段展示了如何在Spring Boot应用程序中设置事务传播行为为`REQUIRES_NEW`。总之，正确选择和配置事务传播行为对于构建健壮的、能够正确处理并发和错误情况的应用程序至关重要。