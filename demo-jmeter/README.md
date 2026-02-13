## 安装并运行

### 测试环境用的测试脚本

以下测试是`test.jmx`文件使用`JSR223`脚本测试`json`解析效率：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0" jmeter="5.4.1">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Test Plan" enabled="true">
      <stringProp name="TestPlan.comments"></stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.tearDown_on_shutdown">true</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Thread Group" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <intProp name="LoopController.loops">-1</intProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">8</stringProp>
        <stringProp name="ThreadGroup.ramp_time">0</stringProp>
        <boolProp name="ThreadGroup.scheduler">false</boolProp>
        <stringProp name="ThreadGroup.duration"></stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
        <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
      </ThreadGroup>
      <hashTree>
        <JSR223Sampler guiclass="TestBeanGUI" testclass="JSR223Sampler" testname="JSR223 Sampler" enabled="true">
          <stringProp name="cacheKey">true</stringProp>
          <stringProp name="filename"></stringProp>
          <stringProp name="parameters"></stringProp>
          <stringProp name="script">import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

String JSON=&quot;{\&quot;errorCode\&quot;:0,\&quot;errorMessage\&quot;:null,\&quot;dataObject\&quot;:\&quot;你好\&quot;}&quot;;
ObjectMapper mapper = new ObjectMapper();
JsonNode node = mapper.readTree(JSON);
int errorCode = node.get(&quot;errorCode&quot;).asInt();
if(errorCode&gt;0) {
	vars.put(&quot;registerSuccess&quot;,&quot;false&quot;);
}else {
	vars.put(&quot;registerSuccess&quot;,&quot;true&quot;);
}</stringProp>
          <stringProp name="scriptLanguage">groovy</stringProp>
        </JSR223Sampler>
        <hashTree/>
      </hashTree>
    </hashTree>
  </hashTree>
</jmeterTestPlan>

```



### `dcli`安装`jmeter master`模式

1. 安装`dcli`命令行工具

   ```bash
   sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
   ```

2. 安装`jmeter master`

   ```bash
   sudo dcli jdk install && sudo dcli jmeter install
   
   # 在提示中选择master模式
   ```

3. 运行`jmeter`测试，注意：`test.jmx`内容是上面提到的测试脚本。

   ```bash
   jmeter -n -t test.jmx
   ```



### `dcli`安装`jmeter master slave`模式

1. 安装`dcli`命令行工具

   ```bash
   sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
   ```

2. 在`master`虚拟机中安装`jmeter master`

   ```bash
   sudo dcli jdk install && sudo dcli jmeter install
   
   # 选择master模式
   # remote_hosts填写所有jmeter slave ip地址
   ```

3. 在`slave`虚拟机中安装`jmeter slave`

   ```bash
   sudo dcli jdk install && sudo dcli jmeter install
   
   # 选择slave模式
   # rmi监听ip地址填写slave本机ip地址，因为master需要使用rmi端口和slave通讯
   ```

4. 在`master`中启动分布式测试，注意：`192.168.235.144`是`jmeter slave`的`ip`地址

   ```bash
   jmeter -n -t test.jmx -R 192.168.235.144
   ```

5. 停止测试

   ```bash
   ./stoptest.sh
   ```


### Windows11安装

在https://archive.apache.org/dist/jmeter/binaries/下载apache-jmeter-5.6.2.zip，解压后运行bin/jmeter.bat脚本即可启动jmeter。

## 单机性能测试

>提示：使用`dcli`安装`jmeter master`模式。

启动[OpenResty](https://gitee.com/dexterleslie/demonstration/tree/main/demo-benchmark/demo-openresty-benchmark)辅助测试

```sh
# 复制部署配置
ansible-playbook playbook-deployer-config.yml --inventory inventory.ini

# 启动测试目标
ansible-playbook playbook-service-start.yml --inventory inventory.ini

# 销毁测试目标
ansible-playbook playbook-service-destroy.yml --inventory inventory.ini
```

启动`JMeter`测试，通过本站链接获取[perf-assistant.jmx](https://gitee.com/dexterleslie/demonstration/blob/main/demo-jmeter/perf-assistant.jmx)

>硬件配置：
>
>- 虚拟平台：Hypervisor:VMware ESXi, 7.0.3, 20328353、Model:PowerEdge R740xd、Processor Type:Intel(R) Xeon(R) Platinum 8269CY CPU @ 2.50GHz
>- 主机配置：16C8G
>- JMeter内存配置：4G

```bash
$ jmeter -n -t perf-assistant.jmx
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
Creating summariser <summary>
Created the tree successfully using perf-assistant.jmx
Starting standalone test @ October 29, 2025 5:46:17 PM CST (1761731177153)
Waiting for possible Shutdown/StopTestNow/HeapDump/ThreadDump message on port 4445
summary + 956448 in 00:00:12 = 76792.3/s Avg:     0 Min:     0 Max:    64 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary + 2750246 in 00:00:30 = 91674.9/s Avg:     0 Min:     0 Max:    91 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 3706694 in 00:00:42 = 87308.8/s Avg:     0 Min:     0 Max:    91 Err:     0 (0.00%)
summary + 2777830 in 00:00:30 = 92594.3/s Avg:     0 Min:     0 Max:    73 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 6484524 in 00:01:12 = 89497.3/s Avg:     0 Min:     0 Max:    91 Err:     0 (0.00%)
```



## 判断是否硬件瓶颈导致`jmeter`分布式测试无法提高性能

在研究`jmeter`分布式测试性能过程中，会遇到这样的情况：使用笔记本电脑启动多台虚拟机用于部署`jmeter`集群，但是在测试过程中继续添加更多的虚拟机后`QPS`却无法提升，这是因为笔记本电脑单机性能达到瓶颈。

怎么测试笔记本电脑单机性能达到瓶颈呢？可以通过创建多个虚拟机分别运行单机版的`jmeter`，通过此方法找出笔记本电脑最多运行多少个`jmeter`虚拟机就达到性能瓶颈。



## 非基于`kubernetes`的`jmeter`分布式测试

>注意：使用`dcli`分别在`master`虚拟机上安装`jmeter master`模式，在`slave`虚拟机上安装`jmeter slave`模式。

使用`/home/xxx/xxx.jmx`文件启动分布式`jmeter`测试

```bash
# 启动所有远程主机分布式测试
jmeter -n -t /home/xxx/xxx.jmx -r

# 启动指定远程主机分布式测试
jmeter -n -t /home/xxx/xxx.jmx -R 192.168.1.1,192.168.1.2
```

停止分布式测试，注意：不能关闭`master`进程，否则`master`无法接收停止信号转发给`slave`以达到停止测试

> [jmeter-stop-remote-server](https://stackoverflow.com/questions/33511399/jmeter-stop-remote-server)

```bash
# 停止分布式测试
./shutdown.sh

# 停止分布式测试
./stoptest.sh
```



## 基于`kubernetes`的`jmeter`分布式测试

>注意：推荐使用这个方式运行`jmeter`分布式测试，因为方便部署和管理。
>
>[Load Testing With Jmeter On Kubernetes and OpenShift](https://blog.kubernauts.io/load-testing-as-a-service-with-jmeter-on-kubernetes-fc5288bb0c8b)

`jmeter slave`以`DaemonSet`方式在`kubernetes`集群中运行。

示例的详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-jmeter/demo-jmeter-master-slave/k8s)

运行示例步骤：

1. 搭建`openresty`目标，用于协助`jmeter`性能测试，<a href="/性能测试/启动性能测试辅助目标.html#使用kubernetes启动" target="_blank">参考链接</a>

2. 因为此`jmeter`支持自定义`RedisBenchmarkSampler`插件用于性能测试`redis`，所以需要先编译此插件 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-jmeter/demo-jmeter-customize-plugin)

   ```bash
   # 编译插件命令
   mvn package
   ```

3. 编译`docker`镜像

   ```bash
   ./build-images.sh
   ```

4. 推送`docker`镜像

   ```bash
   ./push-images.sh
   ```

5. 搭建`kubernetes`集群，<a href="/kubernetes/安装k8s.html#使用二进制程序安装k8s" target="_blank">参考链接</a>

6. `ubuntu`配置`kubectl`客户端以直接在`ubuntu`上运行`jmeter`分布式测试，<a href="/kubernetes/kubectl命令.html#ubuntu安装kubectl命令" target="_blank">参考链接</a>

7. 启动测试

   ```bash
   ./start_test.sh jmeter.jmx
   ```

8. 测试期间通过`http://192.168.1.10:30001`（其中`192.168.1.10`是`k8s`集群的任何一个节点`ip`地址）登录`openresty`目标`grafana`查看压力测试相关数据

9. 测试期间通过`http://192.168.1.10:30000/`（其中`192.168.1.10`是`k8s master`节点的`ip`地址）登录`jmeter`的`grafana`查看`jmeter`监听器上报的测试数据

10. 停止测试

   ```bash
   ./stop_test.sh
   ```



## 基于`kubernetes`和非基于`kubernetes`的`jmeter`分布式测试结果对比

实验配置如下：

- `jmeter master`/`k8s master`虚拟机`centOS8-stream`，4核（无限制`CPU`）+`8G`内存
- 3台`jmeter slave`/`k8s worker`虚拟机`centOS8-stream`，2核（最高`4400MHz CPU`频率）+`4G`内存

实验结果：

- 基于`kubernetes QPS`最高`50k/s`左右
- 非基于`kubernetes QPS`最高`59k/s`左右

