## kubebuilder命令配置

```shell
# 下载kubebuilder
curl -L -o /tmp/kubebuilder "https://go.kubebuilder.io/dl/latest/$(go env GOOS)/$(go env GOARCH)"

# 配置全局kubebuilder命令
sudo chmod +x /tmp/kubebuilder && sudo mv /tmp/kubebuilder /usr/local/bin/
```

