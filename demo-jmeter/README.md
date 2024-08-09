# `jmeter`使用

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

   

## 单机测试

>注意：使用`dcli`安装`jmeter master`模式。

使用`/home/xxx/xxx.jmx`文件启动`jmeter`测试

```bash
jmeter -n -t /home/xxx/xxx.jmx
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
