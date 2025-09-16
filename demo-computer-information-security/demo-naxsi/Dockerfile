FROM debian:bullseye AS builder

# 设置时区
RUN rm -f /etc/localtime && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

ADD sources.list /etc/apt/sources.list
RUN apt update
# 安装openresty和naxsi的编译依赖
RUN apt install make libreadline-dev libpcre3-dev libssl-dev zlib1g-dev gcc perl wget -y
# debian报告gmake command not found错误，centOS8不会报告这个错误
# RUN ln -s /usr/bin/make /usr/bin/gmake

ARG varNaxsiVersion=1.3
ARG varOpenrestyVersion=1.19.9.1

RUN cd /tmp && wget https://fut001.oss-cn-hangzhou.aliyuncs.com/nginx/naxsi-$varNaxsiVersion.tar.gz
RUN cd /tmp && wget https://fut001.oss-cn-hangzhou.aliyuncs.com/nginx/openresty-$varOpenrestyVersion.tar.gz

RUN rm -rf /usr/local/openresty
RUN cd /tmp && rm -rf openresty-$varOpenrestyVersion && tar -xzf openresty-$varOpenrestyVersion.tar.gz
RUN cd /tmp && rm -rf naxsi-$varNaxsiVersion && tar -xzf naxsi-$varNaxsiVersion.tar.gz
RUN cd /tmp/openresty-$varOpenrestyVersion && ./configure --add-module=/tmp/naxsi-$varNaxsiVersion/naxsi_src
RUN cd /tmp/openresty-$varOpenrestyVersion && gmake && gmake install

RUN cp /tmp/naxsi-$varNaxsiVersion/naxsi_config/naxsi_core.rules /usr/local/openresty/nginx/conf

FROM debian:bullseye

COPY --from=builder /usr/local/openresty /usr/local/openresty

RUN ln -sf /dev/stdout /usr/local/openresty/nginx/logs/access.log
RUN ln -sf /dev/stderr /usr/local/openresty/nginx/logs/error.log

ADD sources.list /etc/apt/sources.list
RUN apt update

# openresty运行时依赖
RUN apt install openssl -y

CMD /usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/nginx/conf/nginx.conf  -g "daemon off;"
