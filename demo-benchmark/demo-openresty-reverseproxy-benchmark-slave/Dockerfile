FROM registry.cn-hangzhou.aliyuncs.com/future-public/demo-openresty-base-dev

COPY uuid.lua /usr/local/openresty/lualib/resty
RUN chmod 755 /usr/local/openresty/lualib/resty/uuid.lua

RUN apt update
# uuid.lua依赖于uuid-dev库
RUN apt install uuid-dev -y
