FROM 192.168.235.138/library/demo-openresty-base-dev

COPY uuid.lua /usr/local/openresty/lualib/resty
RUN chmod 755 /usr/local/openresty/lualib/resty/uuid.lua

RUN apt update
# uuid.lua依赖于uuid-dev库
RUN apt install uuid-dev -y

COPY sample.conf /usr/local/openresty/nginx/conf/nginx.conf
