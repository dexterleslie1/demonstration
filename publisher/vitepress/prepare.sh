#!/bin/bash

# 可以重复执行这个脚本
# 软链接外部文档到 docs 中

# 定义源目录到目标目录的对照
declare -A source_dir_to_target_dir_map
source_dir_to_target_dir_map["../../demo-cms-system"]="cms"
source_dir_to_target_dir_map["../../front-end/demo-vite"]="vite"
source_dir_to_target_dir_map["../../front-end/demo-vue"]="vue"
source_dir_to_target_dir_map["../../front-end/demo-nuxt"]="nuxt"
source_dir_to_target_dir_map["../../front-end/demo-element-ui"]="element-ui"
source_dir_to_target_dir_map["../../front-end/html+js+css"]="html-js-css"
source_dir_to_target_dir_map["../../front-end/axios"]="axios"
source_dir_to_target_dir_map["../../demo-cloudflare"]="cloudflare"
source_dir_to_target_dir_map["../../linux"]="linux"
source_dir_to_target_dir_map["../../demo-spring-boot"]="springboot"
source_dir_to_target_dir_map["../../spring-cloud"]="springcloud"
source_dir_to_target_dir_map["../../demo-redis"]="redis"
source_dir_to_target_dir_map["../../demo-encrypt-decrypt"]="密码算法"
source_dir_to_target_dir_map["../../docker"]="docker"
source_dir_to_target_dir_map["../../demo-软件工程"]="软件工程"
source_dir_to_target_dir_map["../../demo-java/demo-library"]="java-library"
source_dir_to_target_dir_map["../../demo-java"]="java"
source_dir_to_target_dir_map["../../demo-jmeter"]="jmeter"
source_dir_to_target_dir_map["../../demo-git"]="git"
source_dir_to_target_dir_map["../../demo-shell-scripting"]="shell-scripting"
source_dir_to_target_dir_map["../../demo-benchmark"]="benchmark"
source_dir_to_target_dir_map["../../demo-dcli"]="dcli"
source_dir_to_target_dir_map["../../demo-科学上网"]="科学上网"
source_dir_to_target_dir_map["../../demo-frp"]="frp"
source_dir_to_target_dir_map["../../demo-golang"]="golang"
source_dir_to_target_dir_map["../../ansible"]="ansible"
source_dir_to_target_dir_map["../../aws"]="aws"
source_dir_to_target_dir_map["../../demo-aliyun"]="aliyun"
source_dir_to_target_dir_map["../../demo-gcp"]="gcp"
source_dir_to_target_dir_map["../../demo-ssl-tls-https"]="ssl-tls-https"
source_dir_to_target_dir_map["../../demo-mysql-n-mariadb"]="mysql-n-mariadb"
source_dir_to_target_dir_map["../../demo-word-to-pdf"]="word-to-pdf"
source_dir_to_target_dir_map["../../demo-idea"]="idea"
source_dir_to_target_dir_map["../../demo-http"]="http"
source_dir_to_target_dir_map["../../python"]="python"
source_dir_to_target_dir_map["../../front-end/demo-nodejs"]="nodejs"
source_dir_to_target_dir_map["../../openresty"]="openresty"
source_dir_to_target_dir_map["../../demo-english"]="english"
source_dir_to_target_dir_map["../../front-end"]="front-end"
source_dir_to_target_dir_map["../../front-end/electron"]="electron"
source_dir_to_target_dir_map["../../front-end/多媒体技术"]="多媒体技术"
source_dir_to_target_dir_map["../../front-end/demo-adobe"]="adobe"
source_dir_to_target_dir_map["../../front-end/demo-figma"]="figma"
source_dir_to_target_dir_map["../../demo-maven"]="maven"
source_dir_to_target_dir_map["../../demo-windows"]="windows"
source_dir_to_target_dir_map["../../demo-tencent"]="tencent"
source_dir_to_target_dir_map["../../demo-zookeeper"]="zookeeper"
source_dir_to_target_dir_map["../../elasticsearch"]="elasticsearch"
source_dir_to_target_dir_map["../../demo-tidb"]="tidb"

for key in ${!source_dir_to_target_dir_map[@]}; do
    # 删除符号链接目录
    rm -rf docs/${source_dir_to_target_dir_map[$key]}
    mkdir -p docs/${source_dir_to_target_dir_map[$key]}
    # 查询源目录中 *.md 文件
    file_list=(`find $key -maxdepth 1 \( -iname "*\.md" -o -iname "*\.png" -o -iname "*\.jpg" \) -printf '%f\n'`)

    # 复制所有 *.md 文件到 docs 中
    for file in ${file_list[@]}; do
        cp $key/$file docs/${source_dir_to_target_dir_map[$key]}/$file
    done
done
