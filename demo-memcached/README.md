## centOS8 安装 memcached

> https://www.tutorialspoint.com/how-to-install-and-configure-memcached-on-centos-8

```shell
# 安装 memcached
sudo dnf install memcached

# 编辑 memcached 配置文件 /etc/sysconfig/memcached
PORT="11211"
USER="memcached"
MAXCONN="1024"
CACHESIZE="64"
OPTIONS="-l 0.0.0.0"

# 设置 memcached 开机自启动
sudo systemctl start memcached
sudo systemctl enable memcached
```



## docker-compose 运行 memcached

> 参考当前目录 docker-compose.yaml



## spring-boot集成 memcached

> https://www.cnblogs.com/xifengxiaoma/p/11115130.html
>
> 参考 spring-boot-memcached demo

