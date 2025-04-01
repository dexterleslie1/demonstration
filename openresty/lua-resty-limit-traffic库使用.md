# `lua-resty-limit-traffic`库使用

## 运行示例步骤

`lua-resty-limit-traffic`相关示例请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/openresty/demo-lib-lua-resty-limit-traffic)

1. 编译示例

   ```bash
   docker compose build
   ```

2. 运行示例

   ```bash
   docker compose up -d
   ```

3. 运行`jmeter`并打开 [`jmeter-assistant.jmx`文件](https://gitee.com/dexterleslie/demonstration/blob/master/openresty/demo-lib-lua-resty-limit-traffic/jmeter-assistant.jmx) 辅助调试

## `resty.limit.conn`

> 限制客户端并发连接数。
>
> [参考链接](https://github.com/openresty/lua-resty-limit-traffic/blob/master/lib/resty/limit/conn.md)

示例的详细配置请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/openresty/demo-lib-lua-resty-limit-traffic/nginx-lua-resty-limit-conn.conf)

## `resty.limit.count`

>在给定的时间窗口内限制固定数量的请求。
>
>[参考链接](https://github.com/openresty/lua-resty-limit-traffic/blob/master/lib/resty/limit/count.md)

示例的详细配置请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/openresty/demo-lib-lua-resty-limit-traffic/nginx-lua-resty-limit-count.conf)

## `resty.limit.req`

>限制客户端请求速率。
>
>[参考链接](https://github.com/openresty/lua-resty-limit-traffic/blob/master/lib/resty/limit/req.md)

示例的详细配置请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/openresty/demo-lib-lua-resty-limit-traffic/nginx-lua-resty-limit-req.conf)

## `resty.limit.traffic`

>合并`resty.limit.conn`、`resty.limit.count`、`resty.limit.req`限制器。提醒：todo 合并多个`limiters`并``rejected`时，无法知道具体哪个`limiter`拒绝了服务，所以暂时不能应用。
>
>[参考链接](https://github.com/openresty/lua-resty-limit-traffic/blob/master/lib/resty/limit/traffic.md)

## 自定义同时使用多个限制器

>由于`resty.limit.traffic`存在以上提到的问题，尝试自定义同时使用多个限制器。提示：todo 在测试连接数限制时，`jmeter`使用15个线程测试`openresty`一直报告超出允许的最大连接数15阈值。