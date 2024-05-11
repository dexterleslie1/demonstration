# mysqldump的使用

1. 准备实验环境

   参考 <a href="/MySQL或MariaDB学习/使用docker-compose运行MySQL.html#单机版-mysql" target="_blank">使用 docker-compose 运行 MySQL</a>

2. 创建用于测试的数据库和数据表

   ```sql
   CREATE DATABASE IF NOT EXISTS demo_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   USE demo_test;
   
   CREATE TABLE IF NOT EXISTS `test1`(
           id             BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
           name           VARCHAR(64) NOT NULL COMMENT '名称',
           create_time    DATETIME NOT NULL COMMENT '创建时间'
   ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
   
   INSERT INTO `test1`(name,create_time) VALUES('用户1',now());
   INSERT INTO `test1`(name,create_time) VALUES('用户2',now());
   ```

3. 使用`mysqldump`导出数据

   > `--master-data` 选项与复制和主从复制相关，它允许你在备份中包含二进制日志（binlog）的坐标，以便稍后可以将这个备份用于基于时间点的恢复或设置新的从服务器。`--master-data`默认值是1。
   >
   > `--master-data` 有几个可能的值：
   >
   > - `1` 或 `=1`：这将在输出中添加 CHANGE MASTER TO 语句，其中包含二进制日志的文件名和位置。这通常用于设置新的从服务器。
   > - `2` 或 `=2`：这也将添加 CHANGE MASTER TO 语句，但输出会被注释掉。这允许你手动检查或编辑这些值，然后再用于设置从服务器。

   ```sh
   # 导出数据文件 demo_test.sql
   mysqldump -uroot -p --single-transaction --quick --lock-tables=false --master-data=2 demo_test > demo_test.sql
   
   # 压缩数据文件
   tar -czf demo_test.sql.tar.gz demo_test.sql
   
   # 解压数据文件
   tar -xvzf demo_test.sql.tar.gz
   ```

4. 使用导出数据文件导入数据到新的数据库

   ```sh
   # 新的数据库名为 demo_test2，则导入数据 SQL 如下：
   mysql -uroot -p demo_test2 < demo_test.sql
   ```

   