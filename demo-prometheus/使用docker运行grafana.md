# 使用`docker`运行`grafana`

> 参考这个资料使用`docker`运行`grafana`[链接](https://grafana.com/docs/grafana/latest/setup-grafana/installation/docker/)

运行`grafana`

> 注意：需要手动授权`data-grafana`目录所有人有写权限`sudo chmod -R a+w data-grafana`

```bash
docker run --rm -p 3000:3000 \
--name=grafana \
--volume "$PWD/data-grafana:/var/lib/grafana" \
grafana/grafana:6.6.2
```

使用浏览器访问`grafana`登录界面`http://localhost:3000`，帐号：`admin`，密码：`admin`