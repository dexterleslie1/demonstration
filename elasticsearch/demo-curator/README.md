## elasticsearch curator安装和使用

### 宿主机直接安装curator命令使用

> 参考
> https://blog.51cto.com/elasticsearch/5768186

```
# 准备centOS8系统

# 使用kibana创建测试索引
PUT /ecommerce-xxx-service-2023-05-01
{
  "settings": {
    "number_of_shards": 6,
    "number_of_replicas": 1
  }
}

# 安装最新curator，国内下载比较慢需要耐心等待
pip3 install elasticsearch-curator

# 查看curator命令是否安装成功
curator_cli --version
curator --version

# 使用curator_cli命令显示elasticsearch所有索引
curator_cli --host 192.168.1.181 --port 9200 show-indices

# 使用curator.yml和action.yml配置curator命令相关参数，curator.yml配置elasticsearch主机相关参数，action.yml用于配置curator命令执行的动作，下面演示使用curator删除30天前ecommerce-开头的日志

curator.yml
# Remember, leave a key empty if there is no value.  None will be a string,
## not a Python "NoneType"

client:
  hosts: 192.168.1.181
  port: 9200
  url_prefix:
  use_ssl: False
  certificate:
  client_cert:
  client_key:
  ssl_no_validate: False
  http_auth:
  timeout: 30
  master_only: False

logging:
  loglevel: INFO
  logfile: /var/log/curator.log
  logformat: default
  blacklist: ['elasticsearch', 'urllib3']
  
action.yml
---
# Remember, leave a key empty if there is no value.  None will be a string,
# not a Python "NoneType"
#
# Also remember that all examples have 'disable_action' set to True.  If you
# want to use this action as a template, be sure to set this to False after
# copying it.
actions:
  1:
    action: delete_indices
    description: >-
      Delete indices older than 20 days (based on index name), for logstash-
      prefixed indices. Ignore the error if the filter does not result in an
      actionable list of indices (ignore_empty_list) and exit cleanly.
    options:
      ignore_empty_list: True
      disable_action: False
    filters:
    - filtertype: pattern
      kind: prefix
      value: ecommerce-
    - filtertype: age
      source: name
      direction: older
      timestring: '%Y-%m-%d'
      unit: days
      unit_count: 30
      
# 执行curator命令
curator --config curator.yml action.yml

# 查看当前目录的 log.log日志输入
cat log.log
```



### 使用docker容器安装curator并使用

> NOTE: 通过分析日志提示，要注意curator镜像版本和elasticsearch的兼容性，否则无法删除过期索引
>
> 参考
> https://hub.docker.com/r/untergeek/curator

```
# 使用kibana创建测试索引
PUT /ecommerce-xxx-service-2023-05-01
{
  "settings": {
    "number_of_shards": 6,
    "number_of_replicas": 1
  }
}

curator.yml内容如下:
# Remember, leave a key empty if there is no value.  None will be a string,
## not a Python "NoneType"

client:
  hosts: 192.168.1.181
  port: 9200
  url_prefix:
  use_ssl: False
  certificate:
  client_cert:
  client_key:
  ssl_no_validate: False
  http_auth:
  timeout: 30
  master_only: False

logging:
  loglevel: INFO
  logfile: /tmp/curator.log
  logformat: default
  blacklist: ['elasticsearch', 'urllib3']

action.yml内容如下:
---
# Remember, leave a key empty if there is no value.  None will be a string,
# not a Python "NoneType"
#
# Also remember that all examples have 'disable_action' set to True.  If you
# want to use this action as a template, be sure to set this to False after
# copying it.
actions:
  1:
    action: delete_indices
    description: >-
      Delete indices older than 20 days (based on index name), for logstash-
      prefixed indices. Ignore the error if the filter does not result in an
      actionable list of indices (ignore_empty_list) and exit cleanly.
    options:
      ignore_empty_list: True
      disable_action: False
    filters:
    - filtertype: pattern
      kind: prefix
      value: ecommerce-
    - filtertype: age
      source: name
      direction: older
      timestring: '%Y-%m-%d'
      unit: days
      unit_count: 30

# 定时触发删除过期索引
docker run --rm --name curator -v ./curator.yml:/.curator/curator.yml -v ./action.yml:/.curator/action.yml --entrypoint /bin/sh untergeek/curator:5.7.6 -c "while true; do /curator/curator --config /.curator/curator.yml /.curator/action.yml; sleep 10; done;"

# 运行容器删除一次过期的索引
docker run --rm   -v ./curator.yml:/.curator/curator.yml   -v ./action.yml:/.curator/action.yml    untergeek/curator:5.7.6        --config /.curator/curator.yml /.curator/action.yml
```

