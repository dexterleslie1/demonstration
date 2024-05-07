#!/bin/bash

projectVersion=1.0.0
dockerRegistryPrefix=106.75.188.68:10001/yyd-public

docker push $dockerRegistryPrefix/demo-microservice-eureka:$projectVersion
docker push $dockerRegistryPrefix/demo-microservice-zuul:$projectVersion
docker push $dockerRegistryPrefix/demo-microservice-helloworld:$projectVersion
