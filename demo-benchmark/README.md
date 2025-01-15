# 基准测试

## 概念

### `top`命令`load average`解析

>[参考链接](https://en.wikipedia.org/wiki/Load_%28computing%29)

前面三个值分别对应系统当前1分钟、5分钟、15分钟内的平均load。load用于反映当前系统的负载情况，对于16核的系统，如果每个核上cpu利用率为30%，则在不存在uninterruptible进程的情况下，系统load应该维持在4.8左右。对16核系统，如果load维持在16左右，在不存在uninterrptible进程的情况下，意味着系统CPU几乎不存在空闲状态，利用率接近于100%。结合iowait、vmstat和loadavg可以分析出系统当前的整体负载，各部分负载分布情况。
