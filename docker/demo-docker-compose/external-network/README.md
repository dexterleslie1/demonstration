# docker-compose external network用法

## 运行demo

```
# 创建external网络
cd service-network
docker-compose up -d

# 运行服务以测试使用external网络
cd service1
docker-compose up -d
cd service2
docker-compose up -d

# 测试service1和service2是否能够相互ping通
docker exec -it servic1 /bin/sh
docker exec -it service2 /bin/sh

# 在service1容器中ping service2
ping service2
# 在service2容器中ping service1
ping service1

```

