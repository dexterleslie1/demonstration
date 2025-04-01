# `CPU`性能测试

```
# 使用sysbench产生cpu负载和分析
sysbench --test=cpu --cpu-max-prime=2000000 --threads=100 run
# 使用mpstat分析cpu负载，下面命令表每隔2秒收集一次cpu使用情况
mpstat -P ALL 2
# 使用vmstat分析cpu负载，vmstat列具体含义使用man vmstat命令查看，下面命令表示每隔2秒收集一次vmstat相关数据
vmstat 2
```

