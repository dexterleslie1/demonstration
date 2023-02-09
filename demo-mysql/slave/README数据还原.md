# 数据库还原应急处理方案

## 还原前检测步骤

* 注意： 使用slave-xxx-restore原來的數據庫名稱還原數據，因為binlog中有use xxx記錄

* docker exec -it slave-xxx-live /bin/bash 接入同步容器后 stop slave; 命令停止同步进程
* 查看amazon最近单的时间
* 查看amazon最近的全量备份是否存在
* 分析google数据损坏的原因
* google binlog是否损毁，如果没有损毁查看binlog最新单的时间是不是比amazon的单新，则使用google全量备份+binlog还原数据库，amazon单最新则使用amazon还原

## 使用google还原数据库步骤

* 切换到工作目录，准备数据还原容器
dcli mariadb slave_restore_pre

* 重建数据数据库
docker exec -it slave-xxx-restore /bin/bash
drop database xxx;
create database xxx;

* 找到google最新的全量备份还原并查看binlog位置信息
docker cp /data/backup-xxx/2022-06-07/xxxx.tar.gz slave-xxx-restore:/root/
docker exec -it slave-xxx-restore /bin/bash
cd /root/
tar -xvzf xxx.tar.gz
mysql -uroot -p xxx < xxx.sql
head -n 50 xxx.sql | grep "CHANGE MASTER"

* 定位到binlog位置信息后到google现在相关binlog
tar -xvzf binlog.tar.gz master1.000097 master.000098
docker cp binlog.tar.gz slave-xxx-restore:/root/

* 定位drop database SQL位置
mysqlbinlog master1.000098 | grep -i -C 50 "drop database"
mysqlbinlog master1.000097 --start-position=binlog位置 > 1.sql
mysql -uroot -p xxx < 1.sql
mysqlbinlog master1.000098 --stop-position=删除数据库binlog位置 > 1.sql

* 确定数据是否还原成功
use xxx;
select * from xxx order by id desc limit 1\G;

## 使用amazon还原数据库步骤

* 切换到工作目录，准备数据还原容器
dcli mariadb slave_restore_pre

* 重建数据库
drop database xxx;
create database xxx;

* 使用全量备份还原数据库
docker cp /data/slave-xxx/fullbackup/2022-06-07.gz slave-xxx-restore:/root/
gzip -dkc fullybackup-xxx.gz > restore.sql
mysql -uroot -p123456 xxx < restore.sql 

* 确定binlog文件和日志position
head -n 50 restore.sql | grep "CHANGE MASTER"

* 确定使用binlog还原数据库

mysqlbinlog slave-bin.00000xx | grep -i -C 50 "drop database"
mysqlbinlog slave-bin.00000xx --start-position=xxxx > 1.sql
mysqlbinlog slave-bin.00000xx --stop-position=xxxx > 1.sql

* 确定数据是否还原成功
use xxx;
select * from xxx order by id desc limit 1\G;

## 导出还原后数据

mysqldump -uroot -p --single-transaction --quick --lock-tables=false xxx > xxx.sql
tar -cvzf xxx.tar.gz xxx.sql
docker cp slave-xxx-live:/root/xxx.tar.gz .

