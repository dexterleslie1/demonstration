# `SQL`



## 注释

在 MySQL 中，注释用于在 SQL 代码中添加说明或备注，使代码更易于理解和维护。MySQL 支持两种类型的注释：单行注释和多行注释。`--` 和 `#` 是单行注释的两种不同方式，它们之间有一些细微的区别。

**`--` (双连字符)**

1. **语法**：

   - `--` 后面必须跟一个空格或控制字符（如换行符）才能开始注释内容。
   - 注释直到行末结束。

2. **示例**：

   ```sql
   SELECT * FROM users; -- 这是一个查询用户表的语句
   
   select * from operation_log where content='' OR 1=1 -- '
   ```

3. **特点**：

   - `--` 注释在某些 SQL 方言（如标准 SQL 和 PostgreSQL）中不被支持，但在 MySQL 和大多数其他数据库系统（如 SQL Server 和 Oracle 的 PL/SQL 块中）中是有效的。
   - 在某些客户端工具中，如果 `--` 后面紧跟着某些特定字符（如 `--` 本身的连续使用），可能会产生意外的行为或错误，尽管在 MySQL 中这种情况不常见。

**`#` (井号)**

1. **语法**：

   - `#` 后面可以直接跟注释内容，不需要空格。
   - 注释直到行末结束。

2. **示例**：

   ```sql
   SELECT * FROM users; # 这是一个查询用户表的语句
   
   select * from operation_log where content='' OR 1=1 #'
   ```

3. **特点**：

   - `#` 是 MySQL 和其他一些数据库系统（如 SQLite 和 Microsoft Access）中的标准单行注释符号。
   - 在某些情况下，`#` 注释可能更直观，因为它不需要额外的空格来分隔注释符号和注释内容。

**多行注释**

除了单行注释，MySQL 还支持多行注释，使用 `/* ... */` 语法：

```sql
/*
这是一个多行注释。
可以在这里写多行说明。
*/
SELECT * FROM users;
```

**总结**

- `--` 和 `#` 都可以用于单行注释。
- `--` 后面需要跟一个空格或控制字符，而 `#` 则不需要。
- 在 MySQL 中，两者在功能上是等价的，选择哪种方式主要取决于个人或团队的编码规范。

选择使用哪种注释方式通常取决于团队的偏好和编码规范，确保代码的一致性和可读性是最重要的。



## `insert ignore`

在 MariaDB（及 MySQL）中，`INSERT IGNORE` 是一种特殊的插入语法，用于**忽略插入过程中遇到的某些错误**（如唯一键冲突、非空约束违反等），使插入操作继续执行而不中断。它的核心作用是“静默跳过”不符合约束的记录，而非让整条 SQL 语句因个别错误而失败。


### **一、核心语法**
`INSERT IGNORE` 的语法与普通 `INSERT` 类似，只需在 `INSERT` 关键字后添加 `IGNORE` 即可：
```sql
INSERT IGNORE INTO table_name (column1, column2, ...)
VALUES (value1, value2, ...);
```
或批量插入：
```sql
INSERT IGNORE INTO table_name (column1, column2)
VALUES 
    (v1, v2),
    (v3, v4),
    (v5, v6);
```


### **二、适用场景：哪些错误会被忽略？**
`INSERT IGNORE` 会**忽略以下类型的错误**，但不会影响其他未冲突的记录：
#### 1. **唯一键冲突（Unique Key Violation）**
当插入的记录违反了表的**唯一索引（UNIQUE INDEX）**或**主键（PRIMARY KEY）**约束时，`INSERT IGNORE` 会跳过该条记录，继续处理后续数据。  
**示例**：  
假设表 `users` 的 `email` 字段有唯一索引：
```sql
-- 创建带唯一索引的表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE,
    name VARCHAR(50)
);

-- 插入第一条记录（成功）
INSERT IGNORE INTO users (email, name) VALUES ('alice@example.com', 'Alice');

-- 再次插入相同 email（唯一键冲突，被忽略）
INSERT IGNORE INTO users (email, name) VALUES ('alice@example.com', 'Alice Duplicate');
```
此时第二条插入会被忽略，不会报错，且 `users` 表中仅保留第一条记录。


