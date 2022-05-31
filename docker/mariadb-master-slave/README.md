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

```

