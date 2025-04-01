# 演示nginx直接安装在centOS操作系统上进行cc攻击效果

## 搭建测试环境

> 测试结论：nginx并发处理能力很强，能够开启数以万计的进程并发处理客户端请求，Jmeter不能够瘫痪nginx

- 使用dcli在centOS8上安装openresty
- 复制nginx.conf文件到/usr/local/openresty/nginx/conf/目录
- 复制lua_common.lua脚本文件到目录/usr/local/openresty/nginx/conf/lua/
- 使用Jmeter打开jmeter.jmx文件进行cc测试