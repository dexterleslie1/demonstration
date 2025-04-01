# `performance_schema`用法

## 什么是`performance_schema`呢？

`performance_schema`是MySQL数据库中的一个内置系统数据库，主要用于监控和收集数据库运行过程中的性能统计信息和指标。以下是关于`performance_schema`的详细介绍：

一、基本概念

- **生产者（Instruments）**：用于采集MySQL中各种各样的操作产生的事件信息，对应配置表中的配置项可以称为监控采集配置项。
- **消费者（Consumers）**：对应的消费者表用于存储来自Instruments采集的数据，对应配置表中的配置项可以称为消费存储配置项。

二、主要特点和功能

1. **实时监控**：提供了一种在数据库运行时实时检查Server内部执行情况的方法，通过监控数据库内部的事件来实现。
2. **性能分析**：通过收集和分析MySQL的性能数据，如CPU使用率、内存使用率、I/O操作等，以及MySQL的各种操作，如查询、锁等，帮助用户了解数据库的性能瓶颈。
3. **丰富的数据表**：包含了多个表，用于记录MySQL服务器的各种性能指标数据，如`events_waits_current`、`threads`等。
4. **非持久化存储**：`performance_schema`中的配置和数据是保存在内存中的，MySQL实例停止时会全部丢失，因此需要在配置文件中持久化配置项。

三、使用场景

- **性能优化**：通过分析`performance_schema`中的数据，可以找出数据库的性能瓶颈，进而进行优化。
- **故障排查**：在数据库出现故障时，可以通过查询`performance_schema`中的数据来定位问题。
- **监控和报告**：生成数据库的性能监控报告，为数据库的日常运维提供依据。

四、启用和配置

- **启用**：在MySQL 5.7及以后的版本中，`performance_schema`默认是启用的。如果需要手动启用，可以在MySQL的配置文件（如`my.cnf`或`my.ini`）中添加`performance_schema=ON`。
- **配置**：`performance_schema`提供了多个启动选项，用于配置其行为。这些选项可以通过`mysqld --verbose --help`命令查看。

五、注意事项

- **性能开销**：启用`performance_schema`会增加MySQL的性能开销，因此建议在生产环境中谨慎使用。
- **数据准确性**：由于`performance_schema`中的数据是实时收集的，因此其准确性受到MySQL运行状态和配置的影响。
- **版本兼容性**：不同版本的MySQL在`performance_schema`的实现上可能存在差异，因此在使用时需要注意版本兼容性。

综上所述，`performance_schema`是MySQL中一个强大的性能监控和分析工具，通过合理使用可以大大提高数据库的性能和稳定性。然而，由于其性能开销和复杂性，需要在实际应用中权衡利弊并谨慎使用。



## 查看`mysql`是否支持`performance_schema`引擎

```sql
show engines;
```

输出中包含`performance_schema`支持为`YES`表示支持



## 启用`performance_schema`功能

注意：`mysql5.7`之后的版本默认启用`performance_schema`，`mysql5.6`之前版本或`mariadb`默认关闭`performance_schema`功能，需要手动启用。

查看`performance_schema`是否已启用，`ON`表示已启用

```sql
show variables like 'performance_schema';
```

手动启用`performance_schema`

- 编辑`my.cnf`

  ```nginx
  [mysqld]
  performance_schema=ON
  ```

- 重新启用`mysql`实例使`performance_schema`配置生效



## 显示`performance_schema`所有表

```sql
# 切换到performance_schema数据库
use performance_schema;

# 显示performance_schema数据库下所有表
show tables;
```



## 通过`setup_instruments`和`setup_consumers`启用或禁用采集和存储的指标

### 什么是`setup_instruments`和`setup_consumers`？

在MySQL的性能监控和调优环境中，特别是与性能模式（Performance Schema）相关的上下文中，`setup_instruments` 和 `setup_consumers` 是两个重要的配置部分，它们分别用于启用或禁用特定的性能监控仪器（instruments）和消费者（consumers）。

**Performance Schema**

MySQL的Performance Schema是一个用于监控MySQL服务器性能的功能。它提供了详细的服务器运行时性能数据，帮助开发者、数据库管理员等了解MySQL的内部运作情况，以便进行性能调优。

**setup_instruments**

`setup_instruments` 是指配置哪些性能监控仪器（instruments）应该被启用或禁用。在MySQL中，性能监控仪器代表了可以收集的数据点，比如等待事件、语句、文件I/O操作等。通过配置`setup_instruments`，用户可以精细控制哪些数据点被监控，以便减少监控开销并专注于对性能分析有用的数据。

配置`setup_instruments`通常涉及到设置或修改`performance_schema.setup_instruments`表。例如，要启用或禁用某个特定的监控仪器，你可以更新该表的相关行。

**setup_consumers**

`setup_consumers` 是指配置哪些消费者（consumers）应该被启用或禁用。在Performance Schema中，消费者是指接收和处理来自监控仪器的数据的组件。这些消费者可以是文件、表或内存中的缓冲区等，它们将收集到的数据用于不同的目的，比如实时显示、记录到文件或用于后续分析。

通过配置`setup_consumers`，用户可以控制哪些消费者被用来处理收集到的性能数据。这有助于减少不必要的资源消耗，并确保数据被发送到最需要的地方。

配置`setup_consumers`通常涉及到设置或修改`performance_schema.setup_consumers`表。例如，如果你只对将性能数据保存到表中感兴趣，你可以启用那些将数据写入表的消费者，并禁用其他不必要的消费者。

**注意事项**

