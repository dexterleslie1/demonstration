# `gce`使用

### `gce`虚拟机安装`ops agent`监控

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

## 