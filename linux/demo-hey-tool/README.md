# hey工具使用

## 注意

> macOS需要使用 sudo ./hey运行程序
>
> 能够充分利用所有CPU进行压力测试，但是似乎不能够建立大量TCP连接(需要进一步证明这个工具)



## 参考

> https://stackoverflow.com/questions/43256621/how-to-limit-request-per-second-in-apache-benchmark-tools
> https://github.com/rakyll/hey



## 使用

```
# github.com下载hey二进制程序

# 重命名为hey命令并放置到/usr/local/bin目录中
mv hey_linux_amd64 /usr/local/bin/hey

# hey程序添加执行权限
chmod o+w /usr/local/bin/hey

# -c并发线程1个，-z 60s持续测试60秒，-q 10控制每秒10个请求
hey -q 10 -z 60s -c 1 http://192.168.1.102/

# 不限制请求速度
hey -z 60s -c 100 http://192.168.1.102/
```

