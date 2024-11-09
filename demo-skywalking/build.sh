#!/bin/bash

current_dir=`pwd`

cat /dev/null > ./.build.log

echo "编译源代码..." && mvn package

(echo "编译elasticsearch镜像..." && \
  docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-skywalking-elasticsearch \
  --file Dockerfile-elasticsearch . >> $current_dir/.build.log 2>&1) || \
{ echo "镜像编译失败！！！"; exit; }

(echo "编译eureka镜像..." && \
  cd service-eureka && \
  docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-skywalking-service-eureka \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
{ echo "镜像编译失败！！！"; exit; }

(echo "编译gateway镜像..." && \
  cd service-gateway && \
  docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-skywalking-service-gateway \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
{ echo "镜像编译失败！！！"; exit; }

(echo "编译service-level-first-provider镜像..." && \
  cd service-level-first-provider && \
  docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-skywalking-service-level-first-provider \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
{ echo "镜像编译失败！！！"; exit; }

(echo "编译service-level-second-provider镜像..." && \
  cd service-level-second-provider && \
  docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-skywalking-service-level-second-provider \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
{ echo "镜像编译失败！！！"; exit; }

(echo "编译service-zuul镜像..." && \
  cd service-zuul && \
  docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-skywalking-service-zuul \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
{ echo "镜像编译失败！！！"; exit; }

