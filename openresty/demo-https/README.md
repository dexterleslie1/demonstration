## 演示openresty https配置

openresty、nginx https配置

> http://nginx.org/en/docs/http/configuring_https_servers.html
>

演示步骤

```shell
# 生成https证书
docker run --rm -it -v $(pwd):/opt/temp nginx sh -c 'cd /opt/temp && openssl req -out cert.csr -new -nodes -newkey rsa:2048 -keyout key.pem'

docker run --rm -it -v $(pwd):/opt/temp nginx sh -c 'cd /opt/temp && openssl x509 -req -days 36500 -in cert.csr -signkey key.pem -out cert.crt && rm cert.csr'

# 运行openresty
docker-compose up
```

