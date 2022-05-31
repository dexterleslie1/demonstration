#!/bin/bash

set -e

mysql -uroot -p123456 -P3306 -hdemo-mariadb-master -e "grant replication slave on *.* to root@'%' identified by '123456'" && echo "成功创建slave复制用户"

mysql -uroot -p123456 -P3306 -hdemo-mariadb-master -e "flush privileges"
