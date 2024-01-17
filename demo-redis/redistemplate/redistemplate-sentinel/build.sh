#!/bin/bash

mvn clean package

docker build --tag docker.118899.net:10001/yyd-public/demo-redistemplate-sentinel --file Dockerfile .
