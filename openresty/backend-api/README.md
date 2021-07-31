##### 运行openresty
```shell script
docker run --rm --name=openresty-demo -p 80:80 -v $(pwd)/nginx.conf:/usr/local/openresty/nginx/conf/nginx.conf openresty/openresty
```

##### nginx的proxy buffer参数总结
https://www.cnblogs.com/wshenjin/p/11608744.html