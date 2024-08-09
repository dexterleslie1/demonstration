#!/bin/bash

set -e
set -x

# 编译插件
mvn package

sudo rm -f /usr/local/jmeter/lib/ext/demo-jmeter-customize*.jar

# 复制插件到jmeter lib/ext目录
sudo cp ./target/demo-jmeter-customize-plugin-1.0.0.jar /usr/local/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0.jar

# 复制插件依赖的库到jmeter lib/ext目录，否则会报告ClassNotFound错误
# 注意：下面jar-with-dependencies.jar打包包括jmeter的依赖，导致测试时候有莫名其妙的报错和log.info不能输出日志等问题，
# 所以不采取jar-with-dependencies.jar方式打包依赖
#sudo cp ./target/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar /usr/local/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar
# 采用复制指定的依赖库方式避免ClassNotFound错误
sudo cp ./target/lib/hutool*.jar /usr/local/jmeter/lib/ext/
sudo chown root:root /usr/local/jmeter/lib/ext/hutool*.jar
sudo cp ./target/lib/jedis*.jar /usr/local/jmeter/lib/ext/
sudo chown root:root /usr/local/jmeter/lib/ext/jedis*.jar

# 修改插件属主为root
sudo chown root:root /usr/local/jmeter/lib/ext/demo-jmeter-customize*.jar
