#!/bin/bash

# 编译elasticsearch
docker build --tag docker.118899.net:10001/yyd-public/demo-elk-elasticsearch --file Dockerfile-elasticsearch .
