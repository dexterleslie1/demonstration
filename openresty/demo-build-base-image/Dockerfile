# 用于从源代码编译openresty docker镜像
FROM debian:bullseye AS builder

RUN rm -f /etc/localtime && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN apt update
RUN apt install make libreadline-dev libpcre3-dev libssl-dev zlib1g-dev gcc perl wget -y
# debian:bullseye报告make已经存在，centOS8不会报告这个错误
# RUN ln -s /usr/bin/make /usr/bin/gmake

ARG varNaxsiVersion=1.3
ARG varOpenrestyVersion=1.19.9.1

RUN cd /tmp && wget https://fut001.oss-cn-hangzhou.aliyuncs.com/nginx/naxsi-$varNaxsiVersion.tar.gz
RUN cd /tmp && wget https://fut001.oss-cn-hangzhou.aliyuncs.com/nginx/openresty-$varOpenrestyVersion.tar.gz

RUN rm -rf /usr/local/openresty
RUN rm -rf /usr/local/openresty.tar
RUN cd /tmp && rm -rf openresty-$varOpenrestyVersion && tar -xzf openresty-$varOpenrestyVersion.tar.gz
RUN cd /tmp && rm -rf naxsi-$varNaxsiVersion && tar -xzf naxsi-$varNaxsiVersion.tar.gz

# https://github.com/knyar/nginx-lua-prometheus/issues/67
# --with-http_stub_status_module为了lua脚本支持读取ngx.var.connections_reading变量
# 否则nginx-lua-prometheus插件不能正常运行
RUN cd /tmp/openresty-$varOpenrestyVersion && ./configure --add-module=/tmp/naxsi-$varNaxsiVersion/naxsi_src --with-http_stub_status_module --with-http_v2_module
RUN cd /tmp/openresty-$varOpenrestyVersion && gmake && gmake install

RUN cp /tmp/naxsi-$varNaxsiVersion/naxsi_config/naxsi_core.rules /usr/local/openresty/nginx/conf

RUN cd /usr/local && tar -czf openresty.tar openresty

FROM debian:bullseye

COPY --from=builder /usr/local/openresty.tar /usr/local

RUN cd /usr/local && tar -xvzf openresty.tar

RUN rm -f openresty.tar

# 支持日志通过docker compose logs查看
RUN ln -sf /dev/stdout /usr/local/openresty/nginx/logs/access.log
RUN ln -sf /dev/stderr /usr/local/openresty/nginx/logs/error.log

RUN apt update
RUN apt install vim -y
# run apt install logrotate -y
# run apt install systemd -y
# run systemctl enable cron
# run systemctl restart cron
# add logrotate.conf /etc/logrotate.d/openresty
# run chmod 0644 /etc/logrotate.d/openresty

# 否则报告error while loading shared libraries: libssl.so.1.1: cannot open shared object file: No such file or directory错误
RUN apt install openssl -y

CMD /usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/nginx/conf/nginx.conf  -g "daemon off;"
