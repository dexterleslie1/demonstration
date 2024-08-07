#!/bin/bash

projectVersion=1.0.0
dockerRegistryPrefix=106.75.188.68:10001/yyd-public

# 当前工作目录
current_dir=`pwd`

> $current_dir/.build.log

(cd .. && mvn package)

echo "开始编译eureka容器镜像。。。"
(cd ../architecture-eureka && \
  docker build --tag $dockerRegistryPrefix/demo-microservice-eureka:$projectVersion \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
  { echo '编译eureka容器镜像失败！详细原因查看 .build.log 日志'; exit; }

echo "开始编译zuul容器镜像。。。"
(cd ../architecture-zuul && \
  docker build --tag $dockerRegistryPrefix/demo-microservice-zuul:$projectVersion \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
  { echo '编译zuul容器镜像失败！详细原因查看 .build.log 日志'; exit; }

echo "开始编译helloworld容器镜像。。。"
(cd ../architecture-helloworld && \
  docker build --tag $dockerRegistryPrefix/demo-microservice-helloworld:$projectVersion \
  --file Dockerfile . >> $current_dir/.build.log 2>&1) || \
  { echo '编译helloworld容器镜像失败！详细原因查看 .build.log 日志'; exit; }