实验结论：非基于`kubernetes`性能高于基于`kubernetes`环境，可能是由于`jmeter`运行容器环境性能有所降低或者`kubernetes flannel`网络性能不如虚拟机之间直接通讯的网络性能高导致（`todo`：未排查得到证据证明这个猜想）。但是总体基于`kubernetes`环境的性能损耗还是在可接受范围内的。



## `GCP`平台测试基于`kubernetes`的`jmeter`分布式压测结果

实验配置如下：

- 1台`k8s master`虚拟机`centOS8-stream`，虚拟类型`e2`+4核+`8G`内存
- 5台`k8s worker`虚拟机`centOS8-stream`，虚拟类型`e2`+4核+`8G`内存
- 1台`openresty`辅助测试目标虚拟机`centOS8-stream`，虚拟类型`e2`+16核+`16G`内存

实验结果：`QPS`稳定在`164k/s`

实验结论：`GCP`平台上压测每台`k8s worker`能够产生约`32k/s`的`QPS`，`jmeter`集群产生的总`QPS`和`k8s worker`数量成正比的。



## `jmeter`调优

### 调整堆内存

编译`/usr/local/jmeter/bin/jmeter`添加`HEAP="-Xms2g -Xmx2g"`到`# resolve links`之后。



### 分布式测试调优`jmeter`结果样本`sender`模式

>[JMeter mode setting : Helps in optimizing the load generation](https://www.apexon.com/blog/jmeter-mode-setting-helps-in-optimizing-the-load-generation/)
>
>[Using a different sample sender](https://jmeter.apache.org/usermanual/remote-test.html#sendermode)

测试计划中的监听器将结果发送回客户端JMeter，后者将结果写入指定文件。默认情况下，样本在生成时同步发送回。这可能会影响服务器测试的最大吞吐量；在线程可以继续之前，必须返回采样结果。可以设置一些JMeter属性来改变这种行为。

1. 设置`statistical`模式

   此模式主要用于汇总采样，不采样所有字段。此外，采样率取决于批处理模式所描述的属性。样品将根据线组名称和样品标签进行分组。它只累积以下字段，其他字段在样本之间的变化将被忽略：
   将累积的字段为：1。时间流逝，2。延迟，3。字节数，4。样本计数和5。错误计数。
   这种模式在一定程度上减少了样本数据对网络的影响，并且在分布式环境中也将使用更少的客户端资源。因此，建议在考虑客户端系统性能、网络性能等因素后设置有效阈值。

   注意：经过测试设置此模式进行分布式测试单`slave`节点性能和单机`jmeter`性能相当

   编辑`/usr/local/jmeter/bin/jmeter.properties`设置`mode=Statistical`

2. 设置`num_sample_threshold`阈值，以减少压力测试样本回传次数导致测试间隙停顿。

   编辑`/usr/local/jmeter/bin/jmeter.properties`设置`num_sample_threshold=81920`



## 使用`docker`运行`jmeter+influxdb+grafana`

> [JMeter的基本使用（jmeter+influxDB+Grafana）](https://zhuanlan.zhihu.com/p/621684630?utm_id=0)
>
> 提醒：`grafana dashboards JSON`文件是通过手动导入`https://grafana.com/grafana/dashboards/5496`第三方模板后再导出为`JSON`得到的。

示例的详细配置请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-jmeter/demo-docker-with-influxdb-grafana)

`jmeter+influxdb+grafana`是为了图形化显示`jmeter`压测结果。

运行步骤：

- 编译镜像

  ```bash
  docker compose build
  ```

- 运行示例

  ```bash
  docker compose up -d
  ```

- 访问`grafana http://localhost:3000/`查看`jmeter`压测状态

- 启动测试制造`influxdb+grafana`数据

  ```bash
  ./start_test.sh
  ```




## `todo jmeter`单机性能调优

>`todo`：搜索一篇外国资料描述`jmeter`单机或者分布式测试的性能调优博客。调优后使得`jmeter`分布式测试在相同的硬件配置下发挥出更高的性能。
>
>`todo`：centos8 系统优化 centos内核优化（在gcp中测试jmeter时，测试以下配置是否有调优效果）`https://blog.51cto.com/u_16099314/10091045`

	1、内存
	2、线程数
	3、调整Stastic
	4、queue size=8192



## 调整`jmeter`日志级别

在开发插件过程中，需要调整`jmeter`日志级别为`DEBUG`以打印插件调试信息，[参考链接](https://www.blazemeter.com/blog/jmeter-logging)

通过`jmeter`菜单修改日志级别，`Options`>`Log Level`>`DEBUG`



## beanshell和jsr223

### 性能测试

>结论：jsr223 Groovy性能最高，然后是beanshell > jsr223 Javascript > jsr223 Java。

使用本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-jmeter/beanshell-and-jsr223)协助测试。

硬件配置：

- 虚拟平台：Hypervisor:VMware ESXi, 7.0.3, 20328353、Model:PowerEdge R740xd、Processor Type:Intel(R) Xeon(R) Platinum 8269CY CPU @ 2.50GHz
- 主机配置：8C8G

beanshell测试结果：

```sh
$ jmeter -n -t beanshell.jmx
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
Creating summariser <summary>
Created the tree successfully using beanshell.jmx
Starting standalone test @ October 29, 2025 3:54:15 PM CST (1761724455526)
Waiting for possible Shutdown/StopTestNow/HeapDump/ThreadDump message on port 4445
summary + 735555 in 00:00:14 = 51894.7/s Avg:     2 Min:     0 Max:  1178 Err:     0 (0.00%) Active: 219 Started: 219 Finished: 0
summary + 2146627 in 00:00:30 = 71554.2/s Avg:     3 Min:     0 Max:  2045 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 2882182 in 00:00:44 = 65246.1/s Avg:     3 Min:     0 Max:  2045 Err:     0 (0.00%)
summary + 2127924 in 00:00:30 = 70930.8/s Avg:     3 Min:     0 Max:  1372 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 5010106 in 00:01:14 = 67545.3/s Avg:     3 Min:     0 Max:  2045 Err:     0 (0.00%)
summary + 2104715 in 00:00:30 = 70157.2/s Avg:     3 Min:     0 Max:  1996 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 7114821 in 00:01:44 = 68297.5/s Avg:     3 Min:     0 Max:  2045 Err:     0 (0.00%)
```

jsr223 Java语言测试结果：

```sh
$ jmeter -n -t jsr223-java.jmx
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
Creating summariser <summary>
Created the tree successfully using jsr223-java.jmx
Starting standalone test @ October 29, 2025 3:57:07 PM CST (1761724627474)
Waiting for possible Shutdown/StopTestNow/HeapDump/ThreadDump message on port 4445
summary +   1857 in 00:00:22 =   83.8/s Avg:  2144 Min:     4 Max:  6659 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary +   2580 in 00:00:30 =   86.0/s Avg:  2978 Min:     4 Max:  6585 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary =   4437 in 00:00:52 =   85.1/s Avg:  2629 Min:     4 Max:  6659 Err:     0 (0.00%)
summary +   2738 in 00:00:30 =   91.2/s Avg:  2820 Min:     4 Max:  6726 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary =   7175 in 00:01:22 =   87.3/s Avg:  2702 Min:     4 Max:  6726 Err:     0 (0.00%)
summary +   2744 in 00:00:30 =   91.5/s Avg:  2799 Min:     4 Max:  6392 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary =   9919 in 00:01:52 =   88.4/s Avg:  2728 Min:     4 Max:  6726 Err:     0 (0.00%)
```

jsr223 JavaScript语言测试结果：

```sh
$ jmeter -n -t jsr223-javascript.jmx
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
Creating summariser <summary>
Created the tree successfully using jsr223-javascript.jmx
Starting standalone test @ October 29, 2025 4:00:17 PM CST (1761724817433)
Waiting for possible Shutdown/StopTestNow/HeapDump/ThreadDump message on port 4445
summary +  32323 in 00:00:12 = 2658.8/s Avg:    54 Min:     0 Max:  1295 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary +  92123 in 00:00:30 = 3070.8/s Avg:    83 Min:     0 Max:  1088 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 124446 in 00:00:42 = 2952.0/s Avg:    76 Min:     0 Max:  1295 Err:     0 (0.00%)
summary +  64862 in 00:00:30 = 2161.9/s Avg:   117 Min:     0 Max:  1260 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 189308 in 00:01:12 = 2623.5/s Avg:    90 Min:     0 Max:  1295 Err:     0 (0.00%)
summary +  66342 in 00:00:30 = 2211.5/s Avg:   116 Min:     0 Max:  1421 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 255650 in 00:01:42 = 2502.5/s Avg:    97 Min:     0 Max:  1421 Err:     0 (0.00%)
```

jsr223 Groovy语言测试结果：

```sh
$ jmeter -n -t jsr223-groovy.jmx
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
Creating summariser <summary>
Created the tree successfully using jsr223-groovy.jmx
Starting standalone test @ October 29, 2025 4:04:47 PM CST (1761725087739)
Waiting for possible Shutdown/StopTestNow/HeapDump/ThreadDump message on port 4445
summary + 1488739 in 00:00:12 = 125536.6/s Avg:     1 Min:     0 Max:   916 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary + 4582240 in 00:00:30 = 152741.3/s Avg:     1 Min:     0 Max:  1138 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 6070979 in 00:00:42 = 145034.0/s Avg:     1 Min:     0 Max:  1138 Err:     0 (0.00%)
summary + 4529011 in 00:00:30 = 150967.0/s Avg:     1 Min:     0 Max:   981 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 10599990 in 00:01:12 = 147511.0/s Avg:     1 Min:     0 Max:  1138 Err:     0 (0.00%)
summary + 4459892 in 00:00:30 = 148663.1/s Avg:     1 Min:     0 Max:  2136 Err:     0 (0.00%) Active: 256 Started: 256 Finished: 0
summary = 15059882 in 00:01:42 = 147850.3/s Avg:     1 Min:     0 Max:  2136 Err:     0 (0.00%)
```



## 脚本编程

>说明：使用jsr223 Groovy脚本编程，因为性能高。

### 打印日志

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-jmeter/demo-jsr223-groovy-scripting.jmx)

```groovy
// 打印日志
log.info("测试日志")
```

### JSR223 PostProcessor获取接口响应json

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-jmeter/perf-assistant.jmx)

