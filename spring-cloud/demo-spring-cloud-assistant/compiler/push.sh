#!/bin/bash

projectVersion=1.0.0
dockerRegistryPrefix=registry.cn-hangzhou.aliyuncs.com/future-public

docker push $dockerRegistryPrefix/demo-spring-cloud-assistant-eureka:$projectVersion
docker push $dockerRegistryPrefix/demo-spring-cloud-assistant-zuul:$projectVersion
docker push $dockerRegistryPrefix/demo-spring-cloud-assistant-helloworld:$projectVersion
