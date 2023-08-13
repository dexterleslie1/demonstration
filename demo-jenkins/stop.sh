#!/bin/bash

echo '关闭jenkins、gitlab服务。。。'
docker-compose down

echo '关闭harbor服务。。。'
(cd harbor && sudo docker-compose down)