#### 2. **非空约束违反（NOT NULL Violation）**
如果插入的记录中某个被标记为 `NOT NULL` 的字段未提供值（且无默认值），`INSERT IGNORE` 会跳过该记录。  
**示例**：  
假设表 `products` 的 `name` 字段是 `NOT NULL`：
```sql
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10,2)
);

-- 插入时未提供 name（违反 NOT NULL，被忽略）
INSERT IGNORE INTO products (price) VALUES (99.99);
```
该记录会被静默跳过，不会报错。


#### 3. **外键约束违反（Foreign Key Violation）**
当插入的记录引用了不存在的父表记录（如子表的外键字段指向父表不存在的主键），`INSERT IGNORE` 会跳过该记录（需注意：MariaDB 默认对 `InnoDB` 引擎的外键约束严格检查，部分场景可能仍报错，需结合具体配置）。  


### **三、不适用的场景：哪些错误仍会触发失败？**
`INSERT IGNORE` **不会忽略以下错误**，遇到时会直接终止整个插入操作并报错：
- **数据类型不匹配**（如将字符串插入 `INT` 类型字段）。
- **权限不足**（如用户无 `INSERT` 权限）。
- **存储引擎限制**（如 `FULLTEXT` 索引相关错误）。
- **触发器（Trigger）中的错误**（触发器执行失败会终止插入）。
- **磁盘空间不足**等底层错误。  


### **四、关键行为特点**
1. **受影响的行数（Affected Rows）**  
   对于被忽略的记录，`INSERT IGNORE` 返回的受影响行数为 `0`；成功插入的记录返回 `1`。  
   可通过 `ROW_COUNT()` 函数获取实际受影响的行数（需在 SQL 执行后立即调用）。

   **示例**：
   ```sql
   INSERT IGNORE INTO users (email, name) VALUES ('bob@example.com', 'Bob');
   SELECT ROW_COUNT();  -- 成功插入返回 1，冲突被忽略返回 0
   ```


2. **自增主键（AUTO_INCREMENT）的行为**  
   如果插入的记录因唯一键冲突被忽略，**自增主键不会递增**。例如：
   ```sql
   CREATE TABLE logs (
       id INT PRIMARY KEY AUTO_INCREMENT,
       event_type VARCHAR(20) UNIQUE
   );
   
   INSERT IGNORE INTO logs (event_type) VALUES ('login');  -- id=1
   INSERT IGNORE INTO logs (event_type) VALUES ('login');  -- 被忽略，id 不递增（仍为 1）
   INSERT IGNORE INTO logs (event_type) VALUES ('logout'); -- id=2
   ```


### **五、与 `ON DUPLICATE KEY UPDATE` 的对比**
`INSERT IGNORE` 和 `ON DUPLICATE KEY UPDATE` 都用于处理唯一键冲突，但行为不同：
| 特性             | `INSERT IGNORE`                | `ON DUPLICATE KEY UPDATE`          |
| ---------------- | ------------------------------ | ---------------------------------- |
| 冲突时的行为     | 跳过冲突行，不插入             | 执行 `UPDATE` 更新现有行           |
| 是否修改现有数据 | 否                             | 是（需指定更新逻辑）               |
| 适用场景         | 允许忽略重复数据（如日志去重） | 需要更新重复数据（如统计次数累加） |


### **六、使用注意事项**
1. **数据一致性风险**  
   `INSERT IGNORE` 会静默跳过错误，可能导致数据不完整（如本应报错的缺失字段未被发现）。建议仅在明确需要忽略重复数据的场景下使用，并配合应用层校验。

