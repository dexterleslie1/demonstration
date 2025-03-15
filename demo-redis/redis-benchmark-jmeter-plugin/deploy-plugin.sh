#!/bin/bash

set -e
set -x

# 编译插件
mvn package

sudo rm -f /usr/local/jmeter/lib/ext/redis-benchmark-jmeter-plugin*.jar

# 复制插件到jmeter lib/ext目录
sudo cp ./target/redis-benchmark-jmeter-plugin-1.0.0-jar-with-dependencies.jar /usr/local/jmeter/lib/ext/redis-benchmark-jmeter-plugin-1.0.0.jar

# 复制插件依赖的库到jmeter lib/ext目录，否则会报告ClassNotFound错误
#sudo cp ./target/required-lib/*.jar /usr/local/jmeter/lib/ext/

# 修改插件属主为root
sudo chown root:root /usr/local/jmeter/lib/ext/redis-benchmark-jmeter-plugin*.jar
