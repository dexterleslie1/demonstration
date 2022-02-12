#!/bin/bash

# 编译elasticsearch
docker build --tag docker.118899.net:10001/yyd-public/demo-elk-elasticsearch --file Dockerfile-elasticsearch .

# 编译elasticsearch-head
docker build --tag docker.118899.net:10001/yyd-public/demo-elk-elasticsearch-head --file Dockerfile-elasticsearch-head .