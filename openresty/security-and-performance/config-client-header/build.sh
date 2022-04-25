#!/bin/bash

# docker 镜像版本
version=1.0.0
# docker 镜像前缀
dockerRegistryPrefix=docker.118899.net:10001/yyd-public

docker build --tag $dockerRegistryPrefix/demo-openresty-client-header:$version .