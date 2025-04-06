#!/bin/bash

echo -n "是否配置yum源？[y/n]: "     
read varConfigYum
if [[ "$varConfigYum" == "y" ]]; then
        set -x
        (cd /etc/yum.repos.d && rm -f *)
        cp CentOS-Base.repo /etc/yum.repos.d/
        yum clean all && yum makecache
fi

set -e
set -x

yum install perl -y

grep -q '^net.ipv4.tcp_syncookies' /etc/sysctl.conf && sed -i 's/^net.ipv4.tcp_syncookies.*/net.ipv4.tcp_syncookies=1/' /etc/sysctl.conf || echo 'net.ipv4.tcp_syncookies=1' >> /etc/sysctl.conf
grep -q '^net.ipv4.tcp_tw_reuse' /etc/sysctl.conf && sed -i 's/^net.ipv4.tcp_tw_reuse.*/net.ipv4.tcp_tw_reuse=1/' /etc/sysctl.conf || echo 'net.ipv4.tcp_tw_reuse=1' >> /etc/sysctl.conf
grep -q '^net.ipv4.tcp_tw_recycle' /etc/sysctl.conf && sed -i 's/^net.ipv4.tcp_tw_recycle.*/net.ipv4.tcp_tw_recycle=1/' /etc/sysctl.conf || echo 'net.ipv4.tcp_tw_recycle=1' >> /etc/sysctl.conf
grep -q '^net.ipv4.tcp_fin_timeout' /etc/sysctl.conf && sed -i 's/^net.ipv4.tcp_fin_timeout.*/net.ipv4.tcp_fin_timeout=30/' /etc/sysctl.conf || echo 'net.ipv4.tcp_fin_timeout=30' >> /etc/sysctl.conf

which make || yum install make -y

# lua日志汇总后不会导致error.log迅速增长，取消以下逻辑
#grep -q '0 17 \* \* \* cat /dev/null > /usr/local/openresty/nginx/logs/error.log' /var/spool/cron/root || echo '0 17 * * * cat /dev/null > /usr/local/openresty/nginx/logs/error.log' >> /var/spool/cron/root
sed -i '/^0 17 \* \* \* cat \/dev\/null > \/usr\/local\/openresty\/nginx\/logs\/error.log/d' /var/spool/cron/root || true

service crond restart
chkconfig crond on

varNaxsiVersion=1.3
varOpenrestyVersion=1.15.8.1

cp naxsi-${varNaxsiVersion}.tar.gz /tmp
cp openresty-${varOpenrestyVersion}.tar.gz /tmp

yum install readline-devel pcre-devel openssl-devel gcc -y

(cd /tmp && rm -rf openresty-${varOpenrestyVersion} && tar -xzf openresty-${varOpenrestyVersion}.tar.gz)
(cd /tmp && rm -rf naxsi-${varNaxsiVersion} && tar -xzf naxsi-${varNaxsiVersion}.tar.gz)
(cd /tmp/openresty-${varOpenrestyVersion} && ./configure --add-module=/tmp/naxsi-${varNaxsiVersion}/naxsi_src)
(cd /tmp/openresty-${varOpenrestyVersion} && make install)

cp nginx.service /etc/init.d/nginx
chmod +x /etc/init.d/nginx

cp /tmp/naxsi-${varNaxsiVersion}/naxsi_config/naxsi_core.rules /usr/local/openresty/nginx/conf

mkdir /usr/local/openresty/nginx/conf/lua | true
cp my_common.lua /usr/local/openresty/nginx/conf/lua
chmod 755 /usr/local/openresty/nginx/conf/lua/my_common.lua
cp ops_setup_timer.lua /usr/local/openresty/nginx/conf/lua
chmod 755 /usr/local/openresty/nginx/conf/lua/ops_setup_timer.lua
cp lock.lua /usr/local/openresty/lualib/resty
chmod 755 /usr/local/openresty/lualib/resty/lock.lua
cp http_headers.lua /usr/local/openresty/lualib/resty
chmod 755 /usr/local/openresty/lualib/resty/http_headers.lua
cp http_connect.lua /usr/local/openresty/lualib/resty
chmod 755 /usr/local/openresty/lualib/resty/http_connect.lua
cp http.lua /usr/local/openresty/lualib/resty
chmod 755 /usr/local/openresty/lualib/resty/http.lua
cp uuid.lua /usr/local/openresty/lualib/resty
chmod 755 /usr/local/openresty/lualib/resty/uuid.lua
cp ip2region.lua /usr/local/openresty/lualib/resty
chmod 755 /usr/local/openresty/lualib/resty/ip2region.lua
cp cookie.lua /usr/local/openresty/lualib/resty
chmod 755 /usr/local/openresty/lualib/resty/cookie.lua

yum install libuuid-devel -y

# 配置geoip2
#/usr/local/openresty/bin/opm get anjia0532/lua-resty-maxminddb
#rm -rf /tmp/libmaxminddb-1.6.0
#curl --output /tmp/libmaxminddb-1.6.0.tar.gz https://fut001.oss-cn-hangzhou.aliyuncs.com/nginx/libmaxminddb-1.6.0.tar.gz
#tar -xvzf /tmp/libmaxminddb-1.6.0.tar.gz -C /tmp
#(cd /tmp/libmaxminddb-1.6.0 && ./configure && make && make check && make install)
#echo "/usr/local/lib" > /etc/ld.so.conf.d/local.conf
#ldconfig
#curl --output /usr/local/openresty/GeoLite2-City.mmdb https://fut001.oss-cn-hangzhou.aliyuncs.com/nginx/GeoLite2-City.mmdb

# 配置ip2region
curl --output /usr/local/openresty/ip2region.db https://fut001.oss-cn-hangzhou.aliyuncs.com/ip/ip2region.db

cp nginx.conf /usr/local/openresty/nginx/conf/
sed -i "s/##upstreamBackendd##/xxx.xxx.xxx.xxx/" /usr/local/openresty/nginx/conf/nginx.conf
sed -i "s/##frontend##/true/" /usr/local/openresty/nginx/conf/nginx.conf

grep -q 'sudo -i rm -f /usr/local/openresty/nginx/logs/nginx.pid' /etc/rc.local || echo 'sudo -i rm -f /usr/local/openresty/nginx/logs/nginx.pid' >> /etc/rc.local
grep -q 'sudo -i service nginx start' /etc/rc.local || echo 'sudo -i service nginx start' >> /etc/rc.local
chmod +x /etc/rc.d/rc.local

yum remove epel-release -y
yum install epel-release -y

#rm -f /usr/local/openresty/nginx/logs/access.log
#rm -f /usr/local/openresty/nginx/logs/error.log

sysctl -p || true

service nginx restart

chkconfig nginx off
