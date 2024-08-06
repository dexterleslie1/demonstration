# `ConfigMap`和`Secret`配置应用程序

## `ConfigMap`用法

### 创建`configmap`

#### 使用`kubectl create configmap`创建`ConfigMap`

```shell
### 从指定键值对创建configmap
# 创建ConfigMap时指定键值对
kubectl create configmap demo-config1 --from-literal=sleep-interval=25

# 查询configmap对列表
kubectl get configmap

# 查看指定configmap详细信息
kubectl get configmap demo-config1 -o yaml

# 创建包含多个键值对的ConfigMap
kubectl create configmap demo-config2 --from-literal=foo=bar --from-literal=bar=baz

# 查看指定configmap详细信息
kubectl get configmap demo-config2 -o yaml



### 从文件内容创建configmap

# demo.conf内容如下:
demo {
 hello = demo
}

# 从文件内容创建configmap
kubectl create configmap demo-config3 --from-file=demo.conf

# 查看configmap详细信息
kubectl get configmap demo-config3 -o yaml



### 从文件内容创建configmap并指定key

# demo.conf内容如下:
demo {
 hello = demo
}

# 从文件内容创建configmap并指定key=customkey
kubectl create configmap demo-config3 --from-file=customkey=demo.conf

# 查看configmap详细信息
kubectl get configmap demo-config3 -o yaml



### 从文件夹创建configmap

# configmap-dir/key1内容
value1
# configmap-dir/key2内容
value2

# 从文件夹创建configmap
kubectl create configmap demo-config5 --from-file=./configmap-dir

# 查看configmap详细信息
kubectl get configmap demo-config5 -o yaml


### 预读取文件内容，再使用yaml创建ConfigMap
# https://stackoverflow.com/questions/51268488/kubernetes-configmap-set-from-file-in-yaml-configuration

# 模拟创建nginx.conf文件内容如下:
server {
	listen 80;
}

# 输出创建configmap的yaml文件格式
kubectl create configmap --dry-run=client somename --from-file=nginx.conf --output yaml

# 修改configmap yaml输出如下:
apiVersion: v1
data:
  nginx.conf: "server {\n\tlisten 80;\n}\n"
kind: ConfigMap
metadata:
  name: somename
```



#### 使用`yaml`文件创建`configmap`

```
# 1.yaml内容
apiVersion: v1
kind: ConfigMap
metadata:
 name: configmap1
data:
 1.properties: |
  username: admin
  password: 123456
 2.properties: |
  key1: value1
  key2: value2
  
# 创建configmap
kubectl create -f 1.yaml

# 查看configmap详细信息
kubectl get configmap configmap1 -o yaml
```



### 在容器中使用`configmap`

#### 使用环境变量方式暴露`configmap`条目到容器中

```bash
# 创建configmap
kubectl create configmap demo-config1 --from-literal=sleep-interval=25

# 使用环境变量方式暴露configmap到容器中
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["/bin/sh", "-c", "sleep 3600"]
    env:
     - name: INTERVAL
       valueFrom:
        configMapKeyRef:
         name: demo-config1
         key: sleep-interval
         # configmap可选，即使configmap不存在pod依然能够启动
         # optional: true
         
# 创建pod
kubectl create -f 1.yaml

# 查看pod中的环境变量INTERVAL
kubectl exec -it pod1 sh

# 在pod的shell中执行命令env查看环境变量
/ # env



### 使用环境变量方式一次暴露多个configmap条目

# 创建configmap
kubectl create configmap demo-config1 --from-literal=key1=value1 --from-literal=key2=value2

# 用于创建pod，暴露的环境变量命名为MY_key1、MY_key2
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["/bin/sh", "-c", "sleep 3600"]
    envFrom:
     # 如果不指定prefix，则使用configmap中的key，环境变量为key1、key2
     - prefix: MY_
       configMapRef:
        name: demo-config1
        
# 创建pod
kubectl create -f 1.yaml

# 进入pod中的shell并使用命令env查看环境变量
kubectl exec -it pod1 sh
/ # env
```



#### 使用命令行参数方式暴露`configmap`条目

