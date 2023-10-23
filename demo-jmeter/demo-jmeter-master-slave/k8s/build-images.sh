#!/bin/bash

cp -r ../../demo-jmeter-customize-plugin/target ./target

docker build --tag docker.118899.net:10001/yyd-public/demo-jmeter-base:latest -f Dockerfile-base .

docker build --tag docker.118899.net:10001/yyd-public/demo-jmeter-master:latest -f Dockerfile-master .

docker build --tag docker.118899.net:10001/yyd-public/demo-jmeter-slave:latest -f Dockerfile-slave .
