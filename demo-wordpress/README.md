## 使用 docker compose 运行 wordpress

参考

> https://yeasy.gitbook.io/docker_practice/compose/wordpress

创建 docker-compose.yaml 内容如下：

```yaml
version: "3"
services:
   db:
     image: mariadb:10.4.19
     command:
       - --character-set-server=utf8mb4
       - --collation-server=utf8mb4_general_ci
       - --skip-character-set-client-handshake
       - --innodb-buffer-pool-size=1g
     volumes:
       - /data/data-test:/var/lib/mysql
     restart: always
     environment:
       - LANG=C.UTF-8
       - TZ=Asia/Shanghai
       - MYSQL_ROOT_PASSWORD=123456
       - MYSQL_DATABASE=wordpress
       - MYSQL_USER=wordpress
       - MYSQL_PASSWORD=wordpress

   wordpress:
     depends_on:
       - db
     image: wordpress:6.4.3
     ports:
       - "8000:80"
     restart: always
     environment:
       WORDPRESS_DB_HOST: db:3306
       WORDPRESS_DB_USER: wordpress
       WORDPRESS_DB_PASSWORD: wordpress
```

运行 wordpress

```sh
docker compose up -d
```

使用浏览器访问 http://192.168.1.205:8000/，根据提示初始化设置 wordpress



## 使用 syntaxhighlighter 插件高亮显示代码

参考

> https://wordpress.com/plugins/syntaxhighlighter