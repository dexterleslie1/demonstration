#!/bin/bash

# 清除上次编译日志
> .build.log

projectVersion=1.0.0
dockerRegistryPrefix=192.168.1.151:81/demo-devops

docker-compose \
    -f docker-compose-compiler.yml up --abort-on-container-exit \
    || exit $?

(cd ../architecture-eureka && \
    docker build \
    --tag $dockerRegistryPrefix/architecture-eureka:$projectVersion --file Dockerfile .) \
    || exit $?

(cd ../architecture-zuul && \
    docker build --tag $dockerRegistryPrefix/architecture-zuul:$projectVersion --file Dockerfile .) \
    || exit $?

(cd ../architecture-helloworld && \
    docker build --tag $dockerRegistryPrefix/architecture-helloworld:$projectVersion --file Dockerfile .) \
    || exit $?

# mvn clean清除
docker-compose \
    -f docker-compose-compiler-cleanup.yml up --abort-on-container-exit \
    || exit $?


