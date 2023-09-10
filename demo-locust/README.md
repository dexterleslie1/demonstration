## locust单机入门



### 第一个测试

```
# ubuntu locust安装
# 环境组建版本: Python 3.8.10、pip 20.0.2、locust 2.16.1
# NOTE: 在ubuntu dcli开发环境因为enquiries依赖的click版本为7.1.5和新版locust依赖的click版本8.1.7冲突，所以导致执行locust -V报错，解决办法卸载click sudo pip uninstall click==7.1.5，再重新安装8.1.6版本sudo pip install click==8.1.7
# https://docs.locust.io/en/stable/installation.html
sudo pip install pysocks
sudo pip install locuse --proxy sock5://192.168.1.55:1080

# centOS8 locust安装
# 环境组建版本: Python 3.9.17、pip 23.2.1、locust 2.16.1
# 因为现有的python版本为3.6太低，所以先卸载现有的python再安装版本3.9
yum remove python3 -y
yum install python39 -y
# 升级pip到最新版本，NOTE: 太旧的pip版本会导致locust安装过程中报错失败
pip3 install pysocks
pip3 install pip --upgrade --proxy socks5://192.168.1.55:1080
pip install locust --proxy socks5://192.168.1.55:1080

# 查看当前locust版本验证是否安装成功
locust -V

# 编写测试，参考locustfile.py
# 启动locust web
locust

# 打开浏览器访问 http://0.0.0.0:8089
# 通过WebUI点击新增测试
```



## locust master、slave模式

> https://docs.locust.io/en/stable/running-distributed.html

```
# 启动master
locust --master

# 启动worker
locust --worker --master-host=xxx
```



## locust docker

> 参考docker-compose.yml
> https://docs.locust.io/en/1.5.2/running-locust-docker.html



## locust4j

> NOTE: 最新版的locust4j-2.2.1不能连接locust-2.16.1，估计官方locust4j停止维护，所以不采用locust+java方案测试jdbc。
> https://www.blazemeter.com/blog/locust-performance-testing
