#!/bin/bash

# 编译elasticsearch
docker build --tag demo-skywalking-elasticsearch --file Dockerfile-elasticsearch .

mvn package
