#!/bin/bash

mvn clean package

docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-redistemplate-cluster .
