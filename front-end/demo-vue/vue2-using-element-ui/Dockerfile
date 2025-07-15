FROM registry.cn-hangzhou.aliyuncs.com/future-public/openresty-base:1.1.1

RUN rm -rf /usr/local/openresty/nginx/html/*

# 注意：不能使用下面的方式复制 dist 目录到 /usr/local/openresty/nginx/html/ 目录中，否则在发布后节目不能正常显示。
# COPY dist/* /usr/local/openresty/nginx/html/
COPY dist/ /usr/local/openresty/nginx/html/