```groovy
// 1. 获取响应
String responseJSON = prev.getResponseDataAsString()
log.info(responseJSON)
```

### JSR223 Groovy解析json字符串

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-jmeter/perf-assistant.jmx)

```groovy
// 解析json字符串
def jsonObject = new JsonSlurper().parseText(responseJSON)
def errorCode = jsonObject.errorCode
if(errorCode==null || errorCode>0) {
	def errorMessage = jsonObject.errorMessage
	log.info("登录失败，原因：" + errorMessage)
}
```

### JSR223 Groovy md5编码

```groovy
import java.security.MessageDigest

def originalPassword = vars.get("PASSWORD")
def md5Password = MessageDigest.getInstance("MD5").digest(originalPassword.bytes).encodeHex().toString()
vars.put("md5Password", md5Password)
//log.info("md5Password: " + md5Password)
```

### 根据响应errorCode标记样本失败

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-jmeter/perf-assistant.jmx)
>
>提示：在JSR223 PostProcessor中编写下面Groovy脚本。

```groovy
import groovy.json.JsonSlurper

// 1. 获取响应
String responseJSON = prev.getResponseDataAsString()
//log.info(responseJSON)

// 解析json字符串
def jsonObject = new JsonSlurper().parseText(responseJSON)
def errorCode = jsonObject.errorCode
if(errorCode==null || errorCode>0) {
	def errorMessage = jsonObject.errorMessage
	log.info("登录失败，原因：" + errorMessage)

	// 告诉JMeter这个样本失败
	// 标记样本为失败
    prev.setSuccessful(false)
    // 设置失败消息
    prev.setResponseMessage("登录失败，原因：" + errorMessage)
}
```

### 线程变量（当前线程内共享）

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-jmeter/perf-assistant.jmx)

保存Token到线程变量中

```groovy
import groovy.json.JsonSlurper

// 1. 获取响应
String responseJSON = prev.getResponseDataAsString()

...

def accessToken = jsonObject.data
// 保存到线程变量（当前线程内共享）
vars.put("ACCESS_TOKEN", accessToken)
```

在HTTP Header Manager中引用线程变量

![image-20251029175939393](image-20251029175939393.png)



## Random Controller和Random Order Controller的区别

**Random Controller** 和 **Random Order Controller** 在 JMeter 中有本质的区别，让我详细解释：

---

### **一、核心区别对比表**

| 特性         | Random Controller（随机控制器） | Random Order Controller（随机顺序控制器） |
| ------------ | ------------------------------- | ----------------------------------------- |
| **执行方式** | 每次迭代**只执行一个**子元素    | 每次迭代**执行全部**子元素                |
| **执行顺序** | 随机选择**一个**执行            | 随机**排序**后按序执行全部                |
| **执行数量** | 1/N（N个子元素）                | N/N（全部子元素）                         |
| **适用场景** | 模拟用户**选择不同路径**        | 模拟操作**顺序随机但完整**                |

---

### **二、详细工作原理**

#### **1. Random Controller（随机控制器）**
```xml
<RandomController>
  <hashTree>
    <HTTPSamplerProxy testname="API 1"/>
    <HTTPSamplerProxy testname="API 2"/>
    <HTTPSamplerProxy testname="API 3"/>
  </hashTree>
</RandomController>
```

**执行结果示例**：
- **迭代1**：执行 `API 2`（随机选择）
- **迭代2**：执行 `API 1`（随机选择）
- **迭代3**：执行 `API 3`（随机选择）
- **迭代4**：执行 `API 2`（随机选择）

**特点**：每次只执行**一个**请求，类似于"单选按钮"。

---

#### **2. Random Order Controller（随机顺序控制器）**
```xml
<RandomOrderController>
  <hashTree>
    <HTTPSamplerProxy testname="API 1"/>
    <HTTPSamplerProxy testname="API 2"/>
    <HTTPSamplerProxy testname="API 3"/>
  </hashTree>
</RandomOrderController>
```

**执行结果示例**：
- **迭代1**：执行 `API 2` → `API 3` → `API 1`（随机排序）
- **迭代2**：执行 `API 1` → `API 2` → `API 3`（随机排序）
- **迭代3**：执行 `API 3` → `API 1` → `API 2`（随机排序）

**特点**：每次执行**全部**请求，但顺序随机，类似于"洗牌"。

---

### **三、实际场景对比**

#### **场景1：用户行为模拟**
##### **Random Controller - 模拟用户选择**
```xml
<!-- 用户每次只做一件事 -->
<RandomController testname="用户随机行为">
  <hashTree>
    <HTTPSamplerProxy testname="浏览商品"/>
    <HTTPSamplerProxy testname="查看订单"/>
    <HTTPSamplerProxy testname="搜索内容"/>
    <HTTPSamplerProxy testname="个人中心"/>
  </hashTree>
</RandomController>
```
**结果**：用户可能浏览商品、查看订单、搜索内容，但**每次只做一件事**。

##### **Random Order Controller - 模拟完整流程**
```xml
<!-- 用户完成所有操作，但顺序随机 -->
<RandomOrderController testname="完整流程随机顺序">
  <hashTree>
    <HTTPSamplerProxy testname="登录"/>
    <HTTPSamplerProxy testname="浏览"/>
    <HTTPSamplerProxy testname="下单"/>
    <HTTPSamplerProxy testname="支付"/>
  </hashTree>
</RandomOrderController>
```
**结果**：用户完成登录、浏览、下单、支付**所有步骤**，但顺序可能不同。

---

#### **场景2：API 测试**
##### **Random Controller - 压力测试不同接口**
```xml
<!-- 测试不同接口的并发性能 -->
<RandomController testname="随机API压力测试">
  <hashTree>
    <HTTPSamplerProxy testname="用户查询接口"/>
    <HTTPSamplerProxy testname="订单查询接口"/>
    <HTTPSamplerProxy testname="商品查询接口"/>
  </hashTree>
</RandomController>
```

##### **Random Order Controller - 测试接口组合**
```xml
<!-- 测试接口组合的并发性能 -->
<RandomOrderController testname="接口组合测试">
  <hashTree>
    <HTTPSamplerProxy testname="创建用户"/>
    <HTTPSamplerProxy testname="创建订单"/>
    <HTTPSamplerProxy testname="更新库存"/>
  </hashTree>
</RandomOrderController>
```

---

### **四、配置参数详解**

#### **共同参数**
```xml
<boolProp name="InterleaveAcrossThreads">false</boolProp>
```
- **false**（默认）：每个线程独立随机
- **true**：所有线程共享随机序列

#### **特殊配置示例**
```xml
<!-- Random Controller 特殊配置 -->
<RandomController>
  <boolProp name="InterleaveAcrossThreads">true</boolProp>
</RandomController>

<!-- Random Order Controller 特殊配置 -->
<RandomOrderController>
  <boolProp name="InterleaveAcrossThreads">false</boolProp>
</RandomOrderController>
```

---

### **五、性能影响对比**

#### **执行时间计算**

假设每个请求耗时1秒：

| 控制器类型              | 子元素数量 | 单次迭代时间 | 10次迭代总时间 |
| ----------------------- | ---------- | ------------ | -------------- |
| Random Controller       | 3          | 1秒          | 10秒           |
| Random Order Controller | 3          | 3秒          | 30秒           |

**结论**：Random Order Controller 的执行时间会随子元素数量线性增长。

---

### **六、混合使用案例**

#### **复杂用户行为模拟**
```xml
<TestPlan>
  <ThreadGroup>
    <hashTree>
      <!-- 第一层：随机选择主要行为 -->
      <RandomController testname="主要行为选择">
        <hashTree>
          <!-- 选项1：购物流程 -->
          <RandomOrderController testname="购物流程">
            <hashTree>
              <HTTPSamplerProxy testname="浏览商品"/>
              <HTTPSamplerProxy testname="加入购物车"/>
              <HTTPSamplerProxy testname="结算订单"/>
            </hashTree>
          </RandomOrderController>
          
          <!-- 选项2：信息查询 -->
          <RandomOrderController testname="信息查询">
            <hashTree>
              <HTTPSamplerProxy testname="查询订单"/>
              <HTTPSamplerProxy testname="查看物流"/>
              <HTTPSamplerProxy testname="联系客服"/>
            </hashTree>
          </RandomOrderController>
        </hashTree>
      </RandomController>
    </hashTree>
  </ThreadGroup>
</TestPlan>
```