2. **唯一索引的必要性**  
   必须确保表中存在明确的唯一索引（或主键），否则 `INSERT IGNORE` 无法判断哪些记录是“重复”的，可能无法正确忽略错误。

3. **性能影响**  
   批量插入时，`INSERT IGNORE` 会逐行检查约束，可能比普通 `INSERT` 略慢（但远快于因错误回滚整个事务）。


### **总结**
`INSERT IGNORE` 是 MariaDB 中处理批量插入时重复数据的实用工具，适用于允许跳过部分冲突记录的场景（如日志收集、去重导入）。使用时需明确业务需求，确保忽略的错误不会影响数据完整性，并注意自增主键和受影响行数的行为特点。



## `on duplicate key update`

在 MariaDB（及 MySQL）中，`ON DUPLICATE KEY UPDATE` 是 `INSERT` 语句的扩展语法，用于**处理插入时遇到的唯一键（PRIMARY KEY 或 UNIQUE INDEX）冲突**。当插入的记录与表中已有记录的唯一键冲突时，该语法会**执行更新操作**（而非报错或忽略），从而实现“冲突时更新已有记录”的效果。


### **一、核心语法**
基本语法结构如下：
```sql
INSERT INTO table_name (column1, column2, ..., columnN)
VALUES (value1, value2, ..., valueN)
ON DUPLICATE KEY UPDATE 
    column1 = value1,          -- 冲突时更新 column1 为新值（或 VALUES(column1)）
    column2 = value2,          -- 冲突时更新 column2 为新值
    ...;
```
或批量插入时：
```sql
INSERT INTO table_name (column1, column2)
VALUES 
    (v1, v2),
    (v3, v4),
    (v5, v6)
ON DUPLICATE KEY UPDATE 
    column1 = VALUES(column1),  -- 使用插入语句中的值更新（推荐）
    column2 = VALUES(column2);
```


### **二、触发条件：唯一键冲突**
`ON DUPLICATE KEY UPDATE` 仅在**插入的记录违反唯一键约束**时触发更新操作。唯一键可以是：
- **主键（PRIMARY KEY）**：表的唯一标识字段（如 `id`）。
- **唯一索引（UNIQUE INDEX）**：表中其他字段的唯一约束（如 `email`、`username`）。

**示例：创建带唯一索引的表**
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- 主键（隐式唯一索引）
    email VARCHAR(100) UNIQUE,          -- 显式唯一索引
    name VARCHAR(50),
    login_count INT DEFAULT 0,          -- 登录次数统计
    last_login TIMESTAMP                -- 最后登录时间
);
```


### **三、核心行为：冲突时更新**
当插入的记录与已有记录的唯一键（如 `email`）冲突时，`ON DUPLICATE KEY UPDATE` 会执行以下操作：
1. **跳过插入**：不新增一条记录。
2. **更新已有记录**：根据 `ON DUPLICATE KEY UPDATE` 子句指定的字段和值，更新冲突记录的其他字段。


### **四、典型使用场景**
#### 1. **统计计数（如登录次数）**
每次用户登录时，若已存在该用户的记录，则更新登录次数和最后登录时间：
```sql
INSERT INTO users (email, name, login_count, last_login)
VALUES ('alice@example.com', 'Alice', 1, NOW())
ON DUPLICATE KEY UPDATE 
    login_count = login_count + 1,  -- 已有记录：登录次数+1
    last_login = NOW();             -- 最后登录时间更新为当前时间
```


#### 2. **同步数据（避免重复）**
从外部系统同步用户信息时，若用户已存在（通过 `email` 唯一标识），则更新其姓名：
```sql
INSERT INTO users (email, name)
VALUES ('bob@example.com', 'Bob New Name')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name);  -- 冲突时，用新插入的 name 更新已有记录的 name
```


#### 3. **批量处理（高效去重）**
批量插入多条记录，冲突时更新对应字段（减少数据库交互次数）：
```sql
INSERT INTO users (email, login_count)
VALUES 
    ('alice@example.com', 2),  -- 假设 alice 已存在，冲突时更新
    ('carol@example.com', 1)   -- carol 不存在，正常插入
