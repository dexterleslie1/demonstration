# `future`



## `random-id-picker` 服务

>中文名称为随机 `id` 选择器服务。
>
>背景：在海量订单或者商品数据场景中，需要模拟根据订单 `ID` 或者商品 `ID` 列表查询订单或者商品信息。此时需要借助随机 `ID` 选择器按照一定的频率随机抽取 `ID` 列表。

### 服务组件

`future-random-id-picker` 组件：

>使用 `SpringBoot` 实现以 `restful` 方式提供接口的核心服务。

- `GitHub` 地址：`https://github.com/dexterleslie1/future-random-id-picker.git`



`future-random-id-picker-sdk` 组件：

>`SpringBoot` 应用集成 `random-id-picker` 服务使用的 `sdk`。

- `GitHub` 地址：`https://github.com/dexterleslie1/future-random-id-picker-sdk.git`



### `Docker Compose` 运行服务

`docker-compose.yaml` 如下：

```yaml
version: "3.1"

services:
  # 随机 id 选择器服务
  future-random-id-picker-api:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/random-id-picker-service
    environment:
      - JAVA_OPTS=-Xmx512m
      - TZ=Asia/Shanghai
      - db_host=future-random-id-picker-db
      - db_port=3306
    ports:
      - '50000:8080'
  future-random-id-picker-db:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/random-id-picker-db
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_general_ci
      - --skip-character-set-client-handshake
      - --innodb-buffer-pool-size=256m
    environment:
      - LANG=C.UTF-8
      - TZ=Asia/Shanghai
      - MYSQL_ROOT_PASSWORD=123456

```



### `SpringBoot` 应用集成

`POM` 配置片段：

```xml
<!-- 随机 id 选择器服务 -->
<dependency>
    <groupId>com.github.dexterleslie1</groupId>
    <artifactId>future-random-id-picker-sdk</artifactId>
    <version>1.0.2</version>
</dependency>

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

`application.properties` 中配置随机 `id` 选择器服务：

```properties
# 随机id选择器服务ip地址
spring.future.random.id.picker.host=${random_id_picker_host:localhost}
# 随机id选择器服务端口
spring.future.random.id.picker.port=${random_id_picker_port:50000}
# 随机id选择器服务本地缓存id最大总数
spring.future.random.id.picker.cache-size=102400
# 随机id选择器服务支持的flag列表
spring.future.random.id.picker.flag-list=order,product
```

在 `SpringBoot Application` 中启用随机 `id` 选择器服务：

```java
@SpringBootApplication
@EnableFutureRandomIdPicker
public class ApplicationService {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationService.class, args);
    }
}
```

使用随机 `id` 服务接口：

```java
@Resource
RandomIdPickerService randomIdPickerService;
```



## `future-dockerizing`

>`GitHub` 地址 `https://github.com/dexterleslie1/future-dockerizing.git`
>
>`Docker` 公共镜像项目。

### `openresty-base`

>`OpenResty` 基础镜像

#### 制作镜像

编译镜像

```bash
./build.sh
```

推送镜像

```bash
./push.sh
```



#### 使用 - `ad-hoc` 方式

参考源代码中的 `nginx.conf.template` 编写 `nginx.conf`：

