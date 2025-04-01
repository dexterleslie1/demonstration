# `gce`



## 创建 Ubuntu 实例

提醒：

- 创建实例选择 us-central1  (Iowa) 比较便宜。



### 配置 SSH 连接

参考 <a href="/linux/ubuntu-n-debian.html#生成-ssh-密钥对" target="_blank">链接</a> 生成 SSH 密钥对

参考 [链接](https://cloud.google.com/compute/docs/connect/add-ssh-keys#add_ssh_keys_to_instance_metadata) 上传公钥，步骤：通过 `Security and access` > `SSH Keys` 功能添加公钥到实例中



## 虚拟机安装`ops agent`监控

> 支持`CPU`、内存、硬盘、网络的监控。
>
> 参考`Google ops agent`安装文档 [链接](https://cloud.google.com/stackdriver/docs/solutions/agents/ops-agent/installation)

安装最新版本的`ops agent`

```bash
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install
```

查看`ops agent`是否正常运行

```bash
sudo systemctl status google-cloud-ops-agent"*"
```
