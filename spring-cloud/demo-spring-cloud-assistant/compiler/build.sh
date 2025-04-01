#!/bin/bash

projectVersion=1.0.1
dockerRegistryPrefix=registry.cn-hangzhou.aliyuncs.com/future-public

# 当前工作目录
current_dir=`pwd`

> $current_dir/.build.log

(cd .. && mvn package)

echo "开始编译redis容器镜像。。。"
(cd ../ && \
  docker build --tag $dockerRegistryPrefix/demo-spring-cloud-assistant-redis:$projectVersion \
  --file Dockerfile-redis . >> $current_dir/.build.log 2>&1) || \
  { echo '编译redis容器镜像失败！详细原因查看 .build.log 日志'; exit; }

echo "开始编译db容器镜像。。。"
(cd ../ && \
  docker build --tag $dockerRegistryPrefix/demo-spring-cloud-assistant-db:$projectVersion \
  --file Dockerfile-db . >> $current_dir/.build.log 2>&1) || \
  { echo '编译db容器镜像失败！详细原因查看 .build.log 日志'; exit; }

echo "开始编译elasticsearch容器镜像。。。"
(cd ../ && \
  docker build --tag $dockerRegistryPrefix/demo-spring-cloud-assistant-elasticsearch:$projectVersion \
  --file Dockerfile-elasticsearch . >> $current_dir/.build.log 2>&1) || \
  { echo '编译elasticsearch容器镜像失败！详细原因查看 .build.log 日志'; exit; }

echo "开始编译eureka容器镜像。。。"
(cd ../architecture-eureka && \
  docker build --tag $dockerRegistryPrefix/demo-spring-cloud-assistant-eureka:$projectVersion \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
  { echo '编译eureka容器镜像失败！详细原因查看 .build.log 日志'; exit; }

echo "开始编译gateway容器镜像。。。"
(cd ../architecture-gateway && \
  docker build --tag $dockerRegistryPrefix/demo-spring-cloud-assistant-gateway:$projectVersion \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
  { echo '编译gateway容器镜像失败！详细原因查看 .build.log 日志'; exit; }

echo "开始编译helloworld容器镜像。。。"
(cd ../architecture-helloworld && \
  docker build --tag $dockerRegistryPrefix/demo-spring-cloud-assistant-helloworld:$projectVersion \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
  { echo '编译helloworld容器镜像失败！详细原因查看 .build.log 日志'; exit; }
