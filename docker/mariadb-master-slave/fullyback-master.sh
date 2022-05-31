#!/bin/bash

# master全量备份

set -e

docker exec demo-mariadb-master mysqldump -uroot -p123456 --single-transaction --quick --lock-tables=false --master-data testdb > .testdb.sql

echo "成功全量备份到当前目录的.testdb.sql文件"