**执行逻辑**：
1. 随机选择"购物流程"或"信息查询"
2. 执行选定流程中的所有步骤（顺序随机）

---

### **七、选择指南**

#### **选择 Random Controller 当：**
✅ 模拟用户**选择不同路径**  
✅ 测试**单个接口**的性能  
✅ 需要**减少测试时间**  
✅ 模拟**互斥操作**（登录/注册不能同时进行）

#### **选择 Random Order Controller 当：**
✅ 模拟**完整业务流程**  
✅ 测试**接口组合**的性能  
✅ 验证**数据依赖性**（如：创建→查询→更新）  
✅ 模拟**真实用户操作序列**

---

### **八、调试技巧**

#### **查看执行日志**
```groovy
// 在子元素中添加调试信息
log.info("执行: " + prev.getSampleLabel())
vars.put("LAST_ACTION", prev.getSampleLabel())
```

#### **统计执行分布**
```groovy
// 统计 Random Controller 的选择分布
def action = prev.getSampleLabel()
def counter = vars.getObject("COUNTER_" + action) ?: 0
vars.putObject("COUNTER_" + action, counter + 1)

// 定期输出统计
if (vars.get("ITERATION").toInteger() % 100 == 0) {
    log.info("=== 执行分布统计 ===")
    ["API 1", "API 2", "API 3"].each { api ->
        def count = vars.getObject("COUNTER_" + api) ?: 0
        log.info("${api}: ${count} 次")
    }
}
```

---

### **总结**

| 方面       | Random Controller        | Random Order Controller |
| ---------- | ------------------------ | ----------------------- |
| **本质**   | 随机**选择**             | 随机**排序**            |
| **执行量** | 1/N                      | N/N                     |
| **性能**   | 快                       | 慢（随元素数量增加）    |
| **真实度** | 低（用户不会只做一件事） | 高（完整业务流程）      |

**简单记忆**：
- **Random Controller** = 随机**选一个**执行
- **Random Order Controller** = 随机**排顺序**执行全部

根据您的测试目标选择合适的控制器，也可以组合使用以达到更真实的模拟效果。

## 断言

JMeter断言是**验证测试结果是否符合预期的重要组件**，它的主要作用是检查服务器响应数据是否满足特定的条件，从而判断请求是否真正成功。

### JMeter断言的核心作用

#### 1. **验证响应正确性**

- 确认服务器返回了期望的数据
- 检查业务逻辑是否正确执行
- 验证API接口返回的状态和数据格式

#### 2. **自动化测试验证**

- 替代人工检查响应内容
- 实现测试的自动化和持续集成
- 提供客观的测试结果判断

#### 3. **错误检测和定位**

- 快速发现功能缺陷
- 定位具体失败的请求和原因
- 提高测试效率

### 常用断言类型及应用场景

#### 1. **响应断言（Response Assertion）**

最常用的断言类型，检查响应文本、代码、消息等

**配置示例：**

```
应用范围：主样本/子样本
要测试的响应字段：响应文本、响应代码、响应头
模式匹配规则：包含、匹配、相等、字符串
测试模式：预期的文本或正则表达式
```

**典型应用：**

- 检查HTTP状态码：`200`
- 验证响应包含特定关键词：`"success":true`
- 确认错误消息不存在：`"error"`

#### 2. **JSON断言（JSON Assertion）**

专门用于验证JSON格式的响应数据

**配置示例：**

```
JSON路径表达式：$.status
预期值：success
Additionally assert value：勾选
Match as regular expression：根据需要使用正则
```

**典型应用：**

```
{
  "code": 200,
  "message": "操作成功",
  "data": {...}
}
```

- 验证code字段值为200
- 检查message字段包含"成功"
- 验证data对象不为空

#### 3. **持续时间断言（Duration Assertion）**

验证响应时间是否在可接受范围内

**配置示例：**

```
持续时间（毫秒）：5000
```

表示响应必须在5秒内完成，否则断言失败

#### 4. **大小断言（Size Assertion）**

验证响应数据的大小

**配置示例：**

```
大小（字节）：1024
比较类型：等于、大于、小于等
```

- 检查响应体不超过限制大小
- 验证文件下载完整性

#### 5. **XML断言（XML Assertion）**

验证XML格式响应的有效性

**典型应用：**

- 检查XML格式是否正确
- 验证必需的元素存在

#### 6. **BeanShell断言（BeanShell Assertion）**

使用脚本语言进行复杂逻辑的断言

**Groovy脚本示例：**

```
import groovy.json.JsonSlurper

// 解析JSON响应
def response = prev.getResponseDataAsString()
def json = new JsonSlurper().parseText(response)

// 复杂验证逻辑
if (json.code != 200) {
    Failure = true
    FailureMessage = "Expected code 200 but got: " + json.code
} else if (!json.data.users || json.data.users.size() == 0) {
    Failure = true  
    FailureMessage = "Users list is empty"
}

// 或者直接使用断言
assert json.code == 200 : "Code should be 200"
assert json.message.contains("成功") : "Should contain success message"
```

### 断言配置最佳实践

#### 1. **分层断言策略**

```
<!-- 基础断言：所有请求都应配置 -->
<ResponseAssertion>
    <test_field>response_code</test_field>
    <test_type>2</test_type> <!-- 等于 -->
    <test_pattern>200</test_pattern>
</ResponseAssertion>

<!-- 业务断言：根据具体接口配置 -->
<JSONAssertion>
    <jsonPathExpr>$.success</jsonPathExpr>
    <expectedValue>true</expectedValue>
</JSONAssertion>

<!-- 性能断言：关键接口配置 -->
<DurationAssertion>
    <maxDuration>3000</maxDuration>
</DurationAssertion>
```

#### 2. **断言作用域设置**

- **Main Sample Only**：只验证主请求
- **Sub Samples Only**：只验证子请求（如重定向）
- **Main and Sub Samples**：验证所有请求

#### 3. **模式匹配技巧**

```
包含：适用于部分匹配，如检查成功标志
匹配：支持正则表达式，适合复杂模式
相等：精确匹配，适用于固定返回值
字符串：简单文本匹配
```

### 实际应用场景示例

#### 场景1：登录接口验证

```
// 响应断言配置：
响应代码：200
响应文本包含："登录成功"

// JSON断言配置：
JSON Path: $.token
预期值：非空（使用正则表达式：.+）
```

#### 场景2：查询用户信息接口

```
// JSON断言配置：
JSON Path: $.user.status  
预期值：active

JSON Path: $.user.age
比较：大于 18
```

#### 场景3：文件上传接口

```
// 大小断言：验证文件大小合理
响应大小：大于 100字节

// 持续时间断言：上传应在10秒内完成
最大时长：10000毫秒
```

### 断言结果查看

#### 1. **查看结果树中的断言结果**

- 绿色：断言通过
- 红色：断言失败，显示详细错误信息

#### 2. **聚合报告中的断言统计**

- 显示断言失败的数量和比例
- 帮助评估系统稳定性

#### 3. **断言监听器**

专门的断言结果收集器，提供更详细的断言分析

### 常见问题解决

#### 1. **断言误报**

- 检查响应数据的动态内容（如时间戳、随机数）
- 使用正则表达式忽略动态部分
- 考虑使用包含而非精确匹配

#### 2. **性能影响**

- 避免在大量请求上使用复杂的BeanShell断言
- 优先使用内置断言类型
- 合理使用断言作用域

#### 3. **编码问题**

- 确保断言文本的编码与响应一致
- 处理中文等特殊字符的匹配

**总结：** JMeter断言是确保测试质量的关键工具，合理配置断言可以大大提高测试的准确性和效率。建议根据业务需求选择合适的断言类型，并建立分层的断言验证策略。

### 示例

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-jmeter/demo-断言.jmx

## ctx上下文对象

`ctx`是JMeter中的**上下文对象**（Context），它是JMeter提供的一个核心Java对象，用于在测试执行过程中访问和操作JMeter的内部状态和运行时信息。

### ctx对象的基本概念

#### 定义

`ctx`是 `org.apache.jmeter.threads.JMeterContext`类的实例，通过它可以直接访问JMeter引擎的各种运行时信息和控制方法。

#### 访问方式

在JMeter的各种脚本组件中都可以直接使用 `ctx`变量：

- **JSR223 Sampler/PreProcessor/PostProcessor**
- **BeanShell Sampler/PreProcessor/PostProcessor**
- **JSR223 Assertion**
- **JSR223 Listener**

### ctx对象的主要属性和方法

#### 1. **线程相关属性**

```
// 获取当前线程组
def threadGroup = ctx.getThreadGroup()

// 获取当前线程
def thread = ctx.getCurrentThread()

// 获取线程编号
def threadNumber = ctx.getThreadNum()

// 获取线程名称
def threadName = ctx.getThread().getName()

// 检查是否为第一个线程
def isFirstThread = ctx.isFirstInThreadGroup()
```

#### 2. **采样器相关方法**

```
// 获取前一个采样器的结果
def prev = ctx.getPreviousResult()
if (prev) {
    def responseCode = prev.getResponseCode()
    def responseData = prev.getResponseDataAsString()
    def isSuccessful = prev.isSuccessful()
    def startTime = prev.getStartTime()
    def endTime = prev.getEndTime()
    def latency = prev.getLatency()
}

// 获取当前采样器
def currentSampler = ctx.getCurrentSampler()

// 获取采样器标签
def samplerLabel = ctx.getSamplerLabel()
```

