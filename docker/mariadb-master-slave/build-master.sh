#!/bin/bash

docker build --tag docker.118899.net:10001/yyd-public/demo-mariadb-master --file Dockerfile-master .

docker build --tag docker.118899.net:10001/yyd-public/demo-mariadb-master-config --file Dockerfile-master-config .
