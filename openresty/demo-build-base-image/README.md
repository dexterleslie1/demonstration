###### 使用参考
- ad-hoc使用
```shell script
docker run -d --name openresty-xxx -e TZ=Asia/Shanghai \
  -v $PWD/nginx.conf:/usr/local/openresty/nginx/conf/nginx.conf \
  -v $PWD/naxsi.rules:/usr/local/openresty/nginx/conf/naxsi.rules \
  -p 80:80 --restart always \
  docker.118899.net:10001/yyd-public/yyd-openresty:1.0.0
```
    
- docker-compose 使用，yaml配置
```yaml

```