#### 3. **测试控制方法**

```
// 停止整个测试（立即停止）
ctx.stopTestNow()

// 停止测试（等待当前迭代完成）
ctx.stopTest()

// 停止当前线程
ctx.stopThread(0)  // 0表示立即停止
ctx.stopThread(1)  // 1表示停止并清理资源

// 暂停当前线程
ctx.suspendThread()

// 恢复当前线程
ctx.resumeThread()
```

#### 4. **变量和属性访问**

```
// 访问JMeter变量（等同于vars）
def myVar = ctx.getVariables().get("myVariable")
ctx.getVariables().put("newVariable", "value")

// 访问JMeter属性（等同于props）
def propertyValue = ctx.getProperties().getProperty("some.property")
ctx.getProperties().setProperty("some.property", "newValue")

// 注意：通常更推荐使用vars和props对象
```

#### 5. **引擎和测试计划信息**

```
// 获取JMeter引擎
def engine = ctx.getEngine()

// 获取测试计划
def testPlan = ctx.getEngine().getTestPlan()

// 获取运行计划
def runningPlan = ctx.getEngine().getRunningPlan()
```

### 实际使用场景示例

#### 场景1：在断言中获取详细错误信息

```
import org.apache.jmeter.samplers.SampleResult

// 获取前一个采样结果
def sampleResult = ctx.getPreviousResult()

if (!sampleResult.isSuccessful()) {
    // 获取响应数据
    def responseData = sampleResult.getResponseDataAsString()
    def responseCode = sampleResult.getResponseCode()
    def responseMessage = sampleResult.getResponseMessage()
    
    // 记录详细错误信息
    log.error("请求失败 - 标签: " + sampleResult.getSampleLabel())
    log.error("响应代码: " + responseCode)
    log.error("响应消息: " + responseMessage)
    log.error("响应数据: " + responseData.substring(0, Math.min(responseData.length(), 500)))
    
    // 可以根据条件决定是否停止测试
    if (responseCode == "500") {
        ctx.stopTestNow()
    }
}
```

#### 场景2：基于线程状态的条件逻辑

```
// 只在第一个线程执行某些操作
if (ctx.isFirstInThreadGroup()) {
    log.info("这是线程组中的第一个线程: " + ctx.getThreadNum())
    
    // 初始化操作
    ctx.getVariables().put("startTime", System.currentTimeMillis().toString())
}

// 获取线程执行次数
def iteration = ctx.getThread().getIteration()
log.info("线程 " + ctx.getThreadNum() + " 第 " + iteration + " 次迭代")

// 在特定迭代执行特殊逻辑
if (iteration == 5) {
    log.info("第5次迭代，执行特殊验证")
}
```

#### 场景3：动态调整测试行为

```
// 根据响应时间动态调整后续请求
def prevResult = ctx.getPreviousResult()
def responseTime = prevResult.getTime()

if (responseTime > 5000) {
    log.warn("响应时间过长: " + responseTime + "ms")
    
    // 设置标志让后续请求使用更简单的参数
    ctx.getVariables().put("useSimpleMode", "true")
    
    // 或者降低后续请求的负载
    if (ctx.getThreadNum() == 0) { // 只在第一个线程调整
        ctx.getEngine().setProperty("ramp_up_time", "30") // 延长 ramp-up
    }
}
```

#### 场景4：测试执行监控

```
// 在测试开始时记录
if (ctx.getThreadNum() == 0 && ctx.getCurrentSampler() == null) {
    log.info("测试开始执行")
    ctx.getVariables().put("testStartTime", System.currentTimeMillis().toString())
}

// 在采样器中监控进度
def totalThreads = ctx.getThreadGroup().getNumberOfThreads()
def activeThreads = ctx.getThreadGroup().getNumberOfActiveThreads()
def progress = (totalThreads - activeThreads) / totalThreads * 100

log.info("测试进度: " + progress + "%, 活跃线程: " + activeThreads)
```

### ctx与其他对象的区别

#### ctx vs vars

```
// ctx方式
def value1 = ctx.getVariables().get("variableName")

// vars方式（推荐）
def value2 = vars.get("variableName")

// vars是更简洁的写法，内部调用的是ctx.getVariables()
```

#### ctx vs props

```
// ctx方式
def prop1 = ctx.getProperties().getProperty("property.name")

// props方式（推荐）
def prop2 = props.getProperty("property.name")

// props是更简洁的写法，内部调用的是ctx.getProperties()
```

#### ctx vs prev

```
// ctx方式获取前一个结果
def result1 = ctx.getPreviousResult()

// prev变量方式（推荐）
def result2 = prev

// prev是专门为后处理器设计的便捷变量
```

### 重要注意事项

#### 1. **性能考虑**

- `ctx`提供了底层访问，频繁调用可能影响性能
- 在循环中避免重复调用相同的方法

#### 2. **线程安全**

- `ctx`对象是线程安全的，每个线程有自己的上下文
- 但跨线程访问需要小心处理

#### 3. **脚本语言兼容性**

- **Groovy**：完全支持所有ctx方法，性能最佳
- **BeanShell**：支持大部分方法，但性能较差
- **JavaScript**：支持有限，不推荐

#### 4. **调试技巧**

```
// 打印所有可用的ctx方法（调试用）
ctx.metaClass.methods.each { method ->
    println "Method: " + method.name
}

// 检查对象类型
println "ctx class: " + ctx.getClass().name
```

### 实用工具函数封装

```
// 封装常用的ctx操作
class JMeterContextUtil {
    
    static void stopOnCondition(boolean condition, String reason = "") {
        if (condition) {
            if (reason) log.error("停止测试: " + reason)
            ctx.stopTestNow()
        }
    }
    
    static void logSampleInfo() {
        def prev = ctx.getPreviousResult()
        if (prev) {
            log.info("Sample: ${prev.getSampleLabel()}, Time: ${prev.getTime()}ms, Success: ${prev.isSuccessful()}")
        }
    }
    
    static boolean isFirstThread() {
        return ctx.isFirstInThreadGroup()
    }
}

// 使用示例
JMeterContextUtil.stopOnCondition(!prev.isSuccessful(), "断言失败")
JMeterContextUtil.logSampleInfo()
```

**总结：** `ctx`是JMeter脚本编程中最强大的工具之一，它提供了对JMeter引擎的完整访问能力。虽然功能强大，但在日常使用中，优先考虑使用更高层次的抽象（如 `vars`、`props`、`prev`）会让代码更清晰易读。`ctx`主要用于需要深度控制JMeter行为的复杂场景中。

## prev内置变量

`prev`是JMeter中的一个**内置变量**，专门用于在**后处理器**（PostProcessor）中访问**前一个采样器的结果**。它是JMeter脚本编程中的重要概念，极大地简化了测试结果的处理逻辑。

### prev对象的基本概念

#### 定义

`prev`是 `org.apache.jmeter.samplers.SampleResult`类的实例，代表最近一次执行的采样器（Sampler）的结果数据。

#### 访问范围

**只能在以下组件中使用：**

- **JSR223 PostProcessor**
- **BeanShell PostProcessor**
- **JSR223 Assertion**
- **BeanShell Assertion**

**不能在以下组件中使用：**

- 前置处理器（PreProcessor）
- 普通采样器（Sampler）
- 监听器（Listener）
- 其他非后处理组件

### prev对象的主要属性和方法

#### 1. **基本响应信息**

```
// 响应代码（如：200, 404, 500）
def responseCode = prev.getResponseCode()

// 响应消息（如：OK, Not Found）
def responseMessage = prev.getResponseMessage()

// 是否请求成功
def isSuccess = prev.isSuccessful()

// 采样器标签/名称
def sampleLabel = prev.getSampleLabel()

// 线程名称
def threadName = prev.getThreadName()
```

#### 2. **时间和性能指标**

```
// 开始时间（毫秒）
def startTime = prev.getStartTime()

// 结束时间（毫秒）  
def endTime = prev.getEndTime()

// 总耗时（毫秒）
def time = prev.getTime()

// 延迟时间（毫秒）- 从请求发出到收到响应的时间
def latency = prev.getLatency()

// 连接时间（毫秒）
def connectTime = prev.getConnectTime()

// 字节大小
def bodySize = prev.getBodySize()
def headersSize = prev.getHeadersSize()
def bytesAsLong = prev.getBytesAsLong()
```

#### 3. **响应数据访问**

```
// 响应数据（字节数组）
def responseDataBytes = prev.getResponseData()

// 响应数据（字符串）
def responseDataString = prev.getResponseDataAsString()

// 响应头
def responseHeaders = prev.getResponseHeaders()

// 请求头
def requestHeaders = prev.getRequestHeaders()

// URL
def url = prev.getUrlAsString()

// HTTP方法
def method = prev.getHTTPMethod()
```

#### 4. **子结果处理**

```
// 获取子采样器结果（如重定向）
def subResults = prev.getSubResults()

// 获取采样器数据
def samplerData = prev.getSamplerData()

// 获取断言结果
def assertionResults = prev.getAssertionResults()
```

#### 5. **线程控制：setStopThread**

`prev.setStopThread(boolean stop)` 用于**控制当前线程是否停止执行**。调用后，JMeter 会在当前采样器及其后处理器执行完毕后，**不再执行该线程内后续的采样器**，常用于遇到非预期响应时提前结束该线程的测试。

