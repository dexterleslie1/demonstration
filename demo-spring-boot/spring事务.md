# 事务

>`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-transaction`



## `@Transactional timeout`用法

超时后事务自动回滚

```java
/**
 * 购买图书
 *
 * @param userId
 * @param bookId
 * @param bookNum
 * @param throwException 是否抛出异常
 * @param sleepSeconds   睡眠秒数，模拟事务处理超时
 */
@Transactional(timeout = 2/* 事务处理超过2秒失败并自动回滚 */,
        rollbackFor = CustomCheckedException.class/* 检查型异常（Checked Exception）**：默认情况下，检查型异常（即继承自`Exception`但不继承自`RuntimeException`的异常）不会导致事务回滚。如果你希望某个检查型异常也能导致事务回滚，你需要在`rollbackFor`属性中显式指定该异常类型 */
        , noRollbackFor = NullPointerException.class/* 默认规则会回滚NullPointerException，使用noRollbackException修改此默认行为 */)
public void buy(Long userId, Long bookId, Integer bookNum, boolean throwException, Integer sleepSeconds,
                boolean throwNullPointerException) throws CustomCheckedException {
    // 查询图书信息
    Book book = this.bookDao.get(bookId);
    BigDecimal price = book.getPrice();

    // 总价钱
    BigDecimal totalPrice = price.multiply(new BigDecimal(bookNum));
    // 扣除余额
    accountDao.updateBalance(userId, totalPrice);

    if (sleepSeconds != null && sleepSeconds > 0) {
        try {
            Thread.sleep(1000 * sleepSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    if (throwException)
        throw new CustomCheckedException("模拟业务异常");

    // 扣除库存
    bookDao.updateStock(bookId, bookNum);

    if (throwNullPointerException)
        throw new NullPointerException("模拟空指针异常");
}
```

```java
// 测试超时
try {
    this.bookService.buy(userId, bookId, bookNum, false, 3, false);
    Assertions.fail();
} catch (TransactionTimedOutException e) {
    Assertions.assertEquals(account1.getBalance(), this.accountDao.get(userId).getBalance());
    Assertions.assertEquals(book1.getStock(), this.bookDao.get(bookId).getStock());
}
```



## @Transactional rollbackFor、noRollbackFor

提醒：

- 检查型异常（Checked Exception）：默认情况下，检查型异常（即继承自`Exception`但不继承自`RuntimeException`的异常）不会导致事务回滚。如果你希望某个检查型异常也能导致事务回滚，你需要在`rollbackFor`属性中显式指定该异常类型
- 默认规则会回滚NullPointerException，使用noRollbackException修改此默认行为

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

### rollbackFor、noRollbackFor 用法

```java
/**
 * 购买图书
 *
 * @param userId
 * @param bookId
 * @param bookNum
 * @param throwException 是否抛出异常
 * @param sleepSeconds   睡眠秒数，模拟事务处理超时
 */
@Transactional(timeout = 2/* 事务处理超过2秒失败并自动回滚 */,
        rollbackFor = CustomCheckedException.class/* 检查型异常（Checked Exception）：默认情况下，检查型异常（即继承自`Exception`但不继承自`RuntimeException`的异常）不会导致事务回滚。如果你希望某个检查型异常也能导致事务回滚，你需要在`rollbackFor`属性中显式指定该异常类型 */
        , noRollbackFor = NullPointerException.class/* 默认规则会回滚NullPointerException，使用noRollbackException修改此默认行为 */)
public void buy(Long userId, Long bookId, Integer bookNum, boolean throwException, Integer sleepSeconds,
                boolean throwNullPointerException) throws CustomCheckedException {
    // 查询图书信息
    Book book = this.bookDao.get(bookId);
    BigDecimal price = book.getPrice();

    // 总价钱
    BigDecimal totalPrice = price.multiply(new BigDecimal(bookNum));
    // 扣除余额
    accountDao.updateBalance(userId, totalPrice);

    if (sleepSeconds != null && sleepSeconds > 0) {
        try {
            Thread.sleep(1000 * sleepSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    if (throwException)
        throw new CustomCheckedException("模拟业务异常");

    // 扣除库存
    bookDao.updateStock(bookId, bookNum);

    if (throwNullPointerException)
        throw new NullPointerException("模拟空指针异常");
}
```

