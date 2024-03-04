## cloud source repository

> 参考 source-repository
> https://cloud.google.com/source-repositories/docs



## 启用api

> 参考 enable-service
>
> https://console.cloud.google.com/apis/library/sourcerepo.googleapis.com

## 调试 gcp 防火墙

### 前言

在使用 gcp 防火墙的来源 ip CIDR 过滤特性做源站保护时，可能会因为第三方的 cdn 节点的 CIDR 变动并且未更新到 gcp 防火墙中而导致错误拦截此类 cdn 节点。此时需要调试 gcp 防火墙并找出被拦截 cdn 节点所属的 CIDR。

### 方法

1、手动创建名为 deny-logging-debug 的 gcp 防火墙规则，拦截行为设置为 “拒绝”，来源 ip 地址 CIDR 为所有 ipv4 地址 0.0.0.0/0，优先级别为 65535（gcp 防火墙最低优先级别，为了让这条规则在所有其他规则后执行），打开防火墙日志记录功能。

2、绑定次 gcp 防火墙规则到虚拟机中。

3、通过点击防火墙规则中查看日志功能导航到日志查询分析功能中，此时点击 “清除所有过滤” 按钮，再点击查询按钮不断地加载最新日志以查看是否有显示状态为 "DENIDED" 的日志。

4、如果发现有状态为 ”DENIDED“ 的日志，此时展开此日志并查看来源的 ip 地址后，把次 ip 地址添加到对应放行的防火墙规则中。
