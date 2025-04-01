FROM debian:buster

# 复制编译后的openresty压缩包
COPY openresty.tar /usr/local

# 解压编译后的openresty压缩包到/usr/local目录
RUN cd /usr/local && tar -xvzf openresty.tar

# 删除编译后的openresty压缩包
RUN rm -f openresty.tar

ADD sources.list /etc/apt/sources.list
RUN apt update

# openresty运行时依赖
RUN apt install openssl -y

CMD /usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/nginx/conf/nginx.conf  -g "daemon off;"