| 参数 | 说明 |
|------|------|
| `stop` | `true`：停止当前线程，不再执行后续采样器；`false`：不停止（默认行为） |

**典型用法：** 在后处理器或断言中根据响应码、响应体内容等判断为“非预期”时，先 `prev.setSuccessful(false)` 并设置 `prev.setResponseMessage(...)`，再调用 `prev.setStopThread(true)`，可避免该线程继续发无效请求。

```groovy
// 示例：非预期响应时停止当前线程
def responseCode = prev.getResponseCode()
if (responseCode != "200") {
    prev.setSuccessful(false)
    prev.setResponseMessage("非预期响应: 状态码=" + responseCode)
    prev.setStopThread(true)  // 当前线程后续采样器不再执行
    return
}
def response = prev.getResponseDataAsString()
if (!response.contains("slideshow")) {
    prev.setSuccessful(false)
    prev.setResponseMessage("非预期响应: JSON中缺少slideshow字段")
    prev.setStopThread(true)
    return
}
```

**注意：** 仅影响**当前线程**，其他线程照常运行；停止的是“该线程后续的采样器”，当前采样器及当前后处理器/断言仍会执行完。

### 实际使用场景示例

#### 场景1：在PostProcessor中处理结果

```
// JSR223 PostProcessor 示例
log.info("=== 处理采样器结果 ===")
log.info("采样器: " + prev.getSampleLabel())
log.info("响应码: " + prev.getResponseCode())
log.info("耗时: " + prev.getTime() + "ms")
log.info("响应数据长度: " + prev.getResponseDataAsString().length())

// 根据响应内容进行逻辑处理
def response = prev.getResponseDataAsString()
if (response.contains("error")) {
    log.warn("响应中包含错误信息")
    prev.setSuccessful(false) // 手动标记失败
}

// 提取数据保存到变量
if (prev.getResponseCode() == "200") {
    def jsonResponse = new groovy.json.JsonSlurper().parseText(response)
    vars.put("userId", jsonResponse.data.id.toString())
    vars.put("userName", jsonResponse.data.name)
}
```

#### 场景2：在Assertion中进行复杂验证

```
// JSR223 Assertion 示例
import groovy.json.JsonSlurper

try {
    // 获取响应数据
    def response = prev.getResponseDataAsString()
    def responseCode = prev.getResponseCode()
    
    // 首先检查HTTP状态码
    if (responseCode != "200") {
        AssertionResult.setFailure(true)
        AssertionResult.setFailureMessage("期望状态码200，实际得到: " + responseCode)
        return
    }
    
    // 解析JSON响应
    def json = new JsonSlurper().parseText(response)
    
    // 复杂业务逻辑验证
    if (!json.success) {
        AssertionResult.setFailure(true)
        AssertionResult.setFailureMessage("业务失败: " + json.message)
    } else if (json.data.items.size() == 0) {
        AssertionResult.setFailure(true) 
        AssertionResult.setFailureMessage("数据列表为空")
    } else if (prev.getTime() > 5000) {
        AssertionResult.setFailure(true)
        AssertionResult.setFailureMessage("响应时间超时: " + prev.getTime() + "ms")
    }
    
} catch (Exception e) {
    AssertionResult.setFailure(true)
    AssertionResult.setFailureMessage("解析响应时发生异常: " + e.getMessage())
}
```

#### 场景3：性能监控和数据提取

```
// 性能阈值检查
def responseTime = prev.getTime()
def responseCode = prev.getResponseCode()

// 设置性能断言
if (responseTime > 3000) {
    log.warn("慢响应检测 - 采样器: " + prev.getSampleLabel() + ", 耗时: " + responseTime + "ms")
    
    // 可以在这里设置自定义指标
    if (responseTime > 5000) {
        // 标记为失败或记录特殊指标
        prev.setSuccessful(false)
    }
}

// 提取关键性能指标到变量
vars.put("current_response_time", responseTime.toString())
vars.put("current_response_size", prev.getBytesAsLong().toString())

// 响应时间趋势分析（需要在多个请求间保持状态）
def prevTime = vars.get("previous_response_time")
if (prevTime) {
    def timeDiff = responseTime - prevTime.toInteger()
    log.info("响应时间变化: " + timeDiff + "ms")
}
vars.put("previous_response_time", responseTime.toString())
```

#### 场景4：响应数据解析和验证

```
// XML响应处理
if (prev.getContentType()?.contains("xml")) {
    def xmlResponse = prev.getResponseDataAsString()
    
    // 检查必需元素
    if (!xmlResponse.contains("<status>success</status>")) {
        AssertionResult.setFailure(true)
        AssertionResult.setFailureMessage("XML响应缺少成功状态标识")
    }
    
    // 提取XML数据
    def userIdPattern = /<user_id>(\d+)<\/user_id>/
    def matcher = xmlResponse =~ userIdPattern
    if (matcher.find()) {
        vars.put("extracted_user_id", matcher[0][1])
    }
}

// JSON响应处理
else if (prev.getContentType()?.contains("json")) {
    try {
        def json = new groovy.json.JsonSlurper().parseText(prev.getResponseDataAsString())
        
        // 数据完整性检查
        if (json.containsKey("required_field") && json.required_field) {
            vars.put("validation_passed", "true")
        } else {
            AssertionResult.setFailure(true)
            AssertionResult.setFailureMessage("缺少必需字段 required_field")
        }
        
    } catch (Exception e) {
        AssertionResult.setFailure(true)
        AssertionResult.setFailureMessage("JSON解析失败: " + e.getMessage())
    }
}
```

### prev与ctx的区别对比

#### 访问方式对比

```
// 使用prev（推荐在后处理器中）
def responseCode1 = prev.getResponseCode()
def responseData1 = prev.getResponseDataAsString()

// 使用ctx（也可以，但更繁琐）
def responseCode2 = ctx.getPreviousResult().getResponseCode()
def responseData2 = ctx.getPreviousResult().getResponseDataAsString()

// 使用prev更简洁直观
```

#### 使用范围对比

| 特性     | prev               | ctx                    |
| -------- | ------------------ | ---------------------- |
| 使用组件 | 仅后处理器、断言   | 几乎所有脚本组件       |
| 数据类型 | SampleResult       | JMeterContext          |
| 主要功能 | 访问前一个采样结果 | 访问JMeter运行时上下文 |
| 性能     | 直接引用，高效     | 需要方法调用，稍慢     |

### 高级用法示例

#### 1. **动态修改采样结果**

```
// 在某些情况下修改原始结果
if (prev.getResponseCode() == "200" && prev.getResponseDataAsString().contains("maintenance")) {
    // 将成功的响应改为失败
    prev.setSuccessful(false)
    prev.setResponseCode("503") // Service Unavailable
    prev.setResponseMessage("Service in maintenance mode")
}

// 添加自定义数据到采样结果
prev.setDataType("custom_data_type")
prev.setEncodingAndType("UTF-8")
```

#### 2. **批量结果处理**

```
// 处理重定向链
def subResults = prev.getSubResults()
if (subResults && subResults.length > 0) {
    log.info("检测到 " + subResults.length + " 个重定向")
    
    for (int i = 0; i < subResults.length; i++) {
        def subResult = subResults[i]
        log.info("重定向 " + (i+1) + ": " + subResult.getUrlAsString() + " -> " + subResult.getResponseCode())
    }
}
```

#### 3. **条件性结果过滤**

```
// 根据特定条件过滤或修改结果
def shouldFilter = false

// 检查响应内容
def response = prev.getResponseDataAsString()
if (response.contains("deprecated")) {
    log.warn("跳过已弃用的接口响应")
    shouldFilter = true
}

// 检查性能指标
if (prev.getTime() > 10000) {
    log.warn("异常慢响应: " + prev.getTime() + "ms")
    shouldFilter = true
}

// 标记需要过滤的结果
if (shouldFilter) {
    prev.setIgnoreSubControllers(true) // 忽略子控制器
    // 或者完全移除这个结果的影响
}
```

### 调试技巧

#### 1. **打印完整结果信息**

```
// 调试脚本 - 打印所有可用信息
log.info("=== PREV DEBUG INFO ===")
log.info("Sample Label: " + prev.getSampleLabel())
log.info("Response Code: " + prev.getResponseCode())
log.info("Response Message: " + prev.getResponseMessage())
log.info("Success: " + prev.isSuccessful())
log.info("Time: " + prev.getTime())
log.info("Latency: " + prev.getLatency())
log.info("Body Size: " + prev.getBodySize())
log.info("URL: " + prev.getUrlAsString())
log.info("Method: " + prev.getHTTPMethod())
log.info("Content Type: " + prev.getContentType())

// 响应头（前200字符）
def headers = prev.getResponseHeaders()
log.info("Response Headers: " + (headers ? headers.substring(0, Math.min(headers.length(), 200)) : "null"))

// 响应数据（前500字符）
def response = prev.getResponseDataAsString()
log.info("Response Data: " + (response ? response.substring(0, Math.min(response.length(), 500)) : "null"))
```

#### 2. **安全检查模板**

```
// 安全的prev使用模板
if (prev != null) {
    try {
        // 在这里安全地使用prev的所有方法
        def code = prev.getResponseCode()
        def data = prev.getResponseDataAsString()
        // ... 其他操作
    } catch (Exception e) {
        log.error("处理prev对象时发生异常: " + e.getMessage())
    }
} else {
    log.error("prev对象为null")
}
```

### 最佳实践建议

