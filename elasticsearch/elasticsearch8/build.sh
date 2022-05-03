#!/bin/bash

# 编译elasticsearch
docker build --tag docker.118899.net:10001/yyd-public/demo-elasticsearch8 --file Dockerfile-elasticsearch .

## 编译elasticsearch-head
#docker build --tag docker.118899.net:10001/yyd-public/demo-elasticsearch8-head --file Dockerfile-elasticsearch-head .
