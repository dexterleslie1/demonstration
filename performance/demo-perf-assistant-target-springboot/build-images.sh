#!/bin/bash

mvn clean package

docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-perf-assistant-target-springboot .