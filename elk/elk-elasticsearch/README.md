# elasticsearch用法

## 使用docker-compose运行elasticsearch

```shell script
# 创建读写权限的 ~/data-demo-elk-elasticsearch 目录
sudo mkdir ~/data-demo-elk-elasticsearch
sudo chmod -R a+w ~/data-demo-elk-elasticsearch

# 启动elasticsearch
docker-compose up -d

# 删除elasticsearch
docker-compose down
```