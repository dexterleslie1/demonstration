# 编译`openresty docker`基础镜像

> 例子详细细节请参考`https://gitee.com/dexterleslie/demonstration/tree/master/openresty/demo-build-base-image`

编译基础镜像，编译后的基础镜像为`registry.cn-hangzhou.aliyuncs.com/future-public/demo-openresty-base-dev`

```bash
./build.sh
```

推送基础镜像到镜像仓库中

```bash
./push.sh
```

使用`docker compose`测试基础镜像

```bash
docker compose up -d
```

访问`http://localhost`显示`Hello world!`
