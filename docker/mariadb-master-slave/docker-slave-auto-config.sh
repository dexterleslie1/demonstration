#!/bin/bash

set -e

mysql -uroot -p123456 -P3306 -hdemo-mariadb-slave -e "create database testdb" && echo "成功创建testdb数据库"

mysql -uroot -p123456 -P3306 -hdemo-mariadb-slave testdb < .testdb.sql && echo "成功还原testdb数据库"

# 解析.testdb.sql获取master_log_file和master_log_pos
varMasterLogFile=`grep -r "CHANGE MASTER TO" .testdb.sql | awk -F '[ = , ;]' '{print $5}'`
varMasterLogPos=`grep -r "CHANGE MASTER TO" .testdb.sql | awk -F '[ = , ;]' '{print $8}'`

mysql -uroot -p123456 -P3306 -hdemo-mariadb-slave -e "change master 'testdb' to master_host='$MASTER_HOST_IP',master_port=3306,master_user='root',master_password='123456',master_log_file=$varMasterLogFile,master_log_pos=$varMasterLogPos" && echo "成功执行change master sql命令"

mysql -uroot -p123456 -P3306 -hdemo-mariadb-slave -e "start slave 'testdb'" && echo "成功启动slave复制进程"
