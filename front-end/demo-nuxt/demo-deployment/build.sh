#!/bin/bash

# 编译
docker-compose -f docker-compose-compiler.yml up

docker-compose build
