## 用于openresty性能测试配置

### 结论

> 使用ngx.sleep(5)和ngx.exit(ngx.HTTP_FORBIDDEN)能够很有效地防御DDoS攻击

### 模拟测试

```
# 创建两台centOS8
# 其中跑openresty centOS8 4核8G
# 其中跑hey工具centOS8为12核6G

# 启动openresty
docker-compose up -d

# 在hey工具同一个centOS8中启动3个hey进程制造负载(因为如果一个hey工具-c 4096会导致12核cpu也被完全消耗产生不了负载)
hey -z 1800s -c 512 http://192.168.1.185/
```

