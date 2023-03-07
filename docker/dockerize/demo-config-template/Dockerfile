from openresty/openresty

copy ./sources.list /etc/apt/sources.list

run apt-get update

run apt-get install -y curl

env DOCKERIZE_VERSION v0.6.1
run curl --silent --output dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz https://bucketxyh.oss-cn-hongkong.aliyuncs.com/docker/dockerize-linux-amd64-v0.6.1.tar.gz

run tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

#copy ./nginx.tmpl /usr/local/openresty/nginx/conf/nginx.tmpl 

#CMD ["/usr/local/openresty/bin/openresty", "-g", "daemon off;"]
#CMD dockerize -template /usr/local/openresty/nginx/conf/nginx.tmpl:/usr/local/openresty/nginx/conf/nginx.conf -stdout /usr/local/openresty/nginx/logs/access.log -stderr /usr/local/openresty/nginx/logs/error.log /usr/local/openresty/bin/openresty -g "daemon off;"
CMD dockerize -template /usr/local/openresty/nginx/conf/nginx.tmpl:/usr/local/openresty/nginx/conf/nginx.conf /usr/local/openresty/bin/openresty -g "daemon off;"