```nginx
#user  nobody;
#worker_processes  1;
worker_rlimit_nofile 65535;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;
error_log  logs/error.log  notice;

events {
    worker_connections  65535;
}


http {
    #log_format access '[$time_local] "$request" $status $request_body "$http_refferer" "$http_user_agent" $http_x_forwarded_for';
    include       mime.types;
    include       /usr/local/openresty/nginx/conf/naxsi_core.rules;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;
    gzip on;
    gzip_min_length 1k;
    gzip_buffers 16 64k;
    gzip_http_version 1.1;
    gzip_comp_level 6;
    gzip_types application/json text/plain application/javascript text/css application/xml;
    gzip_vary on;
    server_tokens off;
    autoindex off;
    access_log off;
    client_body_buffer_size  10k;
    client_header_buffer_size 1k;
    client_max_body_size 120k;
    large_client_header_buffers 2 8k;
    gzip_proxied any;

    # 反向代理配置
    proxy_buffering on;
    proxy_buffer_size 8k;
    proxy_buffers 32 8k;
    proxy_busy_buffers_size 16k;

    proxy_cache_path /tmp/proxy_cache levels=1:2 keys_zone=cache_one:200m inactive=1d max_size=2g use_temp_path=off;

    # nginx 开启websocket支持
    # https://blog.csdn.net/u011411069/article/details/98475433
    map $http_upgrade $connection_upgrade {
        default upgrade;
        '' close;
    }

    upstream backend {
        keepalive 1024;
        server 192.168.1.181:8080;
    }

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        # 支持vue、react、vitepress等编译静态网站发布
        location / {
            root   /usr/local/openresty/nginx/html;
            index  index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        # 支持websocket反向代理
        location / {
            include /usr/local/openresty/nginx/conf/naxsi.rules;
            proxy_set_header Host $host:$server_port;
            proxy_set_header x-forwarded-for $remote_addr;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_pass http://backend;
        }

        # 支持restful api反向代理
        location /api/ {
            include /usr/local/openresty/nginx/conf/naxsi.rules;
            proxy_set_header Host $host:$server_port;
            proxy_set_header x-forwarded-for $remote_addr;
            proxy_http_version 1.1;
            proxy_set_header Connection '';
            proxy_pass http://backend;
        }

        location /request_denied {
            default_type application/json;
            return 403 '{"errorCode":600,"errorMessage":"您提交数据存在安全问题，被服务器拒绝，修改数据后重试"}';
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   /usr/local/openresty/nginx/html;
        }
    }
}
```

参考源代码中的 `naxsi.rules.template` 编写 `naxsi.rules`：

```nginx
LearningMode;
SecRulesEnabled;
DeniedUrl "/request_denied";

## Check & Blocking Rules
CheckRule "$SQL >= 8" BLOCK;
CheckRule "$RFI >= 8" BLOCK;
CheckRule "$TRAVERSAL >= 4" BLOCK;
CheckRule "$EVADE >= 4" BLOCK;
CheckRule "$XSS >= 8" BLOCK;

BasicRule wl:2;
BasicRule wl:16;
BasicRule wl:1001 "mz:$URL:/api/v1/client/register|$BODY_VAR:additionalinformation";
BasicRule wl:1015 "mz:$URL:/api/v1/message/ack|$BODY_VAR:ids";
```

`docker run` 命令运行容器：

```shell script
docker run -d --name openresty-xxx -e TZ=Asia/Shanghai \
  -v $PWD/nginx.conf:/usr/local/openresty/nginx/conf/nginx.conf \
  -v $PWD/naxsi.rules:/usr/local/openresty/nginx/conf/naxsi.rules \
  -p 80:80 --restart always \
  registry.cn-hangzhou.aliyuncs.com/future-public/openresty-base:x.x.x
```



#### 使用 - `Docker Compose` 方式

参考源代码中的 `nginx.conf.template` 编写 `nginx.conf`：

