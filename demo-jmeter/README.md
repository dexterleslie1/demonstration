# `jmeter`使用

## 安装并运行

### 运行`jmeter`测试

以下测试是使用`JSR223`脚本测试`json`解析效率：

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



## 分布式测试

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

> 参考 demo-docker-with-influxdb-grafana
>
> https://zhuanlan.zhihu.com/p/621684630?utm_id=0

