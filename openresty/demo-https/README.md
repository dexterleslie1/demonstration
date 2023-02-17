# 演示openresty https配置

## openresty、nginx https配置

> http://nginx.org/en/docs/http/configuring_https_servers.html
>
> 使用OpenSSL生成private.pem、certificate.crt证书，其中private.pem私钥，certificate.crt为x.509证书
>
> 设置nginx.conf文件
> server {
>         listen       80;
>         listen       443 ssl;
>         server_name  localhost;
>         ssl_certificate certificate.crt;
>         ssl_certificate_key private.pem;

## 演示步骤

```shell
# 生成https证书
docker run --rm -it -v $(pwd):/opt/temp nginx sh -c 'cd /opt/temp && openssl req -out cert.csr -new -nodes -newkey rsa:2048 -keyout key.pem'

docker run --rm -it -v $(pwd):/opt/temp nginx sh -c 'cd /opt/temp && openssl x509 -req -days 3650 -in cert.csr -signkey key.pem -out cert.crt && rm cert.csr'

# 运行openresty
docker-compose up
```