```bash
# 创建configmap
kubectl create configmap demo-config1 --from-literal=sleep-interval=25

# entrypoint.sh内容如下:
#!/bin/sh

echo `date` - app is going to sleep $1 seconds...
sleep $1
echo `date` - app sleep finishing.

# Dockerfile内容如下:
FROM busybox

COPY entrypoint.sh /
RUN chmod a+x /entrypoint.sh
CMD ["5"]
ENTRYPOINT ["sh", "/entrypoint.sh"]

# 编译镜像
docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args .

# 以自定义参数运行容器
docker run --rm --name=demo registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args 1

# 推送镜像
docker push registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args
    command: ["sh", "/entrypoint.sh"]
    env:
     - name: INTERVAL
       valueFrom:
        configMapKeyRef:
         name: demo-config1
         key: sleep-interval
    # 字段pod.spec.containers.args中无法直接引用configmap的条目
    # 但可以利用configmap条目初始化某个环境变量，然后再在参数字段中引用该环境变量
    args: ["$(INTERVAL)"]
 
# 查看pod日志
kubectl logs -f pod1
```



#### 使用`configmap`卷将条目暴露为文件

> configmap卷会将configmap中的每个条目均暴露成一个文件。运行在容器中的进程可通过读取文件内容获取对应的条目值。

```bash
## 创建configmap

# 创建configmap-files/my-nginx-config.conf内容如下:
server {
	listen	80;
	server_name www.kubia-example.com;

	gzip 	on;
	gzip_types	text/plain application/xml;
	
	location / {
		root 	/usr/share/nginx/html;
		index	index.html	index.htm;
	}
}

# 创建configmap-files/sleep-interval内容如下:
25

# 从目录configmap-files创建configmap
kubectl create configmap demo-config1 --from-file=configmap-files

# 查看configmap详细信息
kubectl get configmap demo-config1 -o yaml

## 创建pod引用configmap卷
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       mountPath: /etc/nginx/conf.d
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     
# 创建pod
kubectl create -f 1.yaml
   
## 调试pod中的nginx是否使用configmap卷中my-nginx-config.conf gzip配置

# 临时端口转发
kubectl port-forward pod1 8080:80

# 使用curl调试gzip，日志中包含Content-Encoding: gzip表示nginx gzip配置生效
curl -H "Accept-Encoding: gzip" -I localhost:8080

# 进入pod查看被挂载的configmap内容
kubectl exec -it pod1 ls /etc/nginx/conf.d
kubectl exec -it pod1 cat /etc/nginx/conf.d/my-nginx-config.conf


### 上面例子存在问题，它会在/etc/nginx/conf.d目录下暴露my-nginx-config.conf和sleep-interval两个文件，其中sleep-interval是configmap的一个key，但是不会被nginx使用。可以通过使用items指定configmap卷中需要暴露的条目达到隐藏sleep-interval目的并把my-nginx-config.conf暴露为名为gzip.conf的文件。

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       mountPath: /etc/nginx/conf.d
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     # 指定只暴露configmap中的my-nginx-config.conf到名为gzip.conf文件
     # configmap中的sleep-interval不会被暴露
     items:
      - key: my-nginx-config.conf
        path: gzip.conf
        
# 查看pod中/etc/nginx/conf.d目录内容
kubectl exec -it pod1 ls /etc/nginx/conf.d
kubectl exec -it pod1 cat /etc/nginx/conf.d/gzip.conf



### configmap独立条目作为文件被挂载且不隐藏容器目录中已存在的其他文件或者目录。上面例子存在问题，因为挂在configmap卷到/etc/nginx/conf.d目录中，如果/etc/nginx/conf.d目录存在其他文件或者目录，则这些已存在的文件或者目录都会被挂载隐藏，所以需要使用subPath字段解决此问题。

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       # mountPath: /etc/nginx/conf.d
       # 指定挂载至某一个文件而不是/etc/nginx/conf.d目录
       mountPath: /etc/nginx/conf.d/my-n-c.conf
       # 指定需要挂载的configmap卷中的key
       subPath: gzip.conf
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     # 指定只暴露configmap中的my-nginx-config.conf到名为gzip.conf文件
     # configmap中的sleep-interval不会被暴露
     items:
      - key: my-nginx-config.conf
        path: gzip.conf
        
# 查看pod中/etc/nginx/conf.d目录内容
kubectl exec -it pod1 ls /etc/nginx/conf.d
kubectl exec -it pod1 cat /etc/nginx/conf.d/my-n-c.conf



### 指定挂载文件的默认权限

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       # mountPath: /etc/nginx/conf.d
       # 指定挂载至某一个文件而不是/etc/nginx/conf.d目录
       mountPath: /etc/nginx/conf.d/my-n-c.conf
       # 指定需要挂载的configmap卷中的key
       subPath: gzip.conf
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     # 挂载文件的默认权限 rw-rw----
     defaultMode: 0660
     # 指定只暴露configmap中的my-nginx-config.conf到名为gzip.conf文件
     # configmap中的sleep-interval不会被暴露
     items:
      - key: my-nginx-config.conf
        path: gzip.conf
 
# 查看pod中/etc/nginx/conf.d目录内容
kubectl exec -it pod1 -- ls -alh /etc/nginx/conf.d
```



