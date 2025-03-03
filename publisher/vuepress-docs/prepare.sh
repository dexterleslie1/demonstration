#!/bin/bash

# 可以重复执行这个脚本
# 软链接外部文档到 docs 中
# (cd docs && rm -rf 英语学习 && ln -s ../../../demo-english 英语学习 || exit 0)
# (cd docs && rm -rf intellij-idea && ln -s ../../../demo-idea intellij-idea || exit 0)
# (cd docs && rm -rf MySQL或MariaDB学习 && ln -s ../../../demo-mysql MySQL或MariaDB学习 || exit 0)
# (cd docs && rm -rf docker容器 && ln -s ../../../docker docker容器 || exit 0)
# # (cd docs && rm -rf React技术 && ln -s ../../../front-end/demo-reactjs React技术 || exit 0)
# (cd docs && rm -rf shell脚本编程 && ln -s ../../../demo-shell-scripting shell脚本编程 || exit 0)

# 定义源目录到目标目录的对照
declare -A source_dir_to_target_dir_map
source_dir_to_target_dir_map["../../demo-english"]="英语学习"
source_dir_to_target_dir_map["../../demo-idea"]="intellij-idea"
source_dir_to_target_dir_map["../../demo-mysql-n-mariadb"]="mysql-n-mariadb"
# source_dir_to_target_dir_map["../../demo-mariadb"]="MariaDB"
source_dir_to_target_dir_map["../../docker"]="docker容器"
source_dir_to_target_dir_map["../../front-end/demo-reactjs"]="React技术"
source_dir_to_target_dir_map["../../front-end/demo-vue"]="vue"
source_dir_to_target_dir_map["../../front-end/demo-nuxt"]="nuxt"
source_dir_to_target_dir_map["../../demo-shell-scripting"]="shell脚本编程"
# source_dir_to_target_dir_map["../../linux/ubuntu"]="ubuntu使用"
source_dir_to_target_dir_map["../../linux"]="linux使用"
source_dir_to_target_dir_map["../../front-end/html+js+css/demo-less-sass-scss-postcss"]="less-sass-postcss"
source_dir_to_target_dir_map["../../front-end/html+js+css/demo-less-sass-scss-postcss/demo-less"]="less"
source_dir_to_target_dir_map["../../front-end/html+js+css/demo-less-sass-scss-postcss/demo-scss"]="scss"
source_dir_to_target_dir_map["../../demo-prometheus"]="prometheus"
source_dir_to_target_dir_map["../../demo-redis"]="redis"
source_dir_to_target_dir_map["../../demo-maven"]="maven"
source_dir_to_target_dir_map["../../demo-git"]="git"
source_dir_to_target_dir_map["../../demo-spring-boot"]="spring-boot"
source_dir_to_target_dir_map["../../demo-java"]="java"
source_dir_to_target_dir_map["../../demo-computer-information-security"]="计算机信息安全"
source_dir_to_target_dir_map["../../demo-encrypt-decrypt"]="密码算法"
source_dir_to_target_dir_map["../../demo-openldap"]="openldap"
source_dir_to_target_dir_map["../../demo-jira"]="jira"
source_dir_to_target_dir_map["../../demo-软件工程"]="软件工程"
# source_dir_to_target_dir_map["../../demo-jsoup"]="jsoup"
# source_dir_to_target_dir_map["../../performance/jvm"]="jvm性能"
source_dir_to_target_dir_map["../../demo-数据结构和算法"]="数据结构和算法"
source_dir_to_target_dir_map["../../demo-minio"]="minio"
source_dir_to_target_dir_map["../../demo-locust"]="locust"
source_dir_to_target_dir_map["../../demo-harbor"]="harbor"
source_dir_to_target_dir_map["../../openresty"]="openresty"
source_dir_to_target_dir_map["../../lua"]="lua"
source_dir_to_target_dir_map["../../performance"]="性能测试"
source_dir_to_target_dir_map["../../demo-jmeter"]="jmeter"
source_dir_to_target_dir_map["../../spring-cloud"]="spring-cloud"
source_dir_to_target_dir_map["../../demo-ssl-tls-https"]="ssl-tls-https"
source_dir_to_target_dir_map["../../demo-kubernetes"]="kubernetes"
source_dir_to_target_dir_map["../../demo-postman"]="postman"
source_dir_to_target_dir_map["../../aws"]="aws"
source_dir_to_target_dir_map["../../demo-aliyun"]="aliyun"
source_dir_to_target_dir_map["../../demo-vmware"]="vmware"
source_dir_to_target_dir_map["../../demo-java/demo-library"]="java-library"
source_dir_to_target_dir_map["../../elasticsearch"]="elasticsearch"
source_dir_to_target_dir_map["../../demo-terraform"]="terraform"
source_dir_to_target_dir_map["../../demo-golang"]="golang"
source_dir_to_target_dir_map["../../python"]="python"
source_dir_to_target_dir_map["../../demo-chrome"]="chrome"
source_dir_to_target_dir_map["../../demo-gcp"]="gcp"
source_dir_to_target_dir_map["../../demo-wireshark"]="wireshark"
source_dir_to_target_dir_map["../../demo-gost"]="gost"
source_dir_to_target_dir_map["../../front-end/demo-nodejs"]="nodejs"
source_dir_to_target_dir_map["../../demo-macos"]="macos"
source_dir_to_target_dir_map["../../demo-elk"]="elk"
source_dir_to_target_dir_map["../../demo-android"]="android"
source_dir_to_target_dir_map["../../demo-skywalking"]="skywalking"
source_dir_to_target_dir_map["../../demo-ruby"]="ruby"
source_dir_to_target_dir_map["../../demo-toolset"]="toolset"
source_dir_to_target_dir_map["../../demo-selenium"]="selenium"
source_dir_to_target_dir_map["../../demo-canal"]="canal"
source_dir_to_target_dir_map["../../demo-debezium"]="debezium"
source_dir_to_target_dir_map["../../demo-zookeeper"]="zookeeper"
source_dir_to_target_dir_map["../../demo-benchmark"]="benchmark"

for key in ${!source_dir_to_target_dir_map[@]}; do
    # 删除符号链接目录
    rm -rf docs/${source_dir_to_target_dir_map[$key]}
    mkdir -p docs/${source_dir_to_target_dir_map[$key]}
    # 查询源目录中 *.md 文件
    md_file_list=(`find $key -maxdepth 1 -iname "*\.md" -printf '%f\n'`)

    # 创建所有 *.md 文件软链接到 docs 中
    for md_file in ${md_file_list[@]}; do
        ln -s ../../$key/$md_file docs/${source_dir_to_target_dir_map[$key]}/$md_file
    done
done
