#!/bin/bash

set -e

# 保证/data目录已经存在
if [ ! -d "/data/" ]; then
        mkdir /data
fi

# 判断/data/redis-cluster目录是否已经存在，是则提示并退出安装
if [ -d "/data/redis-cluster" ]; then
        echo "/data/redis-cluster目录已经存在，不能安装redis集群"
        exit
fi

# 提示用户输入redis主机ip地址
read -p "输入redis主机ip地址：" varRedisHostIp
if [ "$varRedisHostIp" == "" ]; then
        echo "请输入redis主机ip地址"
        exit
fi

# 提示用户输入redis节点数
read -p "输入redis节点数(默认值3)：" varRedisNodeCount
if [ "$varRedisNodeCount" == "" ]; then
        varRedisNodeCount=3
fi

# 提示用户输入redis起始端口
read -p "输入redis起始端口，后续节点端口基于起始端口递增（默认值6390）：" varRedisNodeStartPort
if [ "$varRedisNodeStartPort" == "" ]; then
	varRedisNodeStartPort=6390
fi

# 确保系统没有安装redis
yum remove redis -y || true

# 编译并安装redis
yum install -y gcc make

cp ./redis-5.0.0.tar.gz /tmp/redis-5.0.0.tar.gz
(cd /tmp && rm -rf redis-5.0.0)
(cd /tmp && tar -xvzf redis-5.0.0.tar.gz)
(cd /tmp/redis-5.0.0/deps && PREFIX=/usr/ make hiredis jemalloc linenoise lua)
(cd /tmp/redis-5.0.0 && PREFIX=/usr/ make distclean)
(cd /tmp/redis-5.0.0 && PREFIX=/usr/ make install)

# 测试redis-cli --version和redis-server命令是否正常
redis-cli --version
redis-server --version

# 配置系统内核调优参数
grep -q '^vm.overcommit_memory' /etc/sysctl.conf && sed -i 's/^vm.overcommit_memory.*/vm.overcommit_memory=1/' /etc/sysctl.conf || echo 'vm.overcommit_memory=1' >> /etc/sysctl.conf
grep -q '^net.core.somaxconn' /etc/sysctl.conf && sed -i 's/^net.core.somaxconn.*/net.core.somaxconn=1024/' /etc/sysctl.conf || echo 'net.core.somaxconn=1024' >> /etc/sysctl.conf

grep -q '^echo never > /sys/kernel/mm/transparent_hugepage/enabled' /etc/rc.local || echo 'echo never > /sys/kernel/mm/transparent_hugepage/enabled' >> /etc/rc.local

# 配置redis各个节点redis.conf
for ((i=0; i<$varRedisNodeCount; i++))
do
        mkdir -p /data/redis-cluster/node$i
        cp -f redis.conf.template /data/redis-cluster/node$i/redis.conf
        varRedisPortTemporary=$(($varRedisNodeStartPort+$i))
        sed -i "s/^port 6380/port ${varRedisPortTemporary}/" /data/redis-cluster/node$i/redis.conf
done

# 启动redis各个节点
for ((i=0; i<$varRedisNodeCount; i++))
do
	# TODO 如果失败退出shell
        (cd /data/redis-cluster/node$i && redis-server redis.conf)
done

sleep 5

# 创建redis集群
varClusterCreationStr=""
for ((i=0; i<$varRedisNodeCount; i++))
do
        varRedisPortTemporary=$(($varRedisNodeStartPort+$i))
        varClusterCreationStr="$varClusterCreationStr $varRedisHostIp:$varRedisPortTemporary"
        if [[ $i -lt $varRedisNodeCount ]]; then
                varClusterCreationStr="$varClusterCreationStr "
        fi
done

echo "yes" | redis-cli --cluster create $varClusterCreationStr

# 配置开机自动启动redis集群
for ((i=0; i<$varRedisNodeCount; i++))
do
        grep -q "^sudo -i sh -c \"cd /data/redis-cluster/node${i} && /usr/bin/redis-server redis.conf\"" /etc/rc.local || echo "sudo -i sh -c \"cd /data/redis-cluster/node${i} && /usr/bin/redis-server redis.conf\"" >> /etc/rc.local
done

grep -q "sleep 20" /etc/rc.local || echo "sleep 20" >> /etc/rc.local