1. **始终检查null**：在使用prev前确保它不是null
2. **异常处理**：包装可能失败的操作为try-catch块
3. **性能考虑**：避免在prev上做过于复杂的计算
4. **日志记录**：合理使用log输出调试信息
5. **变量管理**：及时清理不再需要的临时变量

**总结：** `prev`是JMeter后处理器中的核心变量，它提供了对最近一次采样结果的完整访问能力。相比使用 `ctx.getPreviousResult()`，`prev`更加简洁高效，是在后处理器中处理测试结果的**首选方式**。掌握 `prev`的使用是编写高质量JMeter测试脚本的关键技能。

## Content-Type: application/x-www-form-urlencoded; charset=UTF-8提交

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-jmeter/demo-x-www-form-urlencoded提交.jmx

## Once Only Controller是什么呢？

>具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-jmeter/demo-onceonlycontroller.jmx

在Apache JMeter中，**Once Only Controller（仅一次控制器）** 是一种**逻辑控制器（Logic Controller）**，其核心作用是：**确保它包含的子元素（如Sampler、其他控制器等）在整个测试计划执行期间仅运行一次**，无论外部的循环结构（如线程组的循环次数、循环控制器的迭代次数）如何设置。

### 一、核心特性

1. **单次执行**：

   当线程组或外部循环触发多次迭代时，Once Only Controller内的内容**仅在第一次迭代时执行**，后续迭代会直接跳过它。

   例如：若线程组设置为“循环5次”，则Once Only Controller内的Sampler只会运行1次，而非5次。

2. **作用于当前线程**：

   JMeter的测试以“线程”为单位执行，每个线程独立维护自己的执行状态。因此，Once Only Controller的“仅一次”是针对**单个线程**而言的——不同线程会各自执行一次其中的内容（若线程组有多个线程）。

### 二、典型使用场景

Once Only Controller通常用于**初始化操作**（仅需执行一次的场景），避免重复执行浪费资源或导致错误：

#### 1. 初始化变量/数据

例如：通过JDBC Sampler从数据库加载测试所需的初始化数据（如用户Token、配置参数），只需在每个线程启动时执行一次，无需每次迭代都查询数据库。

#### 2. 登录操作（单点登录）

若测试场景中需要用户先登录再执行后续请求，且登录状态（如Cookie、Session）在会话期间有效，可将登录请求放在Once Only Controller中——确保每个线程仅登录一次，后续迭代直接使用已登录的状态。

#### 3. 创建测试前置资源

例如：通过HTTP请求创建一个临时测试订单、上传一个测试文件等，只需在每个线程开始时创建一次，后续迭代直接使用该资源的ID。

### 三、与其他控制器的区别

| 控制器类型                 | 作用                               | 与Once Only Controller的区别                                 |
| -------------------------- | ---------------------------------- | ------------------------------------------------------------ |
| **Loop Controller**        | 控制子元素重复执行指定次数         | Loop Controller是“强制循环N次”，而Once Only是“仅循环1次后停止” |
| **Thread Group（线程组）** | 控制线程数量和整体循环次数         | 线程组的循环是“全局重复执行所有子元素”，而Once Only可局部限制某部分仅执行一次 |
| **Transaction Controller** | 将多个请求视为一个事务统计性能指标 | Transaction Controller不涉及执行次数控制，仅用于结果统计     |

### 四、使用示例

假设测试场景：线程组有2个线程，循环3次；需先登录（仅一次），再执行3次查询用户信息。

**结构配置**：

```
线程组（线程数=2，循环次数=3）
├─ Once Only Controller          // 仅执行1次
│  └─ HTTP请求（登录接口）        // 每个线程仅登录1次
└─ Loop Controller（循环次数=3）  // 循环执行3次查询
   └─ HTTP请求（查询用户信息）    // 每个线程执行3次查询
```

**执行结果**：

- 每个线程的登录请求仅执行1次（共2次登录，对应2个线程）；
- 每个线程的查询请求执行3次（共6次查询）；
- 总请求数 = 2（登录） + 2×3（查询）= 8次。

### 五、注意事项

1. **线程独立性**：若线程组有多个线程，每个线程会各自执行一次Once Only Controller内的内容（即“仅一次”是 per-thread，而非 per-test）。
2. **与Cookie/Session的配合**：若使用Once Only Controller处理登录，需确保JMeter的Cookie管理器（HTTP Cookie Manager）启用“自动管理Cookies”，否则登录后的Session无法传递到后续请求。
3. **避免滥用**：不要将需要每次迭代更新的操作（如动态生成参数）放入Once Only Controller，否则可能导致后续请求使用过期数据。

总之，Once Only Controller是JMeter中用于**局部单次初始化**的关键控制器，合理使用可优化测试性能并避免不必要的重复操作。

## Module Controller是什么呢？

JMeter中的**Module Controller（模块控制器）**是一种逻辑控制器，它的核心作用是**动态地调用测试计划中的某个线程组或Sampler**。简单来说，它就像一个“指针”或“书签”，允许你在运行时决定执行哪一部分脚本，而不是在编写脚本时就写死。

它是实现JMeter脚本**模块化**和**复用**的关键组件。

### 核心工作原理

1. **定义目标**：你需要在测试计划中创建一个或多个可复用的逻辑单元（通常放在一个单独的线程组内）。
2. **设置引用**：在需要使用该逻辑的地方放置Module Controller。
3. **指定路径**：在Module Controller的配置面板中，选择你想要调用的那个具体节点（Node）。

当测试运行到Module Controller时，它会直接跳转到你指定的节点去执行，执行完毕后返回继续执行后续步骤。

### 主要用途与优势

- **代码复用（DRY原则）**

  这是最大的优势。例如，你的测试流程中多次需要“用户登录”和“登出”操作。你可以将这两步单独做成一个模块，然后在主流程的多个地方通过Module Controller来调用，避免重复编写相同的HTTP请求，极大减少维护成本。

- **实现条件化执行**

  结合**If Controller（如果控制器）**，你可以根据变量或函数的结果，决定执行哪一套逻辑。

  - *示例*：如果 `${isNewUser}`为 `true`，则通过Module Controller执行“新用户注册流程”；否则跳过该模块。

- **简化复杂场景**

  对于包含大量前置处理（如获取Token、设置Cookie）的场景，可以将这些通用步骤封装成模块。不同的业务接口只需要调用这个模块即可获得所需的环境状态，无需在每个Sampler前重复配置前置处理器。

### 关键配置项

在Module Controller的配置界面中，最重要的选项是 **Module to Run**（要运行的模块）：

- **下拉菜单**：列出了当前测试计划中所有的线程组和Sampler。
- **选择方式**：你需要准确选择你要跳转到的目标节点名称。

### 注意事项与局限

虽然Module Controller非常强大，但在使用时需要注意以下几点：

1. **作用域限制**：Module Controller只能调用**同级或下级**节点，**无法跨层级调用父级节点**。
2. **线程组隔离**：默认情况下，如果你在主线程组中调用子线程组里的模块，可能无法正确传递变量（除非勾选了“Run Thread Groups consecutively”选项或使用其他特殊配置）。
3. **性能考量**：频繁的模块跳转会消耗极少量的资源，但在超大规模并发下，建议进行压测验证。

### 视野拓展：Module Controller vs Include Controller

很多初学者容易混淆这两者，它们虽然都涉及“调用”，但机制完全不同：

| 特性         | **Module Controller (模块控制器)** | **Include Controller (包含控制器)** |
| ------------ | ---------------------------------- | ----------------------------------- |
| **调用对象** | 当前.jmx文件内部的**节点/片段**    | 外部独立的**.jmx文件**              |
| **适用场景** | 同一脚本内的逻辑复用、条件执行     | 跨脚本的用例复用、团队协作拆分脚本  |
| **灵活性**   | 高，支持动态切换（配合If控制器）   | 低，运行时静态加载，不支持动态切换  |

如果你的目标是调用另一个完整的测试计划文件，应该使用**Include Controller**；如果只是想在当前脚本里复用一段逻辑，使用**Module Controller**是最佳实践。

## Transaction Controller是什么呢？

JMeter中的**Transaction Controller（事务控制器）**是一种用于**逻辑分组**的逻辑控制器。它的主要作用是将多个独立的Sampler（采样器）组合在一起，视为一个单一的“事务”或“业务操作”来进行度量和统计。

简单来说，它就像是一个“文件夹”或“容器”，把完成某件具体事情（如“用户登录”、“提交订单”）所必须经历的所有步骤打包起来，让JMeter能告诉你“做这件事总共花了多少时间”。

### 核心功能与作用

1. **计算总耗时**

   这是事务控制器最主要的功能。它会自动计算其**内部所有Sampler**以及**子控制器**的执行时间总和，并在结果树中显示为一个单独的样本（Sample）。

   - *示例*：一个“提交订单”的事务可能包含：查询库存 -> 扣减库存 -> 生成订单号 -> 支付请求。事务控制器会给出这4个步骤加起来的总响应时间。

2. **生成父样本（Parent Sample）**

   在查看结果树（View Results Tree）中，事务控制器可以决定是否生成一个“父样本”。

   - **不勾选 "Generate parent sample"**：你会看到每个子请求（如查询库存、支付）的详细数据，同时也会看到一个总的事务数据。
   - **勾选 "Generate parent sample"**：子请求的详细数据会被折叠隐藏，只显示顶层的总事务数据，使报告更简洁。

