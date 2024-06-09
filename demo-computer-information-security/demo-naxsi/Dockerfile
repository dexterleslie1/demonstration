FROM debian:buster

# 复制编译后的openresty压缩包
COPY openresty.tar /usr/local

# 解压编译后的openresty压缩包到/usr/local目录
RUN cd /usr/local && tar -xvzf openresty.tar

# 删除编译后的openresty压缩包
RUN rm -f openresty.tar

# 安装和配置logrotate
ADD sources.list /etc/apt/sources.list
# RUN apt update
# RUN apt install vim -y
# RUN apt install logrotate -y
# RUN apt install systemd -y
# RUN systemctl enable cron
# RUN systemctl restart cron
# ADD logrotate.conf /etc/logrotate.d/openresty
# RUN chmod 0644 /etc/logrotate.d/openresty

CMD /usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/nginx/conf/nginx.conf  -g "daemon off;"
