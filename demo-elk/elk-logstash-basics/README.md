# 学习logstash使用

## 安装和配置logstash，并使用最小配置启动logstash

下载logstash，下载logstash-linux.tar.gz

[下载](https://www.elastic.co/cn/downloads/logstash)

解压logstash-linux.tar.gz并修改解压后的目录属主

```shell
# 解压logstash
tar -xvxf logstash-linux.tar.gz

# 修改目录属主为当前目录，否则会因为权限问题启动失败
chown -R dexterleslie:dexterleslie logstash
```
 
启动logstash

```shell
# 输入源stdin，输出目标stdout
./logstash -e ""
```

## logstash命令行参数

-e 后面跟着字符串，该字符串可以被当做logstash的配置（如果是“” 则默认使用stdin作为输入，stdout作为输出）

```shell
# 默认输入源stdin，输出目标stdout
./logstash -e ""

# 指定输入源stdin，输出目标stdout
./logstash -e "input { stdin {} } output { stdout {} }"
```
-f 通过这个命令可以指定Logstash的配置文件，根据配置文件配置logstash

```shell
# 使用stdin-and-stdout.conf配置文件
./logstash -f stdin-and-stdout.conf
```

-t 测试配置文件是否正确，然后退出。

```shell
# 测试stdin-and-stdout.conf配置文件是否正确
./logstash -f stdin-and-stdout.conf -t
```

## 添加或者删除tag、field

参考conditional-if.conf配置文件

## 条件if在filter中用法

参考conditional-if.conf配置文件

## beats+elasticsearch配置

logstash配置如下：
```
input {
  beats {
    port=>5044
  }
}

filter {
  if !([fields][sourceType]) {
    mutate {
      add_field=>{"logSourceType"=>"chat-user-unknown"}
    }
  } else {
    mutate {
      add_field=>{"logSourceType"=>"%{[fields][sourceType]}"}
    }
  }
}

output {
  elasticsearch {
    hosts=>["192.168.1.171:9200"]
    # logstash输出日志到elasticsearch，index和type如何动态赋值？
    # https://blog.csdn.net/minicto/article/details/78644328
    index=>"%{[logSourceType]}-%{+YYYY.MM.dd}"
  }
}
```

## plugins-outputs-elasticsearch使用

>[plugins-outputs-elasticsearch](https://www.elastic.co/guide/en/logstash/6.7/plugins-outputs-elasticsearch.html) <br>
[基于ElasticSearch+Logstash+Kibana的日志分析、存储、展示](https://www.linuxidc.com/Linux/2018-11/155518.htm)

## plugins-inputs-file使用

>[plugins-inputs-file](https://www.elastic.co/guide/en/logstash/6.5/plugins-inputs-file.html)<br>
[logstash input-file 插件使用详解](https://blog.csdn.net/wjacketcn/article/details/50960843)

logstash配置如下：
```
input {
  file {
    path => "/var/log/messages"
  }
}

output {
  stdout { }
}
```

## plugins-filters-mutate插件，add_field、remove_field、add_tag、replace、copy等

[mutate插件](https://www.elastic.co/guide/en/logstash/current/plugins-filters-mutate.html)

## logstash配置文件访问环境变量

[访问环境变量](https://www.elastic.co/guide/en/logstash/current/environment-variables.html)

## 补充资料

[忽略事件](https://discuss.elastic.co/t/drop-logs-when-there-is-a-jsonparsefailure/28655)

[配置脚本中访问事件的数据和字段，包括@metadata](https://www.elastic.co/guide/en/logstash/current/event-dependent-configuration.html#conditionals)