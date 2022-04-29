#!/bin/bash

# 编译elasticsearch
docker build --tag docker.118899.net:10001/yyd-public/demo-elasticsearch --file Dockerfile-elasticsearch .

## 编译elasticsearch-head
#docker build --tag docker.118899.net:10001/yyd-public/demo-elasticsearch-head --file Dockerfile-elasticsearch-head .