3. **控制思考时间（Think Time）是否计入**

   通过 **"Include duration of timer and pre-post processors in generated sample"** 选项，可以控制定时器（Timer）产生的等待时间是否计入事务的总耗时。

   - 如果不勾选，事务时间只包含网络传输和处理时间。
   - 如果勾选，事务时间包含了人为设定的等待时间（模拟用户浏览页面的停顿）。

### 配置参数详解

在添加Transaction Controller后，右侧的属性面板中有两个关键复选框：

- **Generate parent sample**（生成父样本）
  - **推荐开启**：在进行聚合报告（Aggregate Report）分析时，开启此选项可以避免数据冗余，让你直接看到每个业务功能的性能指标，而不是一堆零散的请求指标。
- **Include duration of timer and pre-post processors in generated sample**（包含定时器和前后置处理器的持续时间）
  - **按需选择**：通常情况下，为了精确测量服务器处理性能，建议**不勾选**（默认），这样排除了人为延迟，数据更真实。

### 实际应用场景

假设你正在测试一个电商网站的下单流程，这个流程涉及以下请求：

1. GET /home (打开首页)
2. POST /login (用户登录)
3. GET /product?id=123 (查看商品详情)
4. POST /cart/add (加入购物车)
5. POST /checkout (结算)

**不使用事务控制器**：

在聚合报告中，你会看到5行数据，分别对应上述5个URL。你很难直观地看出“下单”这一动作的性能如何。

**使用事务控制器**：

你将以上5个Sampler全部拖入一个名为“用户下单流程”的Transaction Controller中。此时在聚合报告中，你会看到一行名为“用户下单流程”的数据，其响应时间就是这5步的总和。这让你能清晰地评估核心业务流程的健康状况。

### 注意事项

- **仅作为容器**：Transaction Controller本身不发送任何请求，它只是一个逻辑包装器。
- **嵌套使用**：你可以在一个大的事务控制器内部再嵌套小的子事务控制器。例如，“用户下单流程”是一个大事务，其中“加入购物车”可以作为其中的一个小事务。这样既能看整体，也能看局部细节。

## Synchronizing Timer是什么呢？

>具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-jmeter/demo-synchronizing-timer.jmx

JMeter中的 **Synchronizing Timer（同步定时器）** 是一种**逻辑控制器**，它的核心作用是**将分散在不同线程中的执行流在特定时刻合并，使它们在同一时间点集中爆发**。

你可以把它想象成一个现实生活中的**“起跑发令枪”**或**“十字路口的红绿灯”**。它不会减少总的任务量，但会改变任务的执行节奏，瞬间聚集大量的并发压力。

### 核心工作机制

在默认情况下，JMeter的线程组会按照设定的 `Ramp-Up Period`（启动时间）逐渐启动所有线程。这意味着并发请求是平滑上升的。

当添加了 Synchronizing Timer 后，流程变为：

1. **挂起（Hold）**：线程在执行到定时器时，会暂停执行，进入等待状态。
2. **累积（Accumulate）**：新的线程到达定时器后，也会加入等待，形成一个“蓄水池”。
3. **释放（Release）**：当等待的线程数量达到预设的阈值时，定时器会一次性释放所有这些线程，让它们同时向下执行。

这种机制使得原本分散的请求能够在同一毫秒内发出，从而模拟出真实的流量洪峰。

### 关键配置参数

在添加 Synchronizing Timer 后，你需要配置以下两个核心参数：

1. **Number of Simultaneous Threads to Group by（集合点线程数）**
   - 这是最重要的参数。它定义了需要**积累多少个线程**才会触发一次释放。
   - **填写方式**：
     - **固定数字**：例如填写 `100`。这意味着每当有100个线程到达定时器时，就会一次性释放这100个线程。
     - **变量**：例如填写 `${__P(group_size, 50)}`。这允许你通过命令行参数动态改变集合点的大小。
     - **0**：这是一个特殊情况。如果填写 `0`，JMeter会尝试将所有虚拟用户（即线程组中的所有线程）都积累起来，在同一时刻释放。这在模拟“万人秒杀”场景时非常有用。
2. **Timeout in milliseconds（超时时间）**
   - 为了避免线程无限期等待（例如，永远达不到设定的线程数），必须设置一个超时时间。
   - 如果在超时时间内，积累的线程数达到了设定值，则立即释放。
   - 如果在超时时间内，积累的线程数始终未达到设定值，定时器也会释放当前已积累的所有线程。
   - **建议**：务必设置一个合理的超时时间（例如 `300000`毫秒 = 5分钟），以防止脚本卡死。

### 典型应用场景

1. **模拟秒杀/抢购**
   - **场景**：测试电商平台的“整点秒杀”活动。
   - **实现**：设置 `Number of Simultaneous Threads`为一个较大的值（如1000）。这将确保在点击“抢购”按钮的那一刻，有1000个并发请求同时涌向服务器，测试系统在极限并发下的表现。
2. **创造突发流量**
   - **场景**：某些系统在特定时间点（如新闻发布、直播开始）会突然面临巨大的流量冲击。
   - **实现**：使用 Synchronizing Timer 可以在测试过程中人为制造出这种“脉冲式”的流量波峰，观察系统的弹性和恢复能力。
3. **控制执行顺序（配合线程组）**
   - **场景**：你需要确保某些前置操作（如登录、获取Token）全部完成后，再进行后续的业务压测。
   - **实现**：在前置操作的线程组末尾添加 Synchronizing Timer，设置 `Number of Simultaneous Threads`等于该线程组的总线程数。这样就能确保所有前置线程都跑完后，再释放后续线程组。

### 注意事项与局限性

- **仅作用于同级线程组**：Synchronizing Timer 只能同步它所在线程组内的线程。它不能跨线程组进行同步。如果需要跨线程组同步，需要使用更高级的 **Inter-Thread Communication** 插件。
- **可能导致资源瓶颈**：由于瞬间释放大量线程，可能会对JMeter自身的负载机（Load Generator）造成巨大的网络和CPU压力。请务必监控负载机的资源使用情况，必要时使用分布式测试。
- **并非越多越好**：过度使用同步定时器可能会导致测试结果失真，因为它创造了一种现实中并不常见的、绝对均匀的并发峰值。在实际测试中，通常需要结合 **Stepping Thread Group（阶梯式线程组）** 来模拟更真实的流量爬坡过程。

### 总结

Synchronizing Timer 是JMeter中用于**制造并发峰值**的神器。它通过“积累-释放”的机制，帮助测试人员精准地模拟出现实世界中各种突发的高并发场景，是进行尖峰测试（Spike Testing）和压力测试不可或缺的工具。

## Test Plan中的Run Thread Groups consecutively是什么作用呢？

在 JMeter 中，**Run Thread Groups consecutively**（按顺序运行线程组）是一个非常实用的配置选项。它的核心作用是改变默认的执行顺序，强制多个线程组按照它们在测试计划（Test Plan）树状列表中从上到下的顺序依次执行。

以下是关于该选项的详细解析：

### 1. 核心作用

- **串行执行**：当勾选此选项后，JMeter 会等待**当前**线程组完全停止（即所有用户已结束、所有循环已完成），才会启动**下一个**线程组。
- **默认行为对比**：如果不勾选（默认状态），JMeter 会采用**并行执行**模式。此时，所有激活的线程组会在同一时刻启动，共同抢占系统资源。

### 2. 适用场景

该选项通常用于模拟具有严格先后顺序的业务流程，或者为了控制服务器压力峰值：

- **依赖关系测试**

  当业务场景存在逻辑依赖时使用。例如，必须先执行“用户登录”线程组获取 Token，再执行“提交订单”线程组。如果两者并行，后者很可能因缺少认证信息而失败。

- **分阶段压测**

  用于模拟流量逐步增加的过程。例如：

  - 第一阶段：仅运行“基准测试”线程组，确认系统在低负载下的稳定性。
  - 第二阶段：待第一阶段结束后，自动运行“峰值测试”线程组，冲击系统极限。

- **避免资源冲突**

  当需要严格控制并发总量时。例如，你有两组脚本分别模拟移动端和 Web 端的流量，但你只想模拟总共 1000 个用户的并发，而不是每组各 1000。此时可以让它们串行运行。

### 3. 注意事项与局限性

在使用该功能时，需要注意以下几点以确保测试符合预期：

- **并非绝对隔离**

  虽然线程组之间是按顺序执行的，但**同一个线程组内部**依然遵循多线程并发的逻辑。如果你需要完全隔离不同的测试场景（例如完全独立的数据库操作），建议使用**非 GUI 模式下的多实例运行**，或者使用 **Setup Thread Group / Teardown Thread Group**。

- **定时器的作用范围**

  定时器（Timers）通常只对同一个线程组内的采样器生效。在线程组切换的间隙，定时器不会起作用。如果需要全局暂停，建议在测试计划级别添加定时器。

- **监听器的影响**

  如果在运行过程中查看实时结果树（View Results Tree），串行执行会让日志更加清晰，便于区分不同阶段的请求记录。

### 4. 高级替代方案

对于更复杂的执行逻辑（如条件触发、循环嵌套等），可以考虑以下组件：

- **Module Controller (模块控制器)**：允许在一个线程组内调用另一个线程组的内容，实现更细粒度的逻辑编排。
- **Include Controller (包含控制器)**：可以将测试计划拆分为多个独立的 .jmx 文件，然后在主计划中引用，配合逻辑控制器实现动态执行。
