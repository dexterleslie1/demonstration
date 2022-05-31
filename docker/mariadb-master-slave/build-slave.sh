#!/bin/bash

docker build --tag docker.118899.net:10001/yyd-public/demo-mariadb-slave --file Dockerfile-slave .

docker build --tag docker.118899.net:10001/yyd-public/demo-mariadb-slave-config --file Dockerfile-slave-config .