```java
Account account1 = this.accountDao.get(userId);
Book book1 = this.bookDao.get(bookId);

// 测试抛出异常
try {
    this.bookService.buy(userId, bookId, bookNum, true, 0, false);
    Assertions.fail();
} catch (CustomCheckedException e) {
    Assertions.assertEquals(e.getMessage(), "模拟业务异常");
    Assertions.assertEquals(account1.getBalance(), this.accountDao.get(userId).getBalance());
    Assertions.assertEquals(book1.getStock(), this.bookDao.get(bookId).getStock());
}
```

```java
// 测试NullPointerException
try {
    this.bookService.buy(userId, bookId, bookNum, false, 0, true);
    Assertions.fail();
} catch (NullPointerException e) {
    // 验证书本库存
    Assertions.assertEquals(book1.getStock() - bookNum, this.bookDao.get(bookId).getStock());
    // 验证账户余额
    Assertions.assertEquals(account1.getBalance().subtract(book.getPrice().multiply(new BigDecimal(bookNum))), this.accountDao.get(userId).getBalance());
}
```



## `@Transactional isolation`用法



### 读未提交隔离级别，存在脏读问题

```java
// 读未提交隔离级别，存在脏读问题
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public Account getWithIsolationReadUncommitted(Long id) {
    return this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
            new BeanPropertyRowMapper<>(Account.class));
}
```

```java
Account account2 = this.accountDao.get(userId);
Book book2 = this.bookDao.get(bookId);

// 测试读未提交隔离级别的存在脏读问题
ExecutorService executorService = Executors.newCachedThreadPool();
Future future = executorService.submit(() -> {
    try {
        this.bookService.buy(userId, bookId, bookNum, true, 1, false);
    } catch (CustomCheckedException e) {
        throw new RuntimeException(e);
    }
});

Thread.sleep(100);
Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(bookNum)))
        , this.accountDao.getWithIsolationReadUncommitted(userId).getBalance());
```



### 读已提交隔离级别，解决脏读问题，但仍存在不可重复读问题

```java
// 读已提交隔离级别，解决脏读问题，但仍存在不可重复读问题
@Transactional(isolation = Isolation.READ_COMMITTED)
public Account[] getWithIsolationReadCommited(Long id) {
    Account account = this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
            new BeanPropertyRowMapper<>(Account.class));
    try {
        Thread.sleep(500);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }

    Account account1 = this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
            new BeanPropertyRowMapper<>(Account.class));

    // 返回值为数组是为了配合演示读已提交隔离级别存在不可重复读问题
    return new Account[]{account, account1};
}
```

```java
// 测试读已提交隔离级别解决脏读问题
future = executorService.submit(() -> {
    try {
        this.bookService.buy(userId, bookId, bookNum, false, 1, false);
    } catch (CustomCheckedException e) {
        throw new RuntimeException(e);
    }
});

Thread.sleep(100);
Assertions.assertEquals(account2.getBalance()
        , this.accountDao.getWithIsolationReadCommited(userId)[0].getBalance());

future.get();
Assertions.assertEquals(book2.getStock() - bookNum, this.bookDao.get(bookId).getStock());
Assertions.assertEquals(account2.getBalance().subtract(book.getPrice().multiply(new BigDecimal(bookNum))), this.accountDao.get(userId).getBalance());

// 测试读已提交隔离级别存在不可重复读问题
account2 = this.accountDao.get(userId);
book2 = this.bookDao.get(bookId);
future = executorService.submit(() -> {
    try {
        this.bookService.buy(userId, bookId, bookNum, false, 0, false);
        Thread.sleep(200);
        this.bookService.buy(userId, bookId, bookNum, false, 0, false);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
});

Thread.sleep(100);
Account[] accountWithIsolationReadCommitedArr = this.accountDao.getWithIsolationReadCommited(bookId);

future.get();
// 读取account余额同一个事物读取到两个不同的余额
Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(bookNum))), accountWithIsolationReadCommitedArr[0].getBalance());
Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(2 * bookNum))), accountWithIsolationReadCommitedArr[1].getBalance());
```