### `configmap`热更新且不重启应用程序

> 在次之前提过，使用环境变量或者命令行参数作为配置员的弊端在于无法在进程运行时更新配置。将configmap暴露为卷可以达到配置热更新的效果，无须重新创建pod或者重启容器。
>
> NOTE: 如果挂载的是容器中的单个文件而不是完整的卷，configmap更新之后对应的文件不会被更新！

```bash
## 创建configmap

# 创建configmap-files/my-nginx-config.conf内容如下:
server {
	listen	80;
	server_name www.kubia-example.com;

	gzip 	on;
	gzip_types	text/plain application/xml;
	
	location / {
		root 	/usr/share/nginx/html;
		index	index.html	index.htm;
	}
}

# 从目录configmap-files创建configmap
kubectl create configmap demo-config1 --from-file=configmap-files

# 查看configmap详细信息
kubectl get configmap demo-config1 -o yaml

## 创建pod引用configmap卷
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       mountPath: /etc/nginx/conf.d
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     
# 创建pod
kubectl create -f 1.yaml

# 进入pod查看被挂载的configmap内容
kubectl exec -it pod1 cat /etc/nginx/conf.d/my-nginx-config.conf

# 编辑configmap demo-config1配置，修改gzip on为gzip off
kubectl edit configmap demo-config1

# 等待约1分钟后再次查看my-nginx-config.conf发现gzip热更新为off
kubectl exec -it pod1 cat /etc/nginx/conf.d/my-nginx-config.conf
```



### 其他参考综合应用例子

#### 键值对存储

```shell
# 1.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: configmap1
data:
 1.properties: |
  username: admin
  password: 123456
 2.properties: |
  key1: value1
  key2: value2

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "sleep 3600;"]
   volumeMounts:
   - name: volume1
     # 指定volume中的路径2repath.properties挂载到/2repath.properties
     # https://kubernetes.io/docs/concepts/storage/volumes/#using-subpath
     mountPath: /2repath.properties
     subPath: 2repath.properties
     # 挂载volume1到/root目录中
   - name: volume1
     mountPath: /root
 volumes:
 - name: volume1
   configMap:
    name: configmap1
    items:
     # 使用path指定configmap的2.properties这个key映射到volume的映射路径为2repath.properties
     # 并且volume被挂载后1.properties不会被挂载，因为只指定了2.properties
     # https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#add-configmap-data-to-a-specific-path-in-the-volume
     - key: 2.properties
       path: 2repath.properties

# 查询configmap列表
kubectl get configmap

# 显示configmap详细信息
kubectl describe configmap configmap1

# 进入pod查看1.properties
kubectl exec -it pod1 /bin/sh
/ # ls
/ # cat 2repath.properties 
/ # ls /root/
/ # cat /root/2repath.properties 
/ # 
```





#### `nginx.conf`配置存储

```yaml
# 1.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: configmap1
data:
 nginx.conf: |
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
        #include       /usr/local/openresty/nginx/conf/naxsi_core.rules;
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
        client_max_body_size 2m;
        large_client_header_buffers 2 8k;
        gzip_proxied any;

        # 反向代理配置
        proxy_buffering on;
        proxy_buffer_size 8k;
        proxy_buffers 32 8k;
        proxy_busy_buffers_size 16k;

        proxy_cache_path /tmp/proxy_cache levels=1:2 keys_zone=cache_one:200m inactive=1d max_size=2g use_temp_path=off;

        upstream backend {
            keepalive 1024;
            server yyd-ops-api-dev:8080;
        }

        server {
            listen       80;
            server_name  localhost;

            #charset koi8-r;

            #access_log  logs/host.access.log  main;

            set $naxsi_extensive_log 1;

            location / {
                #include /usr/local/openresty/nginx/conf/naxsi.rules;
                proxy_set_header Host $host:$server_port;
                #proxy_set_header x-forwarded-for $remote_addr;
                proxy_http_version 1.1;
                proxy_set_header Connection '';
                proxy_pass http://backend;
            }

            location /request_denied {
                default_type application/json;
                return 403 '{"errorCode":600,"errorMessage":"您提交数据存在安全问题，被服务器拒绝，修改数据后重试"}';
            }
        }
    }

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "sleep 3600;"]
   volumeMounts:
   - name: volume1
     mountPath: /root/nginx.conf
     subPath: nginx.conf
 volumes:
 - name: volume1
   configMap:
    name: configmap1
    items:
    - key: nginx.conf
      path: nginx.conf
  
# NOTE: 使用下面命令获取yaml文件内的nginx.conf内容
# 否则直接复制粘贴nginx.conf内容到yaml会报告yaml文件格式错误
# https://stackoverflow.com/questions/51268488/kubernetes-configmap-set-from-file-in-yaml-configuration
kubectl create configmap --dry-run=client somename --from-file=nginx.conf --output yaml
apiVersion: v1
data:
  nginx.conf: |
    #user  nobody;
    #worker_processes  1;
    worker_rlimit_nofile 65535;
......

# 查看configmap
kubectl get configmap
kubectl describe configmap configmap1

# 进入容器查看nginx.conf
kubectl exec -it pod1 /bin/sh
/ # cat /root/nginx.conf 
#user  nobody;
  #worker_processes  1;
  worker_rlimit_nofile 65535;
......
```



