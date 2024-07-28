# `openresty https`设置

> [Configuring HTTPS servers](http://nginx.org/en/docs/http/configuring_https_servers.html)

示例的详细设置请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/openresty/demo-https)

运行示例步骤：

- 生成`https`证书

  ```bash
  docker run --rm -it -v $(pwd):/opt/temp nginx sh -c 'cd /opt/temp && openssl req -out cert.csr -new -nodes -newkey rsa:2048 -keyout key.pem'
  
  docker run --rm -it -v $(pwd):/opt/temp nginx sh -c 'cd /opt/temp && openssl x509 -req -days 36500 -in cert.csr -signkey key.pem -out cert.crt && rm cert.csr'
  ```

- 运行`openresty`

  ```bash
  docker-compose up
  ```

- 访问示例`https://localhost`

示例`https`的核心配置如下：

```nginx
listen       82 ssl;
server_name  localhost;

ssl_certificate cert.crt;
ssl_certificate_key key.pem;
```

