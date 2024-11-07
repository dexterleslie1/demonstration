#!/bin/bash

# 编译elasticsearch
docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-elk-elasticsearch --file Dockerfile-elasticsearch .
docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-elk-logstash --file Dockerfile-logstash .
docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-elk-curator --file Dockerfile-curator .
