# 演示docker commit用法

## 命名volume commit

```
# 准备容器
docker run -it --name demo-docker-commit1 -v vol1:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"

# 从容器demo-docker-commit1创建镜像demo-docker-commit1-duplicate 
docker commit demo-docker-commit1 demo-docker-commit1-duplicate

# 从镜像demo-docker-commit1-duplidate运行容器
docker run --rm -it demo-docker-commit1-duplicate /bin/bash
```