```nginx
#user  nobody;
#worker_processes  1;
worker_rlimit_nofile 65535;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;
error_log  logs/error.log  notice;

events {
    worker_connections  65535;
}


http {
    #log_format access '[$time_local] "$request" $status $request_body "$http_refferer" "$http_user_agent" $http_x_forwarded_for';
    include       mime.types;
    include       /usr/local/openresty/nginx/conf/naxsi_core.rules;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;
    gzip on;
    gzip_min_length 1k;
    gzip_buffers 16 64k;
    gzip_http_version 1.1;
    gzip_comp_level 6;
    gzip_types application/json text/plain application/javascript text/css application/xml;
    gzip_vary on;
    server_tokens off;
    autoindex off;
    access_log off;
    client_body_buffer_size  10k;
    client_header_buffer_size 1k;
    client_max_body_size 120k;
    large_client_header_buffers 2 8k;
    gzip_proxied any;

    # 反向代理配置
    proxy_buffering on;
    proxy_buffer_size 8k;
    proxy_buffers 32 8k;
    proxy_busy_buffers_size 16k;

    proxy_cache_path /tmp/proxy_cache levels=1:2 keys_zone=cache_one:200m inactive=1d max_size=2g use_temp_path=off;

    # nginx 开启websocket支持
    # https://blog.csdn.net/u011411069/article/details/98475433
    map $http_upgrade $connection_upgrade {
        default upgrade;
        '' close;
    }

    upstream backend {
        keepalive 1024;
        server 192.168.1.181:8080;
    }

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        # 支持vue、react、vitepress等编译静态网站发布
        location / {
            root   /usr/local/openresty/nginx/html;
            index  index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        # 支持websocket反向代理
        location / {
            include /usr/local/openresty/nginx/conf/naxsi.rules;
            proxy_set_header Host $host:$server_port;
            proxy_set_header x-forwarded-for $remote_addr;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_pass http://backend;
        }

        # 支持restful api反向代理
        location /api/ {
            include /usr/local/openresty/nginx/conf/naxsi.rules;
            proxy_set_header Host $host:$server_port;
            proxy_set_header x-forwarded-for $remote_addr;
            proxy_http_version 1.1;
            proxy_set_header Connection '';
            proxy_pass http://backend;
        }

        location /request_denied {
            default_type application/json;
            return 403 '{"errorCode":600,"errorMessage":"您提交数据存在安全问题，被服务器拒绝，修改数据后重试"}';
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   /usr/local/openresty/nginx/html;
        }
    }
}
```

参考源代码中的 `naxsi.rules.template` 编写 `naxsi.rules`：

```nginx
LearningMode;
SecRulesEnabled;
DeniedUrl "/request_denied";

## Check & Blocking Rules
CheckRule "$SQL >= 8" BLOCK;
CheckRule "$RFI >= 8" BLOCK;
CheckRule "$TRAVERSAL >= 4" BLOCK;
CheckRule "$EVADE >= 4" BLOCK;
CheckRule "$XSS >= 8" BLOCK;

BasicRule wl:2;
BasicRule wl:16;
BasicRule wl:1001 "mz:$URL:/api/v1/client/register|$BODY_VAR:additionalinformation";
BasicRule wl:1015 "mz:$URL:/api/v1/message/ack|$BODY_VAR:ids";
```

`docker-compose.yaml`：

```yaml
openresty:
  image: registry.cn-hangzhou.aliyuncs.com/future-public/openresty-base:x.x.x
  environment:
    - TZ=Asia/Shanghai
  ports:
    - "80:80"
  volumes:
    - ./nginx.conf:/usr/local/openresty/nginx/conf/nginx.conf
    - ./naxsi.rules:/usr/local/openresty/nginx/conf/naxsi.rules
```



### `mariadb-auto-backup`

>每天早上6点22分定时备份数据库到亚马逊 `oss`。

#### 制作镜像

编译镜像

```bash
./build.sh
```

推送镜像

```bash
./push.sh
```



#### 使用 - `Docker Compose` 方式

Docker Compose 文件如下：

```yaml
future-auth-db-backup:
    image: registry.cn-hangzhou.aliyuncs.com/yyd-public/mariadb-auto-backup:x.x.x
    environment:
      # 是否启用自动备份
      - ENABLE_AUTO_BACKUP=true
      # 被备份的数据库主机ip或者主机名，例如：demo-mariadb-backup-server
      - DBHOST=future-auth-db
      # 被备份的数据库用户名，例如：root
      - DBUSER=root
      # 被备份的数据库密码
      - DBPASSWORD=123456
      # 被备份的数据库名称，例如：demo_db
      - DBNAME=future_auth
      # 亚马逊桶名称，例如：backup-db-all
      - BUCKETNAME=backup-db-all
      # 亚马逊桶下的子目录，例如：demo-test
      - BUCKETSUBDIR=future-auth
      # 亚马逊access key id
      - AWS_ACCESS_KEY_ID=xxx
      # 亚马逊access key密钥
      - AWS_SECRET_ACCESS_KEY=xxxxxx
    networks:
      - net
```