#### 综合应用案例

```shell
# 给容器传递ConfigMap条目作为环境变量
# 2.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfig5
data:
 k1: v1
 k2: v2
 k3: v3

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "echo $MYENV1; sleep 7200"]
    env:
     - name: MYENV1
       valueFrom:
        configMapKeyRef:
         name: myconfig5
         key: k3

# 查看pod日志，控制台会输出v3
kubectl logs -f pod1

# 一次性传递ConfigMap的所有条目作为环境变量
# 2.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfig5
data:
 k1: v1
 k2: v2
 k3: v3

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "echo $MYCONFIG_k1; sleep 7200"]
    envFrom:
     - prefix: MYCONFIG_
       configMapRef:
        name: myconfig5

# 查看pod日志，输出v1
kubectl logs -f pod1

# 传递ConfigMap条目作为命令行参数
# 2.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfig5
data:
 k1: v1
 k2: "7"
 k3: v3

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args
    env:
     - name: MYENV1
       valueFrom:
        configMapKeyRef:
         name: myconfig5
         key: k2
    args: ["$(MYENV1)"]

# 查看pod日志
kubectl logs -f pod1

# 使用ConfigMap卷将条目暴露为文件
# redis.conf 内容如下:
daemon: yes
bind: 0.0.0.0
cluster: yes

# redis1.conf 内容如下:
daemon: yes
bind: 0.0.0.1
cluster: yes

# 从redis.conf和redis1.conf创建configmap
kubectl create configmap myconfigredis --from-file=redis.conf --from-file=redis1.conf

# 指定暴露ConfigMap中redis1.conf条目
# 2.yaml 内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "ls /etc/redis/; sleep 7200"]
    volumeMounts:
     - name: config
       mountPath: /etc/redis
       readOnly: true
 volumes:
  - name: config
    configMap:
     name: myconfigredis
     # 只暴露指定ConfigMap条目，例如：只显示redis1.conf条目，不显示redis.conf条目
     items:
        # 指定暴露ConfigMap条目的key
      - key: redis1.conf
        # 指定暴露ConfigMap条目的key重命名为新的文件名
        path: my-redis1.conf

# 查看pod日志，日志只输出my-redis1.conf
kubectl logs -f pod1

# mountPath以目录方式挂载会导致目录中已存在的文件被隐藏
# 针对以上缺陷使用ConfigMap独立条目作为文件被挂载且不隐藏文件夹中其他文件
# 指定只暴露redis1.conf
# 2.yaml 内容如下: 
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "ls /etc/redis; sleep 7200"]
    volumeMounts:
     - name: config
       mountPath: /etc/redis/my-redis.conf
       subPath: redis1.conf
 volumes:
  - name: config
    configMap:
     name: myconfigredis
```



## `Secret`用法

> configmap和secret对比，secret条目的内容以base64格式编码。secret条目可以用于存储二进制文件大小限制于1MB。secret卷存储于内存(secret采用内存文件系统挂载secret到容器目录)。



### 默认令牌`secret`介绍

> 每个pod都会被自动挂载上一个secret卷。这个secret包含3个条目分别为ca.crt、namespace、token，包含了从pod内部安全访问kubernetes API服务器所需的全部信息。

