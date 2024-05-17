## 验证rds是否符合需求

1. 通过控制台创建数据库
2. 配置数据库防火墙
   只允许内部网络连接
   临时允许外部指定ip连接
3. 创建并测试特权用户是否能够停止binlog
4. 创建并测试非特权用户是否能够停止binlog
5. 测试非特权用户是否能够访问其他未授权的数据库
6. 导入现有数据到数据库中
7. 配置应用app通过内网连接数据库
8. 模拟时间点还原数据
9. 分析上面数据库操作的审计日志
10. 配置数据库的slave模式实时同步外部数据库
    同步多个外部数据源
11. 停止数据库的slave模式并切换到master模式后配置应用app连接该数据库并测试服务是否正常
12. 模拟数据库的slave模式切换master模式后时间点还原数据

## 创建数据库

### 通过控制台创建数据库

数据库标识符会被使用到连接数据库的端点中，例如：数据库标识符db1，会生成名为db1.cpoqcayek8wb.us-east-1.rds.amazonaws.com的数据库连接端点。

选择 mariadb 10.4.x 版本

可用性与持久性>多可用区部署>创建备用实例（建议用于生产用途）如果打开会导致费用成倍增加。

打开CloudWatch日志记录功能。

关闭公开访问

### 配置数据库防火墙

默认情况下，数据库不能被公网或者ec2实例连接。需要通过在控制台中rds管理面板设置ec2和rds连接配置后，ec2实例才能够连接到rds实例。

#### 设置ec2实例连接到rds实例

ec2实例连接到rds实例的设置本质上是分别设置名为rds-ec2-1和ec2-rds-1防火墙规则，其中rds-ec2-1被绑定到rds实例上，ec2-rds-1被绑定到ec2实例上。

通过telnet命令测试rds实例是否连通

```sh
yum install telnet
telnet db1.cpoqcayek8wb.us-east-1.rds.amazonaws.com 3306
```

参考 https://blog.saeloun.com/2023/05/08/connect-aws-rds-with-ec2/

#### 设置外网连接到rds实例

通过控制台rds管理面板启用rds实例的公开访问功能，并且编辑rds实例的防火墙允许3306入栈流量。

通过telnet命令测试rds实例是否连通

```sh
yum install telnet
telnet db1.cpoqcayek8wb.us-east-1.rds.amazonaws.com 3306
```



### 测试用户是否能够停止binlog

创建数据库，创建表并插入数据

```sql
create database if not exists demo1 character set utf8mb4 collate utf8mb4_unicode_ci;

use demo1;

create table if not exists t1(
    
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;
```



#### 特权用户



#### 非特权用户
