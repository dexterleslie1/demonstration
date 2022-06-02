# 演示docker volume用法

## 命名volume

```
# 自动创建vol1 volume （在宿主机目录/var/lib/docker/volumes 中）并挂载到容器/data目录下，执行echo SHELL命令输出 “Test content11” 到 /data/1.txt 文件中
docker run -it --rm -v vol1:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"

# 创建vol2并输出内容到其中
docker volume create vol2
docker run -it --rm -v vol2:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"
```

## 绑定volume

```
# 创建容器b2
docker run --name b2 -it -v ~/demo-docker-volume-b2:/data --rm busybox

# 在宿主机的存储卷上进行简单操作
cd ~/demo-docker-volume-b2
echo "hello" > 1.txt

# 在容器中查看1.txt
cat /data/1.txt
```

## 匿名volume，暂时没有需求用到不研究