### 可重复读隔离级别，解决脏读，不可重复读问题

```java
// 可重复读隔离级别，解决脏读，不可重复读问题
@Transactional(isolation = Isolation.REPEATABLE_READ)
public Account[] getWithIsolationRepeatableRead(Long id) {
    Account account = this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
            new BeanPropertyRowMapper<>(Account.class));
    try {
        Thread.sleep(500);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }

    Account account1 = this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
            new BeanPropertyRowMapper<>(Account.class));

    // 返回值为数组是为了配合演示可重复读隔离级别解决了不可重复读问题
    return new Account[]{account, account1};
}
```

```java
// 测试可重复读隔离级别解决不可重复读问题
account2 = this.accountDao.get(userId);
book2 = this.bookDao.get(bookId);
future = executorService.submit(() -> {
    try {
        this.bookService.buy(userId, bookId, bookNum, false, 0, false);
        Thread.sleep(200);
        this.bookService.buy(userId, bookId, bookNum, false, 0, false);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
});

Thread.sleep(100);
Account[] accountArr = this.accountDao.getWithIsolationRepeatableRead(bookId);

future.get();
// 读取account余额同一个事物读取到两个不同的余额
Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(bookNum))), accountArr[0].getBalance());
Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(bookNum))), accountArr[1].getBalance());
```



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



### 使用事务传播特性实现当业务抛出异常，只回滚余额，不回滚库存

```java
// 使用事务传播特性实现当业务抛出异常，只回滚余额，不回滚库存
@Transactional(rollbackFor = CustomCheckedException.class)
public void buyWithTxPropagation(Long userId, Long bookId, Integer bookNum) throws CustomCheckedException {
    // 查询图书信息
    Book book = this.bookDao.get(bookId);
    BigDecimal price = book.getPrice();

    // 总价钱
    BigDecimal totalPrice = price.multiply(new BigDecimal(bookNum));
    // 扣除余额
    accountDao.updateBalanceWithPropagationRequired(userId, totalPrice);

    // 扣除库存
    bookDao.updateStockWithPropagationRequiresNew(bookId, bookNum);

    throw new CustomCheckedException("模拟业务异常");
}
```

```java
// 此事务和父级事务是同一个事务，如果父级事务抛出异常，此事务回滚
@Transactional(propagation = Propagation.REQUIRED)
public void updateBalanceWithPropagationRequired(Long id, BigDecimal total) {
    String sql = "update account set balance=balance-? where id=?";
    this.jdbcTemplate.update(sql, total, id);
}
```

```java
// 如果父级事务抛出异常，此事物不会回滚
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void updateStockWithPropagationRequiresNew(Long id, Integer stock) {
    jdbcTemplate.update("update book set stock=stock-? where id = ?", stock, id);
}
```

```java
// region 测试事务传播特性

Account account3 = this.accountDao.get(bookId);
Book book3 = this.bookDao.get(bookId);

try {
    this.bookService.buyWithTxPropagation(userId, bookId, bookNum);
    Assertions.fail();
} catch (CustomCheckedException ex) {
    Assertions.assertEquals(account3.getBalance(), this.accountDao.get(userId).getBalance());
    Assertions.assertEquals(book3.getStock() - bookNum, this.bookDao.get(bookId).getStock());
}

// endregion
```



## 事务失效有哪些情况呢？

### MyBatis 配置多数据源时

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-mysql-n-mariadb/demo-order-management-app)

配置事物管理器

```java
@Bean(name = "orderTransactionManager")
@Primary
public PlatformTransactionManager transactionManager(@Qualifier("orderDataSource") DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
}
```

在支持事务的方法中配置事务管理器

```java
// 抛出异常后回滚事务
@Transactional(rollbackFor = Exception.class, transactionManager = "orderTransactionManager")
public void createOrder(Long userId, Long productId, Integer amount) throws Exception {
```

