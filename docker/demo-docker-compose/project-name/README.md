## 设置 docker compose project name

参考
https://stackoverflow.com/questions/44924082/set-project-name-in-docker-compose-file

方法1、通过 docker-compose.yaml 中 name 属性指定 project name

```yaml
version: "3.0"

name: test

services:
  my_busybox:
    # container_name: my_busybox
    image: busybox
    command: |
      sh -c "sleep infinity"

```

方法2、通过 .env 设置 project name

```properties
COMPOSE_PROJECT_NAME=mytest
```

方法3、通过 docker compose 命令 -p 参数设置 project name

up 命令

```sh
docker compose -p mytest1 up -d
```

ps 命令

```sh
docker compose -p mytest1 ps
```

down 命令

```sh
docker compose -p mytest1 down -v
```

