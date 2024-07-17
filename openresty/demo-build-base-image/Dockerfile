FROM debian:buster

COPY openresty.tar /usr/local

RUN cd /usr/local && tar -xvzf openresty.tar

RUN rm -f openresty.tar

## 日志输出到stdout、stderr NOTE: docker-compose mount了日志目录到硬盘，不需要控制台输出
#RUN ln -sf /dev/stdout /usr/local/openresty/nginx/logs/access.log
#RUN ln -sf /dev/stderr /usr/local/openresty/nginx/logs/error.log

add sources.list /etc/apt/sources.list
run apt update
run apt install vim -y
# run apt install logrotate -y
# run apt install systemd -y
# run systemctl enable cron
# run systemctl restart cron
# add logrotate.conf /etc/logrotate.d/openresty
# run chmod 0644 /etc/logrotate.d/openresty

# 否则报告error while loading shared libraries: libssl.so.1.1: cannot open shared object file: No such file or directory错误
RUN apt install openssl -y

CMD /usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/nginx/conf/nginx.conf  -g "daemon off;"
