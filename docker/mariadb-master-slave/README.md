# 基于docker的mariadb master slave演示

## todo

* dockerize template模版化shell

## 运行demo

```
# 编译master镜像
sh build-master.sh

# 运行master
docker-compose -f docker-compose-master.yml up -d

# 全量备份master
sh fullyback-master.sh
```

