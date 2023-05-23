# ab(apache benchmark)工具使用

## 结论

> 慢单线程工具，没有充分使用操作系统多CPU。

## 使用

```
# centOS8安装ab工具
yum -y install httpd-tools

# ubuntu安装ab工具
sudo apt install apache2-utils

# 请求总数100w，并发线程1000，-r表示socket错误也不退出ab命令
ab -r -n 1000000 -c 512 http://192.168.1.102/

# -c表示并发线程512，-t表示最大测试持续时间60秒，-n表示最大测试请求总数1亿
ab -r -t 60 -n 100000000 -c 512 http://192.168.1.111/
```

