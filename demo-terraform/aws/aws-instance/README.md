## 运行demo

```
# 配置terraform aws provider验证 ~/.aws/config

# 下载aws provider依赖
terraform init

# 创建虚拟机
terraform apply

# 使用ssh命令测试连接主机
ssh -i private.key centos@xxx

# 销毁虚拟机
terraform destroy
```

