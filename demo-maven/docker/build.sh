#!/bin/bash

# # 判断源代码压缩包是否存在
# if [ ! -f src.zip ]; then
# 	echo "需要提供源代码压缩包src.zip才能执行编译"
#     exit 1
# fi

# # 解压源代码压缩包到 /tmp
# rm -f /tmp/src.zip
# rm -rf /tmp/src/
# cp src.zip /tmp/src.zip
# (cd /tmp && unzip -d src src.zip && mv src/*/* src/)

# # 挂载 /tmp 目录下的源代码到 maven docker 实例中进行编译
# docker run -it --rm -v /tmp/src:/usr/src/mymaven -v "$PWD/.m2:/root/.m2" -w /usr/src/mymaven maven:3.3-jdk-8 mvn clean package

# # 复制编译后的jar到当前编译目录
# rm -f *.jar
# cp /tmp/src/target/*.jar .

# 使用docker-compose编译jar
# docker-compose -f docker-compose-compiler.yml up

# 编译发布demo docker镜像
docker build --progress=plain --tag docker.118899.net:10001/demo/demo-maven:1.0.0 --file Dockerfile .
# https://stackoverflow.com/questions/64804749/why-is-docker-build-not-showing-any-output-from-commands
# docker build --progress=plain --no-cache --tag docker.118899.net:10001/demo/demo-maven:1.0.0 --file Dockerfile .
