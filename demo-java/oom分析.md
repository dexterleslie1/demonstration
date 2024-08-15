# `OOM`分析

## 参考资料

>[Java性能监控与故障处理分析工具（Heap memory和Native memory）](https://juejin.cn/post/7128333688030363656)

## `dump`堆文件

1. 通过`java`命令行参数设置`OOM`错误时自动`dump`堆文件
2. 使用`arthas dump`堆文件
3. 使用`jmap`命令`dump`堆文件

## 使用`jprofiler`分析堆文件

通过`jprofiler`的`heap walker`的`Biggest objects`视图推断出内存泄漏的代码位置