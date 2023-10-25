## grafana用法

### 使用docker运行grafana

> https://grafana.com/docs/grafana/latest/setup-grafana/installation/docker/

```shell
# 运行grafana，NOTE: 需要手动授权 data-grafana 目录所有人有写权限 sudo chmod -R a+w data-grafana
docker run --rm -p 3000:3000 \
--name=grafana \
--volume "$PWD/data-grafana:/var/lib/grafana" \
grafana/grafana:6.6.2

# 使用浏览器访问grafana UI，帐号: admin，密码: admin
http://localhost:3000
```