ON DUPLICATE KEY UPDATE 
    login_count = VALUES(login_count);  -- 使用插入语句中的 login_count 值更新
```


### **五、关键细节与技巧**
#### 1. **使用 `VALUES()` 引用插入值**
在 `ON DUPLICATE KEY UPDATE` 子句中，推荐使用 `VALUES(column)` 函数引用插入语句中指定的值。例如：
```sql
-- 插入时 name 设为 'Alice'，冲突时更新为插入的 'Alice'
INSERT INTO users (email, name) 
VALUES ('alice@example.com', 'Alice')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name);  -- 等价于 name = 'Alice'
```
若直接写 `name = 'Alice'`，当插入值变化时（如动态生成），需手动修改 SQL，而 `VALUES()` 会自动关联插入值，更灵活。


#### 2. **自增主键的行为**
当插入因唯一键冲突被转换为更新操作时，**自增主键（AUTO_INCREMENT）不会递增**。例如：
```sql
-- 第一次插入：id=1（自增）
INSERT INTO users (email) VALUES ('alice@example.com');

-- 第二次插入冲突（email 已存在），触发更新（id 仍为 1）
INSERT INTO users (email) VALUES ('alice@example.com')
ON DUPLICATE KEY UPDATE 
    email = VALUES(email);  -- id 不会变为 2
```


#### 3. **触发器的执行**
`ON DUPLICATE KEY UPDATE` 触发更新时，**会触发 `BEFORE UPDATE` 和 `AFTER UPDATE` 触发器**，但不会触发 `BEFORE INSERT` 和 `AFTER INSERT` 触发器（因为插入操作被取消）。


#### 4. **批量插入的冲突处理**
批量插入时，每条记录的冲突是独立处理的：
- 某条记录冲突 → 仅更新该记录。
- 其他记录正常插入（无冲突时）。


### **六、与 `INSERT IGNORE` 的对比**
| 特性             | `ON DUPLICATE KEY UPDATE`                                    | `INSERT IGNORE`                |
| ---------------- | ------------------------------------------------------------ | ------------------------------ |
| 冲突时的行为     | 执行 `UPDATE` 更新已有记录                                   | 跳过冲突行，不插入             |
| 是否修改现有数据 | 是（需指定更新逻辑）                                         | 否                             |
| 适用场景         | 需要更新重复数据（如统计次数、最后更新时间）                 | 允许忽略重复数据（如日志去重） |
| 返回值           | 受影响行数：冲突行返回 2（1次删除+1次插入模拟），成功插入返回 1 | 冲突行返回 0，成功插入返回 1   |


### **七、注意事项**
1. **唯一索引的必要性**  
   必须确保表中存在**主键或唯一索引**，否则 `ON DUPLICATE KEY UPDATE` 不会触发更新，而是直接报错（如重复插入非唯一字段）。

2. **数据一致性**  
   更新逻辑需确保原子性。例如，若更新涉及多个字段，需避免因并发操作导致数据不一致（可通过事务或锁机制解决）。

3. **性能优化**  
   - 批量插入时，`ON DUPLICATE KEY UPDATE` 比“先查询是否存在，再决定插入或更新”更高效（减少网络和锁开销）。
   - 唯一索引的字段需尽量短（如 `INT` 比 `VARCHAR(100)` 更快）。

4. **SQL 注入风险**  
   动态拼接 SQL 时，需使用参数化查询（如预编译语句），避免因用户输入导致的 SQL 注入。


### **总结**
`ON DUPLICATE KEY UPDATE` 是 MariaDB 中处理唯一键冲突的高效工具，适用于需要“冲突时更新已有记录”的场景（如统计计数、数据同步）。通过合理设计唯一索引和更新逻辑，可以显著提升数据库操作的效率和数据一致性。