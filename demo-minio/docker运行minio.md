# 使用`docker`或`docker compose`运行`minio`

>[`minio`对象存储](http://docs.minio.org.cn/docs/master/minio-docker-quickstart-guide)

## 使用`docker`运行`minio`

```bash
docker run --rm --name demo-minio -p 9000:9000 -e "MINIO_ROOT_USER=admin" -e "MINIO_ROOT_PASSWORD=admin123456" minio/minio:RELEASE.2021-06-17T00-10-46Z server /data
```

## 使用`docker compose`运行`minio`

`docker-compose.yaml`配置如下：

```yaml
version: "3.0"

services:
  minio-demo:
    container_name: minio-demo
    image: minio/minio:RELEASE.2021-06-17T00-10-46Z
    command: server /data
    environment:
      - MINIO_ROOT_USER=root
      - MINIO_ROOT_PASSWORD=root123456
    #volumes:
    # 存储minio业务数据
    #  - ~/data-minio-demo/data:/data
    #  - ~/data-minio-demo/config:/root/.minio
    #ports:
    #  - '9000:9000'
    network_mode: host
```

启动`minio`服务

```bash
docker compose up -d
```

