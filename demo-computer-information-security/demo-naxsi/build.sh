#!/bin/bash

version=1.1.1

set -x
set -e

sudo rm -f openresty.tar

docker build -f Dockerfile-compile --tag demo-openresty-compile-intermediate:$version .

# 复制编译后的/usr/local/openresty目录到宿主机
docker rm -f demo-openresty-compile-intermediate-instance
docker create -it --name demo-openresty-compile-intermediate-instance demo-openresty-compile-intermediate:$version /bin/bash
docker cp demo-openresty-compile-intermediate-instance:/usr/local/openresty.tar openresty.tar

docker build -f Dockerfile --tag demo-openresty:$version .

echo "成功编译openresty基础镜像，镜像标签为： demo-openresty:$version"

