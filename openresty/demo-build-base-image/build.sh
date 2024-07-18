#!/bin/bash

set -x
set -e

sudo rm -f openresty.tar

docker build -f DockerfileOpenrestyCompile --tag openresty-compile-dev .

# 复制编译后的/usr/local/openresty目录到宿主机
docker rm -f openresty-compile-instance-dev
docker create -it --name openresty-compile-instance-dev openresty-compile-dev /bin/bash
docker cp openresty-compile-instance-dev:/usr/local/openresty.tar openresty.tar

# docker build -f Dockerfile --tag $dockerRegistryPrefix/demo-openresty-base-dev .
docker compose build

echo "成功编译openresty基础镜像" && sudo rm -f openresty.tar

