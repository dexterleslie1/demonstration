# stress-ng使用

## 参考资料

[stress-ng使用](https://www.tecmint.com/linux-cpu-load-stress-test-with-stress-ng-tool/)

## 总结

stress-ng用于模拟产生系统负载，以便分析系统相关指标，不能用于硬件的性能测试。硬件的性能测试应该使用支持能够对比不同硬件得分的软件

## 安装

```shell
sudo apt install stress-ng
```

## 使用

```shell
# 模拟用户空间cpu负载，调用sqrt函数产生计算负载，8个进程，持续测试30秒
sudo stress-ng --cpu 8 -v --timeout 30s
```