- 修改`setup_instruments`和`setup_consumers`的配置可能会对MySQL服务器的性能产生影响，特别是在高负载的情况下。因此，在做出更改之前，建议先在测试环境中进行评估。
- MySQL的性能模式（Performance Schema）从MySQL 5.5版本开始引入，并在后续版本中不断完善。不同版本的MySQL在性能模式的实现和配置选项上可能有所不同，因此建议查阅特定版本的官方文档以获取准确的信息。

### 启用或禁用采集和存储指标

当数据库初始化完成并启动时，并非所有的`instruments`（在采集配置项的配置表中，每一项都有一个开关字段，或为`YES`，或为`NO`）和`consumers`（与采集配置项类似，也有一个对应的事件类型保存表配置项，为`YES`表示对应的表保存性能数据，为`NO`表示对应的表不保存性能数据）都启用了，所以默认不会收集所有的事件。

可能你想检测的事件并没有打开，需要进行设置。可以使用如下两条语句打开对应的`instruments`和`consumers`，我们以配置监测等待事件数据为例进行说明。

打开等待事件的采集器配置项开关，需要修改`setup_instruments`配置表中对应的采集器配置项。

```sql
# 查看等待事件的采集器配置项
select * from performance_schema.setup_instruments where name like 'wait%';

# 打开等待事件的采集器配置项
update performance_schema.setup_instruments set enabled='yes',timed='yes' where name like 'wait%';
```

打开等待事件的保存表配置项开关，修改`setup_consumers`配置表中对应的配置项。

```sql
# 查看等待事件的保存配置项
select * from performance_schema.setup_consumers where name like '%wait%';

# 打开等待事件的保存配置项
update setup_consumers set enabled='yes' where name like '%wait%';
```

配置好之后，我们就可以查看服务器当前正在做什么了。可以通过查询`events_waits_current`表来得知，该表中每个线程只包含一行数据，用于显示每个线程的最新监视事件（正在做的事情）。

```sql
select * from performance_schema.events_waits_current;
```

`_current`表中每个线程只保留一条记录，且一旦线程完成工作，该表中就不会再记录该线程的事件信息了。`*_history`表中记录每个线程已经执行完成的事件信息，但每个线程的事件信息只记录`10`条，再多就会被覆盖掉。`*_history_long`表中记录所有线程的事件信息，但总记录数量是`10000`行，超过会被覆盖掉。

`summary`表提供所有事件的汇总信息。该组中的表以不同的方式汇总事件数据（如：按用户、按主机、按线程等汇总）。



## 使用示例

>[系统数据库`performance_schema`详解](https://blog.csdn.net/b379685397/article/details/122829117)

### 查看最近执行失败的`SQL`语句

使用代码对数据库的某些操作（比如：使用`java`的`ORM`框架操作数据库） 报出语法错误，但是代码并没有记录`SQL`语句文本的功能，在`MySQL`数据库层能否查看到具体的`SQL`语句文本，看看是否哪里写错了？这个时候，大多数人首先想到的就是去查看错误日志。很遗憾，对于`SQL`语句的语法错误，错误日志并不会记录。

实际上，在`performance_schema`的语句事件记录表中针对每一条语句的执行状态都记录了较为详细的信息，例如：`events_statements_`表和`events_statements_summary_by_digest`表（`events_statements_`表记录了语句所有的执行错误信息，而`events_statements_summary_by_digest`表只记录了语句在执行过程中发生错误的语句记录统计信息，不记录具体的错误类型，例如：不记录语法错误类的信息）。下面看看如何使用这两个表查询语句发生错误的语句信息。

首先，我们模拟一条语法错误的`SQL`语句，使用`events_statements_history_long`表或者`events_statements_history`表查询发生语法错误的`SQL`语句：

```sql
select * from;
```

然后，查询`events_statements_history`表中错误号大于`0`的记录

```sql
select * from performance_schema.events_statements_history where mysql_errno>0 order by statement_id desc;
```



### 查看最近的事务执行信息

我们可以通过慢查询日志查询到一条语句的执行总时长，但是如果数据库中存在着一些大事务在执行过程中回滚了，或者在执行过程中异常中止，这个时候慢查询日志就爱莫能助了，这时我们可以借助`performance_schema`的`events_transactions_*`表来查看与事务相关的记录，在这些表中详细记录了是否有事务被回滚、活跃（长时间未提交的事务也属于活跃事务）或已提交等信息。

首先需要进行配置启用，事务事件默认并未启用

```sql
# 查看事务事件是否已启用
select * from performance_schema.setup_instruments where name like 'transaction%';
select * from performance_schema.setup_consumers where name like '%transaction%';

# 启用事务事件
update performance_schema.setup_instruments set enabled='yes',timed='yes' where name like 'transaction%';
update setup_consumers set enabled='yes' where name like '%transaction%';
```

开启一个新会话用于执行事务，并模拟事务回滚。

```sql
# 开启事务
begin;

# 模拟慢sql
select sleep(10);
```

查询活跃事务，活跃事务表示当前正在执行的事务事件，需要从`events_transactions_current`表中查询。`STATE=ACTIVE`为当前活跃的事务

```sql
select * from performance_schema.events_transactions_current order by thread_id desc;
```

会话中回滚事务

```sql
# 回滚事务
rollback;
```

查询事务事件当前表和事务事件历史记录表，可以看到在两表中都记录了一行事务事件信息，线程`ID`为`49`的线程执行了一个事务，事务状态为`ROLLED BACK`。

```sql
select * from performance_schema.events_transactions_history_long order by thread_id desc;
```

 但是当我们关闭会话以后，事务事件当前表中的记录就消失了。

```sql
select * from performance_schema.events_transactions_current order by thread_id desc;
```

