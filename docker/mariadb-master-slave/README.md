# 基于docker的mariadb master slave演示

## 运行demo

```
# 编译master镜像
sh build-master.sh

# 运行master
docker-compose -f docker-compose-master.yml up -d

# 全量备份master
sh fullyback-master.sh

# 编译slave镜像
sh build-slave.sh

# 修改.env varMasterHostIp

# 启动slave同步
docker-compose -f docker-compose-slave.yml up -d

# 查看slave同步状态
docker exec demo-mariadb-slave mysql -uroot -p123456 -e "show slave 'testdb' status\G"

# 实时和延迟同步show slave status\G;解析，Relay_Log_Pos=1338时，表示1338为relay log将要记录的position，但是未执行该position SQL（实时同步通过mysqlbinlog搜索relay log日志是无法搜索到该position，延迟同步能够搜索到该position）

# mysqlbinlog使用，左闭右开原则，表示只解析633（包含）位置到1035（不包含）位置的SQL
mysqlbinlog mysqld-relay-bin-testdb.000002 --start-position=633 --stop-position=1035

```

