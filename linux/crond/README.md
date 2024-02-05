## 演示crond配置和使用



### cron相关命令

查看 crontab  内容

```sh
crontab -l
```



### 使用 crontab 应用 hello-cron 配置(不通过 crontab -e编辑cron服务)

hello-cron 内容如下：

```sh
* * * * * date >> /tmp/1.txt
```

应用 hello-cron

```sh
crontab /etc/cron.d/hello-cron
```

显示 crontab 内容

```sh
crontab -l
```



### centOS8 配置 crond

备注： centOS8 不需要设置 crond 服务，此服务默认已经启动。

重启 crond 服务

```sh
systemctl restart crond
```

输入命令crontab -e编辑cron配置，下面配置表示06:45和14:45会自动执行cron任务

```sh
45 6,14 * * * sh /data/backup/autobackup.sh
```

每一分钟执行一次

```sh
* * * * * sh /opt/1.sh
```

早上17:00自动清除/usr/local/openresty/nginx/logs/error.log日志

```sh
0 17 * * * cat /dev/null > /usr/local/openresty/nginx/logs/error.log
```

每2天23点执行，https://serverfault.com/questions/204265/how-to-configure-cron-job-to-run-every-2-days-at-11pm

```sh
0 23 */2 * * insert_your_script_here.sh
```



### ubuntu 20.4 配置 crond

备注：  ubuntu20.4 不需要设置 crond 服务，此服务默认已经启动。

重启 cron 服务

```sh
sudo systemctl restart cron
```



### 查看crond运行日志

```sh
tail -f /var/log/cron
```



### docker 配置 cron

https://stackoverflow.com/questions/37458287/how-to-run-a-cron-job-inside-a-docker-container

Dockerfile 内容如下：

```dockerfile
#FROM ubuntu:latest
FROM mariadb:10.4.19

RUN apt-get update && apt-get -y install cron

# Copy hello-cron file to the cron.d directory
COPY hello-cron /etc/cron.d/hello-cron
 
# Give execution rights on the cron job
RUN chmod 0644 /etc/cron.d/hello-cron

# Apply cron job
RUN crontab /etc/cron.d/hello-cron
 
# Run the command on container startup
# printenv > /etc/environment用于把docker-compose传递的环境变量传递到cron中
CMD ["sh", "-c", "printenv > /etc/environment && cron -f"]

```

hello-cron 内容如下：

```
# cron输出重定向到docker logs中
# https://stackoverflow.com/questions/36441312/how-to-redirect-cron-job-output-to-stdout
* * * * * echo "`date` - $ENVVAR1" >> /tmp/1.txt && date > /proc/$(cat /var/run/crond.pid)/fd/1 2>&1

```

编译镜像

```sh
docker build --rm -t cron-example .
```

运行镜像

```sh
docker run -d --rm -e ENVVAR1=v1 -e TZ=Asia/Shanghai --name test1 cron-example
```

查看容器 cron 服务是否正常运行，进入容器 cli 后，通过查看 /tmp/1.txt 输出确认 cron 服务是否正常运行

```sh
docker exec -it test1 /bin/bash
tail -f /tmp/1.txt
```