```bash
# 用于创建pod
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "ls /etc/redis; sleep 7200"

# 创建pod
kubectl create -f 1.yaml

# 查看pod详细信息，可以看到pod Volumes中挂载了一个名为default-token-przdr secret卷，并且能够看到Mounts显示secret卷被挂载到/var/run/secrets/kubernetes.io/serviceaccount目录中
kubectl describe pod pod1

# 查看secret卷列表
kubectl get secret

# 查询secret卷详细信息
kubectl describe secret default-token-przdr

# 进入pod查看secret卷挂载目录
kubectl exec -it pod1 sh
/ # cd /var/run/secrets/kubernetes.io/serviceaccount
/ # ls -alh
```



### `secret`卷存储于内存中

> 通过挂载secret卷至文件夹/etc/nginx/certs将证书和私钥成功传递给容器。secret卷采用内存文件系统tmpfs挂载到容器目录中，存储在secret中的数据不会写入磁盘，这样就无法被窃取。

```
# 创建https证书
openssl genrsa -out https.key 2048
openssl req -new -x509 -key https.key -out https.cert -days 3650 -subj /CN=www.kubia-example.com

# 创建secret
kubectl create secret generic demo-secret1 --from-file=https.key --from-file=https.cert

# 在pod中使用secret
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: certs
       mountPath: /etc/nginx/certs
       readOnly: true
    ports:
     - containerPort: 80
 volumes:
  - name: certs
    secret:
     secretName: demo-secret1

# 查看 /etc/nginx/certs对应的mount point，可以发现是使用tmpfs文件系统
kubectl exec pod1 -- mount | grep certs
```



### 使用`secret`配置`nginx https`

```bash
## 创建nginx configmap

# my-nginx-config.conf内容如下:
server {
	listen	80;
	listen	443 ssl;
	server_name	www.kubia-example.com;
	ssl_certificate	certs/https.cert;
	ssl_certificate_key	certs/https.key;
	ssl_protocols	TLSv1 TLSv1.1 TLSv1.2;
	ssl_ciphers	HIGH:!aNULL:!MD5;
	
	location / {
		root /usr/share/nginx/html;
		index index.html index.htm;
	}
}

# 创建configmap
kubectl create configmap demo-config1 --from-file=my-nginx-config.conf

# 创建https证书
openssl genrsa -out https.key 2048
openssl req -new -x509 -key https.key -out https.cert -days 3650 -subj /CN=www.kubia-example.com

# 创建bar文件，内容为foo
echo bar > foo

# 创建secret
kubectl create secret generic secret-https --from-file=https.key --from-file=https.cert --from-file=foo

# 查看secret详细信息
kubectl get secret secret-https -o yaml

# 在pod中使用secret
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: https-config
       mountPath: /etc/nginx/conf.d
       readOnly: true
     - name: certs
       mountPath: /etc/nginx/certs
       readOnly: true
    ports:
     - containerPort: 80
 volumes:
  - name: https-config
    configMap:
     name: demo-config1
     items:
      - key: my-nginx-config.conf
        path: https.conf
  - name: certs
    secret:
     secretName: secret-https

# 测试nginx是否正确使用secret中的证书和密钥
kubectl port-forward pod1 443:443
curl https://localhost -k -v
```



### 通过环境变量暴露`secret`条目

```bash
# 从键值对创建secret
kubectl create secret generic demo-secret1 --from-literal=key1=value1

# 查看secret详细信息
kubectl get secret demo-secret1 -o yaml

# 通过环境变量暴露secret条目到pod中
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: busybox
    name: busybox
    command: ["/bin/sh", "-c", "sleep 3600;"]
    env:
     - name: MY_KEY1
       valueFrom:
        secretKeyRef:
         name: demo-secret1
         key: key1
                 
# 创建pod
kubectl create -f 1.yaml

# 进入pod查看环境变量
kubectl exec -it pod1 sh
/ # env
```



### 私有镜像拉取时提供帐号和密码

> [Pull an Image from a Private Registry](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/)

```shell
# 创建帐号和密码secret
kubectl create secret docker-registry regcred --docker-server=my.docker.hub --docker-username=xxx --docker-password=xxxx

# 查看secret
kubectl get secret regcred --output=yaml

# 在pod中使用secret拉取镜像
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment-yyd-ops-db
spec:
 selector:
  matchLabels:
   app: yyd-ops-db
 template:
  metadata:
   labels:
    app: yyd-ops-db
  spec:
   # 提供私有镜像拉取帐号和密码
   imagePullSecrets:
   - name: regcred
   containers:
   - name: yyd-ops-db
     image: my.docker.hub/yyd-private/yyd-ops-db:1.0.0
     ports:
     - containerPort: 3306
     env:
     - name: MYSQL_ROOT_PASSWORD
       value: "123456"
     - name: TZ
       value: "Asia/Shanghai"
```

