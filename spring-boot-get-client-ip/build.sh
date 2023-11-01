#!/bin/bash

mvn clean package

docker build --tag docker.118899.net:10001/yyd-public/demo-spring-boot-get-client-ip --file Dockerfile .