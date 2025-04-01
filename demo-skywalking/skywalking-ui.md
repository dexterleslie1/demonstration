# `skywalking ui`



## `Dashboard APM`功能

`Dashboard APM`功能中`Global`、`Service`、`Instance`、`Endpoint`关系为`Global`由多个`Service`组成，`Service`由多个`Instance`组成，`Instance`由多个`Endpoint`组成。

`Dashboard`>`APM`>`Global`功能用于查看所有`services`的`CPM(Call Per Minute)/PPM(Packets Per Minute)`、慢`services`（单位毫秒）、不健康`services`、慢端点（单位毫秒）、全局相应时间百分位（单位毫秒）。

`Dashboard`>`APM`>`Service`功能用于查看各个服务的详细监控信息，包括服务平均响应时间（单位毫秒）、服务响应时间百分位（单位毫秒）、成功率、服务的`CPM/PPM`、服务实例的`CPM/PPM`、慢服务实例（单位毫秒）、服务实例的成功率。

`Dashboard`>`APM`>`Instance`功能用于查看各个服务实例的详细监控信息，包括服务实例`CPM/PPM`、服务实例网络吞吐（单位字节）、服务实例的成功率、服务实例的响应延迟（单位毫秒）、服务实例`JVM CPU`，内存，`GC`相关信息。

`Dashboard`>`APM`>`Endpoint`功能用于查看服务实例的各个`Endpoint`详细监控信息，包括服务实例每个`Endpoint`的`CPM/PPM`、服务实例的慢`Endpoint`（单位毫秒）、服务实例的`Endpoint`成功率、各个`Endpoint`负载，平均响应时间（单位毫秒），平均响应时间的百分位（单位毫秒），成功率。



## `Topology`功能

通过图形的方式查看服务之间的拓扑关系。



## `Trace`功能



### 自动刷新和`Time Range`过滤条件自动设置为当前时间

在没有启用自动刷新功能时，每次点击`Search`按钮`Time Range`过滤条件不会自动设置为当前最新时间。

通过点击启用右上角`Auto`功能后，每次点击`Search`按钮`Time Range`过滤条件都会自动设置为当前最新时间。



### 左手链路调用记录排序切换

左手链路调用界面可用通过切换`Duration`或者`Start Time`排序链路调用记录。



### 根据`TraceID`查看链路调用

调用接口结束后通过`http`响应头获取`X-Request-Id`，如：`2a961cd9857441dc9f9f83f46f1055d9.55.17310784033110013`

填写`TraceID`后点击`Search`按钮，选中链路调用记录切换到`Table`视图，此时能够清晰地查看服务之间的调用关系和各个服务处理链路调用的耗时信息。

点击`View Logs`按钮支持查看当前链路调用对应的日志记录。



### 使用`Duration`过滤指定耗时范围的链路调用

`Duration`填写`10 - 5000`表示过滤链路调用耗时在`10`毫秒到`5000`毫秒间的调用。



## `Log`功能

调用接口结束后通过`http`响应头获取`X-Request-Id`，如：`2a961cd9857441dc9f9f83f46f1055d9.55.17310784033110013`

填写`TraceID`后点击`Search`按钮，相关链路调用日志会显示出来。