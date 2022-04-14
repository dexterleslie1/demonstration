# 演示Jmeter使用

## Jmeter cli使用

### 使用/home/xxx/xxx.jmx文件启动Jmeter测试
```
./jmeter -n -t /home/xxx/xxx.jmx
```

### 使用/home/xxx/xxx.jmx文件启动分布式Jmeter测试
```
# 启动所有远程主机分布式测试
./jmeter -n -t /home/xxx/xxx.jmx -r

# 启动指定远程主机分布式测试
./jmeter -n -t /home/xxx/xxx.jmx -R 192.168.1.1,192.168.1.2
```

### 停止分布式测试（NOTE：不能关闭master进程，否则master无法接收停止信号转发给slave以达到停止测试）

> 参考资料  
> [jmeter-stop-remote-server](https://stackoverflow.com/questions/33511399/jmeter-stop-remote-server)

```
# 停止分布式测试
./shutdown.sh
# 停止分布式测试
./stoptest.sh
```