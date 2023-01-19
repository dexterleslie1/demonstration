# 演示自动执行SQL更新脚本

## 运行demo

```shell
# 编译镜像
sh build.sh

# 多次执行如下命令
docker-compose up

# 进入容器查看数据，会发现每次执行docker-compose up后表test1会增加多一条记录
docker exec -it demo-mariadb /bin/bash
mysql -uroot -p123456
use testdb
select * from test1\G;
```

