FROM registry.cn-hangzhou.aliyuncs.com/future-public/demo-openresty-base-dev

COPY nginx.conf /usr/local/openresty/nginx/conf/nginx.conf

COPY prometheus_keys.lua /usr/local/openresty/nginx/conf/prometheus_keys.lua
COPY prometheus_resty_counter.lua /usr/local/openresty/nginx/conf/prometheus_resty_counter.lua
COPY prometheus.lua /usr/local/openresty/nginx/conf/prometheus.lua
