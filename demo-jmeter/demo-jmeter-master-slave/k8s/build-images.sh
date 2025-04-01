#!/bin/bash

set -x
set -e

# 复制jmeter自定义插件到本目录，因为此插件支持redis性能测试
rm -rf ./target
cp -r ../../demo-jmeter-customize-plugin/target ./target

docker build --tag demo-k8s-jmeter-base:latest -f Dockerfile-base .

docker build --tag demo-k8s-jmeter-master:latest -f Dockerfile-master .

docker build --tag demo-k8s-jmeter-slave:latest -f Dockerfile-slave .
