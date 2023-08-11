#!/bin/bash

# 清除上次编译日志
> .build.log

projectVersion=1.0.0
dockerRegistryPrefix=192.168.1.151:81/demo-devops

current_dir=`pwd`

echo '开始从源代码编译。。。'
docker-compose \
    -f docker-compose-compiler.yml up --abort-on-container-exit \
    >> $current_dir/.build.log 2>&1 \
    || { echo '源代码编译失败！详细原因查看 .build.log 日志'; exit; }

echo '开始编译容器镜像eureka。。。'
(cd ../architecture-eureka && \
    docker build \
    --tag $dockerRegistryPrefix/architecture-eureka:$projectVersion \
    --file Dockerfile . >> $current_dir/.build.log 2>&1) \
    || { echo '编译容器镜像eureka失败！详细原因查看 .build.log 日志'; exit; }

echo '开始编译容器镜像zuul。。。'
(cd ../architecture-zuul && \
    docker build --tag $dockerRegistryPrefix/architecture-zuul:$projectVersion \
    --file Dockerfile . >> $current_dir/.build.log 2>&1) \
    || { echo '编译容器镜像zuul失败！详细原因查看 .build.log 日志'; exit; }

echo '开始编译容器镜像helloworld。。。'
(cd ../architecture-helloworld && \
    docker build --tag $dockerRegistryPrefix/architecture-helloworld:$projectVersion \
    --file Dockerfile . >> $current_dir/.build.log 2>&1) \
    || { echo '编译容器镜像helloworld失败！详细原因查看 .build.log 日志'; exit; }

# mvn clean清除
echo '开始mvn clean。。。'
docker-compose \
    -f docker-compose-compiler-cleanup.yml up --abort-on-container-exit \
    >> $current_dir/.build.log 2>&1 \
    || { echo 'mvn clean失败！详细原因查看 .build.log 日志'; exit; }


