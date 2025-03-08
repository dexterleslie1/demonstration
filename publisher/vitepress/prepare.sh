#!/bin/bash

# 可以重复执行这个脚本
# 软链接外部文档到 docs 中

# 定义源目录到目标目录的对照
declare -A source_dir_to_target_dir_map
source_dir_to_target_dir_map["../../demo-cms-system"]="cms"
source_dir_to_target_dir_map["../../front-end/demo-vite"]="vite"
source_dir_to_target_dir_map["../../front-end/demo-vue"]="vue"
source_dir_to_target_dir_map["../../front-end/demo-element-ui"]="element-ui"
source_dir_to_target_dir_map["../../front-end/html+js+css"]="html-js-css"
source_dir_to_target_dir_map["../../demo-cloudflare"]="cloudflare"
source_dir_to_target_dir_map["../../linux"]="linux"
source_dir_to_target_dir_map["../../demo-spring-boot"]="springboot"
source_dir_to_target_dir_map["../../demo-redis"]="redis"


for key in ${!source_dir_to_target_dir_map[@]}; do
    # 删除符号链接目录
    rm -rf docs/${source_dir_to_target_dir_map[$key]}
    mkdir -p docs/${source_dir_to_target_dir_map[$key]}
    # 查询源目录中 *.md 文件
    file_list=(`find $key -maxdepth 1 \( -iname "*\.md" -o -iname "*\.png" \) -printf '%f\n'`)

    # 复制所有 *.md 文件到 docs 中
    for file in ${file_list[@]}; do
        cp $key/$file docs/${source_dir_to_target_dir_map[$key]}/$file
    done
done
